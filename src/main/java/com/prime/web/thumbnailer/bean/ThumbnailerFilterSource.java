package com.prime.web.thumbnailer.bean;

import java.util.List;
import java.util.Map;

public class ThumbnailerFilterSource 
{
	private Map<String, List<ThumbnailerFilter>> filters;

	public Map<String, List<ThumbnailerFilter>> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, List<ThumbnailerFilter>> filters) {
		this.filters = filters;
	}
}
