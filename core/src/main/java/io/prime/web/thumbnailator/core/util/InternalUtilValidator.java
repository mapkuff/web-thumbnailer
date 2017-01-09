package io.prime.web.thumbnailator.core.util;

import java.io.File;

import org.apache.commons.lang3.BooleanUtils;

public class InternalUtilValidator
{

    public static void fileMustExists( final File file )
    {
        InternalAssert.isTrue( file.exists(), String.format( "[%s] File MUST exists", InternalUtilValidator.class.getSimpleName() ) );
    }

    public static void fileMustNotExists( final File file )
    {
        InternalAssert.isTrue( BooleanUtils.isFalse( file.exists() ), String.format( "[%s] File must NOT exists", InternalUtilValidator.class.getSimpleName() ) );
    }
}
