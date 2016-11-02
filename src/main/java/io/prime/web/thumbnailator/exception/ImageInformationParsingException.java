package io.prime.web.thumbnailator.exception;

public class ImageInformationParsingException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ImageInformationParsingException(String message)
	{
		super(message);
	}
	
	public ImageInformationParsingException(String message, Throwable clause)
	{
		super(message, clause);
	}

}
