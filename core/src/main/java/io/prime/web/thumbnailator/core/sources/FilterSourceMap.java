package io.prime.web.thumbnailator.core.sources;

import java.util.List;
import java.util.Map;

import io.prime.web.thumbnailator.core.filter.ThumbnailatorFilter;

public class FilterSourceMap implements FilterSource
{

    private Map<String, List<ThumbnailatorFilter>> map;

    @Override
    public List<ThumbnailatorFilter> getFilters( final String name )
    {
        return map.get( name );
    }

    public Map<String, List<ThumbnailatorFilter>> getMap()
    {
        return map;
    }

    public void setMap( final Map<String, List<ThumbnailatorFilter>> map )
    {
        this.map = map;
    }
}
