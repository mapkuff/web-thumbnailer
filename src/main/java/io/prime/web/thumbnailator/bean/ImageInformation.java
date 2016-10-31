package io.prime.web.thumbnailator.bean;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import io.prime.web.thumbnailator.exception.ImageInformationParsingException;

public class ImageInformation 
{
	private String filterName;
	
	private String imageId;
	
	public ImageInformation(String filterName, String imageId) 
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
	
	public static ImageInformation fromServletRequest(HttpServletRequest request, MetadataSource metadataSource) throws ImageInformationParsingException
	{
		String imageUrl = request.getRequestURI().substring(metadataSource.getMetadata().getBaseUrl().length());
		imageUrl = StringUtils.trimLeadingCharacter(imageUrl, '/');
		String[] imageTokens = imageUrl.split("/");
		
		if (3 != imageTokens.length) {
			throw new ImageInformationParsingException(String.format("Unable to parse url to ImageInformation, {url: %s, imgUrl: %s}", request.getRequestURI(), imageUrl));
		}
		
		String filterName = imageTokens[0];
		String imageId = imageTokens[1] + '/' + imageTokens[2];
		
		return new ImageInformation(filterName, imageId);
	}
}
