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
import nl.crashdata.chartjs.data.simple.SimpleChartJsXYDataPoint;
import nl.crashdata.chartjs.data.simple.SimpleChartJsConfig;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsConfigBuilder;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.model.chart.Chart;
import com.opsmatters.media.model.chart.ChartDataset;
import com.opsmatters.media.model.chart.SourceType;
import com.opsmatters.media.model.chart.Parameters;

/**
 * Handler to create chart config for ChartJS.
 *
 * @author Gerald Curley (opsmatters)
 */
public class ChartHandler<X extends Serializable, Y extends Serializable>
{
    private static final Logger logger = Logger.getLogger(ChartHandler.class.getName());

    private Chart<X,Y> chart;
    private JDBCDatabaseConnection conn;
    private Parameters parameters = null;
    private SimpleChartJsConfigBuilder<SimpleChartJsXYDataPoint<X,Y>> config = null;

    /**
     * Default constructor.
     */
    public ChartHandler()
    {
    }

    /**
     * Returns the chart for the handler.
     */
    public Chart<X,Y> getChart()
    {
        return chart;
    }

    /**
     * Sets the chart for the handler.
     */
    public void setChart(Chart<X,Y> chart)
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
     * Returns the parameters for the handler.
     */
    public Parameters getParameters() 
    {
        return parameters;
    }

    /**
     * Sets the parameters for the handler.
     */
    public void setParameters(Parameters parameters) 
    {
        this.parameters = parameters;
    }

    /**
     * Returns the chartjs config for the handler.
     */
    public SimpleChartJsConfig<SimpleChartJsXYDataPoint<X,Y>> getConfig()
    {
        return config != null ? config.build() : null;
    }

    /**
     * Gets the config for the handler.
     */
    private void configure() throws Exception
    {
        if(chart != null)
        {
            config = chart.configure();
            if(config == null)
                throw new IllegalArgumentException("invalid chart type: "+chart.getType());

            for(ChartDataset<X,Y> dataset : chart.getDatasets())
            {
                DataSource<X,Y> source = null;
                if(dataset.getSource() != null)
                {
                    if(dataset.getSource().getType() == SourceType.DATABASE)
                        source = new DatabaseDataSource<X,Y>(conn);
                }

                if(source != null)
                    dataset.configure(config,
                        source.getDataPoints(dataset.getSource(), getParameters()));
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
    public static class Builder<X extends Serializable,Y extends Serializable>
    {
        private ChartHandler handler = new ChartHandler<X,Y>();

        /**
         * Sets the chart to use with the handler.
         * @param chart The chart
         * @return This object
         */
        public Builder withChart(Chart<X,Y> chart)
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
         * Sets the parameters to use with the handler.
         * @param parameters The parameters
         * @return This object
         */
        public Builder withParameters(Parameters parameters)
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