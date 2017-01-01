package io.prime.web.thumbnailator.bean.internal;

import java.io.InputStream;

public class ImageCreationContext 
{
	private String fileName;
	
	private InputStream inputStream;
	
	public ImageCreationContext(String fileName, InputStream inputStream) {
		this.fileName = fileName;
		this.inputStream = inputStream;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
}
