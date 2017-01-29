package io.prime.web.thumbnailator.core.util;

import io.prime.web.thumbnailator.core.sources.FilterSource;
import io.prime.web.thumbnailator.core.sources.MetadataSource;

public class ThumbnailatorUtilBuilder
{
    private MetadataSource metadataSource;

    private FilterSource filterSource;

    private ImageIdGenerator imageIdGenerator;

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

    public ThumbnailatorUtil build()
    {
        if ( null == imageIdGenerator ) {
            imageIdGenerator = new UUIDImageIdGenerator();
        }

        // VALIDATION
        InternalAssert.notNull( filterSource );
        InternalAssert.notNull( imageIdGenerator );
        InternalAssert.notNull( metadataSource );

        return new ThumbnailatorUtilImpl( metadataSource, filterSource, imageIdGenerator );
    }
}
