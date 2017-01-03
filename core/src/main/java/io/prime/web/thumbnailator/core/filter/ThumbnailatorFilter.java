package io.prime.web.thumbnailator.core.filter;

import net.coobird.thumbnailator.Thumbnails.Builder;

public interface ThumbnailatorFilter
{
    <T> void filter(Builder<T> builder);
}
