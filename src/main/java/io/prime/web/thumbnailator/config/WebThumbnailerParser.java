package io.prime.web.thumbnailator.config;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.prime.web.thumbnailator.bean.MetadataSourceImpl;
import io.prime.web.thumbnailator.exception.ThumnailerConfigInitializeException;
import io.prime.web.thumbnailator.repository.ThumbnailerRepositoryImpl;
import io.prime.web.thumbnailator.util.ThumbnailerUtil;

public class WebThumbnailerParser  implements BeanDefinitionParser 
{
	public static final String XML_ATTR_SOURCE_DIRECTORY = "source-image-directory";
	public static final String XML_ATTR_FILTERED_DIRECTORY = "filtered-image-directory";
	public static final String XML_ATTR_BASE_URL = "base-url";

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
		
		// Repository
		BeanDefinitionBuilder builder = rootBeanDefinition(ThumbnailerRepositoryImpl.class);
		builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
		AbstractBeanDefinition def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, ThumbnailerRepositoryImpl.class.getName()));
		
		// Utils
		builder = rootBeanDefinition(ThumbnailerUtil.class);
		builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
		def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_SUPPORT);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, ThumbnailerUtil.class.getName()));
		
		// Entity Register Post Processor
		builder = rootBeanDefinition(EntityManagemntBeanPostProcessor.class);
		def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, EntityManagemntBeanPostProcessor.class.getName()));
		
		
		// Entity Register Post Processor
		builder = rootBeanDefinition(MetadataSourceImpl.class);
		def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, MetadataSourceImpl.class.getName()));
		
		return null;
	}

}
