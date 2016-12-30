package com.prime.web.thumbnailer.test.unit.utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import io.prime.web.thumbnailator.util.FileNameValidator;
import io.prime.web.thumbnailator.util.FileNameValidatorImpl;
import io.prime.web.thumbnailator.util.exception.InvalidFileNameException;

@RunWith(value=DataProviderRunner.class)
public class FileNameValidatorTest 
{
	private FileNameValidator validator = new FileNameValidatorImpl();
	
	@DataProvider
	public static Object[][] provideInvalidFileNames() 
	{
		String[][] result = new String[2][1];
		result[0][0] = "File/With/Slash.jpg";
		result[1][0] = "File\\With\\Backslah.jpg";
		return result;
	}
	
	@DataProvider
	public static Object[][] provideValidFileNames() 
	{
		String[][] result = new String[2][1];
		result[0][0] = "sample_file.jpg";
		result[1][0] = "sample-file.jpg";
		return result;
	}
	
	
	@Test(expected=InvalidFileNameException.class)
	@UseDataProvider(value="provideInvalidFileNames")
	public void testInvalidFileNames(String fileName)
	{
		validator.validate(fileName);
	}
	
	@Test
	@UseDataProvider(value="provideValidFileNames")
	public void testValidFileNames(String fileName)
	{
		validator.validate(fileName);
	}
}
