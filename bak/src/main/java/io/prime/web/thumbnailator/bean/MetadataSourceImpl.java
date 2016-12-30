//package io.prime.web.thumbnailator.bean;
//
//import java.io.File;
//import java.io.IOException;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.util.Assert;
//
//import io.prime.web.thumbnailator.config.BeanDefinitionIdentifier;
//
///**
// * Default Implementation of MetadataSource
// */
//public class MetadataSourceImpl implements MetadataSource
//{
//	@Autowired
//	@Qualifier(BeanDefinitionIdentifier.SOURCE_DIRECTORY)
//	private String sourceDirectory;
//
//	@Autowired
//	@Qualifier(BeanDefinitionIdentifier.SOURCE_DIRECTORY)
//	private String filteredDirectory;
//	
//	@Autowired
//	@Qualifier(BeanDefinitionIdentifier.BASE_URL)
//	private String baseUrl;
//	
//	@Autowired
//	@Qualifier(BeanDefinitionIdentifier.DATABASE_ENABLED)
//	private Boolean databaseEnabled;
//	
//	@Autowired
//	private ResourceLoader resourceLoader;
//	
//	private Metadata metadata;
//	
//	@PostConstruct
//	public void init() throws IOException
//	{
//		File sourceDirectory = this.resourceLoader.getResource(this.sourceDirectory).getFile();
//		File filteredDirectory = this.resourceLoader.getResource(this.filteredDirectory).getFile();
//		
//		if( false == sourceDirectory.exists() ) {
//			Assert.isTrue(sourceDirectory.mkdirs(), "Unable to create directory for SourceDirectory, may be a permission issue. Please check on a specify directory: " + sourceDirectory.getAbsolutePath());
//		}
//		
//		if( false == filteredDirectory.exists() ) {
//			Assert.isTrue(filteredDirectory.mkdirs(), "Unable to create directory for Filtered"
//					+ "Directory, may be a permission issue. Please check on a specify directory: " + filteredDirectory.getAbsolutePath());
//		}
//		
//		Assert.isTrue(sourceDirectory.isDirectory(), "SourceDictory must be a directory: " + sourceDirectory.getAbsolutePath());
//		Assert.isTrue(filteredDirectory.isDirectory(), "FilteredDictory must be a directory: " + filteredDirectory.getAbsolutePath()); //TODO
//		
//		metadata = new Metadata(baseUrl, sourceDirectory, filteredDirectory, this.databaseEnabled.booleanValue());
//	}
//	
//	
//
//	public Metadata getMetadata() {
//		return metadata;
//	}
//}
