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

package com.opsmatters.media.model.platform;

//GERALD: check
import java.util.Map;
//import com.opsmatters.media.model.ConfigElement;
//import com.opsmatters.media.model.ConfigParser;
//import com.opsmatters.media.model.platform.aws.Ec2Config;
//import com.opsmatters.media.model.platform.aws.RdsConfig;

/**
 * Represents the configuration of a site environment.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SiteEnvironment extends Environment
{
//GERALD
//    private EnvironmentId id;
    private Site site;
//    private String url = "";
//    private String base = "";
//    private DatabaseConfig database;
//    private Ec2Config ec2;
//    private RdsConfig rds;
//    private SshConfig ssh;
    private boolean stopInstanceWhenIdle = false;

    /**
     * Constructor that takes a site and id.
     */
    protected SiteEnvironment(EnvironmentId id, Site site)
    {
//GERALD
//        setId(id);
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
//GERALD
super.copyAttributes(obj);
//            setId(obj.getId());
            setSite(obj.getSite());
//            setUrl(obj.getUrl());
//            setBase(obj.getBase());
//            setDatabaseConfig(new DatabaseConfig(obj.getDatabaseConfig()));
//            setEc2Config(new Ec2Config(obj.getEc2Config()));
//            setRdsConfig(new RdsConfig(obj.getRdsConfig()));
//            setSshConfig(new SshConfig(obj.getSshConfig()));
        }
    }

    /**
     * Returns the name of the environment.
     */
/* GERALD
    public String toString()
    {
        return getName();
    }
*/
    /**
     * Returns the id of the environment.
     */
/* GERALD
    public String getName()
    {
        return getId().name();
    }
*/
    /**
     * Returns the display name of the environment.
     */
    public String getDisplayName()
    {
//GERALD
//        return site != null ? String.format("%s %s", site.getTitle(), id.value()) : id.value();
        return String.format("%s %s", site.getTitle(), getId().value());
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
//GERALD
//        if(site != null)
            return String.format("%s-%s",
                getSite().getId().toLowerCase(), getId().code());
//        return getId().code();
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
//GERALD
//    public static class Builder implements ConfigParser<SiteEnvironment>
//    public static class Builder extends Environment.Builder<SiteEnvironment, Builder>
    public static class Builder extends Environment.Builder
    {
        // The config attribute names
/* GERALD
        private static final String URL = "url";
        private static final String BASE = "base";
        private static final String DATABASE = "database";
        private static final String EC2 = "ec2";
        private static final String RDS = "rds";
        private static final String SSH = "ssh";
*/
        private SiteEnvironment ret = null;

        /**
         * Constructor that takes a name.
         * @param id The id for the environment
         * @param site The site for the environment
         */
        public Builder(EnvironmentId id, Site site)
        {
            ret = new SiteEnvironment(id, site);
//GERALD
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
         * Returns this object.
         * @return This object
         */
/* GERALD
        @Override
        protected Builder self()
        {
            return this;
        }
*/
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