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
import com.opsmatters.media.cache.admin.Parameters;
import com.opsmatters.media.model.admin.ParameterType;
import com.opsmatters.media.model.admin.ParameterName;
import com.opsmatters.media.model.chart.Dashboard;
import com.opsmatters.media.model.chart.DashboardId;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing the cache of dashboards.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Dashboards extends StaticCache
{
    private static final Logger logger = Logger.getLogger(Dashboards.class.getName());

    private static Map<DashboardId,Dashboard> dashboardMap = new HashMap<DashboardId,Dashboard>();
    private static List<Dashboard> dashboardList = new ArrayList<Dashboard>();

    /**
     * Private constructor.
     */
    private Dashboards()
    {
    }

    /**
     * Loads the set of dashboards.
     */
    public static void load(List<Dashboard> dashboards)
    {
        setInitialised(false);

        clear();

        for(Dashboard dashboard : dashboards)
            add(dashboard);

        logger.info("Loaded "+size()+" dashboards");

        setInitialised(true);
    }

    /**
     * Clears the dashboards.
     */
    private static void clear()
    {
        dashboardList.clear();
        dashboardMap.clear();
    }

    /**
     * Adds the given dashboard.
     */
    private static void add(Dashboard dashboard)
    {
        dashboardList.add(dashboard);
        dashboardMap.put(dashboard.getId(), dashboard);
    }

    /**
     * Returns the dashboard for the given id.
     */
    public static Dashboard get(DashboardId id)
    {
        return dashboardMap.get(id);
    }

    /**
     * Returns the list of dashboards.
     */
    public static List<Dashboard> list()
    {
        return dashboardList;
    }

    /**
     * Returns the number of dashboards.
     */
    public static int size()
    {
        return dashboardList.size();
    }
}