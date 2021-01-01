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

package com.opsmatters.media.config.chart;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.config.YamlConfiguration;
import com.opsmatters.media.model.chart.Chart;
import com.opsmatters.media.model.chart.Dashboard;

/**
 * Class that represents the configuration for charts and dashboards.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartConfiguration extends YamlConfiguration
{
    private static final Logger logger = Logger.getLogger(ChartConfiguration.class.getName());

    public static final String FILENAME = "charts.yml";

    public static final String CHARTS = "charts";
    public static final String DASHBOARDS = "dashboards";

    private List<Chart> charts = new ArrayList<Chart>();
    private List<Dashboard> dashboards = new ArrayList<Dashboard>();

    /**
     * Default constructor.
     */
    public ChartConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public ChartConfiguration(ChartConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(Chart chart : obj.getCharts())
                addChart(new Chart(chart));
            for(Dashboard dashboard : obj.getDashboards())
                addDashboard(new Dashboard(dashboard));
        }
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
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(CHARTS))
        {
            List<Map<String,Object>> charts = (List<Map<String,Object>>)map.get(CHARTS);
            for(Map<String,Object> config : charts)
            {
                for(Map.Entry<String,Object> entry : config.entrySet())
                    addChart(new Chart(entry.getKey(), (Map<String,Object>)entry.getValue()));
            }
        }

        if(map.containsKey(DASHBOARDS))
        {
            List<Map<String,Object>> dashboards = (List<Map<String,Object>>)map.get(DASHBOARDS);
            for(Map<String,Object> config : dashboards)
            {
                for(Map.Entry<String,Object> entry : config.entrySet())
                    addDashboard(new Dashboard(entry.getKey(), (Map<String,Object>)entry.getValue()));
            }
        }
    }

    /**
     * Returns a builder for the configuration.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder
    {
        protected String name = "";
        protected String directory = "";
        protected String filename = "";

        /**
         * Default constructor.
         */
        public Builder()
        {
        }

        /**
         * Sets the name of the configuration.
         * <P>
         * @param name The name of the configuration
         * @return This object
         */
        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        /**
         * Sets the config directory.
         * @param key The config directory
         * @return This object
         */
        public Builder directory(String directory)
        {
            this.directory = directory;
            return this;
        }

        /**
         * Sets the config filename.
         * @param key The config filename
         * @return This object
         */
        public Builder filename(String filename)
        {
            this.filename = filename;
            return this;
        }

        /**
         * Returns the configuration
         * @return The configuration
         */
        public ChartConfiguration build(boolean read)
        {
            ChartConfiguration ret = new ChartConfiguration(name);

            if(read)
            {
                // Read the config file
                File config = new File(directory, filename);
                ret.read(config.getAbsolutePath());
            }

            return ret;
        }
    }
}