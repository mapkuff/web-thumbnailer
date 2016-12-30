//package com.prime.web.thumbnailer.test.unit.utils;
//
//import java.lang.reflect.Field;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.springframework.util.Assert;
//
//import io.prime.web.thumbnailator.util.FileNameValidatorImpl;
//import io.prime.web.thumbnailator.util.ImageIdGenerator;
//import io.prime.web.thumbnailator.util.UUIDImageIdGenerator;
//
//public class UUIDImageIdGeneratorTest 
//{
//	private static ImageIdGenerator generator;
//	
//	@BeforeClass
//	public static void init() throws Exception
//	{
//		generator = new UUIDImageIdGenerator();
//		Field field = UUIDImageIdGenerator.class.getDeclaredField("validator");
//		field.setAccessible(true);
//		field.set(generator, new FileNameValidatorImpl());
//	}
//	
//	@Test
//	public void test()
//	{
//		String fileName = "test.jpg";
//		String imageId = generator.generate(fileName);
//		Assert.isTrue(imageId.endsWith(fileName));
//		Assert.isTrue(6 == imageId.split("/").length);
//	}
//}
