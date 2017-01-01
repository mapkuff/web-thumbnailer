package io.prime.web.thumbnailator.event;

import java.util.Observable;

public class EventManager<T>
{

    private InternalEventManager internalEventManager = new InternalEventManager();

    @SuppressWarnings("unchecked")
    public void subscribe(final Consumer<T> consumer)
    {
        this.internalEventManager.addObserver((s, e) -> {
            consumer.accept((T) e);
        });
    }

    public void notify(final T itemn)
    {
        this.internalEventManager.notifyObservers(itemn);
    }

    public static class InternalEventManager extends Observable
    {
        @Override
        public void notifyObservers(final Object arg)
        {
            this.setChanged();
            super.notifyObservers(arg);
        }
    }

}
