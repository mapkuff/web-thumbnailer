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
		imageUrl = StringUtils.trimTrailingCharacter(imageUrl, '/');
		String[] imageTokens = imageUrl.split("/");
		
		if (imageTokens.length < 2) {
			throw new ImageInformationParsingException(String.format("Unable to parse url to ImageInformation, {url: %s, baseUrl: %s}", request.getRequestURI(), metadataSource.getMetadata().getBaseUrl()));
		}
		
		String filterName = imageTokens[0];
		int tokenLength = imageTokens.length;
		String imageId = "";
		for (int i=1 ; i<tokenLength; i++) {
			imageId += imageTokens[i];
			if (i < tokenLength-1) {
				imageId += "/";
			}
		}
		
		return new ImageInformation(filterName, imageId);
	}
}
