package io.prime.web.thumbnailator.bean;

import java.util.List;
import java.util.Map;

public class ThumbnailatorFilterSource 
{
	private Map<String, List<ThumbnailatorFilter>> filters;

	public Map<String, List<ThumbnailatorFilter>> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, List<ThumbnailatorFilter>> filters) {
		this.filters = filters;
	}
}
