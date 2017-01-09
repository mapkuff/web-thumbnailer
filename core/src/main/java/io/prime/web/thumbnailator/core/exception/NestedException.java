package io.prime.web.thumbnailator.core.exception;

import io.reactivex.Single;

public class NestedException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public NestedException( final Throwable cause )
    {
        super( "Nested Error: " + cause.getMessage(), cause );
    }

    public static <T> Single<T> fromError( final Throwable err )
    {
        return Single. <T> error( new NestedException( err ) );
    }

}
