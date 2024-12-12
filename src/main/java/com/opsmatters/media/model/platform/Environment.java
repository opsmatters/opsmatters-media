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
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.platform.aws.Ec2Config;
import com.opsmatters.media.model.platform.aws.RdsConfig;

/**
 * Represents the configuration of a site environment.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Environment implements ConfigElement
{
    private EnvironmentId id;
    private Site site;
    private String url = "";
    private String ping = "";
    private String base = "";
    private FeedsConfig feeds;
    private DatabaseConfig database;
    private Ec2Config ec2;
    private RdsConfig rds;
    private SshConfig ssh;
    private boolean stopInstanceWhenIdle = false;

    /**
     * Constructor that takes an id.
     */
    protected Environment(EnvironmentId id, Site site)
    {
        setId(id);
        setSite(site);
    }

    /**
     * Copy constructor.
     */
    public Environment(Environment obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Environment obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setSite(obj.getSite());
            setUrl(obj.getUrl());
            setPing(obj.getPing());
            setBase(obj.getBase());
            setFeedsConfig(new FeedsConfig(obj.getFeedsConfig()));
            setDatabaseConfig(new DatabaseConfig(obj.getDatabaseConfig()));
            setEc2Config(new Ec2Config(obj.getEc2Config()));
            setRdsConfig(new RdsConfig(obj.getRdsConfig()));
            setSshConfig(new SshConfig(obj.getSshConfig()));
        }
    }

    /**
     * Returns the name of the environment.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the id of the environment.
     */
    public String getName()
    {
        return getId().name();
    }

    /**
     * Returns the display name of the environment.
     */
    public String getDisplayName()
    {
        return site != null ? String.format("%s %s", site.getTitle(), id.value()) : id.value();
    }

    /**
     * Returns the id of the environment.
     */
    public EnvironmentId getId()
    {
        return id;
    }

    /**
     * Sets the id for the environment.
     */
    public void setId(EnvironmentId id)
    {
        this.id = id;
    }

    /**
     * Sets the id for the environment.
     */
    public void setId(String id)
    {
        setId(EnvironmentId.valueOf(id));
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
        if(site != null)
            return String.format("%s-%s",
                getSite().getId().toLowerCase(), getId().code());
        return getId().code();
    }

    /**
     * Returns the url of the environment.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the url for the environment.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns the uri to ping for the environment.
     */
    public String getPing()
    {
        return ping;
    }

    /**
     * Sets the uri to ping for the environment.
     */
    public void setPing(String ping)
    {
        this.ping = ping;
    }

    /**
     * Returns the base path of the environment.
     */
    public String getBase()
    {
        return base;
    }

    /**
     * Sets the base path for the environment.
     */
    public void setBase(String base)
    {
        this.base = base;
    }

    /**
     * Returns the feeds configuration for the environment.
     */
    public FeedsConfig getFeedsConfig()
    {
        return feeds;
    }

    /**
     * Sets the feeds configuration for the environment.
     */
    public void setFeedsConfig(FeedsConfig feeds)
    {
        this.feeds = feeds;
    }

    /**
     * Returns the database configuration for the environment.
     */
    public DatabaseConfig getDatabaseConfig()
    {
        return database;
    }

    /**
     * Sets the database configuration for the environment.
     */
    public void setDatabaseConfig(DatabaseConfig database)
    {
        this.database = database;
    }

    /**
     * Returns the EC2 configuration for the environment.
     */
    public Ec2Config getEc2Config()
    {
        return ec2;
    }

    /**
     * Sets the EC2 configuration for the environment.
     */
    public void setEc2Config(Ec2Config ec2)
    {
        this.ec2 = ec2;
    }

    /**
     * Returns the RDS configuration for the environment.
     */
    public RdsConfig getRdsConfig()
    {
        return rds;
    }

    /**
     * Sets the RDS configuration for the environment.
     */
    public void setRdsConfig(RdsConfig rds)
    {
        this.rds = rds;
    }

    /**
     * Returns the ssh configuration for the environment.
     */
    public SshConfig getSshConfig()
    {
        return ssh;
    }

    /**
     * Sets the ssh configuration for the environment.
     */
    public void setSshConfig(SshConfig ssh)
    {
        this.ssh = ssh;
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
    public static class Builder implements ConfigParser<Environment>
    {
        // The config attribute names
        private static final String URL = "url";
        private static final String PING = "ping";
        private static final String BASE = "base";
        private static final String FEEDS = "feeds";
        private static final String DATABASE = "database";
        private static final String EC2 = "ec2";
        private static final String RDS = "rds";
        private static final String SSH = "ssh";

        private Environment ret = null;

        /**
         * Constructor that takes a name.
         * @param id The id for the environment
         * @param site The site for the environment
         */
        public Builder(EnvironmentId id, Site site)
        {
            ret = new Environment(id, site);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(URL))
                ret.setUrl((String)map.get(URL));
            if(map.containsKey(PING))
                ret.setPing((String)map.get(PING));
            if(map.containsKey(BASE))
                ret.setBase((String)map.get(BASE));

            String name = ret.getName();

            if(map.containsKey(FEEDS))
                ret.setFeedsConfig(FeedsConfig.builder(name)
                    .parse((Map<String,Object>)map.get(FEEDS)).build());

            if(map.containsKey(DATABASE))
                ret.setDatabaseConfig(DatabaseConfig.builder(name)
                    .parse((Map<String,Object>)map.get(DATABASE)).build());

            if(map.containsKey(EC2))
                ret.setEc2Config(Ec2Config.builder(name)
                    .parse((Map<String,Object>)map.get(EC2)).build());

            if(map.containsKey(RDS))
                ret.setRdsConfig(RdsConfig.builder(name)
                    .parse((Map<String,Object>)map.get(RDS)).build());

            if(map.containsKey(SSH))
                ret.setSshConfig(SshConfig.builder(name)
                    .parse((Map<String,Object>)map.get(SSH)).build());

            return this;
        }

        /**
         * Returns the configured environment instance
         * @return The environment instance
         */
        public Environment build()
        {
            return ret;
        }
    }
}