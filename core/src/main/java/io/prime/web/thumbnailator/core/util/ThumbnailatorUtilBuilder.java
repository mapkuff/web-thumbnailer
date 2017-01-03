package io.prime.web.thumbnailator.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prime.web.thumbnailator.core.bean.SourceFileRecoveryContext;
import io.prime.web.thumbnailator.core.sources.FilterSource;
import io.prime.web.thumbnailator.core.sources.MetadataSource;
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

        return new ThumbnailatorUtilImpl( metadataSource, filterSource, imageIdGenerator, sourceFileRecoveryStrategy );
    }
}
