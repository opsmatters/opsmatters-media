/*
 * Copyright 2025 Gerald Curley
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.FieldDefault;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.cache.StaticCache;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing the set of field defaults.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FieldDefaults extends StaticCache
{
    private static final Logger logger = Logger.getLogger(FieldDefaults.class.getName());

    private static Map<ContentType,Map<String,FieldDefault>> defaultMap = new LinkedHashMap<ContentType,Map<String,FieldDefault>>();

    /**
     * Private constructor.
     */
    private FieldDefaults()
    {
    }

    /**
     * Loads the set of defaults.
     */
    public static void load(List<FieldDefault> defaults)
    {
        setInitialised(false);

        defaultMap.clear();

        int count = 0;
        for(FieldDefault _default : defaults)
        {
            add(_default);
            ++count;
        }

        logger.info(String.format("Loaded %d field defaults", count));

        setInitialised(true);
    }

    /**
     * Adds the given default.
     */
    public static void add(FieldDefault _default)
    {
        Map<String,FieldDefault> defaults = defaultMap.get(_default.getType());
        if(defaults == null)
        {
            defaults = new LinkedHashMap<String,FieldDefault>();
            defaultMap.put(_default.getType(), defaults);
        }

        defaults.put(_default.getName(), _default);
    }

    /**
     * Returns the defaults for the given type.
     */
    public static FieldMap get(ContentType type)
    {
        FieldMap ret = null;

        Map<String,FieldDefault> defaults = defaultMap.get(type);
        if(defaults != null)
        {
            ret = new FieldMap();
            ret.put(TYPE, type.code()); // Add the type code
            for(FieldDefault _default : defaults.values())
            {
                if(_default.isEnabled())
                    ret.put(_default.getName(), _default.getValue());
            }
        }
        
        return ret;
    }

    /**
     * Removes the given default.
     */
    public static void remove(FieldDefault _default)
    {
        Map<String,FieldDefault> defaults = defaultMap.get(_default.getType());
        if(defaults != null)
            defaults.remove(_default.getName());
    }
}