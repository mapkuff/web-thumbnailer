package io.prime.web.thumbnailator.core.util;

import java.io.File;
import java.io.InputStream;

import io.reactivex.Single;

public interface ThumbnailatorUtil
{

    Single<String> create( InputStream inputStream, String fileName );

    Single<File> getSource( String imageId );

    Single<File> getFiltered( String imageId, String filterName );

    Single<String> detectImageMimetype( String filename, InputStream input );

}
