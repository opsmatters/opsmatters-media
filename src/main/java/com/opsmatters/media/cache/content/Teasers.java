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
import com.opsmatters.media.model.content.ContentTeaser;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.ContentType;

/**
 * Class representing the cache of teasers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Teasers
{
    private static Map<String,TeaserList> teaserMap = Collections.synchronizedMap(new HashMap<String,TeaserList>());

    public static final long EXPIRY = 300000L; // 5 mins

    static class TeaserList extends ArrayList<ContentTeaser>
    {
        String code;
        ContentType type;
        long tm = 0L;

        TeaserList(ContentConfig config, List<? extends ContentTeaser> items)
        {
            this.code = config.getCode();
            this.type = config.getType();
            tm = System.currentTimeMillis();
            for(ContentTeaser item : items)
                add(item);
        }

        String getCode()
        {
            return code;
        }

        ContentType getType()
        {
            return type;
        }

        boolean hasExpired()
        {
            return (System.currentTimeMillis() - tm) > EXPIRY;
        }

        void update(ContentTeaser teaser)
        {
            int idx = 0;
            for(ContentTeaser item : this)
            {
                if(teaser.hasSameUniqueId(item))
                {
                    set(idx, teaser);
                    break;
                }

                ++idx;
            }
        }
    }

    /**
     * Private constructor.
     */
    private Teasers()
    {
    }

    /**
     * Sets the teaser list for the given id.
     */
    public static void set(String id, List<? extends ContentTeaser> teasers, ContentConfig config)
    {
        synchronized(teaserMap)
        {
            clear(id);
            if(teasers.size() > 0)
                teaserMap.put(id, new TeaserList(config, teasers));
        }
    }

    /**
     * Sets the teaser list for the given id.
     */
    public static void update(String id, ContentTeaser teaser)
    {
        synchronized(teaserMap)
        {
            TeaserList list = teaserMap.get(id);
            if(list != null)
                list.update(teaser);
        }
    }

    /**
     * Sets the teaser list for the given ids.
     */
    public static void update(List<String> ids, ContentTeaser teaser)
    {
        for(String id : ids)
            update(id, teaser);
    }

    /**
     * Returns the teaser list for the given organisation and id.
     */
    public static List<ContentTeaser> get(String code, String id)
    {
        TeaserList ret = teaserMap.get(id);
        return ret != null && ret.getCode().equals(code) ? ret : null;
    }

    /**
     * Clears all teaser lists.
     */
    public static void clear()
    {
        synchronized(teaserMap)
        {
            teaserMap.clear();
        }
    }

    /**
     * Clears the teaser list for the given id.
     */
    public static void clear(String id)
    {
        synchronized(teaserMap)
        {
            teaserMap.remove(id);
        }
    }

    /**
     * Clears the teaser lists for the given organisation.
     */
    public static void clear(ContentConfig config)
    {
        synchronized(teaserMap)
        {
            Iterator<Entry<String,TeaserList>> iterator = teaserMap.entrySet().iterator();
            while(iterator.hasNext())
            {
                Entry<String,TeaserList> entry = iterator.next();
                TeaserList list = entry.getValue();
                if(list.getCode().equals(config.getCode())
                    && list.getType() == config.getType())
                {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Clear any expired teaser lists.
     */
    public static void clearExpired()
    {
        synchronized(teaserMap)
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
    }

    /**
     * Returns the number of teaser lists.
     */
    public static int size()
    {
        return teaserMap.size();
    }
}