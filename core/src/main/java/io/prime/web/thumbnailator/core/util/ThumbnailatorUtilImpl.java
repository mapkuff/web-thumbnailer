package io.prime.web.thumbnailator.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.helpers.DefaultHandler;

import io.prime.web.thumbnailator.core.bean.Metadata;
import io.prime.web.thumbnailator.core.exception.NestedException;
import io.prime.web.thumbnailator.core.sources.FilterSource;
import io.prime.web.thumbnailator.core.sources.MetadataSource;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class ThumbnailatorUtilImpl implements ThumbnailatorUtil
{
    private final MetadataSource metadataSource;

    private final FilterSource filterSource;

    private final ImageIdGenerator imageIdGenerator;

    private final SingleTransformer<String, InputStream> sourceFileRecoveryTransformer;

    ThumbnailatorUtilImpl( final MetadataSource metadataSource,
                           final FilterSource filterSource,
                           final ImageIdGenerator imageIdGenerator,
                           final SingleTransformer<String, InputStream> sourceFileRecovery )
    {
        this.metadataSource = metadataSource;
        this.filterSource = filterSource;
        this.imageIdGenerator = imageIdGenerator;
        sourceFileRecoveryTransformer = sourceFileRecovery;

    }

    @Override
    public Single<String> create( final InputStream inputStream, final String fileName )
    {
        return Single.just( fileName )
                     .filter( StringUtils::isNotBlank )
                     .switchIfEmpty( Maybe.error( new IllegalArgumentException( "File name is empty." ) ) )
                     .toSingle()
                     .map( imageIdGenerator::generate )
                     .doOnSuccess( imageId -> Single.just( imageId )
                                                    .map( this::resolveSourceFile )
                                                    .doOnSuccess( InternalUtilValidator::fileMustNotExists )
                                                    .doOnSuccess( e -> this.writeDataToFile( e, inputStream ) )
                                                    .toCompletable()
                                                    .blockingAwait() );
    }

    @Override
    public Single<File> getSource( final String imageId )
    {
        return Single.just( imageId )
                     .map( this::resolveSourceFile )
                     .doOnSuccess( InternalUtilValidator::fileMustExists );
    }

    @Override
    public Single<File> getFiltered( final String imageId, final String filterName )
    {
        return Single.fromCallable( () -> this.resolveFilterFile( imageId, filterName ) )
                     .onErrorResumeNext( NestedException::fromError )
                     .doOnSuccess( InternalUtilValidator::fileMustExists )
                     .onErrorResumeNext( this.handleFilteredFileNotFound( imageId, filterName ) );
    }

    @Override
    public Single<String> detectImageMimetype( final String filename, final InputStream input )
    {
        return Single.fromCallable( () ->
        {
            final AutoDetectParser parser = new AutoDetectParser();
            parser.setParsers( new HashMap<MediaType, Parser>() );

            final org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
            metadata.add( TikaMetadataKeys.RESOURCE_NAME_KEY, filename );

            parser.parse( input, new DefaultHandler(), metadata, new ParseContext() );
            IOUtils.closeQuietly( input );

            return metadata.get( HttpHeaders.CONTENT_TYPE );
        } );
    }

    private void writeDataToFile( final File file, final InputStream data ) throws IOException
    {
        final FileOutputStream fileOutputStream = new FileOutputStream( file );
        try {
            IOUtils.copy( data, fileOutputStream );
        }
        finally {
            IOUtils.closeQuietly( data );
            IOUtils.closeQuietly( fileOutputStream );
        }
    }

    private File resolveSourceFile( final String imageId )
    {
        final Metadata metadata = metadataSource.getMetadata();
        final File sourceDirectory = metadata.getSourceDirectory();
        final String osBasedImageId = imageId.replace( '/', File.separatorChar );
        return new File( sourceDirectory.getPath() + File.separator + osBasedImageId );
    }

    private Function<Throwable, Single<File>> handleFilteredFileNotFound( final String imageId, final String filterName )
    {
        return err ->
        {
            if ( err instanceof NestedException ) {
                return Single.error( err );
            }

            return Single.just( imageId )
                         .map( this::resolveSourceFile )
                         .onErrorResumeNext( NestedException::fromError )
                         .doOnSuccess( InternalUtilValidator::fileMustExists )
                         .onErrorResumeNext( this.recoverSourceFile( imageId ) )
                         .doOnSuccess( InternalUtilValidator::fileMustExists )
                         .doOnSuccess( sourceFile -> this.generateFilteredImageFromSource( filterName, sourceFile, this.resolveFilterFile( imageId, filterName ) ) );

        };
    }

    private Function<Throwable, Single<File>> recoverSourceFile( final String imageId )
    {
        return err ->
        {
            if ( err instanceof NestedException ) {
                return Single.error( err );
            }

            return Single.just( imageId )
                         .map( this::resolveSourceFile )
                         .doOnSuccess( sourceFile ->
                         {
                             Single.just( imageId )
                                   .compose( sourceFileRecoveryTransformer )
                                   .doOnSuccess( inputStream -> this.writeDataToFile( sourceFile, inputStream ) )
                                   .toCompletable()
                                   .blockingAwait();
                         } );
        };
    }

    private void generateFilteredImageFromSource( final String filterName, final File sourceFile, final File filteredFile )
    {
        final Builder<File> builder = Thumbnails.of( sourceFile );
        Observable.fromIterable( filterSource.getFilters( filterName ) )
                  .switchIfEmpty( Observable.error( new IllegalArgumentException( "Filters was not found: " + filterName ) ) )
                  .doOnNext( e -> e.filter( builder ) )
                  .doOnComplete( () -> builder.toFile( filteredFile ) )
                  .subscribe();
    }

    private File resolveFilterFile( final String imageId, final String filterName )
    {
        final Metadata metadata = metadataSource.getMetadata();
        final String path = metadata.getFilteredDirectory()
                                    .getPath();

        final String filePath = new StringBuilder().append( path )
                                                   .append( File.separatorChar )
                                                   .append( filterName )
                                                   .append( File.separatorChar )
                                                   .append( imageId )
                                                   .toString();
        return new File( filePath );
    }

}
