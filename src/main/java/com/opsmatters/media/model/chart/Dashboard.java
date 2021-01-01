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

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.Serializable;

/**
 * Represents a dashboard containing widgets.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Dashboard implements Serializable
{
    public static final String TITLE = "title";
    public static final String WIDGETS = "widgets";

    private String id = "";
    private String title = "";
    private Map<String,Widget> widgets = new LinkedHashMap<String,Widget>();

    /**
     * Default constructor.
     */
    public Dashboard(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public Dashboard(Dashboard obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Dashboard obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setTitle(obj.getTitle());
            for(Widget widget : obj.getWidgets().values())
                addWidget(new Widget(widget));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public Dashboard(String id, Map<String, Object> map)
    {
        this(id);

        if(map.containsKey(TITLE))
            setTitle((String)map.get(TITLE));

        if(map.containsKey(WIDGETS))
        {
            List<Map<String,Object>> widgets = (List<Map<String,Object>>)map.get(WIDGETS);
            for(Map<String,Object> config : widgets)
            {
                for(Map.Entry<String,Object> entry : config.entrySet())
                    addWidget(new Widget(entry.getKey(), (Map<String,Object>)entry.getValue()));
            }
        }
    }

    /**
     * Returns the id of the dashboard.
     */
    public String toString()
    {
        return getTitle();
    }

    /**
     * Returns the id of the dashboard.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the dashboard.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the title of the dashboard.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title for the dashboard.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Adds a widget to the widgets for the dashboard.
     */
    public Map<String,Widget> getWidgets()
    {
        return this.widgets;
    }

    /**
     * Adds a widget to the widgets for the dashboard.
     */
    public void addWidget(Widget widget)
    {
        this.widgets.put(widget.getId(), widget);
    }

    /**
     * Returns the number of widgets.
     */
    public int numWidgets()
    {
        return widgets.size();
    }

    /**
     * Returns the widget at the given index.
     */
    public Widget getWidget(int i)
    {
        return widgets.get(i);
    }
}