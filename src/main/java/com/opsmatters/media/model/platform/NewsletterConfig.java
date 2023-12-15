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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Map;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents the newsletter settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class NewsletterConfig implements ConfigElement
{
    private String id = "";
    private int day = -1;
    private int hour = -1;

    /**
     * Constructor that takes an id.
     */
    protected NewsletterConfig(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public NewsletterConfig(NewsletterConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(NewsletterConfig obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setDay(obj.getDay());
            setHour(obj.getHour());
        }
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

    /**
     * Returns the next from date for the newsletter settings.
     */
    public LocalDateTime getFromDate()
    {
        LocalDateTime ret = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
            .withHour(hour).withMinute(0).withSecond(0);
        LocalDateTime next = ret.with(ChronoField.DAY_OF_WEEK, day)
            .withHour(hour).withMinute(0).withSecond(0);
        if(next.equals(ret) || next.isAfter(ret))
            ret = ret.minusDays(7); // If the from date is in the future, go back a week
        return ret.with(ChronoField.DAY_OF_WEEK, day);
    }

    /**
     * Returns a builder for the configuration.
     * @param id The id of the configuration
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<NewsletterConfig>
    {
        // The config attribute names
        private static final String DAY = "day";
        private static final String HOUR = "hour";

        private NewsletterConfig ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new NewsletterConfig(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(DAY))
                ret.setDay((Integer)map.get(DAY));
            if(map.containsKey(HOUR))
                ret.setHour((Integer)map.get(HOUR));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public NewsletterConfig build()
        {
            return ret;
        }
    }
}