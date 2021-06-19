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
import com.opsmatters.media.model.platform.aws.EC2Settings;
import com.opsmatters.media.model.platform.aws.RDSSettings;

/**
 * Represents a site environment.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Environment implements java.io.Serializable
{
    public static final String NAME = "name";
    public static final String KEY = "key";
    public static final String URL = "url";
    public static final String PING = "ping";
    public static final String PATH = "path";
    public static final String FEEDS = "feeds";
    public static final String DATABASE = "database";
    public static final String EC2 = "ec2";
    public static final String RDS = "rds";
    public static final String SSH = "ssh";

    private EnvironmentName name;
    private String key = "";
    private String url = "";
    private String ping = "";
    private String path = "";
    private FeedsSettings feeds;
    private DatabaseSettings database;
    private EC2Settings ec2;
    private RDSSettings rds;
    private SshSettings ssh;

    /**
     * Constructor that takes a name.
     */
    protected Environment(String name)
    {
        setName(name);
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
            setName(obj.getName());
            setKey(obj.getKey());
            setUrl(obj.getUrl());
            setPing(obj.getPing());
            setPath(obj.getPath());
            setFeedsSettings(new FeedsSettings(obj.getFeedsSettings()));
            setDatabaseSettings(new DatabaseSettings(obj.getDatabaseSettings()));
            setEC2Settings(new EC2Settings(obj.getEC2Settings()));
            setRDSSettings(new RDSSettings(obj.getRDSSettings()));
            setSshSettings(new SshSettings(obj.getSshSettings()));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public Environment(String name, Map<String, Object> map)
    {
        this(name);

        if(map.containsKey(KEY))
            setKey((String)map.get(KEY));
        if(map.containsKey(URL))
            setUrl((String)map.get(URL));
        if(map.containsKey(PING))
            setPing((String)map.get(PING));
        if(map.containsKey(PATH))
            setPath((String)map.get(PATH));
        if(map.containsKey(FEEDS))
            setFeedsSettings(new FeedsSettings(name, (Map<String,Object>)map.get(FEEDS)));
        if(map.containsKey(DATABASE))
            setDatabaseSettings(new DatabaseSettings(name, (Map<String,Object>)map.get(DATABASE)));
        if(map.containsKey(EC2))
            setEC2Settings(new EC2Settings(name, (Map<String,Object>)map.get(EC2)));
        if(map.containsKey(RDS))
            setRDSSettings(new RDSSettings(name, (Map<String,Object>)map.get(RDS)));
        if(map.containsKey(SSH))
            setSshSettings(new SshSettings(name, (Map<String,Object>)map.get(SSH)));
    }

    /**
     * Returns the name of the environment.
     */
    public String toString()
    {
        return getName().name();
    }

    /**
     * Returns the name of the environment.
     */
    public EnvironmentName getName()
    {
        return name;
    }

    /**
     * Sets the name for the environment.
     */
    public void setName(EnvironmentName name)
    {
        this.name = name;
    }

    /**
     * Sets the name for the environment.
     */
    public void setName(String name)
    {
        setName(EnvironmentName.valueOf(name));
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
     * Returns the feeds settings for the environment.
     */
    public FeedsSettings getFeedsSettings()
    {
        return feeds;
    }

    /**
     * Sets the feeds settings for the environment.
     */
    public void setFeedsSettings(FeedsSettings feeds)
    {
        this.feeds = feeds;
    }

    /**
     * Returns the database settings for the environment.
     */
    public DatabaseSettings getDatabaseSettings()
    {
        return database;
    }

    /**
     * Sets the database settings for the environment.
     */
    public void setDatabaseSettings(DatabaseSettings database)
    {
        this.database = database;
    }

    /**
     * Returns the EC2 settings for the environment.
     */
    public EC2Settings getEC2Settings()
    {
        return ec2;
    }

    /**
     * Sets the EC2 settings for the environment.
     */
    public void setEC2Settings(EC2Settings ec2)
    {
        this.ec2 = ec2;
    }

    /**
     * Returns the RDS settings for the environment.
     */
    public RDSSettings getRDSSettings()
    {
        return rds;
    }

    /**
     * Sets the RDS settings for the environment.
     */
    public void setRDSSettings(RDSSettings rds)
    {
        this.rds = rds;
    }

    /**
     * Returns the ssh settings for the environment.
     */
    public SshSettings getSshSettings()
    {
        return ssh;
    }

    /**
     * Sets the ssh settings for the environment.
     */
    public void setSshSettings(SshSettings ssh)
    {
        this.ssh = ssh;
    }
}