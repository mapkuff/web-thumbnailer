package com.prime.web.thumbnailer.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class WebThumbnailerNameSpaceHandler extends NamespaceHandlerSupport {

	public void init() 
	{
		registerBeanDefinitionParser("web-thumbnailer", new WebThumbnailerParser());
	}
	
}
