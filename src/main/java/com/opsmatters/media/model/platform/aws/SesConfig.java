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

package com.opsmatters.media.model.platform.aws;

import java.util.Map;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents the SES configuration.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SesConfig extends AwsConfig
{
    private String from = "";

    /**
     * Constructor that takes an id.
     */
    protected SesConfig(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public SesConfig(SesConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SesConfig obj)
    {
        if(obj != null)
        {
            setFrom(obj.getFrom());
        }
    }

    /**
     * Returns the from address for the SES configuration.
     */
    public String getFrom()
    {
        return from;
    }

    /**
     * Sets the from address for the SES configuration.
     */
    public void setFrom(String from)
    {
        this.from = from;
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
    public static class Builder implements ConfigParser<SesConfig>
    {
        // The config attribute names
        private static final String REGION = "region";
        private static final String FROM = "from";

        private SesConfig ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new SesConfig(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(REGION))
                ret.setRegion((String)map.get(REGION));
            if(map.containsKey(FROM))
                ret.setFrom((String)map.get(FROM));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public SesConfig build()
        {
            return ret;
        }
    }
}