package io.prime.web.thumbnailator.springboot.config;

import java.io.InputStream;

import io.prime.web.thumbnailator.core.sources.FilterSource;
import io.prime.web.thumbnailator.core.sources.MetadataSource;
import io.prime.web.thumbnailator.core.util.ImageIdGenerator;
import io.reactivex.SingleTransformer;

public class WebThumbnailatorConfigurer
{
    private MetadataSource metadataSource;

    private FilterSource filterSource;

    private ImageIdGenerator imageIdGenerator;

    private SingleTransformer<String, InputStream> sourceFileRecoveryStrategy;

    public MetadataSource getMetadataSource()
    {
        return metadataSource;
    }

    public void setMetadataSource( final MetadataSource metadataSource )
    {
        this.metadataSource = metadataSource;
    }

    public FilterSource getFilterSource()
    {
        return filterSource;
    }

    public void setFilterSource( final FilterSource filterSource )
    {
        this.filterSource = filterSource;
    }

    public ImageIdGenerator getImageIdGenerator()
    {
        return imageIdGenerator;
    }

    public void setImageIdGenerator( final ImageIdGenerator imageIdGenerator )
    {
        this.imageIdGenerator = imageIdGenerator;
    }

    public SingleTransformer<String, InputStream> getSourceFileRecoveryStrategy()
    {
        return sourceFileRecoveryStrategy;
    }

    public void setSourceFileRecoveryStrategy( final SingleTransformer<String, InputStream> sourceFileRecoveryStrategy )
    {
        this.sourceFileRecoveryStrategy = sourceFileRecoveryStrategy;
    }

}
