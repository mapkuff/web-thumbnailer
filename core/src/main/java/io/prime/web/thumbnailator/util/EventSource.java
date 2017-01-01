package io.prime.web.thumbnailator.util;

import java.io.File;
import io.reactivex.Observable;

public interface EventSource
{

    Observable<File> getFileCreation();

}
