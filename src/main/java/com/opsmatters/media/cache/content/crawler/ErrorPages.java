/*
 * Copyright 2024 Gerald Curley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opsmatters.media.cache.content.crawler;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.crawler.ErrorPage;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing the list of error pages.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ErrorPages extends StaticCache
{
    private static final Logger logger = Logger.getLogger(ErrorPages.class.getName());

    private static Map<String,ErrorPage> pageMap = new LinkedHashMap<String,ErrorPage>();

    /**
     * Private constructor.
     */
    private ErrorPages()
    {
    }

    /**
     * Loads the set of error pages.
     */
    public static void load(List<ErrorPage> pages)
    {
        setInitialised(false);

        clear();
        for(ErrorPage page : pages)
        {
            add(page);
        }

        logger.info("Loaded "+size()+" error pages");

        setInitialised(true);
    }

    /**
     * Clears the error pages.
     */
    public static void clear()
    {
        pageMap.clear();
    }

    /**
     * Returns the error page with the given name.
     */
    public static ErrorPage get(String name)
    {
        return pageMap.get(name);
    }

    /**
     * Adds the error page with the given name.
     */
    public static void add(ErrorPage page)
    {
        pageMap.put(page.getName(), page);
    }

    /**
     * Removes the error page with the given name.
     */
    public static void remove(ErrorPage page)
    {
        pageMap.remove(page.getName());
    }

    /**
     * Returns the count of error pages.
     */
    public static int size()
    {
        return pageMap.size();
    }

    /**
     * Returns the list of error pages.
     */
    public static List<ErrorPage> list()
    {
        List<ErrorPage> ret = new ArrayList<ErrorPage>();
        for(ErrorPage page : pageMap.values())
        {
            ret.add(page);
        }

        return ret;
    }
}