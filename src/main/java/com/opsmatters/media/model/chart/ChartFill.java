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

import static nl.crashdata.chartjs.data.ChartJsFillMode.*;

/**
 * Represents a fill for a plot.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartFill
{
    public static final String MODE = "mode";
    public static final String BOUNDARY_TYPE = "boundary-type";
    public static final String DATASET_INDEX = "dataset-index";

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
     * Reads the object from the given YAML Document.
     */
    public ChartFill(Map<String, Object> map)
    {
        if(map.containsKey(MODE))
            setMode((String)map.get(MODE));
        if(map.containsKey(BOUNDARY_TYPE))
            setBoundaryType((String)map.get(BOUNDARY_TYPE));
        if(map.containsKey(DATASET_INDEX))
            setDatasetIndex((Integer)map.get(DATASET_INDEX));
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
}