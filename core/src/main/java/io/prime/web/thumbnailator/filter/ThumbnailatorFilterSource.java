package io.prime.web.thumbnailator.filter;

import java.util.List;
import java.util.Map;

public interface ThumbnailatorFilterSource 
{
	public Map<String, List<ThumbnailatorFilter>> getFilters();

}
