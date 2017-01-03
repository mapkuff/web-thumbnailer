package io.prime.web.thumbnailator.core.util;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import io.prime.web.thumbnailator.core.bean.ImageCreationContext;
import io.prime.web.thumbnailator.core.bean.ImageFilterContext;
import io.prime.web.thumbnailator.core.exception.ThumbnailatorFileNotFoundException;
import io.reactivex.Completable;
import io.reactivex.Observable;

public class InternalUtilValidator
{

    public static void validateBlacklist( final String testingString, final String ... blacklist )
    {
        Completable.fromObservable( Observable.fromIterable( Arrays.asList( blacklist ) )
                                              .switchIfEmpty( Observable.error( new IllegalArgumentException( "Empty blacklist to check!" ) ) )
                                              .doOnNext( e -> Assert.isTrue( BooleanUtils.isFalse( testingString.contains( e ) ),
                                                                             String.format( "Given string must not contains '%s': %s",
                                                                                            e,
                                                                                            testingString ) ) ) )
                   .blockingAwait();
    }

    public static void validate( final ImageCreationContext imageCreationContext )
    {
        Assert.isTrue( StringUtils.isNotBlank( imageCreationContext.getFileName() ),
                       "File name must NOT be blank: " + imageCreationContext.getFileName() );
        Assert.notNull( imageCreationContext.getInputStream(), "Inputstream must NOT be null." );
        validateBlacklist( imageCreationContext.getFileName(), "/" );
    }

    public static void validate( final ImageFilterContext imageFilterContext )
    {
        Assert.isTrue( StringUtils.isNotBlank( imageFilterContext.getFilterName() ), "Filter name must NOT be blank." );
        Assert.isTrue( StringUtils.isNotBlank( imageFilterContext.getImageId() ), "ImageId must NOT be blank." );
        validateBlacklist( imageFilterContext.getFilterName(), "/" );
        validateBlacklist( imageFilterContext.getImageId(), "/" );
    }

    public static void fileMustExists( final File file ) throws ThumbnailatorFileNotFoundException
    {
        Assert.isTrue( BooleanUtils.isTrue( file.exists() ), "File not found: " + file.getPath() );
    }

    public static void fileMustNotExists( final File file ) throws ThumbnailatorFileNotFoundException
    {
        Assert.isTrue( BooleanUtils.isFalse( file.exists() ), "File already exists: " + file.getPath() );
    }
}
