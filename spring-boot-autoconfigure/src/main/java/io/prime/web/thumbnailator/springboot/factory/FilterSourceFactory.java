package io.prime.web.thumbnailator.springboot.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.prime.web.thumbnailator.core.sources.FilterSource;

public class FilterSourceFactory implements ObjectFactory<FilterSource>
{
    private final FilterSource filterSource;

    @Autowired
    public FilterSourceFactory( final FilterSource filterSource )
    {
        this.filterSource = filterSource;
    }

    @Override
    public FilterSource getObject() throws BeansException
    {
        return filterSource;
    }

}
