package io.prime.web.thumbnailator.core.util;

public interface ImageIdGenerator 
{
	/**
	 * generate imageId which contains only A-Z, a-z and 0-9
	 * @param fileName
	 * @return
	 */
	String generate(String fileName);
}
