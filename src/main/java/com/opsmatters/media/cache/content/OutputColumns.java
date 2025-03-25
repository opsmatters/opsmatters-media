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
import com.opsmatters.media.model.content.OutputColumn;

/**
 * Class representing the set of content output columns.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OutputColumns
{
    private static final Logger logger = Logger.getLogger(OutputColumns.class.getName());

    private static Map<ContentType,Map<String,OutputColumn>> columnMap = new LinkedHashMap<ContentType,Map<String,OutputColumn>>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private OutputColumns()
    {
    }

    /**
     * Returns <CODE>true</CODE> if columns have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of columns.
     */
    public static void load(List<OutputColumn> columns)
    {
        initialised = false;

        columnMap.clear();

        int count = 0;
        for(OutputColumn column : columns)
        {
            add(column);
            ++count;
        }

        logger.info(String.format("Loaded %d output columns", count));

        initialised = true;
    }

    /**
     * Adds the given column.
     */
    public static void add(OutputColumn column)
    {
        Map<String,OutputColumn> columns = columnMap.get(column.getType());
        if(columns == null)
        {
            columns = new LinkedHashMap<String,OutputColumn>();
            columnMap.put(column.getType(), columns);
        }

        columns.put(column.getName(), column);
    }

    /**
     * Returns the columns for the given type.
     */
    public static Map<String,String> get(ContentType type)
    {
        Map<String,String> ret = null;

        Map<String,OutputColumn> columns = columnMap.get(type);
        if(columns != null)
        {
            ret = new LinkedHashMap<String,String>();
            for(OutputColumn column : columns.values())
            {
                if(column.isEnabled())
                    ret.put(column.getName(), column.getValue());
            }
        }
        
        return ret;
    }

    /**
     * Removes the given column.
     */
    public static void remove(OutputColumn column)
    {
        Map<String,OutputColumn> columns = columnMap.get(column.getType());
        if(columns != null)
            columns.remove(column.getName());
    }
}