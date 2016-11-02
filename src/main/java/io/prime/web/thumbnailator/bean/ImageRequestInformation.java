package io.prime.web.thumbnailator.bean;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.prime.web.thumbnailator.exception.ImageInformationParsingException;

public class ImageRequestInformation 
{
	private String filterName;
	
	private String imageId;
	
	public ImageRequestInformation(String filterName, String imageId) 
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
	
	public static ImageRequestInformation fromServletRequest(HttpServletRequest request, MetadataSource metadataSource) throws ImageInformationParsingException
	{
		try {
			String requestUri = request.getRequestURI();
			Metadata metadata = metadataSource.getMetadata();
			String baseImageUri = request.getServletContext().getContextPath() + metadata.getBaseUrl();
			
			baseImageUri = StringUtils.trimLeadingCharacter(baseImageUri, '/');
			baseImageUri = StringUtils.trimTrailingCharacter(baseImageUri, '/');
			requestUri = StringUtils.trimLeadingCharacter(requestUri, '/');
			requestUri = StringUtils.trimTrailingCharacter(requestUri, '/');
			String[] baseImageUriTokens = baseImageUri.split("/");
			String[] requestUriTokens = requestUri.split("/");
			
			Assert.isTrue(requestUriTokens.length - baseImageUriTokens.length >= 2, String.format("Invalid image URL. expected this pattern /%s/{filter}/{imageIdToken1}/{imageIdToken2}..., '/%s' given", baseImageUri, requestUri));
			
			String filterName = requestUriTokens[baseImageUriTokens.length];
			int requestUriTokensLength = requestUriTokens.length;
			String imageId = "";
			for (int i=baseImageUriTokens.length+1 ; i<requestUriTokensLength; i++) {
				imageId += "/" + requestUriTokens[i];
			}
			
			return new ImageRequestInformation(filterName, imageId);
			
		} catch (IllegalArgumentException e) {
			throw new ImageInformationParsingException("Unable to parse " + ImageRequestInformation.class.getName() + " from servlet", e);
		}
	}
}
