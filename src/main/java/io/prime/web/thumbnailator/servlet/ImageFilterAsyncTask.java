package io.prime.web.thumbnailator.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prime.web.thumbnailator.bean.ImageInformation;
import io.prime.web.thumbnailator.bean.MetadataSource;
import io.prime.web.thumbnailator.util.ThumbnailatorUtil;

public class ImageFilterAsyncTask implements Runnable
{
	private static Logger logger = LoggerFactory.getLogger(ImageFilterAsyncTask.class);
	
	private AsyncContext asyncContext;
	
	private MetadataSource metadataSource;
	
	private ThumbnailatorUtil thumbnailerUtil;
	
	public ImageFilterAsyncTask(AsyncContext asyncContext, MetadataSource metadataSource,ThumbnailatorUtil thumbnailerUtil) 
	{
		this.asyncContext = asyncContext;
		this.metadataSource = metadataSource;
		this.thumbnailerUtil = thumbnailerUtil;
	}

	public void run() 
	{
		HttpServletRequest request = (HttpServletRequest) this.asyncContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) this.asyncContext.getResponse();
		try {
			ImageInformation imageInformation = ImageInformation.fromServletRequest(request, this.metadataSource);
			File file = this.thumbnailerUtil.get(imageInformation.getImageId(), imageInformation.getFilterName());
			ImageFilter.handleImageServing(response, file);
		} catch (Exception e) {
			try {
				ImageFilter.handleException(response, e);
			} catch (IOException e1) {
				logger.error("Error while handleException: " + e.getMessage(), e);
			}
		}
		asyncContext.complete();
	}
}
