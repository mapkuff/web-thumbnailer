package io.prime.web.thumbnailator.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.persistence.NoResultException;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import io.prime.web.thumbnailator.bean.Metadata;
import io.prime.web.thumbnailator.bean.MetadataSource;
import io.prime.web.thumbnailator.domain.Image;
import io.prime.web.thumbnailator.exception.FilterNotFoundException;
import io.prime.web.thumbnailator.filter.ThumbnailatorFilter;
import io.prime.web.thumbnailator.filter.ThumbnailatorFilterSource;
import io.prime.web.thumbnailator.repository.ThumbnailatorImageRepository;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class ThumbnailatorUtil 
{
	@Autowired
	private MetadataSource metadataSource;
	
	@Autowired
	private ThumbnailatorImageRepository repository;
	
	@Autowired
	private ThumbnailatorFilterSource filterSource;
	
	@Autowired
	private ImageIdGenerator imageIdFactory;
	
	public String create(InputStream input, String fileName) throws IOException
	{
		Metadata metadata = metadataSource.getMetadata();
		String imageId = this.imageIdFactory.generate(fileName);
		
		String ImagePath = metadata.getSourceDirectory().getPath() + File.separator + imageId.replaceAll("/", File.separator);
		String imageDirectoryPath = ImagePath.substring(0, ImagePath.lastIndexOf(File.separator));
		
		File targetDir = new File(imageDirectoryPath);
		Assert.isTrue(targetDir.mkdirs(), "Unable to create directory for image, may be a permission issue: " + imageDirectoryPath);
		File imageSourceFile = new File(ImagePath);
		
		// save into DB
		if (metadata.isDatabaseEnabled()) {
			Image image = new Image();
			try {
				byte[] data = IOUtils.toByteArray(input);
				image.setData(data);
			} finally {
				IOUtils.closeQuietly(input);
			}
			input = new ByteArrayInputStream(image.getData());
			image.setId(imageId);
			repository.save(image);
		}
		
		FileOutputStream fileOutputStream = null;
		try {
			Assert.isTrue(false == imageSourceFile.exists(), "Image already exists: " + ImagePath);
			Assert.isTrue(imageSourceFile.createNewFile(), "Unable to create file: " + ImagePath);
			fileOutputStream = new FileOutputStream(imageSourceFile);
			IOUtils.copy(input, fileOutputStream);
		} finally {
			IOUtils.closeQuietly(fileOutputStream);
			IOUtils.closeQuietly(input);
		}
		return imageId;
	}
	
	public String create(MultipartFile multipartFile) throws IOException 
	{
		return this.create(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
	}
	
	public String create(Part part) throws IOException 
	{
		return this.create(part.getInputStream(), part.getName());
	}
	
	public String create(byte[] data, String fileName) throws IOException 
	{
		return this.create(new ByteArrayInputStream(data), fileName);
	}
	
	
	/**
	 * 
	 * @param imageId
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException for unable to get source file
	 */
	public File getSource(String imageId) throws IOException
	{
		Metadata metadata = this.metadataSource.getMetadata();
		imageId = imageId.replace('/', File.separatorChar);
		File sourceFile = new File(metadata.getSourceDirectory().getPath() + File.separator + imageId );
		
		if (false == sourceFile.exists()) {
			if (false == metadata.isDatabaseEnabled()) {
				throw new FileNotFoundException("Source image was not found: " + sourceFile.getPath());
			}
			// recover file from DB
			try {
				Image image = this.repository.findOne(imageId.replace(File.separatorChar, '/'));
				synchronized (image.getId().intern()) {
					if (false == sourceFile.exists()) {
						Assert.isTrue(sourceFile.createNewFile()); //TODO message
						InputStream input = new ByteArrayInputStream(image.getData());
						OutputStream output = new FileOutputStream(sourceFile);
						try{
							IOUtils.copy(input, output);
						} finally {
							IOUtils.closeQuietly(input);
							IOUtils.closeQuietly(output);
						}
					}
				}
			} catch (NoResultException e) {
				throw new FileNotFoundException("Source image was not found and unable to recover from DB: " + sourceFile.getPath());
			}
		}
		return sourceFile;
	}
	
	
	/**
	 * 
	 * @param imageId
	 * @param filterName
	 * @return filtered image File
	 * @throws IOException 
	 * @throws FileNotFoundException for unable to get source image from imageId
	 * @throws FilterNotFoundException for specify filterName was not found in configuration
	 * @throws SecurityException for permission denied while processing file
	 */
	public File get(String imageId, String filterName) throws IOException 
	{
		Metadata metadata = this.metadataSource.getMetadata();
		try {
			File filteredImage = new File(metadata.getFilteredDirectory().getPath() + File.separator + filterName + File.separator + imageId);
			if (false == filteredImage.exists()) {
				// generate filtered image
				synchronized (imageId.intern()) {
					if (false == filteredImage.exists()) {
						File sourceFile = this.getSource(imageId);
						Assert.isTrue(filteredImage.createNewFile()); //TODO message
						Builder<File> builder = Thumbnails.of(sourceFile);
						List<ThumbnailatorFilter> filters = this.filterSource.getFilters().get(filterName);
						
						if (CollectionUtils.isEmpty(filters)) {
							throw new FilterNotFoundException(filterName);
						}
						for (ThumbnailatorFilter targetFilter : filters) {
							targetFilter.filter(builder);
						}
						
						builder.allowOverwrite(false).toFile(filteredImage);
					}
				}
			}
			return filteredImage;
		} catch (IOException e) {
			throw e;
		}
	}

}
