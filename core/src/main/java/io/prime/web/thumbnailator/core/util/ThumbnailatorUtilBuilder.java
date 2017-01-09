package io.prime.web.thumbnailator.core.util;

import java.io.InputStream;

import io.prime.web.thumbnailator.core.sources.FilterSource;
import io.prime.web.thumbnailator.core.sources.MetadataSource;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;

public class ThumbnailatorUtilBuilder
{
    private MetadataSource metadataSource;

    private FilterSource filterSource;

    private ImageIdGenerator imageIdGenerator;

    private SingleTransformer<String, InputStream> sourceFileRecoveryStrategy;

    private ThumbnailatorUtilBuilder()
    {
        // prevent create new object
    }

    public static ThumbnailatorUtilBuilder getBuilder()
    {
        return new ThumbnailatorUtilBuilder();
    }

    public ThumbnailatorUtilBuilder metadataSource( final MetadataSource metadataSource )
    {
        this.metadataSource = metadataSource;
        return this;
    }

    public ThumbnailatorUtilBuilder filterSource( final FilterSource filterSource )
    {
        this.filterSource = filterSource;
        return this;
    }

    public ThumbnailatorUtilBuilder imageIdGenerator( final ImageIdGenerator imageIdGenerator )
    {
        this.imageIdGenerator = imageIdGenerator;
        return this;
    }

    public ThumbnailatorUtilBuilder sourceFileRecoveryStrategy( final SingleTransformer<String, InputStream> sourceFileRecoveryStrategy )
    {
        this.sourceFileRecoveryStrategy = sourceFileRecoveryStrategy;
        return this;
    }

    public ThumbnailatorUtil build()
    {
        if ( null == sourceFileRecoveryStrategy ) {
            sourceFileRecoveryStrategy = imageId ->
            {
                return Single. <InputStream> error( new IllegalArgumentException( "Source file is missing but not sourceFileRecoveryStrategy was configured: imageId=" + imageId ) );
            };
        }
        if ( null == imageIdGenerator ) {
            imageIdGenerator = new UUIDImageIdGenerator();
        }

        // VALIDATION
        InternalAssert.notNull( filterSource );
        InternalAssert.notNull( imageIdGenerator );
        InternalAssert.notNull( metadataSource );
        InternalAssert.notNull( sourceFileRecoveryStrategy );

        return new ThumbnailatorUtilImpl( metadataSource, filterSource, imageIdGenerator, sourceFileRecoveryStrategy );
    }
}
