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
 * Represents the S3 configuration.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class S3Config extends AwsConfig
{
    private String contentConfig = "";
    private String chartConfig = "";
    private String platformConfig = "";
    private String content = "";

    /**
     * Constructor that takes an id.
     */
    protected S3Config(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public S3Config(S3Config obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(S3Config obj)
    {
        if(obj != null)
        {
            setContentConfigBucket(obj.getContentConfigBucket());
            setChartConfigBucket(obj.getChartConfigBucket());
            setPlatformConfigBucket(obj.getPlatformConfigBucket());
            setContentBucket(obj.getContentBucket());
        }
    }

    /**
     * Returns the content config bucket for the S3 configuration.
     */
    public String getContentConfigBucket()
    {
        return contentConfig;
    }

    /**
     * Sets the content config bucket for the S3 configuration.
     */
    public void setContentConfigBucket(String contentConfig)
    {
        this.contentConfig = contentConfig;
    }

    /**
     * Returns the chart config bucket for the S3 configuration.
     */
    public String getChartConfigBucket()
    {
        return chartConfig;
    }

    /**
     * Sets the chart config bucket for the S3 configuration.
     */
    public void setChartConfigBucket(String chartConfig)
    {
        this.chartConfig = chartConfig;
    }

    /**
     * Returns the platform config bucket for the S3 configuration.
     */
    public String getPlatformConfigBucket()
    {
        return platformConfig;
    }

    /**
     * Sets the platform config bucket for the S3 configuration.
     */
    public void setPlatformConfigBucket(String platformConfig)
    {
        this.platformConfig = platformConfig;
    }

    /**
     * Returns the content bucket for the S3 configuration.
     */
    public String getContentBucket()
    {
        return content;
    }

    /**
     * Sets the content bucket for the S3 configuration.
     */
    public void setContentBucket(String content)
    {
        this.content = content;
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
    public static class Builder implements ConfigParser<S3Config>
    {
        // The config attribute names
        private static final String REGION = "region";
        private static final String CONTENT_CONFIG = "content-config";
        private static final String CHART_CONFIG = "chart-config";
        private static final String PLATFORM_CONFIG = "platform-config";
        private static final String CONTENT = "content";

        private S3Config ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new S3Config(id);
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
            if(map.containsKey(CONTENT_CONFIG))
                ret.setContentConfigBucket((String)map.get(CONTENT_CONFIG));
            if(map.containsKey(CHART_CONFIG))
                ret.setChartConfigBucket((String)map.get(CHART_CONFIG));
            if(map.containsKey(PLATFORM_CONFIG))
                ret.setPlatformConfigBucket((String)map.get(PLATFORM_CONFIG));
            if(map.containsKey(CONTENT))
                ret.setContentBucket((String)map.get(CONTENT));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public S3Config build()
        {
            return ret;
        }
    }
}