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
    private EnvironmentName name;
    private String key = "";
    private String url = "";
    private String ping = "";
    private String path = "";
    private FeedsConfig feeds;
    private DatabaseConfig database;
    private Ec2Config ec2;
    private RdsConfig rds;
    private SshConfig ssh;
    private String status = "";
    private String dashboard = "";

    /**
     * Constructor that takes a name.
     */
    protected Environment(EnvironmentName name)
    {
        setEnvironmentName(name);
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
            setEnvironmentName(obj.getEnvironmentName());
            setKey(obj.getKey());
            setUrl(obj.getUrl());
            setPing(obj.getPing());
            setPath(obj.getPath());
            setFeedsConfig(new FeedsConfig(obj.getFeedsConfig()));
            setDatabaseConfig(new DatabaseConfig(obj.getDatabaseConfig()));
            setEc2Config(new Ec2Config(obj.getEc2Config()));
            setRdsConfig(new RdsConfig(obj.getRdsConfig()));
            setSshConfig(new SshConfig(obj.getSshConfig()));
            setStatus(obj.getStatus());
            setDashboard(obj.getDashboard());
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
     * Returns the name of the environment.
     */
    public String getName()
    {
        return getEnvironmentName().name();
    }

    /**
     * Returns the name of the environment.
     */
    public EnvironmentName getEnvironmentName()
    {
        return name;
    }

    /**
     * Sets the name for the environment.
     */
    public void setEnvironmentName(EnvironmentName name)
    {
        this.name = name;
    }

    /**
     * Sets the name for the environment.
     */
    public void setEnvironmentName(String name)
    {
        setEnvironmentName(EnvironmentName.valueOf(name));
    }

    /**
     * Returns the key of the environment.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the key for the environment.
     */
    public void setKey(String key)
    {
        this.key = key;
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
     * Returns the path of the environment.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the path for the environment.
     */
    public void setPath(String path)
    {
        this.path = path;
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
     * Returns the status dashboard of the environment.
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Sets the status dashboard for the environment.
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * Returns the default dashboard of the environment.
     */
    public String getDashboard()
    {
        return dashboard;
    }

    /**
     * Sets the default dashboard for the environment.
     */
    public void setDashboard(String dashboard)
    {
        this.dashboard = dashboard;
    }

    /**
     * Returns a builder for the environment.
     * @param name The name of the environment
     * @param site The site of the environment
     * @return The builder instance.
     */
    public static Builder builder(String name, Site site)
    {
        return new Builder(EnvironmentName.valueOf(name), site);
    }

    /**
     * Builder to make environment construction easier.
     */
    public static class Builder implements ConfigParser<Environment>
    {
        // The config attribute names
        private static final String URL = "url";
        private static final String PING = "ping";
        private static final String PATH = "path";
        private static final String FEEDS = "feeds";
        private static final String DATABASE = "database";
        private static final String EC2 = "ec2";
        private static final String RDS = "rds";
        private static final String SSH = "ssh";
        private static final String STATUS = "status";
        private static final String DASHBOARD = "dashboard";

        private Environment ret = null;

        /**
         * Constructor that takes a name.
         * @param name The name for the environment
         * @param site The site for the environment
         */
        public Builder(EnvironmentName name, Site site)
        {
            ret = new Environment(name);

            // Generate environment key
            String key = name.code();
            if(site != null)
            {
                key = String.format("%s-%s",
                    site.getId().toLowerCase(), name.code());
            }

            ret.setKey(key);
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
            if(map.containsKey(PATH))
                ret.setPath((String)map.get(PATH));
            if(map.containsKey(STATUS))
                ret.setStatus((String)map.get(STATUS));
            if(map.containsKey(DASHBOARD))
                ret.setDashboard((String)map.get(DASHBOARD));

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