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

import io.prime.web.thumbnailator.core.bean.ImageFilterContext;
import io.prime.web.thumbnailator.core.util.Assert;
import io.prime.web.thumbnailator.core.util.ThumbnailatorUtil;
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
        Single<HttpServletRequest> servlet = null;

        final AtomicReference<HttpServletResponse> targetResponse = new AtomicReference<>();
        final AtomicReference<ImageFilterContext> targetFilterContext = new AtomicReference<>();

        if ( asyncSupport )
        {
            final AsyncContext asyncContext = req.startAsync();
            targetResponse.set( (HttpServletResponse) asyncContext.getResponse() );

            servlet = Single.just( asyncContext.getRequest() )
                            .cast( HttpServletRequest.class )
                            .subscribeOn( Schedulers.io() );
        }
        else
        {
            targetResponse.set( resp );
            servlet = Single.just( req );
        }

        servlet.doOnSuccess( this::validate )
               .map( this::getImageFilterContext )
               .doOnSuccess( targetFilterContext::set )
               .flatMap( e -> thumbnailatorUtil.getFiltered( e.getImageId(), e.getFilterName() ) )
               .doOnSuccess( targetFilterContext::set )
               .doOnSuccess( this.serveSuccessfulResponse( targetResponse.get() ) )
               .doOnError( this.serveErrorResponseResponse( targetResponse.get() ) )
               .subscribe();
    }

    public Consumer<ImageFilterContext> serveSuccessfulResponse( final HttpServletResponse response )
    {
        return filterContext ->
            {
                final File filteredFile = filterContext.getFilteredFile();
                final String mimeType = thumbnailatorUtil.detectImageMimetype( filteredFile.getName(), new FileInputStream( filteredFile ) )
                                                         .blockingGet();
                response.setHeader( "Content-Type", mimeType );
                response.flushBuffer();
                final FileInputStream input = new FileInputStream( filterContext.getFilteredFile() );
                IOUtils.copy( input, response.getOutputStream() );
            };
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
        Assert.isTrue( req.getRequestURI()
                          .contains( baseUrl ) );
    }

    public ImageFilterContext getImageFilterContext( final HttpServletRequest req )
    {
        final String filterName = null; // TODO
        final String imageId = null; // TODO
        return new ImageFilterContext( filterName, imageId );
    }

}
