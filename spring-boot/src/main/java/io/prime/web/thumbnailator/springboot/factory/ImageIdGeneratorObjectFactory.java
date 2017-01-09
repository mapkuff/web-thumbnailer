package io.prime.web.thumbnailator.springboot.factory;

import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.prime.web.thumbnailator.core.util.ImageIdGenerator;
import io.prime.web.thumbnailator.core.util.UUIDImageIdGenerator;

public class ImageIdGeneratorObjectFactory implements ObjectFactory<ImageIdGenerator>
{
    private final Optional<ImageIdGenerator> imageIdGenerator;

    @Autowired
    public ImageIdGeneratorObjectFactory( final Optional<ImageIdGenerator> imageIdGenerator )
    {
        this.imageIdGenerator = imageIdGenerator;
    }

    @Override
    public ImageIdGenerator getObject() throws BeansException
    {
        if ( imageIdGenerator.isPresent() ) {
            return imageIdGenerator.get();
        }

        return new UUIDImageIdGenerator();
    }

}
