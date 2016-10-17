package com.prime.web.thumbnailer.bean;

import java.io.File;

import com.prime.web.thumbnailer.repository.ThumbnailerRepository;

public class Metadata 
{
	private String baseUrl;
	
	private File sourceDirectory;
	
	private File filteredDirectory;
	
	private ThumbnailerFilterSource filterSource;
	
	private ThumbnailerRepository repository;

	public Metadata(String baseUrl, File sourceDirectory, File filteredDirectory, ThumbnailerFilterSource filterSource,
			ThumbnailerRepository repository) 
	{
		this.baseUrl = baseUrl;
		this.sourceDirectory = sourceDirectory;
		this.filteredDirectory = filteredDirectory;
		this.filterSource = filterSource;
		this.repository = repository;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public File getSourceDirectory() {
		return sourceDirectory;
	}

	public File getFilteredDirectory() {
		return filteredDirectory;
	}

	public ThumbnailerFilterSource getFilterSource() {
		return filterSource;
	}

	public ThumbnailerRepository getRepository() {
		return repository;
	}
}
