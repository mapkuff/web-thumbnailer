package com.prime.web.thumbnailer.bean;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.prime.web.thumbnailer.config.BeanDefinitionIdentifier;
import com.prime.web.thumbnailer.repository.ThumbnailerRepository;

/**
 * Default Implementation of MetadataSource
 */
public class MetadataSourceImpl implements MetadataSource
{
	@Autowired
	@Qualifier(BeanDefinitionIdentifier.RAW_IMAGE_FILE)
	private File sourceDirectory;

	@Autowired
	@Qualifier(BeanDefinitionIdentifier.FILTERED_IMAGE_FILE)
	private File filteredDirectory;
	
	@Autowired
	@Qualifier(BeanDefinitionIdentifier.FILTERED_IMAGE_FILE)
	private ThumbnailerFilterSource filterSource;
	
	@Autowired
	@Qualifier(BeanDefinitionIdentifier.FILTERED_IMAGE_FILE)
	private ThumbnailerRepository repository;
	
	@Autowired
	@Qualifier(BeanDefinitionIdentifier.FILTERED_IMAGE_FILE)
	private String baseUrl;
	
	private Metadata metadata;

	public Metadata getMetadata() {
		if ( null == metadata ) {
			metadata = new Metadata(baseUrl, sourceDirectory, filteredDirectory, filterSource, repository);
		}
		return metadata;
	}
}
