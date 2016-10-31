package com.prime.web.thumbnailer.test.unit.factory;

import org.junit.Test;
import org.springframework.util.Assert;

import io.prime.web.thumbnailator.factory.ImageIdFactory;
import io.prime.web.thumbnailator.factory.UUIDImageIdFactory;

public class UUIDImageIdFactoryTest 
{
	private ImageIdFactory factory = new UUIDImageIdFactory();
	
	@Test
	public void test()
	{
		String fileName = "test.jpg";
		String imageId = factory.generate(fileName);
		Assert.isTrue(imageId.endsWith(fileName));
		
	}

}
