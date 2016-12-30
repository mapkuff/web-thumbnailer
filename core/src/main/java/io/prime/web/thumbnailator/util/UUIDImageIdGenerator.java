package io.prime.web.thumbnailator.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

public class UUIDImageIdGenerator implements ImageIdGenerator
{
	@Autowired
	private FileNameValidator validator;
	
	@Override
	public String generate(String fileName) 
	{
		this.validator.validate(fileName);
		UUID uuid = UUID.randomUUID();
		String uuidString = uuid.toString();
		return uuidString.replaceAll("-", "/") + "/" + fileName;
	}
}
