package com.prime.web.thumbnailer.test.unit.factory;

import org.junit.Test;
import org.springframework.util.Assert;

import io.prime.web.thumbnailator.util.ImageIdGenerator;
import io.prime.web.thumbnailator.util.UUIDImageIdGenerator;

public class UUIDImageIdFactoryTest 
{
	private ImageIdGenerator factory = new UUIDImageIdGenerator();
	
	@Test
	public void test()
	{
		String fileName = "test.jpg";
		String imageId = factory.generate(fileName);
		Assert.isTrue(imageId.endsWith(fileName));
		Assert.isTrue(6 == imageId.split("/").length);
	}
}
