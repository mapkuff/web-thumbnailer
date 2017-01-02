package io.prime.web.thumbnailator.bean.internal;

import java.io.File;
import io.prime.web.thumbnailator.bean.internal.spec.ImageIdContainer;

public class ImageFilterContext implements ImageIdContainer
{
    private String filterName;

    private String imageId;

    private File sourceFile;

    private File filteredFile;

    public ImageFilterContext(final String filterName, final String imageId)
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

    public void setResult(final File result)
    {
        filteredFile = result;
    }

    public File getSourceFile()
    {
        return sourceFile;
    }

    public void setSourceFile(final File sourceFile)
    {
        this.sourceFile = sourceFile;
    }
}
