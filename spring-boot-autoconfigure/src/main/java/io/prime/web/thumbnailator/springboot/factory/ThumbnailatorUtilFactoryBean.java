package io.prime.web.thumbnailator.springboot.factory;

import java.io.InputStream;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.prime.web.thumbnailator.core.sources.FilterSource;
import io.prime.web.thumbnailator.core.sources.MetadataSource;
import io.prime.web.thumbnailator.core.util.ImageIdGenerator;
import io.prime.web.thumbnailator.core.util.ThumbnailatorUtil;
import io.prime.web.thumbnailator.core.util.ThumbnailatorUtilBuilder;
import io.reactivex.SingleTransformer;

public class ThumbnailatorUtilFactoryBean implements FactoryBean<ThumbnailatorUtil>
{
    private final ObjectFactory<ImageIdGenerator> imageIdGeneartor;

    private final ObjectFactory<FilterSource> filterSource;

    private final ObjectFactory<MetadataSource> metadataSource;

    private final ObjectFactory<SingleTransformer<String, InputStream>> sourceFileRecoveryStrategy;

    @Autowired
    public ThumbnailatorUtilFactoryBean( final ObjectFactory<ImageIdGenerator> imageIdGeneartor,
                                         final ObjectFactory<FilterSource> filterSource,
                                         final ObjectFactory<MetadataSource> metadataSource,
                                         final ObjectFactory<SingleTransformer<String, InputStream>> sourceFileRecoveryStrategy )
    {
        this.imageIdGeneartor = imageIdGeneartor;
        this.filterSource = filterSource;
        this.metadataSource = metadataSource;
        this.sourceFileRecoveryStrategy = sourceFileRecoveryStrategy;
    }

    @Override
    public ThumbnailatorUtil getObject() throws Exception
    {
        return ThumbnailatorUtilBuilder.getBuilder()
                                       .imageIdGenerator( imageIdGeneartor.getObject() )
                                       .filterSource( filterSource.getObject() )
                                       .metadataSource( metadataSource.getObject() )
                                       .sourceFileRecoveryStrategy( sourceFileRecoveryStrategy.getObject() )
                                       .build();
    }

    @Override
    public Class<ThumbnailatorUtil> getObjectType()
    {
        return ThumbnailatorUtil.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

}
