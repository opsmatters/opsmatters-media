/*
 * Copyright 2022 Gerald Curley
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
package com.opsmatters.media.cache.content;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import com.opsmatters.media.model.content.ContentSummary;

/**
 * Class representing the cache of teasers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Teasers
{
    private static Map<String,TeaserList> teaserMap = Collections.synchronizedMap(new HashMap<String,TeaserList>());

    static class TeaserList extends ArrayList<ContentSummary>
    {
        static final long EXPIRY = 300000L; // 5 mins

        long tm = 0L;

        TeaserList(List<? extends ContentSummary> items)
        {
            tm = System.currentTimeMillis();
            for(ContentSummary item : items)
                add(item);
        }

        boolean hasExpired()
        {
            return (System.currentTimeMillis() - tm) > EXPIRY;
        }
    }

    /**
     * Private constructor.
     */
    private Teasers()
    {
    }

    /**
     * Sets the given teasers.
     */
    public static void set(String id, List<? extends ContentSummary> teasers)
    {
        teaserMap.put(id, new TeaserList(teasers));
    }

    /**
     * Returns the teasers for the given id.
     */
    public static List<ContentSummary> get(String id)
    {
        return teaserMap.get(id);
    }

    /**
     * Clear any expired teasers.
     */
    public synchronized static void clearExpired()
    {
        if(teaserMap.size() > 0)
        {
            Iterator<Entry<String,TeaserList>> iterator = teaserMap.entrySet().iterator();
            while(iterator.hasNext())
            {
                Entry<String,TeaserList> entry = iterator.next();
                if(entry.getValue().hasExpired())
                    iterator.remove();
            }
        }
    }

    /**
     * Clears all teaser lists.
     */
    public static void clear()
    {
        teaserMap.clear();
    }

    /**
     * Returns the number of teaser lists.
     */
    public static int size()
    {
        return teaserMap.size();
    }
}