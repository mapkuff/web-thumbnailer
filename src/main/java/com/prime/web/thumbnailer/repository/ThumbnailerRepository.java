package com.prime.web.thumbnailer.repository;

import com.prime.web.thumbnailer.domain.Image;

public interface ThumbnailerRepository 
{
	Image findImageByImageId(String imageId);
	void save(Image image);
}
