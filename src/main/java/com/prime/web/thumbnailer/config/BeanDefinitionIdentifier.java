package com.prime.web.thumbnailer.config;

import java.util.HashMap;
import java.util.Map;

public class BeanDefinitionIdentifier 
{
	public static final String RAW_IMAGE_FILE = "PRIME_WEB_THUMBNAILER_RAW_IMAGE_DIRECTORY";
	public static final String FILTERED_IMAGE_FILE = "PRIME_WEB_THUMBNAILER_FILTERED_IMAGE_DIRECTORY";
	
	public static final Map<String,String> ATTRIBUTE_TO_BEAN_ID_MAP = new HashMap<String,String>();
	static {
		ATTRIBUTE_TO_BEAN_ID_MAP.put("raw-image-directory", RAW_IMAGE_FILE);
		ATTRIBUTE_TO_BEAN_ID_MAP.put("filtered-image-directory", FILTERED_IMAGE_FILE);
	}
	
}
