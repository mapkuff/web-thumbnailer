package com.prime.web.thumbnailer.config;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import java.io.File;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.prime.web.thumbnailer.exception.ThumnailerConfigInitializeException;
import com.prime.web.thumbnailer.repository.ThumbnailerRepositoryImpl;
import com.prime.web.thumbnailer.util.ThumbnailerUtil;

public class WebThumbnailerParser  implements BeanDefinitionParser 
{
	private final String[] thumbnailer_attributes = new String[]{"raw-image-directory", "filtered-image-directory"};

	public BeanDefinition parse(Element element, ParserContext parserContext) 
	{
		for (String directoryAttribute : thumbnailer_attributes) {
			BeanDefinitionBuilder builder = rootBeanDefinition(File.class);
			String targetDirectory = element.getAttribute(directoryAttribute);
			if (null == targetDirectory || "" == targetDirectory) {
				throw new ThumnailerConfigInitializeException("Configuration for " + directoryAttribute + "was empty.");
			}
			File file = new File(targetDirectory);
			file.mkdirs();
			if (false == file.exists()) {
				throw new ThumnailerConfigInitializeException("Permission denied on creating directory: " + file.getAbsolutePath());
			}
			builder.addConstructorArgValue(targetDirectory);
//			builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
			
			AbstractBeanDefinition def = builder.getRawBeanDefinition();
			def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			def.setSource(parserContext.extractSource(element));
			parserContext.registerBeanComponent(new BeanComponentDefinition(def, BeanDefinitionIdentifier.ATTRIBUTE_TO_BEAN_ID_MAP.get(directoryAttribute)));
		}
		
		BeanDefinitionBuilder builder = rootBeanDefinition(ThumbnailerRepositoryImpl.class);
		builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
		AbstractBeanDefinition def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, ThumbnailerRepositoryImpl.class.getName()));
		
		builder = rootBeanDefinition(ThumbnailerUtil.class);
		builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
		def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_SUPPORT);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, ThumbnailerUtil.class.getName()));
		
		
		builder = rootBeanDefinition(EntityManagemntBeanPostProcessor.class);
		def = builder.getRawBeanDefinition();
		def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		def.setSource(parserContext.extractSource(element));
		parserContext.registerBeanComponent(new BeanComponentDefinition(def, EntityManagemntBeanPostProcessor.class.getName()));
		
		return null;
	}

}
