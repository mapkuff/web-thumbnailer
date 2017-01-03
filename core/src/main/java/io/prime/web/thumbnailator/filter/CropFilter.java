package io.prime.web.thumbnailator.filter;

import org.apache.commons.lang3.BooleanUtils;

import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

public class CropFilter implements ThumbnailatorFilter
{
    private int width;

    private int height;

    private Position position;

    @Override
    public <T> void filter(final Builder<T> builder)
    {
        if (BooleanUtils.isFalse((width > 0) || (height > 0))) {

        }
        if ((width > 0) && (height > 0)) {
            builder.size(width, height);
        } else {
            if (width > 0) {
                builder.width(width);
            } else {
                builder.height(height);
            }
        }

        if (null == position) {
            position = Positions.CENTER;
        }

        builder.crop(position);
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(final int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(final int height)
    {
        this.height = height;
    }

}
