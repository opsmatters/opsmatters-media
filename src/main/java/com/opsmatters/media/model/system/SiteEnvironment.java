/*
 * Copyright 2025 Gerald Curley
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

package com.opsmatters.media.model.system;

import java.util.Map;

/**
 * Represents the configuration of a site environment.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SiteEnvironment extends Environment
{
    private Site site;
    private boolean stopInstanceWhenIdle = false;

    /**
     * Constructor that takes a site and id.
     */
    protected SiteEnvironment(EnvironmentId id, Site site)
    {
        super(id);
        setSite(site);
    }

    /**
     * Copy constructor.
     */
    public SiteEnvironment(SiteEnvironment obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SiteEnvironment obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setSite(obj.getSite());
        }
    }

    /**
     * Returns the display name of the environment.
     */
    public String getDisplayName()
    {
        return String.format("%s %s", site.getName(), getId().value());
    }

    /**
     * Returns the site of the environment.
     */
    public Site getSite()
    {
        return site;
    }

    /**
     * Sets the site of the environment.
     */
    public void setSite(Site site)
    {
        this.site = site;
    }

    /**
     * Returns the key of the environment.
     */
    public String getKey()
    {
        return String.format("%s-%s", getSite().getId().toLowerCase(), getId().code());
    }

    /**
     * Returns <CODE>true</CODE> if the instance for this environment should stop when feeds complete.
     */
    public boolean stopInstanceWhenIdle()
    {
        return stopInstanceWhenIdle;
    }

    /**
     * Set to <CODE>true</CODE> if the instance for this environment should stop when feeds complete.
     */
    public void setStopInstanceWhenIdle(boolean stopInstanceWhenIdle)
    {
        this.stopInstanceWhenIdle = stopInstanceWhenIdle;
    }

    /**
     * Returns a builder for the environment.
     * @param id The id of the environment
     * @param site The site of the environment
     * @return The builder instance.
     */
    public static Builder builder(String id, Site site)
    {
        return new Builder(EnvironmentId.valueOf(id), site);
    }

    /**
     * Builder to make environment construction easier.
     */
    public static class Builder extends Environment.Builder
    {
        private SiteEnvironment ret = null;

        /**
         * Constructor that takes a name.
         * @param id The id for the environment
         * @param site The site for the environment
         */
        public Builder(EnvironmentId id, Site site)
        {
            ret = new SiteEnvironment(id, site);
            super.set(ret);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            super.parse(map);
            return this;
        }

        /**
         * Returns the configured environment instance
         * @return The environment instance
         */
        @Override
        public SiteEnvironment build()
        {
            return ret;
        }
    }
}