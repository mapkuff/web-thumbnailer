package io.prime.web.thumbnailator.core.bean;

import java.io.File;
import java.io.InputStream;

public class ImageCreationContext implements ImageIdContainer
{
    private final String fileName;

    private final InputStream inputStream;

    private File getSourceFile;

    private String imageId;

    public ImageCreationContext( final String fileName, final InputStream inputStream )
    {
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public String getFileName()
    {
        return fileName;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    @Override
    public String getImageId()
    {
        return imageId;
    }

    public void setImageId( final String resultImageId )
    {
        imageId = resultImageId;
    }

    public File getSourceFile()
    {
        return getSourceFile;
    }

    public void setSourceFile( final File targetSourceFile )
    {
        getSourceFile = targetSourceFile;
    }

}
