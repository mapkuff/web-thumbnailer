package io.prime.web.thumbnailator.exception;

public class FilterNotFoundException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FilterNotFoundException(String filterName){
		super(String.format("Filer '%s' was not found in configuration", filterName));
	}

}
