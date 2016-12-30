//package com.prime.web.thumbnailer.test.unit.bean;
//
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockServletContext;
//import org.springframework.util.StringUtils;
//
//import com.prime.web.thumbnailer.test.unit.AbstractSpringTest;
//
//import io.prime.web.thumbnailator.bean.ImageRequestInformation;
//import io.prime.web.thumbnailator.bean.Metadata;
//import io.prime.web.thumbnailator.bean.MetadataSource;
//import io.prime.web.thumbnailator.exception.ImageInformationParsingException;
//
//public class ImageRequestInformationFromServletTest extends AbstractSpringTest
//{
//	private static ServletContext context;
//	
//	@Autowired
//	private MetadataSource metadataSource;
//	
//	@BeforeClass
//	public static void init()
//	{
//		String contextPath = "/testApp";
//		MockServletContext mockContext = new MockServletContext();
//		mockContext.setContextPath(contextPath);
//		context = mockContext;
//	}
//	
//	private MockHttpServletRequest createRequest(String urlWithoutContext)
//	{
//		String contextPath = context.getContextPath();
//		urlWithoutContext = '/' + StringUtils.trimLeadingCharacter(urlWithoutContext, '/');
//		MockHttpServletRequest request = new MockHttpServletRequest(context);
//		request.setRequestURI(contextPath + urlWithoutContext);
//		return request;
////		request.setRequestURI(contextPath + "/imagess/someFilter/image/id/sample.jpg");
//	}
//	
//	@Test
//	public void testValidUrl() throws ImageInformationParsingException
//	{
//		String imageId = "/image/id/sample.jpg";
//		String filterName = "someFilter";
//		Metadata metadata = this.metadataSource.getMetadata();
//		
//		HttpServletRequest request = this.createRequest(String.format("%s/%s%s", metadata.getBaseUrl(), filterName, imageId));
//		ImageRequestInformation imageRequest = ImageRequestInformation.fromServletRequest(request, metadataSource);
//		Assert.assertEquals(filterName, imageRequest.getFilterName());
//		Assert.assertEquals(imageId, imageRequest.getImageId());
//	}
//	
//	// should test this case on servlet filter
////	@Test(expected=ImageInformationParsingException.class)
////	public void testInvalidBaseUrl1() throws ImageInformationParsingException
////	{
////		String imageId = "/image/id/sample.jpg";
////		String filterName = "someFilter";
////		
////		HttpServletRequest request = this.createRequest(String.format("%s/%s%s", "/image", filterName, imageId));
////		ImageRequestInformation.fromServletRequest(request, metadataSource);
////	}
//	
//	// should test this case on servlet filter
////	@Test(expected=ImageInformationParsingException.class)
////	public void testInvalidBaseUrl2() throws ImageInformationParsingException
////	{
////		String imageId = "/image/id/sample.jpg";
////		String filterName = "someFilter";
////		
////		HttpServletRequest request = this.createRequest(String.format("%s/%s%s", "/imagess", filterName, imageId));
////		ImageRequestInformation.fromServletRequest(request, metadataSource);
////	}
//	
//	@Test(expected=ImageInformationParsingException.class)
//	public void testBaseImageUrlTalingWithSlash() throws ImageInformationParsingException
//	{
//		HttpServletRequest request = this.createRequest( "/images/");
//		ImageRequestInformation.fromServletRequest(request, metadataSource);
//	}
//	
//	@Test(expected=ImageInformationParsingException.class)
//	public void testBaseImageUrlWithFilterOnly() throws ImageInformationParsingException
//	{
//		HttpServletRequest request = this.createRequest( "/images/filter");
//		ImageRequestInformation.fromServletRequest(request, metadataSource);
//	}
//	
//	@Test(expected=ImageInformationParsingException.class)
//	public void testBaseImageUrlWithFilterOnlyTalingWithSlash() throws ImageInformationParsingException
//	{
//		HttpServletRequest request = this.createRequest( "/images/filter/");
//		ImageRequestInformation.fromServletRequest(request, metadataSource);
//	}
//}
