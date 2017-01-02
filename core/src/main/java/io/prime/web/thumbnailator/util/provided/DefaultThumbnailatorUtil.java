package io.prime.web.thumbnailator.util.provided;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import io.prime.web.thumbnailator.bean.Metadata;
import io.prime.web.thumbnailator.bean.MetadataSource;
import io.prime.web.thumbnailator.bean.internal.ImageCreationContext;
import io.prime.web.thumbnailator.bean.internal.ImageFilterContext;
import io.prime.web.thumbnailator.bean.internal.spec.ImageIdContainer;
import io.prime.web.thumbnailator.exception.ThumbnailatorFileNotFoundException;
import io.prime.web.thumbnailator.filter.ThumbnailatorFilterSource;
import io.prime.web.thumbnailator.util.ImageIdGenerator;
import io.prime.web.thumbnailator.util.ThumbnailatorUtil;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class DefaultThumbnailatorUtil implements ThumbnailatorUtil
{
    private MetadataSource metadataSource;

    private ThumbnailatorFilterSource filterSource;

    private ImageIdGenerator imageIdFactory;

    final BiConsumer<String, File> sourceRecovery = null;

    private void validateBlacklist( final String testingString, final String ... blacklist )
    {
        Completable.fromObservable( Observable.fromIterable( Arrays.asList( blacklist ) )
                                              .switchIfEmpty( Observable.error( new IllegalArgumentException( "Empty blacklist to check!" ) ) )
                                              .doOnNext( e ->
                                                  {
                                                      if ( testingString.contains( e ) )
                                                      {
                                                          throw new IllegalArgumentException( String.format( "Given string must not contains '%s': %s", e, testingString ) );
                                                      }
                                                  } ) )
                   .blockingAwait();
    }

    private void validate( final ImageCreationContext imageCreationContext )
    {
        if ( StringUtils.isBlank( imageCreationContext.getFileName() ) )
        {
            throw new IllegalArgumentException( "File name must NOT be blank." );
        }
        if ( null == imageCreationContext.getInputStream() )
        {
            throw new IllegalArgumentException( "Inputstream must NOT be null." );
        }

        validateBlacklist( imageCreationContext.getFileName(), "/" );
    }

    private void validate( final ImageFilterContext imageFilterContext )
    {
        if ( StringUtils.isBlank( imageFilterContext.getFilterName() ) )
        {
            throw new IllegalArgumentException( "Filter name must NOT be blank." );
        }
        if ( StringUtils.isBlank( imageFilterContext.getImageId() ) )
        {
            throw new IllegalArgumentException( "ImageId must NOT be blank." );
        }

        validateBlacklist( imageFilterContext.getFilterName(), "/" );
        validateBlacklist( imageFilterContext.getImageId(), "/" );
        // TODO add more validate
    }

    public Single<ImageCreationContext> create( final InputStream inputStream, final String fileName )
    {
        return Single.just( new ImageCreationContext( fileName, inputStream ) )
                     .doOnSuccess( this::validate )
                     .doOnSuccess( this::generateImageId )
                     .doOnSuccess( this::createFileFromImageId )
                     .doOnSuccess( this::writeDataToFile );
    }

    private void generateImageId( final ImageCreationContext imageCreationContext )
    {
        final String filename = imageCreationContext.getFileName();
        final String imageId = imageIdFactory.generate( filename );
        imageCreationContext.setImageId( imageId );
    }

    private void createFileFromImageId( final ImageCreationContext imageCreationContext ) throws IOException
    {
        final Metadata metadata = metadataSource.getMetadata();
        final File sourceDirectory = metadata.getSourceDirectory();
        final String imageId = imageIdFactory.generate( imageCreationContext.getFileName() );
        final String ImagePath = sourceDirectory.getPath() + File.separator + ( imageId.replaceAll( "/", File.separator ) );
        final File imageSourceFile = new File( ImagePath );
        // make sure file doesn't exists
        if ( BooleanUtils.isTrue( imageSourceFile.exists() ) )
        {
            throw new IllegalArgumentException( "Target file already exists: " + imageSourceFile.getPath() );
        }
        imageSourceFile.createNewFile();
    }

    private void writeDataToFile( final ImageCreationContext imageCreationContext ) throws IOException
    {
        final File imageFile = imageCreationContext.getTargetSourceFile();
        final FileOutputStream fileOutputStream = new FileOutputStream( imageFile );
        final InputStream imageData = imageCreationContext.getInputStream();
        try
        {
            IOUtils.copy( imageData, fileOutputStream );
        }
        finally
        {
            IOUtils.closeQuietly( imageData );
            IOUtils.closeQuietly( fileOutputStream );
        }
    }

    public Single<File> getSource( final String imageId )
    {
        return resolveSourceFile( () -> imageId ).doOnSuccess( this::fileMustExists );
    }

    private Single<File> resolveSourceFile( final ImageIdContainer imageIdContainer )
    {
        final String imageId = imageIdContainer.getImageId();
        final Metadata metadata = metadataSource.getMetadata();
        final File sourceDirectory = metadata.getSourceDirectory();
        final String osBasedImageId = imageId.replace( '/', File.separatorChar );
        final File result = new File( sourceDirectory.getPath() + File.separator + osBasedImageId );
        return Single.just( result );
    }

    private void fileMustExists( final File file ) throws ThumbnailatorFileNotFoundException
    {
        if ( BooleanUtils.isFalse( file.exists() ) )

        {
            throw new ThumbnailatorFileNotFoundException( "Source file not found: " + file.getPath() );
        }
    }

    public Single<ImageFilterContext> getFiltered( final String imageId, final String filterName )
    {
        final ImageFilterContext context = new ImageFilterContext( filterName, imageId );
        return Single.just( context )
                     .doOnSuccess( this::validate )
                     .doOnSuccess( this::resolveFilterFile )
                     .doOnSuccess( e -> fileMustExists( e.getFilteredFile() ) )
                     .onErrorResumeNext( handleFilterFileNotFound( context ) );
    }

    private Function<Throwable, Single<ImageFilterContext>> handleFilterFileNotFound( final ImageFilterContext context )
    {
        return err ->
            {
                if ( BooleanUtils.isFalse( err instanceof ThumbnailatorFileNotFoundException ) )
                {
                    return Single.error( err );
                }

                return Single.just( context )
                             .flatMap( this::resolveSourceFile )
                             .doOnSuccess( this::fileMustExists )
                             .onErrorResumeNext( recoverSourceFile( context ) )
                             .map( e -> context )
                             .doOnSuccess( this::generateFilteredImageFromSource )
                             .doOnSuccess( e -> fileMustExists( e.getFilteredFile() ) );
            };
    }

    private Function<Throwable, Single<File>> recoverSourceFile( final ImageFilterContext context )
    {
        return err ->
            {
                if ( BooleanUtils.isFalse( err instanceof ThumbnailatorFileNotFoundException ) )
                {
                    return Single.error( err );
                }
                return Single.just( context )
                             .doOnSuccess( e ->
                                 {
                                     final String imageId = e.getImageId();
                                     final File sourceFile = e.getSourceFile();
                                     sourceRecovery.accept( imageId, sourceFile );
                                 } )
                             .map( ImageFilterContext::getSourceFile )
                             .doOnSuccess( this::fileMustExists );
            };
    }

    private void generateFilteredImageFromSource( final ImageFilterContext context )
    {
        final String filterName = context.getFilterName();
        final File sourceFile = context.getSourceFile();
        final File filteredFile = context.getFilteredFile();

        final Builder<File> builder = Thumbnails.of( sourceFile );
        Observable.fromIterable( filterSource.getFilters( filterName ) )
                  .switchIfEmpty( Observable.error( new IllegalArgumentException( "Filters was not found: " + filterName ) ) )
                  .doOnNext( e -> e.filter( builder ) )
                  .doOnComplete( () -> builder.toFile( filteredFile ) )
                  .subscribe();
    }

    private void resolveFilterFile( final ImageFilterContext imageFilterContext )
    {
        final Metadata metadata = metadataSource.getMetadata();
        final String path = metadata.getFilteredDirectory()
                                    .getPath();
        final String filePath = new StringBuilder().append( path )
                                                   .append( File.separatorChar )
                                                   .append( imageFilterContext.getFilterName() )
                                                   .append( File.separatorChar )
                                                   .append( imageFilterContext.getImageId() )
                                                   .toString();
        imageFilterContext.setResult( new File( filePath ) );
    }

}
