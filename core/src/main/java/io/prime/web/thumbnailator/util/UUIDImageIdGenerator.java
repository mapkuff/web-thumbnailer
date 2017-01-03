package io.prime.web.thumbnailator.util;

import java.util.UUID;

public class UUIDImageIdGenerator implements ImageIdGenerator
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
