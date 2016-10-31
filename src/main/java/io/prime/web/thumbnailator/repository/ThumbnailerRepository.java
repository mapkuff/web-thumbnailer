package io.prime.web.thumbnailator.repository;

import io.prime.web.thumbnailator.domain.Image;

public interface ThumbnailerRepository 
{
	Image findImageByImageId(String imageId);
	void save(Image image);
}
