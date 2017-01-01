package io.prime.web.thumbnailator.util.provided;

import java.io.File;
import javax.annotation.PostConstruct;
import io.prime.web.thumbnailator.util.EventSource;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class DefaultEventSource implements EventSource
{

    private Observable<File> fileCreation;

    @PostConstruct
    public void initFileCreatation()
    {
        final ObservableOnSubscribe<File> onSubscribe = s -> {
        };
        fileCreation = Observable.create(onSubscribe)
                                 .subscribeOn(Schedulers.io())
                                 .share();
    }

    @Override
    public Observable<File> getFileCreation()
    {
        return fileCreation;
    }

}
