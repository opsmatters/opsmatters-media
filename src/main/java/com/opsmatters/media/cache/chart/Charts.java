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
package com.opsmatters.media.cache.chart;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.chart.Chart;

/**
 * Class representing the cache of charts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Charts
{
    private static final Logger logger = Logger.getLogger(Charts.class.getName());

    private static Map<String,Chart> chartMap = new HashMap<String,Chart>();
    private static List<Chart> chartList = new ArrayList<Chart>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Charts()
    {
    }

    /**
     * Returns <CODE>true</CODE> if charts have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of charts.
     */
    public static void load(List<Chart> charts)
    {
        initialised = false;

        clear();

        for(Chart chart : charts)
            add(chart);

        logger.info("Loaded "+size()+" charts");

        initialised = true;
    }

    /**
     * Clears the charts.
     */
    private static void clear()
    {
        chartList.clear();
        chartMap.clear();
    }

    /**
     * Adds the given chart.
     */
    private static void add(Chart chart)
    {
        chartList.add(chart);
        chartMap.put(chart.getId(), chart);
    }

    /**
     * Returns the chart for the given id.
     */
    public static Chart get(String id)
    {
        return chartMap.get(id);
    }

    /**
     * Returns the list of charts.
     */
    public static List<Chart> list()
    {
        return chartList;
    }

    /**
     * Returns the charts.
     */
    public static Map<String,Chart> map()
    {
        return chartMap;
    }

    /**
     * Returns the number of charts.
     */
    public static int size()
    {
        return chartList.size();
    }
}