package io.prime.web.thumbnailator.core.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prime.web.thumbnailator.core.util.ImageIdGenerator;
import io.prime.web.thumbnailator.core.util.UUIDImageIdGenerator;

public class UUIDImageIdGeneratorTest
{
    private final ImageIdGenerator generator = new UUIDImageIdGenerator();

    private final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Test
    public void test()
    {
        final String filename = "sample.jpg";
        final String imageId = generator.generate( "sample.jpg" );
        logger.info( "ImageId: " + imageId );

        assertThat( imageId ).isNotNull()
                             .isNotEmpty()
                             .matches( Pattern.compile( "^(?!/).*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE ) )
                             .doesNotContain( "//" )
                             .endsWith( filename );
    }
}
