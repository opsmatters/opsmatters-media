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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Represents a site containing environments.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Site
{
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String FAVICON = "favicon";
    public static final String THUMBNAIL = "thumbnail";
    public static final String SHORT_DOMAIN = "short-domain";
    public static final String ENABLED = "enabled";
    public static final String S3 = "s3";
    public static final String ENVIRONMENTS = "environments";

    private String id = "";
    private String name = "";
    private String title = "";
    private String favicon = "";
    private String thumbnail = "";
    private String shortDomain = "";
    private boolean enabled = false;
    private S3Settings s3;
    private Map<EnvironmentName,Environment> environments = new LinkedHashMap<EnvironmentName,Environment>();

    /**
     * Constructor that takes an id.
     */
    protected Site(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public Site(Site obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Site obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setName(obj.getName());
            setTitle(obj.getTitle());
            setFavicon(obj.getFavicon());
            setThumbnail(obj.getThumbnail());
            setShortDomain(obj.getShortDomain());
            setEnabled(obj.isEnabled());
            setS3Settings(new S3Settings(obj.getS3Settings()));
            for(Environment environment : obj.getEnvironments().values())
                addEnvironment(new Environment(environment));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public Site(String id, Map<String, Object> map)
    {
        this(id);

        if(map.containsKey(NAME))
            setName((String)map.get(NAME));
        if(map.containsKey(TITLE))
            setTitle((String)map.get(TITLE));
        if(map.containsKey(FAVICON))
            setFavicon((String)map.get(FAVICON));
        if(map.containsKey(THUMBNAIL))
            setThumbnail((String)map.get(THUMBNAIL));
        if(map.containsKey(SHORT_DOMAIN))
            setShortDomain((String)map.get(SHORT_DOMAIN));
        if(map.containsKey(ENABLED))
            setEnabled((Boolean)map.get(ENABLED));
        if(map.containsKey(S3))
            setS3Settings(new S3Settings(id, (Map<String,Object>)map.get(S3)));

        if(map.containsKey(ENVIRONMENTS))
        {
            List<Map<String,Object>> environments = (List<Map<String,Object>>)map.get(ENVIRONMENTS);
            for(Map<String,Object> config : environments)
            {
                for(Map.Entry<String,Object> entry : config.entrySet())
                    addEnvironment(new Environment(entry.getKey(), (Map<String,Object>)entry.getValue()));
            }
        }
    }

    /**
     * Returns the title of the site.
     */
    public String toString()
    {
        return getTitle();
    }

    /**
     * Returns the id of the site.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the site.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the name of the site.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name for the site.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the title of the site.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title for the site.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the site favicon.
     */
    public String getFavicon()
    {
        return favicon;
    }

    /**
     * Sets the site favicon.
     */
    public void setFavicon(String favicon)
    {
        this.favicon = favicon;
    }

    /**
     * Returns the site thumbnail.
     */
    public String getThumbnail()
    {
        return thumbnail;
    }

    /**
     * Sets the site thumbnail.
     */
    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    /**
     * Returns the short domain of the site.
     */
    public String getShortDomain()
    {
        return shortDomain;
    }

    /**
     * Sets the short domain for the site.
     */
    public void setShortDomain(String shortDomain)
    {
        this.shortDomain = shortDomain;
    }

    /**
     * Returns <CODE>true</CODE> if the site is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Set to <CODE>true</CODE> if the site is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Returns the S3 settings for the environment.
     */
    public S3Settings getS3Settings()
    {
        return s3;
    }

    /**
     * Sets the S3 settings for the environment.
     */
    public void setS3Settings(S3Settings s3)
    {
        this.s3 = s3;
    }

    /**
     * Adds a environment to the environments for the site.
     */
    public Map<EnvironmentName,Environment> getEnvironments()
    {
        return this.environments;
    }

    /**
     * Adds a environment to the environments for the site.
     */
    public void addEnvironment(Environment environment)
    {
        this.environments.put(environment.getName(), environment);
    }

    /**
     * Returns the number of environments.
     */
    public int numEnvironments()
    {
        return environments.size();
    }

    /**
     * Returns the environment with the given name.
     */
    public Environment getEnvironment(EnvironmentName name)
    {
        return environments.get(name);
    }
}