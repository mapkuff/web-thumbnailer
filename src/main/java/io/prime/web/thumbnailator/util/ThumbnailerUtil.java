package io.prime.web.thumbnailator.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import io.prime.web.thumbnailator.bean.Metadata;
import io.prime.web.thumbnailator.bean.MetadataSource;
import io.prime.web.thumbnailator.bean.ThumbnailatorFilter;
import io.prime.web.thumbnailator.bean.ThumbnailatorFilterSource;
import io.prime.web.thumbnailator.domain.Image;
import io.prime.web.thumbnailator.exception.EmptyFileUploadException;
import io.prime.web.thumbnailator.exception.FilterNotFoundException;
import io.prime.web.thumbnailator.exception.NestedIOException;
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

	public String upload(MultipartFile file) {
		Metadata metadata = metadataSource.getMetadata();
		try {
			if (file.isEmpty()) {
				throw new EmptyFileUploadException();
			}
			String imageId = this.imageIdFactory.generate(file.getName());
			File targetDir = new File(metadata.getSourceDirectory().getAbsolutePath() + File.separator + imageId.replaceAll("/", File.separator));
			Assert.isTrue(targetDir.mkdirs());

			File serverFile = new File(targetDir.getAbsolutePath() + File.separator + file.getName());
			if (false == serverFile.getAbsolutePath().startsWith(metadata.getSourceDirectory().getAbsolutePath())) {
				throw new RuntimeException("Malform imageId");
			}

			byte[] data = file.getBytes();
			Image image = new Image();
			image.setData(data);
			image.setId(imageId);
			repository.save(image);

			FileUtils.writeByteArrayToFile(serverFile, data);

			return imageId;
		} catch (IOException e) {
			throw new NestedIOException(e.getMessage(), e);
		}
	}
	
	public File getImageFile(String imageId, String filterName) 
	{
		Metadata metadata = this.metadataSource.getMetadata();
		try {
			String imagePath = imageId.replace('/', File.separatorChar);
			File targetFile = new File(metadata.getSourceDirectory().getAbsolutePath() + File.separator + imagePath );
			if (false == targetFile.exists()) {
				Image image = repository.findOne(imageId);
				FileUtils.writeByteArrayToFile(targetFile, image.getData());
			}
			
			File targetFilteredImage = new File(metadata.getFilteredDirectory().getAbsolutePath() + File.separator + filterName + File.separator + imagePath);
			if (false == targetFilteredImage.getAbsolutePath().startsWith(metadata.getFilteredDirectory().getAbsolutePath())) {
				throw new RuntimeException(); //FIXME
			}
			
			if (false == targetFilteredImage.exists()) {
				Builder<File> builder = Thumbnails.of(targetFile);
				if (false == this.filterSource.getFilters().containsKey(filterName)) {
					throw new FilterNotFoundException(filterName);
				}
				List<ThumbnailatorFilter> filters = this.filterSource.getFilters().get(filterName);
				if (null != filters && filters.size() > 0) {
					for (ThumbnailatorFilter targetFilter : filters) {
						targetFilter.filter(builder);
					}
				}
				builder.allowOverwrite(false)
					.toFile(targetFilteredImage);
			}
			return targetFilteredImage;
		} catch (IOException e) {
			throw new NestedIOException(e.getMessage(), e);
		}
	}

}
