package io.prime.web.thumbnailator.servlet.bean;

public class ImageFilterRequest
{
    private final String imageId;
    private final String filterName;

    public ImageFilterRequest( final String imageId, final String filterName )
    {
        super();
        this.imageId = imageId;
        this.filterName = filterName;
    }
    public String getImageId()
    {
        return imageId;
    }
    public String getFilterName()
    {
        return filterName;
    }

}
