package io.prime.web.thumbnailator.core.exception;

import java.io.FileNotFoundException;

public class ThumbnailatorFileNotFoundException extends FileNotFoundException
{
    private static final long serialVersionUID = 1L;

    public ThumbnailatorFileNotFoundException(final String s)
    {
        super(s);
    }
}
