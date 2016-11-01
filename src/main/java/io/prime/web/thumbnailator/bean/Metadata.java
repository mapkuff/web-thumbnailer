package io.prime.web.thumbnailator.bean;

import java.io.File;

public class Metadata 
{
	private String baseUrl;
	
	private File sourceDirectory;
	
	private File filteredDirectory;
	
	private boolean databaseEnabled = true; //TODO
	
	public Metadata(String baseUrl, File sourceDirectory, File filteredDirectory) 
	{
		this.baseUrl = baseUrl;
		this.sourceDirectory = sourceDirectory;
		this.filteredDirectory = filteredDirectory;
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

	public boolean isDatabaseEnabled() {
		return databaseEnabled;
	}

	public void setDatabaseEnabled(boolean databaseEnabled) {
		this.databaseEnabled = databaseEnabled;
	}
}
