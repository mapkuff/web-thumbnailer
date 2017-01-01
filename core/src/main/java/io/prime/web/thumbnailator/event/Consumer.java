package io.prime.web.thumbnailator.event;

public interface Consumer<T>
{
    void accept(T t);
}
