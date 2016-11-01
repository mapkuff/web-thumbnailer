package io.prime.web.thumbnailator.servlet;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import io.prime.web.thumbnailator.bean.MetadataSource;
import io.prime.web.thumbnailator.util.ThumbnailatorUtil;

public class ImageFilter implements Filter
{
	private static Logger logger = LoggerFactory.getLogger(ImageFilter.class);
	
	private  ExecutorService executor;
	
	@Autowired
	private MetadataSource metadataSource;
	
	@Autowired
	private ThumbnailatorUtil thumbnailerUtil;
	
	private Boolean isAsyncSupport = null;
	
	private int numberOfThreads;

	public void init(FilterConfig filterConfig) throws ServletException 
	{
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,	filterConfig.getServletContext());
		String numberOfThreadsString = filterConfig.getInitParameter("NUMBER_OF_THREADS");
		if (StringUtils.isEmpty(numberOfThreadsString)) {
			logger.warn("'NUMBER_OF_THREADS' not specified, use 1 thread to handle requests if async support enabled");
			this.numberOfThreads = 1;
		} else {
			this.numberOfThreads = Integer.parseInt(numberOfThreadsString);
			logger.info("using 'NUMBER_OF_THREADS' = " + this.numberOfThreads + " to handle image request");
		}
		executor = Executors.newFixedThreadPool(numberOfThreads);
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (request.getRequestURI().startsWith(this.metadataSource.getMetadata().getBaseUrl())) {
			if (this.isAsyncSupport(request)) {
				final AsyncContext asyncContext = request.startAsync();
				this.getExcecutor().submit(new ImageFilterAsyncHandler(asyncContext, this.metadataSource, thumbnailerUtil));
			} else {
				new ImageFilterHandler(metadataSource, thumbnailerUtil, request, response).run();
			}
		} else {
			chain.doFilter(request, response);
		}
	}
	
	private boolean isAsyncSupport(HttpServletRequest request)
	{
		if ( null == this.isAsyncSupport ) {
			synchronized (this) {
				if ( null == this.isAsyncSupport ) {
					try {
						ServletRequest.class.getMethod("isAsyncSupported");
						this.isAsyncSupport = request.isAsyncSupported();
						if (false == this.isAsyncSupport) {
							logger.warn("Asynce support is disabled or not configure in the configuration.");
						}
					} catch (NoSuchMethodException e) {
						logger.warn("Async support is disabled due the servlet version is lower than 3.0.", e);
						this.isAsyncSupport = false;
					} finally {
						if (false == this.isAsyncSupport.booleanValue()) {
							logger.warn("Async support is disabled. shutting down executor service");
							this.shutdownExecutor();
						}
					}
				}
			}
		}
		return this.isAsyncSupport.booleanValue();
	}
	
	private ExecutorService getExcecutor()
	{
		return this.executor;
	}

	public void destroy() 
	{
		logger.info("Shutting down executor service");
		this.shutdownExecutor();
	}
	
	private void shutdownExecutor()
	{
		ExecutorService executorService = this.getExcecutor();
		if (false == executorService.isShutdown()) {
			synchronized (this) {
				if (false == executorService.isShutdown()) {
					executorService.shutdown();
				}
			}
		}
	}
}
