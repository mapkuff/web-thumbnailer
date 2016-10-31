package io.prime.web.thumbnailator.factory;

import java.util.UUID;

public class UUIDImageIdFactory implements ImageIdFactory
{
	@Override
	public String generate(String fileName) 
	{
		UUID uuid = UUID.randomUUID();
		String uuidString = uuid.toString();
		return uuidString.replaceAll("-", "/") + "/" + fileName;
	}
}
