package io.prime.web.thumbnailator.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;

import io.prime.web.thumbnailator.bean.ImageInformation;
import io.prime.web.thumbnailator.bean.MetadataSource;
import io.prime.web.thumbnailator.util.ThumbnailatorUtil;

public class ImageFilterHandler implements Runnable
{
	private static Logger logger = LoggerFactory.getLogger(ImageFilterHandler.class);
	
	private MetadataSource metadataSource;
	
	private ThumbnailatorUtil thumbnailerUtil;
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	public ImageFilterHandler(MetadataSource metadataSource, ThumbnailatorUtil thumbnailerUtil,
			HttpServletRequest request, HttpServletResponse response) 
	{
		this.metadataSource = metadataSource;
		this.thumbnailerUtil = thumbnailerUtil;
		this.request = request;
		this.response = response;
	}

	public void run() 
	{
		try {
			ImageInformation imageInformation = ImageInformation.fromServletRequest(this.request, this.metadataSource);
			File file = this.thumbnailerUtil.get(imageInformation.getImageId(), imageInformation.getFilterName());
			String contentType = Files.probeContentType(file.toPath());
			if (false == contentType.startsWith("image")) {
				// TODO
			}
			response.setHeader("Content-Type", contentType);
			response.flushBuffer();
			IOUtils.copy(new FileInputStream(file), response.getOutputStream());
		} catch (Exception e) {
			this.handleException(e);
		}
	}
	
	public void handleException(Exception exception)
	{
		try {
			logger.error("Error while handling web-thumbnailator: " + exception.getMessage(), exception);
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			exception.printStackTrace(printWriter);
			String errorMessage = writer.toString();
			errorMessage = errorMessage.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
			errorMessage = errorMessage.replace("\r\n", "<br />");
			errorMessage = errorMessage.replace("\n", "<br />");
			this.response.setStatus(500);
			this.response
				.getWriter()
				.append("<html>")
					.append("<head>")
						.append("<title>Web Thumbnailator Error</title>")
						.append("<style>")
							.append("body {")
								.append("padding: 20px;")
							.append("}")
						.append("</style>")
					.append("</head>")
					.append("<body>")
						.append("<h1>Error while handling web-thumbnailator</h1>")
						.append("<h2>" + exception.getMessage() + "</h2>")
						.append("<hr />")
						.append("<h4>" + errorMessage + "</h4>")
					.append("</body>")
				.append("</html>")
				;
		} catch (IOException e) {
			throw new NestedRuntimeException(e.getMessage(), e) 
			{
				private static final long serialVersionUID = 1L;
			};
		}
		
	}
}
