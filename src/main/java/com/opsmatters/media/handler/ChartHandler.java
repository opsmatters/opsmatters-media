/*
 * Copyright 2020 Gerald Curley
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

package com.opsmatters.media.handler;

import java.io.Serializable;
import java.util.logging.Logger;
import nl.crashdata.chartjs.data.simple.SimpleChartJsConfig;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsConfigBuilder;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.chart.Chart;
import com.opsmatters.media.model.chart.ChartJsChart;
import com.opsmatters.media.model.chart.ChartDataset;
import com.opsmatters.media.model.chart.SourceType;
import com.opsmatters.media.model.chart.ChartParameters;
import com.opsmatters.media.model.chart.MetricChart;
import com.opsmatters.media.model.chart.MetricConfig;
import com.opsmatters.media.model.chart.MetricsConfig;
import com.opsmatters.media.model.chart.ChartMetric;
import com.opsmatters.media.model.chart.ListChart;
import com.opsmatters.media.model.chart.ListConfig;
import com.opsmatters.media.model.chart.ChartColumn;


/**
 * Handler to create chart config for charts.
 *
 * @author Gerald Curley (opsmatters)
 */
public class ChartHandler<E extends Serializable>
{
    private static final Logger logger = Logger.getLogger(ChartHandler.class.getName());

    private Chart<E> chart;
    private JDBCDatabaseConnection conn;
    private Site site;
    private ChartParameters parameters = null;
    private SimpleChartJsConfigBuilder<E> chartJsConfig = null;
    private MetricsConfig.Builder<Number> metricsConfig = null;
    private ListConfig.Builder<E> listConfig = null;

    /**
     * Default constructor.
     */
    public ChartHandler()
    {
    }

    /**
     * Returns the chart for the handler.
     */
    public Chart<E> getChart()
    {
        return chart;
    }

    /**
     * Sets the chart for the handler.
     */
    public void setChart(Chart<E> chart)
    {
        this.chart = chart;
    }

    /**
     * Returns the JDBC connection for the handler.
     */
    public JDBCDatabaseConnection getConnection()
    {
        return conn;
    }

    /**
     * Sets the JDBC connection for the handler.
     */
    public void setConnection(JDBCDatabaseConnection conn)
    {
        this.conn = conn;
    }

    /**
     * Returns the site for the handler.
     */
    public Site getSite()
    {
        return site;
    }

    /**
     * Sets the site for the handler.
     */
    public void setSite(Site site)
    {
        this.site = site;
    }

    /**
     * Returns the parameters for the handler.
     */
    public ChartParameters getParameters() 
    {
        return parameters;
    }

    /**
     * Sets the parameters for the handler.
     */
    public void setParameters(ChartParameters parameters) 
    {
        this.parameters = parameters;
    }

    /**
     * Returns the chartjs config for the handler.
     */
    public SimpleChartJsConfig<E> getChartJsConfig()
    {
        return chartJsConfig != null ? chartJsConfig.build() : null;
    }

    /**
     * Returns the metrics config for the handler.
     */
    public MetricsConfig<Number> getMetricsConfig()
    {
        return metricsConfig != null ? metricsConfig.build() : null;
    }

    /**
     * Returns the list config for the handler.
     */
    public ListConfig<E> getListConfig()
    {
        return listConfig != null ? listConfig.build() : null;
    }

    /**
     * Gets the config for the handler.
     */
    private void configure() throws Exception
    {
        if(chart != null)
        {
            if(chart instanceof ChartJsChart)
            {
                ChartJsChart<E> chartjsChart = (ChartJsChart<E>)chart;
                chartJsConfig = chartjsChart.configure();
                if(chartJsConfig == null)
                    throw new IllegalArgumentException("invalid chart type: "+chartjsChart.getType());

                for(ChartDataset<E> dataset : chartjsChart.getDatasets())
                {
                    DataSource<E> source = null;
                    if(dataset.getSource() != null)
                    {
                        if(dataset.getSource().getType() == SourceType.DATABASE)
                            source = new DatabaseDataSource<E>(conn, site);
                    }

                    if(source != null)
                    {
                        dataset.configure(chartJsConfig,
                            source.getDataPoints(dataset.getSource(), getParameters()));
                    }
                }
            }
            else if(chart instanceof MetricChart)
            {
                MetricChart<Number> metricChart = (MetricChart<Number>)chart;
                metricsConfig = metricChart.configure();

                for(ChartMetric<Number> metric : metricChart.getMetrics())
                {
                    DataSource<Number> source = null;
                    if(metric.getSource() != null)
                    {
                        if(metric.getSource().getType() == SourceType.DATABASE)
                            source = new DatabaseDataSource<Number>(conn, site);
                    }

                    if(source != null)
                    {
                        metric.configure(metricsConfig,
                            source.getDataPoints(metric.getSource(), getParameters()));
                    }
                }
            }
            else if(chart instanceof ListChart)
            {
                ListChart<E> listChart = (ListChart<E>)chart;
                listConfig = listChart.configure();

                for(ChartColumn column : listChart.getColumns())
                {
                    column.configure(listConfig);
                }

                DataSource<E> source = null;
                if(listChart.getSource() != null)
                {
                    if(listChart.getSource().getType() == SourceType.DATABASE)
                        source = new DatabaseDataSource<E>(conn, site);
                }

                if(source != null)
                {
                    listChart.configure(listConfig,
                        source.getDataPoints(listChart.getSource(), getParameters()));
                }
            }
        }
    }

    /**
     * Returns a builder for the handler.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make handler construction easier.
     */
    public static class Builder<E extends Serializable>
    {
        private ChartHandler handler = new ChartHandler<E>();

        /**
         * Sets the chart to use with the handler.
         * @param chart The chart
         * @return This object
         */
        public Builder withChart(Chart<E> chart)
        {
            handler.setChart(chart);
            return this;
        }

        /**
         * Sets the JDBC connection to use with the handler.
         * @param conn The connection
         * @return This object
         */
        public Builder withConnection(JDBCDatabaseConnection conn)
        {
            handler.setConnection(conn);
            return this;
        }

        /**
         * Sets the site to use with the handler.
         * @param site The site
         * @return This object
         */
        public Builder withSite(Site site)
        {
            handler.setSite(site);
            return this;
        }

        /**
         * Sets the parameters to use with the handler.
         * @param parameters The parameters
         * @return This object
         */
        public Builder withParameters(ChartParameters parameters)
        {
            handler.setParameters(parameters);
            return this;
        }

        /**
         * Returns the configured handler instance
         * @return The handler instance
         */
        public ChartHandler build() throws Exception
        {
            handler.configure();
            return handler;
        }
    }
}