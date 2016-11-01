package io.prime.web.thumbnailator.filter;

import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Coordinate;

public class CropFilter implements ThumbnailatorFilter
{
	private int width;
	private int height;

	public <T> void filter(Builder<T> builder) 
	{
		builder.crop(new Coordinate(this.width, this.height));
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
