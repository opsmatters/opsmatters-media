package com.opsmatters.media.cache;

import java.time.Instant;

/**
 * Base class for all application dynamic caches.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class DynamicCache extends StaticCache
{
    private static Instant lastUpdated;

    /**
     * Returns the date the cache was last updated.
     */
    public static Instant getLastUpdated()
    {
        return lastUpdated;
    }

    /**
     * Sets the date the cache was last updated.
     */
    private static void setLastUpdated(Instant lastUpdated)
    {
        DynamicCache.lastUpdated = lastUpdated;
        if(lastUpdated != null)
            setInitialised(true);
    }

    /**
     * Sets the date the cache was last updated to now.
     */
    public static void updated()
    {
        setLastUpdated(Instant.now());
    }
}