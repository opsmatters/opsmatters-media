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

import nl.crashdata.chartjs.data.colors.ChartJsRGBAColor;

/**
 * Represents the values of a chart color.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ChartColor
{
    RED(ChartJsRGBAColor.RED),
    ORANGE(ChartJsRGBAColor.ORANGE),
    YELLOW(ChartJsRGBAColor.YELLOW),
    GREEN(ChartJsRGBAColor.GREEN),
    BLUE(ChartJsRGBAColor.BLUE),
    PURPLE(ChartJsRGBAColor.PURPLE),
    GREY(ChartJsRGBAColor.GREY),
    WHITE(new ChartJsRGBAColor(255,255,255)),
    BLACK(new ChartJsRGBAColor(0,0,0));

    private ChartJsRGBAColor rgba;

    /**
     * Constructor that takes an RGBA value.
     * @param value The RGBA value for the color
     */
    ChartColor(ChartJsRGBAColor rgba)
    {
        this.rgba = rgba;
    }

    /**
     * Returns the RGBA value of the color.
     * @return The RGBA value of the color.
     */
    public String toString()
    {
        return rgba().toString();
    }

    /**
     * Returns the RGBA value of the color.
     * @return The RGBA value of the color.
     */
    public ChartJsRGBAColor rgba()
    {
        return rgba;
    }

    /**
     * Returns <CODE>true</CODE> if the given color is contained in the list of colors.
     * @param name The color name
     * @return <CODE>true</CODE> if the given color is contained in the list of colors
     */
    public static boolean contains(String name)
    {
        return valueOf(name) != null;
    }
}