package com.prime.web.thumbnailer.exception;

import java.io.IOException;

import org.springframework.core.NestedRuntimeException;

public class NestedIOException extends NestedRuntimeException{

	public NestedIOException(String msg, IOException cause) {
		super(msg, cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
