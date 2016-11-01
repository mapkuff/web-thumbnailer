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

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import io.prime.web.thumbnailator.bean.Metadata;
import io.prime.web.thumbnailator.bean.MetadataSource;
import io.prime.web.thumbnailator.bean.ThumbnailatorFilter;
import io.prime.web.thumbnailator.bean.ThumbnailatorFilterSource;
import io.prime.web.thumbnailator.domain.Image;
import io.prime.web.thumbnailator.exception.FilterNotFoundException;
import io.prime.web.thumbnailator.factory.ImageIdFactory;
import io.prime.web.thumbnailator.repository.ThumbnailatorImageRepository;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class ThumbnailerUtil 
{
	@Autowired
	private MetadataSource metadataSource;
	
	@Autowired
	private ThumbnailatorImageRepository repository;
	
	@Autowired
	private ThumbnailatorFilterSource filterSource;
	
	@Autowired
	private ImageIdFactory imageIdFactory;
	
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
	
	public String create(MultipartFile file) throws IOException 
	{
		return this.create(file.getInputStream(), file.getOriginalFilename());
	}
	
	
	
	/**
	 * 
	 * @param imageId
	 * @param filterName
	 * @return filtered image File
	 * @throws IOException 
	 * @throws FileNotFoundException
	 * @throws NoResultException
	 */
	public File get(String imageId, String filterName) throws IOException 
	{
		Metadata metadata = this.metadataSource.getMetadata();
		try {
			imageId = imageId.replace('/', File.separatorChar);
			File sourceFile = new File(metadata.getSourceDirectory().getPath() + File.separator + imageId );
			if (false == sourceFile.exists()) {
				if (false == metadata.isDatabaseEnabled()) {
					throw new FileNotFoundException("Source image was not found: " + sourceFile.getPath());
				}
				// recover file from DB
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
			}
			
			File filteredImage = new File(metadata.getFilteredDirectory().getPath() + File.separator + filterName + File.separator + imageId);
			if (false == filteredImage.exists()) {
				// generate filtered image
				synchronized (imageId.intern()) {
					if (false == filteredImage.exists()) {
						Builder<File> builder = Thumbnails.of(sourceFile);
						if (false == this.filterSource.getFilters().containsKey(filterName)) {
							throw new FilterNotFoundException(filterName);
						}
						List<ThumbnailatorFilter> filters = this.filterSource.getFilters().get(filterName);
						if (false == CollectionUtils.isEmpty(filters)) {
							for (ThumbnailatorFilter targetFilter : filters) {
								targetFilter.filter(builder);
							}
						}
						builder.allowOverwrite(false)
							.toFile(filteredImage);
					}
				}
			}
			
			return filteredImage;
		} catch (IOException e) {
			throw e;
		}
	}

}
