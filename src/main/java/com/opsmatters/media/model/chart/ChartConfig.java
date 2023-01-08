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

package com.opsmatters.media.model.chart;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.ConfigStore;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents the configuration for charts and dashboards.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartConfig extends ConfigStore
{
    private static final Logger logger = Logger.getLogger(ChartConfig.class.getName());

    public static final String FILENAME = "charts.yml";

    private List<Chart> charts = new ArrayList<Chart>();
    private List<Dashboard> dashboards = new ArrayList<Dashboard>();

    /**
     * Default constructor.
     */
    protected ChartConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public ChartConfig(ChartConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartConfig obj)
    {
        if(obj != null)
        {
            for(Chart chart : obj.getCharts())
                addChart(ChartFactory.newInstance(chart));
            for(Dashboard dashboard : obj.getDashboards())
                addDashboard(new Dashboard(dashboard));
        }
    }

    /**
     * Returns the name of the config file.
     */
    @Override
    public String getFilename()
    {
        return FILENAME;
    }

    /**
     * Returns the charts for this configuration.
     */
    public List<Chart> getCharts()
    {
        return charts;
    }

    /**
     * Sets the charts for this configuration.
     */
    public void setCharts(List<Chart> charts)
    {
        this.charts = charts;
    }

    /**
     * Adds a chart for this configuration.
     */
    public void addChart(Chart chart)
    {
        this.charts.add(chart);
    }

    /**
     * Returns the number of charts.
     */
    public int numCharts()
    {
        return charts.size();
    }

    /**
     * Returns the chart at the given index.
     */
    public Chart getChart(int i)
    {
        return charts.get(i);
    }

    /**
     * Returns the dashboards for this configuration.
     */
    public List<Dashboard> getDashboards()
    {
        return dashboards;
    }

    /**
     * Sets the dashboard for this configuration.
     */
    public void setDashboards(List<Dashboard> dashboards)
    {
        this.dashboards = dashboards;
    }

    /**
     * Adds a dashboard for this configuration.
     */
    public void addDashboard(Dashboard dashboard)
    {
        this.dashboards.add(dashboard);
    }

    /**
     * Returns the number of dashboards.
     */
    public int numDashboards()
    {
        return dashboards.size();
    }

    /**
     * Returns the dashboard at the given index.
     */
    public Dashboard getDashboard(int i)
    {
        return dashboards.get(i);
    }

    /**
     * Returns a builder for the chart config.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make chart config construction easier.
     */
    public static class Builder
        extends ConfigStore.Builder<ChartConfig,Builder>
        implements ConfigParser<ChartConfig>
    {
        // The config attribute names
        private static final String CHARTS = "charts";
        private static final String DASHBOARDS = "dashboards";

        private ChartConfig ret = new ChartConfig();

        /**
         * Default constructor.
         */
        protected Builder()
        {
            filename(ret.getFilename());
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(CHARTS))
            {
                List<Map<String,Object>> charts = (List<Map<String,Object>>)map.get(CHARTS);
                for(Map<String,Object> config : charts)
                {
                    for(Map.Entry<String,Object> entry : config.entrySet())
                        ret.addChart(ChartFactory.newInstance(entry.getKey(), (Map<String,Object>)entry.getValue()));
                }
            }

            if(map.containsKey(DASHBOARDS))
            {
                List<Map<String,Object>> dashboards = (List<Map<String,Object>>)map.get(DASHBOARDS);
                for(Map<String,Object> config : dashboards)
                {
                    for(Map.Entry<String,Object> entry : config.entrySet())
                        ret.addDashboard(Dashboard.builder(entry.getKey())
                            .parse((Map<String,Object>)entry.getValue()).build());
                }
            }

            return this;
        }

        /**
         * Returns this object.
         * @return This object
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Returns the configured chart config instance
         * @return The chart config instance
         */
        @Override
        public ChartConfig build() throws IOException
        {
            read(this);
            return ret;
        }
    }
}