package io.prime.web.thumbnailator.bean;

import java.io.File;

public class ImageFilterContext implements ImageIdContainer, SourceFileRecoveryContext
{
    private final String filterName;

    private final String imageId;

    private File sourceFile;

    private File filteredFile;

    public ImageFilterContext( final String filterName, final String imageId )
    {
        this.filterName = filterName;
        this.imageId = imageId;
    }

    public String getFilterName()
    {
        return filterName;
    }

    @Override
    public String getImageId()
    {
        return imageId;
    }

    public File getFilteredFile()
    {
        return filteredFile;
    }

    public void setResult( final File result )
    {
        filteredFile = result;
    }

    @Override
    public File getSourceFile()
    {
        return sourceFile;
    }

    public void setSourceFile( final File sourceFile )
    {
        this.sourceFile = sourceFile;
    }
}
