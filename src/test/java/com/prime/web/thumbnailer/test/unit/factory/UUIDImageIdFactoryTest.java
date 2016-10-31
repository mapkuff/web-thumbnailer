package com.prime.web.thumbnailer.test.unit.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.Assert;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;

import io.prime.web.thumbnailator.factory.ImageIdFactory;
import io.prime.web.thumbnailator.factory.UUIDImageIdFactory;

@RunWith(value=DataProviderRunner.class)
public class UUIDImageIdFactoryTest 
{
	private ImageIdFactory factory = new UUIDImageIdFactory();
	
	@Test
	public void test()
	{
		String fileName = "test.jpg";
		String imageId = factory.generate(fileName);
		Assert.isTrue(imageId.endsWith(fileName));
		Assert.isTrue(6 == imageId.split("/").length);
	}
}
