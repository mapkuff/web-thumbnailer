package io.prime.web.thumbnailator.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;

import io.prime.web.thumbnailator.bean.ImageCreationContext;
import io.prime.web.thumbnailator.bean.ImageFilterContext;
import io.prime.web.thumbnailator.bean.ImageIdContainer;
import io.prime.web.thumbnailator.bean.Metadata;
import io.prime.web.thumbnailator.bean.SourceFileRecoveryContext;
import io.prime.web.thumbnailator.exception.ThumbnailatorFileNotFoundException;
import io.prime.web.thumbnailator.sources.FilterSource;
import io.prime.web.thumbnailator.sources.MetadataSource;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class DefaultThumbnailatorUtil implements ThumbnailatorUtil
{
    private final MetadataSource metadataSource;

    private final FilterSource filterSource;

    private final ImageIdGenerator imageIdGenerator;

    private final Consumer<SourceFileRecoveryContext> sourceFileRecoveryStrategy;

    DefaultThumbnailatorUtil( final MetadataSource metadataSource,
                              final FilterSource filterSource,
                              final ImageIdGenerator imageIdGenerator,
                              final Consumer<SourceFileRecoveryContext> sourceFileRecoveryStrategy )
    {
        this.metadataSource = metadataSource;
        this.filterSource = filterSource;
        this.imageIdGenerator = imageIdGenerator;
        this.sourceFileRecoveryStrategy = sourceFileRecoveryStrategy;

    }

    @Override
    public Single<ImageCreationContext> create( final InputStream inputStream, final String fileName )
    {
        final ImageCreationContext context = new ImageCreationContext( fileName, inputStream );
        return Single.just( context )
                     .doOnSuccess( InternalUtilValidator::validate )
                     .doOnSuccess( this::generateImageId )
                     .doOnSuccess( this::createFileFromImageId )
                     .doOnSuccess( e -> InternalUtilValidator.fileMustExists( e.getSourceFile() ) )
                     .doOnSuccess( this::writeDataToFile );
    }

    @Override
    public Single<File> getSource( final String imageId )
    {
        return resolveSourceFile( () -> imageId ).doOnSuccess( InternalUtilValidator::fileMustExists );
    }

    @Override
    public Single<ImageFilterContext> getFiltered( final String imageId, final String filterName )
    {
        final ImageFilterContext context = new ImageFilterContext( filterName, imageId );
        return Single.just( context )
                     .doOnSuccess( InternalUtilValidator::validate )
                     .doOnSuccess( this::resolveFilterFile )
                     .doOnSuccess( e -> InternalUtilValidator.fileMustExists( e.getFilteredFile() ) )
                     .onErrorResumeNext( handleFilteredFileNotFound( context ) );
    }

    private void generateImageId( final ImageCreationContext imageCreationContext )
    {
        final String filename = imageCreationContext.getFileName();
        final String imageId = imageIdGenerator.generate( filename );
        imageCreationContext.setImageId( imageId );
    }

    private void createFileFromImageId( final ImageCreationContext imageCreationContext ) throws IOException
    {
        final Metadata metadata = metadataSource.getMetadata();
        final File sourceDirectory = metadata.getSourceDirectory();
        final String imageId = imageIdGenerator.generate( imageCreationContext.getFileName() );
        final String ImagePath = sourceDirectory.getPath() + File.separator + ( imageId.replaceAll( "/", File.separator ) );
        final File imageSourceFile = new File( ImagePath );
        // make sure file doesn't exists
        Assert.isTrue( BooleanUtils.isFalse( imageSourceFile.exists() ), "Target file already exists: " + imageSourceFile.getPath() );
        imageSourceFile.createNewFile();
    }

    private void writeDataToFile( final ImageCreationContext imageCreationContext ) throws IOException
    {
        final File imageFile = imageCreationContext.getSourceFile();
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

    private Single<File> resolveSourceFile( final ImageIdContainer imageIdContainer )
    {
        final String imageId = imageIdContainer.getImageId();
        final Metadata metadata = metadataSource.getMetadata();
        final File sourceDirectory = metadata.getSourceDirectory();
        final String osBasedImageId = imageId.replace( '/', File.separatorChar );
        final File result = new File( sourceDirectory.getPath() + File.separator + osBasedImageId );
        return Single.just( result );
    }

    private Function<Throwable, Single<ImageFilterContext>> handleFilteredFileNotFound( final ImageFilterContext context )
    {
        return err ->
            {
                if ( BooleanUtils.isFalse( err instanceof ThumbnailatorFileNotFoundException ) )
                {
                    return Single.error( err );
                }

                return Single.just( context )
                             .flatMap( this::resolveSourceFile )
                             .doOnSuccess( InternalUtilValidator::fileMustExists )
                             .onErrorResumeNext( recoverSourceFile( context ) )
                             .doOnSuccess( e -> context.setSourceFile( e ) )
                             .map( e -> context )
                             .doOnSuccess( this::generateFilteredImageFromSource )
                             .doOnSuccess( e -> InternalUtilValidator.fileMustExists( e.getFilteredFile() ) );
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
                             .cast( SourceFileRecoveryContext.class )
                             .doOnSuccess( sourceFileRecoveryStrategy )
                             .map( SourceFileRecoveryContext::getSourceFile )
                             .doOnSuccess( InternalUtilValidator::fileMustExists );
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
