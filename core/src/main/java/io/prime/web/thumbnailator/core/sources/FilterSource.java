package io.prime.web.thumbnailator.core.sources;

import java.util.List;

import io.prime.web.thumbnailator.core.filter.ThumbnailatorFilter;

public interface FilterSource
{

    public List<ThumbnailatorFilter> getFilters(String name);

}
