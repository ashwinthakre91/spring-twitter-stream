package com.rawsanj.tweet.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class MostRecentlyUsedMap<K,V> extends LinkedHashMap<K,V>
{
    private static final long serialVersionUID = 1L;
    /** Maximum number of entries allowed in this map */
    private int size;
    public MostRecentlyUsedMap(final int size)
    {
        super(10, 0.75f, true);

        if (size <= 0)
        {
            throw new IllegalArgumentException("Must have at least one entry");
        }

        this.size = size;
    }

    public static <K, V> MostRecentlyUsedMap<K, V> newInstance(int size) {
        return new MostRecentlyUsedMap<K, V>(size);
    }

    protected boolean removeEldestEntry(final Map.Entry<K,V> eldest)
    {
        final boolean remove = size() > size;
        return remove;
    }
}

