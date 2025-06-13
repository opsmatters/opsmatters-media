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

package com.opsmatters.media.model.system.aws;

import java.util.Map;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents the RDS configuration.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RdsConfig extends AwsConfig
{
    private String clusterId = "";

    /**
     * Constructor that takes an id.
     */
    protected RdsConfig(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public RdsConfig(RdsConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(RdsConfig obj)
    {
        if(obj != null)
        {
            setClusterId(obj.getClusterId());
        }
    }

    /**
     * Returns the cluster id for the RDS configuration.
     */
    public String getClusterId()
    {
        return clusterId;
    }

    /**
     * Sets the cluster id for the RDS configuration.
     */
    public void setClusterId(String clusterId)
    {
        this.clusterId = clusterId;
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
    public static class Builder implements ConfigParser<RdsConfig>
    {
        // The config attribute names
        private static final String REGION = "region";
        private static final String CLUSTER_ID = "cluster-id";

        private RdsConfig ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new RdsConfig(id);
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
            if(map.containsKey(CLUSTER_ID))
                ret.setClusterId((String)map.get(CLUSTER_ID));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public RdsConfig build()
        {
            return ret;
        }
    }
}