package com.prime.web.thumbnailer.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.prime.web.thumbnailer.bean.Metadata;
import com.prime.web.thumbnailer.bean.MetadataSource;
import com.prime.web.thumbnailer.bean.ThumbnailerFilter;
import com.prime.web.thumbnailer.bean.ThumbnailerFilterSource;
import com.prime.web.thumbnailer.domain.Image;
import com.prime.web.thumbnailer.exception.EmptyFileUploadException;
import com.prime.web.thumbnailer.exception.FilterNotFoundException;
import com.prime.web.thumbnailer.exception.NestedIOException;
import com.prime.web.thumbnailer.repository.ThumbnailerRepository;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class ThumbnailerUtil 
{
	@Autowired
	private MetadataSource metadataSource;
	
	@Autowired
	private ThumbnailerRepository repository;
	
	@Autowired
	private ThumbnailerFilterSource filterSource;

	public String upload(MultipartFile file) {
		Metadata metadata = metadataSource.getMetadata();
		try {
			if (file.isEmpty()) {
				throw new EmptyFileUploadException();
			}
			UUID uuid = UUID.randomUUID();
			String uuidString = uuid.toString().replace('-', Character.MIN_VALUE);
			String imageId = uuidString + "/" + file.getName();
			File targetDir = new File(metadata.getSourceDirectory().getAbsolutePath() + File.separator + uuidString);
			targetDir.mkdir();

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
				Image image = repository.findImageByImageId(imageId);
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
				List<ThumbnailerFilter> filters = this.filterSource.getFilters().get(filterName);
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
