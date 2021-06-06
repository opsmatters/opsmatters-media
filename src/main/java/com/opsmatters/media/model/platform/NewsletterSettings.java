/*
 * Copyright 2021 Gerald Curley
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

package com.opsmatters.media.model.platform;

import java.util.Map;

/**
 * Represents the newsletter settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class NewsletterSettings implements java.io.Serializable
{
    public static final String DAY = "day";
    public static final String HOUR = "hour";

    private String id = "";
    private int day = -1;
    private int hour = -1;

    /**
     * Default Constructor.
     */
    protected NewsletterSettings(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public NewsletterSettings(NewsletterSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(NewsletterSettings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setDay(obj.getDay());
            setHour(obj.getHour());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public NewsletterSettings(String id, Map<String, Object> map)
    {
        this(id);

        if(map.containsKey(DAY))
            setDay((Integer)map.get(DAY));
        if(map.containsKey(HOUR))
            setHour((Integer)map.get(HOUR));
    }

    /**
     * Returns the id of the newsletter settings.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the newsletter settings.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the newsletter settings.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the day for the newsletter settings.
     */
    public int getDay()
    {
        return day;
    }

    /**
     * Sets the day for the newsletter settings.
     */
    public void setDay(int day)
    {
        this.day = day;
    }

    /**
     * Returns the hour for the newsletter settings.
     */
    public int getHour()
    {
        return hour;
    }

    /**
     * Sets the hour for the newsletter settings.
     */
    public void setHour(int hour)
    {
        this.hour = hour;
    }
}