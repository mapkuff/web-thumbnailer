package io.prime.web.thumbnailator.bean.internal;

import java.io.File;
import java.io.InputStream;
import io.prime.web.thumbnailator.bean.internal.spec.ImageIdContainer;

public class ImageCreationContext implements ImageIdContainer
{
    private String fileName;

    private InputStream inputStream;

    private File targetSourceFile;

    private String imageId;

    public ImageCreationContext(final String fileName, final InputStream inputStream)
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

    public String getImageId()
    {
        return imageId;
    }

    public void setImageId(final String resultImageId)
    {
        this.imageId = resultImageId;
    }

    public File getTargetSourceFile()
    {
        return targetSourceFile;
    }

    public void setTargetSourceFile(final File targetSourceFile)
    {
        this.targetSourceFile = targetSourceFile;
    }

}
