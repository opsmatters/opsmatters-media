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

package com.opsmatters.media.model.system;

import java.util.Map;
import java.util.LinkedHashMap;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import com.opsmatters.media.model.system.aws.S3Config;
import com.opsmatters.media.model.BaseEntity;

/**
 * Represents a site containing environments.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Site extends BaseEntity
{
    private String name = "";
    private String icon = "";
    private String domain = "";
    private String shortDomain = "";
    private int newsletterDay = -1;
    private int newsletterHour = -1;
    private boolean enabled = false;

    private S3Config s3;
    private Map<EnvironmentId,SiteEnvironment> environments = new LinkedHashMap<EnvironmentId,SiteEnvironment>();

    /**
     * Default constructor.
     */
    public Site()
    {
    }

    /**
     * Constructor that takes an id.
     */
    public Site(String id)
    {
        setId(id);
        setCreatedDate(Instant.now());
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
            super.copyAttributes(obj);
            setName(obj.getName());
            setIcon(obj.getIcon());
            setDomain(obj.getDomain());
            setShortDomain(obj.getShortDomain());
            setNewsletterDay(obj.getNewsletterDay());
            setNewsletterHour(obj.getNewsletterHour());
            setEnabled(obj.isEnabled());
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
        return getName();
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
     * Returns the domain of the site.
     */
    public String getDomain()
    {
        return domain;
    }

    /**
     * Sets the domain for the site.
     */
    public void setDomain(String domain)
    {
        this.domain = domain;
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
     * Returns the newsletter day for the site.
     */
    public int getNewsletterDay()
    {
        return newsletterDay;
    }

    /**
     * Sets the newsletter day for the site.
     */
    public void setNewsletterDay(int newsletterDay)
    {
        this.newsletterDay = newsletterDay;
    }

    /**
     * Returns the newsletter hour for the site.
     */
    public int getNewsletterHour()
    {
        return newsletterHour;
    }

    /**
     * Sets the newsletter hour for the site.
     */
    public void setNewsletterHour(int newsletterHour)
    {
        this.newsletterHour = newsletterHour;
    }

    /**
     * Returns the next from date for the newsletter settings.
     */
    public LocalDateTime getNewsletterFromDate()
    {
        LocalDateTime ret = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
            .withHour(newsletterHour).withMinute(0).withSecond(0);
        LocalDateTime next = ret.with(ChronoField.DAY_OF_WEEK, newsletterDay)
            .withHour(newsletterHour).withMinute(0).withSecond(0);
        if(next.equals(ret) || next.isAfter(ret))
            ret = ret.minusDays(7); // If the from date is in the future, go back a week
        return ret.with(ChronoField.DAY_OF_WEEK, newsletterDay);
    }

    /**
     * Returns <CODE>true</CODE> if the site is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Returns <CODE>true</CODE> if this site is enabled.
     */
    public Boolean getEnabledObject()
    {
        return Boolean.valueOf(isEnabled());
    }

    /**
     * Set to <CODE>true</CODE> if the site is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Set to <CODE>true</CODE> if this site is enabled.
     */
    public void setEnabledObject(Boolean enabled)
    {
        setEnabled(enabled != null && enabled.booleanValue());
    }

    /**
     * Sets the configuration for the site.
     */
    public void setConfig(SiteConfig config)
    {
        setS3Config(config.getS3Config());
        setEnvironments(config.getEnvironments());
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
     * Sets the environments for the site.
     */
    public void setEnvironments(Map<EnvironmentId,SiteEnvironment> environments)
    {
        this.environments.clear();
        this.environments.putAll(environments);
    }
}