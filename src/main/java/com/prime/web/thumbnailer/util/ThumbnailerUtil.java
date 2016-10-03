package com.prime.web.thumbnailer.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.multipart.MultipartFile;

import com.prime.web.thumbnailer.bean.ThumbnailerFilter;
import com.prime.web.thumbnailer.bean.ThumbnailerFilterSource;
import com.prime.web.thumbnailer.config.BeanDefinitionIdentifier;
import com.prime.web.thumbnailer.domain.Image;
import com.prime.web.thumbnailer.exception.EmptyFileUploadException;
import com.prime.web.thumbnailer.exception.FilterNotFoundException;
import com.prime.web.thumbnailer.exception.NestedIOException;
import com.prime.web.thumbnailer.repository.ThumbnailerRepository;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class ThumbnailerUtil {

	@Autowired
	@Qualifier(BeanDefinitionIdentifier.RAW_IMAGE_FILE)
	private File directory;

	@Autowired
	@Qualifier(BeanDefinitionIdentifier.FILTERED_IMAGE_FILE)
	private File filteredDirectory;
	
	@Autowired
	private ThumbnailerFilterSource filterSource;
	
	@Autowired
	private ThumbnailerRepository repository;

	public String upload(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new EmptyFileUploadException();
			}
			UUID uuid = UUID.randomUUID();
			String uuidString = uuid.toString().replace('-', Character.MIN_VALUE);
			String imageId = uuidString + "/" + file.getName();
			File targetDir = new File(this.directory.getAbsolutePath() + File.separator + uuidString);
			targetDir.mkdir();

			File serverFile = new File(targetDir.getAbsolutePath() + File.separator + file.getName());
			if (false == serverFile.getAbsolutePath().startsWith(this.directory.getAbsolutePath())) {
				throw new RuntimeException("Malform imageId");
			}

			byte[] data = file.getBytes();
			Image image = new Image();
			image.setData(data);
			image.setId(imageId);
			this.repository.save(image);

			FileUtils.writeByteArrayToFile(serverFile, data);

			return imageId;
		} catch (IOException e) {
			throw new NestedIOException(e.getMessage(), e);
		}
	}
	
	public File download(String imageId, String filterName) 
	{
		try {
			String imagePath = imageId.replace('/', File.separatorChar);
			File targetFile = new File(this.directory.getAbsolutePath() + File.separator + imagePath );
			if (false == targetFile.exists()) {
				Image image = this.repository.findImageByImageId(imageId);
				FileUtils.writeByteArrayToFile(targetFile, image.getData());
			}
			
			File targetFilteredImage = new File(this.filteredDirectory.getAbsolutePath() + File.separator + filterName + File.separator + imagePath);
			if (false == targetFilteredImage.getAbsolutePath().startsWith(this.filteredDirectory.getAbsolutePath())) {
				throw new RuntimeException(); //FIXME
			}
			
			if (false == targetFilteredImage.exists()) {
				Builder<File> builder = Thumbnails.of(targetFile);
				if (false == filterSource.getFilters().containsKey(filterName)) {
					throw new FilterNotFoundException(filterName);
				}
				List<ThumbnailerFilter> filters = filterSource.getFilters().get(filterName);
				if (null != filters && filters.size() > 0) {
					for (ThumbnailerFilter targetFilter : filters) {
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
