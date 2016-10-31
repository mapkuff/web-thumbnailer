package io.prime.web.thumbnailator.bean;

import static net.coobird.thumbnailator.Thumbnails.Builder;

public interface ThumbnailatorFilter 
{
	<T> void filter(Builder<T> builder);
}
