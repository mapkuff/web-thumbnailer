package io.prime.web.thumbnailator.servlet.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prime.web.thumbnailator.core.util.InternalAssert;
import io.prime.web.thumbnailator.core.util.ThumbnailatorUtil;
import io.prime.web.thumbnailator.servlet.bean.ImageFilterRequest;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WebThumbnailatorServlet extends HttpServlet
{
    private final Logger logger = LoggerFactory.getLogger( this.getClass() );

    private static final long serialVersionUID = 1L;

    private String baseUrl;

    private boolean asyncSupport;

    private ThumbnailatorUtil thumbnailatorUtil;

    public String fetchBaseUrl()
    {
        // TODO load from configuration
        return null;
    }

    public ThumbnailatorUtil fetchThumbnailatorUtil()
    {
        // TODO load from configuration
        return null;
    }

    public boolean fetchAsyncSupport()
    {
        // TODO
        return false;
    }

    @Override
    public void init() throws ServletException
    {
        super.init();
        asyncSupport = this.fetchAsyncSupport();
        baseUrl = this.fetchBaseUrl();
        thumbnailatorUtil = this.fetchThumbnailatorUtil();
        this.getServletContext()
            .setAttribute( ThumbnailatorUtil.class.getName(), thumbnailatorUtil );
    }

    @Override
    protected final void doGet( final HttpServletRequest req, final HttpServletResponse resp ) throws ServletException, IOException
    {
        HttpServletRequest targetRequest = null;

        final AtomicReference<HttpServletResponse> targetResponse = new AtomicReference<HttpServletResponse>();

        Scheduler scheduler = null;

        if ( asyncSupport ) {
            final AsyncContext asyncContext = req.startAsync();
            targetResponse.set( (HttpServletResponse) asyncContext.getResponse() );
            targetRequest = (HttpServletRequest) asyncContext.getRequest();
            scheduler = Schedulers.io();
        }

        else {
            targetResponse.set( resp );
            targetRequest = req;
            scheduler = Schedulers.trampoline();
        }

        Single.just( targetRequest )
              .doOnSuccess( this::validate )
              .map( this::toImageFilterContext )
              .doOnSuccess( e -> this.serveSuccessfulResponse( e.getImageId(), e.getFilterName(), targetResponse.get() ) )
              .doOnError( this.serveErrorResponseResponse( targetResponse.get() ) )
              .subscribeOn( scheduler )
              .subscribe();
    }

    public Consumer<File> serveSuccessfulResponse( final String imageId, final String filterName, final HttpServletResponse response )
    {
        return file ->
        {
            final File targetFile = thumbnailatorUtil.getFiltered( imageId, filterName )
                                                     .blockingGet();

            final String mimeType = thumbnailatorUtil.detectImageMimetype( this.getFileNameFromImageId( imageId ), new FileInputStream( targetFile ) )
                                                     .blockingGet();

            response.setHeader( "Content-Type", mimeType );
            response.flushBuffer();
            final FileInputStream input = new FileInputStream( targetFile );
            IOUtils.copy( input, response.getOutputStream() );
        };
    }

    public String getFileNameFromImageId( final String imageId )
    {
        return imageId.substring( imageId.lastIndexOf( '/' ) + 1 );
    }

    public Consumer<Throwable> serveErrorResponseResponse( final HttpServletResponse response )
    {
        return err ->
        {
            logger.error( err.getMessage(), err );
            response.sendError( 400, err.getMessage() );
        };
    }

    public void validate( final HttpServletRequest req )
    {
        InternalAssert.isTrue( req.getRequestURI()
                                  .contains( baseUrl ) );
    }

    public ImageFilterRequest toImageFilterContext( final HttpServletRequest req )
    {
        final String filterName = null; // TODO
        final String imageId = null; // TODO
        return new ImageFilterRequest( imageId, filterName );
    }

}
