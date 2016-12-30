//package io.prime.web.thumbnailator.servlet;
//
//import javax.servlet.AsyncContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import io.prime.web.thumbnailator.bean.MetadataSource;
//import io.prime.web.thumbnailator.util.ThumbnailatorUtil;
//
//public class ImageFilterAsyncHandler implements Runnable
//{
//	private static Logger logger = LoggerFactory.getLogger(ImageFilterAsyncHandler.class);
//	
//	private AsyncContext asyncContext;
//	
//	private MetadataSource metadataSource;
//	
//	private ThumbnailatorUtil thumbnailerUtil;
//	
//	public ImageFilterAsyncHandler(AsyncContext asyncContext, MetadataSource metadataSource,ThumbnailatorUtil thumbnailerUtil) 
//	{
//		this.asyncContext = asyncContext;
//		this.metadataSource = metadataSource;
//		this.thumbnailerUtil = thumbnailerUtil;
//	}
//
//	public void run() 
//	{
//		HttpServletRequest request = (HttpServletRequest) this.asyncContext.getRequest();
//		HttpServletResponse response = (HttpServletResponse) this.asyncContext.getResponse();
//		try {
//			Runnable delegator = new ImageFilterHandler(metadataSource, thumbnailerUtil, request, response);
//			delegator.run();
//		} finally {
//			asyncContext.complete();
//		}
//	}
//}
