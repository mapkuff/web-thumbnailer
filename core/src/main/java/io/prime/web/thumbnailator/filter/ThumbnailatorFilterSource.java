package io.prime.web.thumbnailator.filter;

import java.util.List;

public interface ThumbnailatorFilterSource
{

    public List<ThumbnailatorFilter> getFilters(String name);

}
