package com.opsmatters.media.cache;

/**
 * Base class for all application static caches.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class StaticCache implements java.io.Serializable
{
    private static boolean initialised = false;

    /**
     * Returns <CODE>true</CODE> if the cache has been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Set to <CODE>true</CODE> if the cache has been initialised.
     */
    protected static void setInitialised(boolean initialised)
    {
        StaticCache.initialised = initialised;
    }
}