/*
 * Copyright 2019 Gerald Curley
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

package com.opsmatters.media.model.content;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Contains a list of fields for output.
 *
 * @author Gerald Curley (opsmatters)
 */
public class FieldMap extends LinkedHashMap<String,String>
{
    /**
     * Default constructor.
     */
    public FieldMap()
    {
    }

    /**
     * Copy constructor.
     */
    public FieldMap(FieldMap fields)
    {
        putAll(fields);
    }

    /**
     * Add the fields from the given sources without overwriting existing fields.
     */
    public FieldMap add(FieldSource... sources)
    {
        if(sources != null)
        {
            for(FieldSource source : sources)
            {
                if(source != null)
                {
                    FieldMap fields = source.getFields();
                    if(fields != null)
                    {
                        for(String key : fields.keySet())
                        {
                            String value = get(key);
                            if(value == null || value.length() == 0)
                            {
                                // Ignore fields that have been marked as empty
                                String fieldValue = fields.get(key);
                                if(fieldValue != null && !fieldValue.equals(ContentItem.EMPTY))
                                    put(key, fieldValue);
                            }
                        }
                    }
                }
            }
        }

        return this;
    }

    /**
     * Returns the mapping with the given key.
     */
    public String get(FieldName key)
    {
        return get(key.value());
    }

    /**
     * Returns the mapping with the given key.
     * <p>
     * Returns the fallback if no mapping is found.
     */
    public String get(FieldName key, String fallback)
    {
        String ret = get(key);
        if(ret == null)
            ret = fallback;
        return ret;
    }

    /**
     * Adds the mapping with the given key.
     */
    public void put(FieldName key, String value)
    {
        put(key.value(), value);
    }

    /**
     * Copies all of the mappings from the specified map to this map (optional operation).
     */
    public void putAll(Map<? extends String,? extends String> map)
    {
        if(map != null)
            super.putAll(map);
    }

    /**
     * Returns <CODE>true</CODE> if the mapping with the given key exists.
     */
    public boolean containsKey(FieldName key)
    {
        return containsKey(key.value());
    }
}