package io.prime.web.thumbnailator.core.util;

import java.io.File;
import java.io.InputStream;

import io.prime.web.thumbnailator.core.bean.ImageCreationContext;
import io.prime.web.thumbnailator.core.bean.ImageFilterContext;
import io.reactivex.Single;

public interface ThumbnailatorUtil
{

    Single<ImageCreationContext> create( InputStream inputStream, String fileName );

    Single<File> getSource( String imageId );

    Single<ImageFilterContext> getFiltered( String imageId, String filterName );

    Single<String> detectImageMimetype( String filename, InputStream input );

}
