/*
 * Copyright 2019 Gerald Curley
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
package com.opsmatters.media.model.content.project;

import com.opsmatters.media.model.content.ResourceDetails;

/**
 * Class representing a project.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProjectDetails extends ResourceDetails
{
    private String founded = "";
    private String license = "";
    private String badges = "";
    private String links = "";
    private String website = "";

    /**
     * Default constructor.
     */
    public ProjectDetails()
    {
    }

    /**
     * Copy constructor.
     */
    public ProjectDetails(ProjectDetails obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ProjectDetails obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setFounded(obj.getFounded());
            setLicense(obj.getLicense());
            setBadges(obj.getBadges());
            setLinks(obj.getLinks());
            setWebsite(obj.getWebsite());
        }
    }

    /**
     * Returns the project license.
     */
    public String getLicense()
    {
        return license;
    }

    /**
     * Sets the project license.
     */
    public void setLicense(String license)
    {
        this.license = license;
    }

    /**
     * Returns the project founded year.
     */
    public String getFounded()
    {
        return founded;
    }

    /**
     * Sets the project founded year.
     */
    public void setFounded(String founded)
    {
        this.founded = founded;
    }

    /**
     * Returns the project badges.
     */
    public String getBadges()
    {
        return badges;
    }

    /**
     * Sets the project badges.
     */
    public void setBadges(String badges)
    {
        this.badges = badges;
    }

    /**
     * Returns the project links.
     */
    public String getLinks()
    {
        return links;
    }

    /**
     * Sets the project links.
     */
    public void setLinks(String links)
    {
        this.links = links;
    }

    /**
     * Returns <CODE>true</CODE> if the project links have been set.
     */
    public boolean hasLinks()
    {
        return links != null && links.length() > 0;
    }

    /**
     * Returns the project website.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * Sets the project website.
     */
    public void setWebsite(String website)
    {
        this.website = website;
    }

    /**
     * Returns <CODE>true</CODE> if the project website has been set.
     */
    public boolean hasWebsite()
    {
        return website != null && website.length() > 0;
    }
}