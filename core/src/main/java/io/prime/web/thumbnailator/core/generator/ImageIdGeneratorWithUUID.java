package io.prime.web.thumbnailator.core.generator;

import java.util.UUID;

public class ImageIdGeneratorWithUUID implements ImageIdGenerator
{

    @Override
    public String generate( final String fileName )
    {
        final String uuid = UUID.randomUUID()
                                .toString()
                                .replace( '-', '/' );
        return uuid + '/' + fileName;
    }

}
