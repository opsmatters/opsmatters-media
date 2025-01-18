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
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.platform.aws.S3Config;

/**
 * Represents a site containing environments.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Site implements ConfigElement
{
    private String id = "";
    private String name = "";
    private String title = "";
    private String icon = "";
    private String shortDomain = "";
    private boolean enabled = false;
    private S3Config s3;
    private NewsletterConfig newsletter;
    private Map<EnvironmentId,SiteEnvironment> environments = new LinkedHashMap<EnvironmentId,SiteEnvironment>();

    /**
     * Constructor that takes an id.
     */
    public Site(String id)
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
            setIcon(obj.getIcon());
            setShortDomain(obj.getShortDomain());
            setEnabled(obj.isEnabled());
            setNewsletterConfig(new NewsletterConfig(obj.getNewsletterConfig()));
            setS3Config(new S3Config(obj.getS3Config()));
            for(SiteEnvironment environment : obj.getEnvironments().values())
                addEnvironment(new SiteEnvironment(environment));
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
     * Returns the site icon.
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Sets the site icon.
     */
    public void setIcon(String icon)
    {
        this.icon = icon;
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
     * Returns the newsletter configuration for the site.
     */
    public NewsletterConfig getNewsletterConfig()
    {
        return newsletter;
    }

    /**
     * Sets the newsletter configuration for the site.
     */
    public void setNewsletterConfig(NewsletterConfig newsletter)
    {
        this.newsletter = newsletter;
    }

    /**
     * Returns the S3 configuration for the site.
     */
    public S3Config getS3Config()
    {
        return s3;
    }

    /**
     * Sets the S3 configuration for the site.
     */
    public void setS3Config(S3Config s3)
    {
        this.s3 = s3;
    }

    /**
     * Adds a environment to the environments for the site.
     */
    public Map<EnvironmentId,SiteEnvironment> getEnvironments()
    {
        return this.environments;
    }

    /**
     * Adds an environment to the environments for the site.
     */
    public void addEnvironment(SiteEnvironment environment)
    {
        this.environments.put(environment.getId(), environment);
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
    public SiteEnvironment getEnvironment(EnvironmentId id)
    {
        return environments.get(id);
    }

    /**
     * Returns a builder for the site.
     * @param name The name of the site
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make site construction easier.
     */
    public static class Builder implements ConfigParser<Site>
    {
        // The config attribute names
        private static final String ID = "id";
        private static final String NAME = "name";
        private static final String TITLE = "title";
        private static final String ICON = "icon";
        private static final String SHORT_DOMAIN = "short-domain";
        private static final String ENABLED = "enabled";
        private static final String S3 = "s3";
        private static final String NEWSLETTER = "newsletter";
        private static final String ENVIRONMENTS = "environments";

        private Site ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the site
         */
        public Builder(String id)
        {
            ret = new Site(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(NAME))
                ret.setName((String)map.get(NAME));
            if(map.containsKey(TITLE))
                ret.setTitle((String)map.get(TITLE));
            if(map.containsKey(ICON))
                ret.setIcon((String)map.get(ICON));
            if(map.containsKey(SHORT_DOMAIN))
                ret.setShortDomain((String)map.get(SHORT_DOMAIN));
            if(map.containsKey(ENABLED))
                ret.setEnabled((Boolean)map.get(ENABLED));

            String id = ret.getId();

            if(map.containsKey(NEWSLETTER))
                ret.setNewsletterConfig(NewsletterConfig.builder(id)
                    .parse((Map<String,Object>)map.get(NEWSLETTER)).build());

            if(map.containsKey(S3))
                ret.setS3Config(S3Config.builder(id)
                    .parse((Map<String,Object>)map.get(S3)).build());

            if(map.containsKey(ENVIRONMENTS))
            {
                List<Map<String,Object>> environments = (List<Map<String,Object>>)map.get(ENVIRONMENTS);
                for(Map<String,Object> config : environments)
                {
                    for(Map.Entry<String,Object> entry : config.entrySet())
                    {
                        ret.addEnvironment(SiteEnvironment.builder(entry.getKey(), ret)
                            .parse((Map<String,Object>)entry.getValue()).build());
                    }
                }
            }

            return this;
        }

        /**
         * Returns the configured site instance
         * @return The site instance
         */
        public Site build()
        {
            return ret;
        }
    }
}