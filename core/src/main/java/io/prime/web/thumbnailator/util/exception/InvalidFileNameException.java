package io.prime.web.thumbnailator.util.exception;

import java.util.Arrays;

public class InvalidFileNameException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private InvalidFileNameException(String message)
	{
		super(message);
	}
	
	public static InvalidFileNameException fromContainingReservedWord(String fileName, String[] reservedWords)
	{
		return new InvalidFileNameException(
				String.format(
					"File name must not containing following reserved words %s, %s given.", 
					Arrays.toString(reservedWords), 
					fileName
				)
			);
	}
	
}
