package io.prime.web.thumbnailator.bean.internal;

import java.io.File;

public class ImageFilterContext
{
    private String filterName;

    private String imageId;

    private File result;

    public ImageFilterContext(final String filterName, final String imageId)
    {
        this.filterName = filterName;
        this.imageId = imageId;
    }

    public String getFilterName()
    {
        return filterName;
    }

    public String getImageId()
    {
        return imageId;
    }

    public File getResult()
    {
        return result;
    }

    public void setResult(final File result)
    {
        this.result = result;
    }

    public void setFilterName(final String filterName)
    {
        this.filterName = filterName;
    }

    public void setImageId(final String imageId)
    {
        this.imageId = imageId;
    }
}
