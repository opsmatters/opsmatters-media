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
 * Represents the configuration of an environment.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Environment implements ConfigElement
{
    private EnvironmentId id;
    private String url = "";
    private String base = "";
    private DatabaseConfig database;
    private Ec2Config ec2;
    private RdsConfig rds;
    private SshConfig ssh;

    /**
     * Default constructor.
     */
    protected Environment()
    {
    }

    /**
     * Constructor that takes an id.
     */
    protected Environment(EnvironmentId id)
    {
        setId(id);
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
            setUrl(obj.getUrl());
            setBase(obj.getBase());
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
        return id.value();
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
     * Returns the key of the environment.
     */
    public String getKey()
    {
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
     * Returns a builder for the environment.
     * @param id The id of the environment
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(EnvironmentId.valueOf(id));
    }

    /**
     * Builder to make environment construction easier.
     */
    public static class Builder implements ConfigParser<Environment>
    {
        // The config attribute names
        private static final String URL = "url";
        private static final String BASE = "base";
        private static final String DATABASE = "database";
        private static final String EC2 = "ec2";
        private static final String RDS = "rds";
        private static final String SSH = "ssh";

        private Environment ret = null;

        /**
         * Default constructor.
         */
        public Builder()
        {
        }

        /**
         * Constructor that takes a name.
         * @param id The id for the environment
         */
        public Builder(EnvironmentId id)
        {
            ret = new Environment(id);
        }

        /**
         * Sets the environment.
         * @param environment The environment
         */
        protected void set(Environment environment)
        {
            ret = environment;
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
            if(map.containsKey(BASE))
                ret.setBase((String)map.get(BASE));

            String name = ret.getName();

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