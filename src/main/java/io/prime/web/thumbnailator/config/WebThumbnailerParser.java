package io.prime.web.thumbnailator.config;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import io.prime.web.thumbnailator.bean.MetadataSourceImpl;
import io.prime.web.thumbnailator.exception.ThumnailerConfigInitializeException;
import io.prime.web.thumbnailator.repository.ThumbnailatorImageRepositoryImpl;
import io.prime.web.thumbnailator.repository.UnsupportedThumbnailatorImageRepositoryImpl;
import io.prime.web.thumbnailator.util.ThumbnailatorUtil;
import io.prime.web.thumbnailator.util.UUIDImageIdGenerator;

public class WebThumbnailerParser  implements BeanDefinitionParser 
{
	public static final String XML_ATTR_SOURCE_DIRECTORY = "source-image-directory";
	public static final String XML_ATTR_FILTERED_DIRECTORY = "filtered-image-directory";
	public static final String XML_ATTR_BASE_URL = "base-url";
	public static final String XML_ATTR_DATABASE_ENABLED = "database-enabled";

	public BeanDefinition parse(Element element, ParserContext parserContext) 
	{
		Map<String, String> xmlAttributeToBeanIdentifierMap = new HashMap<String, String>();
		xmlAttributeToBeanIdentifierMap.put(XML_ATTR_SOURCE_DIRECTORY, BeanDefinitionIdentifier.SOURCE_DIRECTORY);
		xmlAttributeToBeanIdentifierMap.put(XML_ATTR_FILTERED_DIRECTORY, BeanDefinitionIdentifier.FILTERED_DIRECTORY);
		xmlAttributeToBeanIdentifierMap.put(XML_ATTR_BASE_URL, BeanDefinitionIdentifier.BASE_URL);
		
		for (Entry<String, String> entry : xmlAttributeToBeanIdentifierMap.entrySet()) {
			
			String xmlAttribute = entry.getKey();
			String identifier = entry.getValue();
			
			BeanDefinitionBuilder builder = rootBeanDefinition(String.class);
			String targetDirectory = element.getAttribute(xmlAttribute);
			if (StringUtils.isEmpty(targetDirectory) ) {
				throw new ThumnailerConfigInitializeException("Configuration for " + xmlAttribute + "was empty.");
			}
			builder.addConstructorArgValue(targetDirectory);
			AbstractBeanDefinition def = builder.getRawBeanDefinition();
			def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			def.setSource(parserContext.extractSource(element));
			parserContext.registerBeanComponent(new BeanComponentDefinition(def, identifier));
		}
		
		// DB ENABLED
		Boolean dbEnabled = new Boolean(element.getAttribute(XML_ATTR_DATABASE_ENABLED));
		BeanDefinitionBuilder builder = rootBeanDefinition(Boolean.class);
		builder.addConstructorArgValue(dbEnabled);
		AbstractBeanDefinition def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, BeanDefinitionIdentifier.DATABASE_ENABLED));
		
		// Repository
		Class<?> repositoryClass = dbEnabled.booleanValue() ? ThumbnailatorImageRepositoryImpl.class : UnsupportedThumbnailatorImageRepositoryImpl.class;
		builder = rootBeanDefinition(repositoryClass);
		builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
		def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, repositoryClass.getName()));
		
		// Utils
		builder = rootBeanDefinition(ThumbnailatorUtil.class);
		builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
		def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_SUPPORT);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, ThumbnailatorUtil.class.getName()));
		
		// Entity Register Post Processor
		if (dbEnabled) {
			builder = rootBeanDefinition(EntityManagemntBeanPostProcessor.class);
			def = builder.getRawBeanDefinition();
			def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			def.setSource(parserContext.extractSource(element));
			parserContext.registerBeanComponent(new BeanComponentDefinition(def, EntityManagemntBeanPostProcessor.class.getName()));
		}
		
		// Metadata Source
		builder = rootBeanDefinition(MetadataSourceImpl.class);
		def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, MetadataSourceImpl.class.getName()));
		
		// Image ID Geneartor
		builder = rootBeanDefinition(UUIDImageIdGenerator.class);
		def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, UUIDImageIdGenerator.class.getName()));
		
		return null;
	}

}
