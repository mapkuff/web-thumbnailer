package io.prime.web.thumbnailator.sources;

import java.util.List;

import io.prime.web.thumbnailator.filter.ThumbnailatorFilter;

public interface FilterSource
{

    public List<ThumbnailatorFilter> getFilters(String name);

}
