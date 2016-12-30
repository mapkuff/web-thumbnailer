package io.prime.web.thumbnailator.filter;

import org.springframework.util.Assert;

import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

public class CropFilter implements ThumbnailatorFilter
{
	private int width;
	private int height;
	private Position position;

	public <T> void filter(Builder<T> builder) 
	{
		Assert.isTrue(this.width > 0 || this.height > 0);
		if (this.width > 0 && this.height > 0) {
			builder.size(width, height);
		} else {
			if ( this.width > 0 ) {
				builder.width(this.width);
			} else {
				builder.height(this.height);
			}
		}
		
		if (null == this.position) {
			this.position = Positions.CENTER;
		}
		
		builder.crop(position);
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
