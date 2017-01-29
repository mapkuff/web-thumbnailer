package io.prime.web.thumbnailator.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import io.prime.web.thumbnailator.core.bean.Metadata;
import io.prime.web.thumbnailator.core.generator.ImageIdGenerator;
import io.prime.web.thumbnailator.core.sources.FilterSource;
import io.prime.web.thumbnailator.core.sources.MetadataSource;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.SingleSubject;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class ThumbnailatorUtilImpl implements ThumbnailatorUtil
{
    private final MetadataSource metadataSource;

    private final FilterSource filterSource;

    private final ImageIdGenerator imageIdGenerator;

    ThumbnailatorUtilImpl( final MetadataSource metadataSource, final FilterSource filterSource, final ImageIdGenerator imageIdGenerator )
    {
        this.metadataSource = metadataSource;
        this.filterSource = filterSource;
        this.imageIdGenerator = imageIdGenerator;
    }

    @Override
    public Single<String> create( final InputStream inputStream, final String fileName )
    {
        final SingleSubject<String> subject = SingleSubject.create();

        final Completable task = Single.just( fileName )
                                       .filter( StringUtils::isNotBlank )
                                       .switchIfEmpty( Maybe.error( new IllegalArgumentException( "File name is empty." ) ) )
                                       .toSingle()
                                       .map( imageIdGenerator::generate )
                                       .doOnSuccess( imageId -> Single.just( imageId )
                                                                      .map( this::resolveSourceFile )
                                                                      .doOnSuccess( this::fileMustNotExists )
                                                                      .observeOn( Schedulers.io() )
                                                                      .doOnSuccess( e -> this.writeDataToFile( e, inputStream ) )
                                                                      .doOnSuccess( e -> subject.onSuccess( imageId ) )
                                                                      .doOnError( subject::onError )
                                                                      .subscribe() )
                                       .doOnError( subject::onError )
                                       .toCompletable();

        return subject.doOnSubscribe( d -> task.subscribe() );
    }

    @Override
    public Single<File> getSource( final String imageId )
    {
        return Single.just( imageId )
                     .map( this::resolveSourceFile )
                     .doOnSuccess( this::fileMustExists );
    }

    @Override
    public Single<File> getFiltered( final String imageId, final String filterName )
    {
        final SingleSubject<File> subject = SingleSubject.create();

        final Completable task = Single.fromCallable( () -> this.resolveFilterFile( imageId, filterName ) )
                                       .doOnSuccess( filteredFile ->
                                       {
                                           if ( filteredFile.exists() ) {
                                               subject.onSuccess( filteredFile );
                                               return;
                                           }

                                           Single.just( imageId )
                                                 .map( this::resolveSourceFile )
                                                 .doOnSuccess( this::fileMustExists )
                                                 .doOnSuccess( sourceFile -> this.generateFilteredImageFromSource( filterName, sourceFile, filteredFile )
                                                                                 .doOnError( subject::onError )
                                                                                 .doOnComplete( () -> subject.onSuccess( filteredFile ) ) )
                                                 .subscribe();
                                       } )
                                       .toCompletable();

        return subject.doOnSubscribe( d -> task.subscribe() );
    }

    public void writeDataToFile( final File file, final InputStream data ) throws IOException
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

    public File resolveSourceFile( final String imageId )
    {
        final Metadata metadata = metadataSource.getMetadata();
        final File sourceDirectory = metadata.getSourceDirectory();
        final String osBasedImageId = imageId.replace( '/', File.separatorChar );
        return new File( sourceDirectory.getPath() + File.separator + osBasedImageId );
    }

    public File resolveFilterFile( final String imageId, final String filterName )
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

    public Completable generateFilteredImageFromSource( final String filterName, final File sourceFile, final File filteredFile )
    {
        final Builder<File> builder = Thumbnails.of( sourceFile );
        return Completable.fromObservable( Observable.fromIterable( filterSource.getFilters( filterName ) )
                                                     .switchIfEmpty( Observable.error( new IllegalArgumentException( "Filters was not found: " + filterName ) ) )
                                                     .doOnNext( e -> e.filter( builder ) )
                                                     .observeOn( Schedulers.io() )
                                                     .doOnComplete( () -> builder.toFile( filteredFile ) ) );
    }

    public void fileMustExists( final File file )
    {
        InternalAssert.isTrue( file.exists(), String.format( "File MUST exists: %s", file ) );
    }

    public void fileMustNotExists( final File file )
    {
        InternalAssert.isTrue( BooleanUtils.isFalse( file.exists() ), String.format( "File must NOT exists: %s", file ) );
    }

    // TODO remove
    // private Single<String> detectImageMimetype( final String filename, final
    // InputStream input )
    // {
    // return Single.fromCallable( () ->
    // {
    // final AutoDetectParser parser = new AutoDetectParser();
    // parser.setParsers( new HashMap<MediaType, Parser>() );
    //
    // final org.apache.tika.metadata.Metadata metadata = new
    // org.apache.tika.metadata.Metadata();
    // metadata.add( TikaMetadataKeys.RESOURCE_NAME_KEY, filename );
    //
    // parser.parse( input, new DefaultHandler(), metadata, new ParseContext()
    // );
    // IOUtils.closeQuietly( input );
    //
    // return metadata.get( HttpHeaders.CONTENT_TYPE );
    // } );
    // }

}
