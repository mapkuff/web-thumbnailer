package io.prime.web.thumbnailator.bean;

public class ImageRequest 
{
	private String filterName;
	
	private String imageId;
	
	public ImageRequest(String filterName, String imageId) 
	{
		this.filterName = filterName;
		this.imageId = imageId;
	}

	public String getFilterName() {
		return filterName;
	}

	public String getImageId() {
		return imageId;
	}
	
}
