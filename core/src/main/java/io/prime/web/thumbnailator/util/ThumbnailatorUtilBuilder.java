package io.prime.web.thumbnailator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prime.web.thumbnailator.bean.SourceFileRecoveryContext;
import io.prime.web.thumbnailator.sources.FilterSource;
import io.prime.web.thumbnailator.sources.MetadataSource;
import io.reactivex.functions.Consumer;

public class ThumbnailatorUtilBuilder
{
    private MetadataSource metadataSource;

    private FilterSource filterSource;

    private ImageIdGenerator imageIdGenerator;

    private Consumer<SourceFileRecoveryContext> sourceFileRecoveryStrategy;

    private ThumbnailatorUtilBuilder()
    {
        // prevent create new object
    }

    public static ThumbnailatorUtilBuilder getBuilder()
    {
        return new ThumbnailatorUtilBuilder();
    }

    public ThumbnailatorUtilBuilder sourceDirectory( final MetadataSource metadataSource )
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

    public ThumbnailatorUtilBuilder sourceFileRecoveryStrategy( final Consumer<SourceFileRecoveryContext> sourceFileRecoveryStrategy )
    {
        this.sourceFileRecoveryStrategy = sourceFileRecoveryStrategy;
        return this;
    }

    public ThumbnailatorUtil build()
    {
        if ( null == sourceFileRecoveryStrategy )
        {
            final Logger logger = LoggerFactory.getLogger( this.getClass() );
            sourceFileRecoveryStrategy = context ->
                {
                    logger.error( "Source file is missing and was not recovered: [imageId: {}, file: {}]",
                                  context.getImageId(),
                                  context.getSourceFile() );
                };
        }
        if ( null == imageIdGenerator )
        {
            imageIdGenerator = new UUIDImageIdGenerator();
        }

        // VALIDATION
        Assert.notNull( filterSource );
        Assert.notNull( imageIdGenerator );
        Assert.notNull( metadataSource );
        Assert.notNull( sourceFileRecoveryStrategy );

        return new DefaultThumbnailatorUtil( metadataSource, filterSource, imageIdGenerator, sourceFileRecoveryStrategy );
    }
}
