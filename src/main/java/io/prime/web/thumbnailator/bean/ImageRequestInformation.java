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
			String requestURI = request.getRequestURI();
			Metadata metadata = metadataSource.getMetadata();
			String imageBaseUrl = request.getServletContext().getContextPath() + metadata.getBaseUrl();
			Assert.isTrue(requestURI.startsWith(imageBaseUrl + '/'), String.format("URL must start with '%s' but '%s' given", imageBaseUrl + '/', requestURI));
			
			String imageUrl = request.getRequestURI().substring(imageBaseUrl.length());
			imageUrl = StringUtils.trimLeadingCharacter(imageUrl, '/');
			imageUrl = StringUtils.trimTrailingCharacter(imageUrl, '/');
			Assert.isTrue(false == StringUtils.isEmpty(imageUrl), "No image information found in url: " + requestURI);
			
			String[] imageTokens = imageUrl.split("/");
			
			Assert.isTrue(
					imageTokens.length >= 2 && StringUtils.hasLength(imageTokens[0]) && StringUtils.hasLength(imageTokens[1]),
					String.format(
							"Not enough image information {filterName: %s, imageId: %s}", 
							imageTokens[0],
							imageTokens.length < 2 ? "" : imageTokens[1]
						)
				);
			
			String filterName = imageTokens[0];
			int tokenLength = imageTokens.length;
			String imageId = "";
			for (int i=1 ; i<tokenLength; i++) {
				imageId += "/" + imageTokens[i];
			}
			
			return new ImageRequestInformation(filterName, imageId);
			
		} catch (IllegalArgumentException e) {
			throw new ImageInformationParsingException("Unable to parse " + ImageRequestInformation.class.getName() + " from servlet", e);
		}
	}
}
