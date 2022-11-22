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
package com.opsmatters.media.model.content.crawler;

import java.util.Map;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents a YAML configuration for a Read More link on a web page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class MoreLink implements ConfigElement
{
    private String selector = "";
    private long interval = 0L;
    private long maxWait = 0L;
    private int count = 1;

    /**
     * Default constructor.
     */
    public MoreLink()
    {
    }

    /**
     * Copy constructor.
     */
    public MoreLink(MoreLink obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(MoreLink obj)
    {
        if(obj != null)
        {
            setSelector(obj.getSelector());
            setInterval(obj.getInterval());
            setMaxWait(obj.getMaxWait());
            setCount(obj.getCount());
        }
    }

    /**
     * Returns the selector for the more link.
     */
    public String getSelector()
    {
        return selector;
    }

    /**
     * Sets the selector for the more link.
     */
    public void setSelector(String selector)
    {
        this.selector = selector;
    }

    /**
     * Returns the number of times to click the more link.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Sets the number of times to click the more link.
     */
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * Returns the interval between selector queries.
     */
    public long getInterval()
    {
        return interval;
    }

    /**
     * Sets the interval between selector queries.
     */
    public void setInterval(long interval)
    {
        this.interval = interval;
    }

    /**
     * Returns the maximum wait time before each click (in milliseconds).
     */
    public long getMaxWait()
    {
        return maxWait;
    }

    /**
     * Sets the maximum wait time before each click (in milliseconds).
     */
    public void setMaxWait(long maxWait)
    {
        this.maxWait = maxWait;
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
    public static class Builder implements ConfigParser<MoreLink>
    {
        // The config attribute names
        private static final String SELECTOR = "selector";
        private static final String INTERVAL = "interval";
        private static final String MAX_WAIT = "max-wait";
        private static final String COUNT = "count";

        private MoreLink ret = new MoreLink();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(SELECTOR))
                ret.setSelector((String)map.get(SELECTOR));
            if(map.containsKey(INTERVAL))
                ret.setInterval((Integer)map.get(INTERVAL));
            if(map.containsKey(MAX_WAIT))
                ret.setMaxWait((Integer)map.get(MAX_WAIT));
            if(map.containsKey(COUNT))
                ret.setCount((Integer)map.get(COUNT));

            return this;
        }

        /**
         * Sets the selector of the link.
         * @param selector The selector of the link
         * @return This object
         */
        public Builder selector(String selector)
        {
            ret.setSelector(selector);
            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public MoreLink build()
        {
            return ret;
        }
    }
}