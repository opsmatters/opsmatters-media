package com.opsmatters.media.util;

import java.lang.reflect.*;
import java.util.logging.Logger;

/**
 * Various utilities for dealing with classes.
 */

public class ClassUtils
{
    private static final Logger logger = Logger.getLogger(ClassUtils.class.getName());

    /**
     * Private constructor.
     */
    private ClassUtils()
    {
    }

    /**
     * Returns a class of the given name.
     */
    public static Object newInstance(String name)
    {
        Object ret = null;

        try
        {
            ret = newInstance(name, null, null);
        }
        catch(IllegalAccessException e)
        {
            logger.severe(StringUtils.serialize(e));
        }
        catch(NoSuchMethodException e)
        {
            logger.severe(StringUtils.serialize(e));
        }
        catch(InvocationTargetException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        return ret;
    }

    /**
     * Returns a class of the given name.
     */
    public static Object newInstance(String name,
        Class[] parameterTypes, Object[] parameters)
        throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        Class cl = null;
        Object ret = null;

        try
        {
            cl = Class.forName(name);
        }
        catch(ClassNotFoundException e)
        {
        }

        Constructor con = null;
        if(cl != null && parameterTypes != null)
            con = cl.getDeclaredConstructor(parameterTypes);

        try
        {
            if(con != null)
                ret = con.newInstance(parameters);
            else if(cl != null)
                ret = cl.newInstance();
        }
        catch(InstantiationException e)
        {
            logger.severe(StringUtils.serialize(e));
        }
        return ret;
    }

    /**
     * Returns an instance of the given class.
     */
    public static Object newInstance(Class cl,
        Class[] parameterTypes, Object[] parameters)
        throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        Object ret = null;

        Constructor con = null;
        if(cl != null && parameterTypes != null)
            con = cl.getDeclaredConstructor(parameterTypes);

        try
        {
            if(con != null)
                ret = con.newInstance(parameters);
        }
        catch(InstantiationException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        return ret;
    }
}
