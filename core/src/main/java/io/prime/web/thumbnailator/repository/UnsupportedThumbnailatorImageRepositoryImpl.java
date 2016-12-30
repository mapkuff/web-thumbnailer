package io.prime.web.thumbnailator.repository;

import io.prime.web.thumbnailator.domain.Image;

public class UnsupportedThumbnailatorImageRepositoryImpl implements ThumbnailatorImageRepository
{
	@Override
	public Image findOne(String imageId) 
	{
		throw new UnsupportedOperationException("WebThumbnailator configuration for 'DatabaseEnabled' was set to false");
	}

	@Override
	public void save(Image image) 
	{
		throw new UnsupportedOperationException("WebThumbnailator configuration for 'DatabaseEnabled' was set to false");
	}
}
