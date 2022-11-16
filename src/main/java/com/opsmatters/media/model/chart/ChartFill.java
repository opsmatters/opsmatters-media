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

package com.opsmatters.media.model.chart;

import java.util.Map;
import nl.crashdata.chartjs.data.ChartJsFill;
import nl.crashdata.chartjs.data.ChartJsFillMode;
import nl.crashdata.chartjs.data.ChartJsBoundaryType;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

import static nl.crashdata.chartjs.data.ChartJsFillMode.*;

/**
 * Represents a fill for a plot.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartFill implements ConfigElement
{
    private ChartJsFillMode mode;
    private ChartJsBoundaryType boundaryType;
    private int datasetIndex;

    /**
     * Default constructor.
     */
    public ChartFill()
    {
    }

    /**
     * Copy constructor.
     */
    public ChartFill(ChartFill obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartFill obj)
    {
        if(obj != null)
        {
            setMode(obj.getMode());
            setBoundaryType(obj.getBoundaryType());
            setDatasetIndex(obj.getDatasetIndex());
        }
    }

    /**
     * Returns the fill mode for the chart.
     */
    public ChartJsFillMode getMode()
    {
        return mode;
    }

    /**
     * Sets the fill mode for the chart.
     */
    public void setMode(ChartJsFillMode mode)
    {
        this.mode = mode;
    }

    /**
     * Sets the fill mode for the chart.
     */
    public void setMode(String mode)
    {
        setMode(ChartJsFillMode.valueOf(mode));
    }

    /**
     * Returns the boundary type for the chart.
     */
    public ChartJsBoundaryType getBoundaryType()
    {
        return boundaryType;
    }

    /**
     * Sets the boundary type for the chart.
     */
    public void setBoundaryType(ChartJsBoundaryType boundaryType)
    {
        this.boundaryType = boundaryType;
    }

    /**
     * Sets the boundary type for the chart.
     */
    public void setBoundaryType(String boundaryType)
    {
        setBoundaryType(ChartJsBoundaryType.valueOf(boundaryType));
    }

    /**
     * Returns the dataset index for the chart fill.
     */
    public int getDatasetIndex()
    {
        return datasetIndex;
    }

    /**
     * Sets the dataset index for the chart fill.
     */
    public void setDatasetIndex(int datasetIndex)
    {
        this.datasetIndex = datasetIndex;
    }

    /**
     * Returns the ChartJs fill object.
     */
    public ChartJsFill configure()
    {
        ChartJsFill ret = null;
        switch(mode)
        {
            case ABSOLUTE_DATASET_INDEX:
                ret = ChartJsFill.absoluteIndex(datasetIndex);
                break;
            case RELATIVE_DATASET_INDEX:
                ret = ChartJsFill.relativeIndex(datasetIndex);
                break;
            case BOUNDARY:
                ret = ChartJsFill.boundary(boundaryType);
                break;
            case DISABLED:
                ret = ChartJsFill.disabled();
                break;
            default:
                throw new IllegalStateException();
        }
 
        return ret;
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
    public static class Builder implements ConfigParser<ChartFill>
    {
        // The config attribute names
        private static final String MODE = "mode";
        private static final String BOUNDARY_TYPE = "boundary-type";
        private static final String DATASET_INDEX = "dataset-index";

        private ChartFill ret = new ChartFill();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(MODE))
                ret.setMode((String)map.get(MODE));
            if(map.containsKey(BOUNDARY_TYPE))
                ret.setBoundaryType((String)map.get(BOUNDARY_TYPE));
            if(map.containsKey(DATASET_INDEX))
                ret.setDatasetIndex((Integer)map.get(DATASET_INDEX));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public ChartFill build()
        {
            return ret;
        }
    }
}