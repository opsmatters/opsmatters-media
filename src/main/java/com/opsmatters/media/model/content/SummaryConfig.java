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
package com.opsmatters.media.model.content;

import java.util.Map;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents a YAML configuration used to parse a content summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SummaryConfig implements ConfigElement
{
    private int maxLength = 0;
    private int minLength = 0;

    /**
     * Default constructor.
     */
    public SummaryConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public SummaryConfig(SummaryConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SummaryConfig obj)
    {
        if(obj != null)
        {
            setMaxLength(obj.getMaxLength());
            setMinLength(obj.getMinLength());
        }
    }

    /**
     * Returns the max summary length for this configuration.
     */
    public int getMaxLength()
    {
        return maxLength;
    }

    /**
     * Sets the max summary length for this configuration.
     */
    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

    /**
     * Returns the min length for this configuration.
     */
    public int getMinLength()
    {
        return minLength;
    }

    /**
     * Sets the min length for this configuration.
     */
    public void setMinLength(int minLength)
    {
        this.minLength = minLength;
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
    public static class Builder implements ConfigParser<SummaryConfig>
    {
        // The config attribute names
        private static final String MAX_LENGTH = "max-length";
        private static final String MIN_LENGTH = "min-length";

        private SummaryConfig ret = new SummaryConfig();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(MAX_LENGTH))
                ret.setMaxLength((Integer)map.get(MAX_LENGTH));
            if(map.containsKey(MIN_LENGTH))
                ret.setMinLength((Integer)map.get(MIN_LENGTH));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public SummaryConfig build()
        {
            return ret;
        }
    }
}