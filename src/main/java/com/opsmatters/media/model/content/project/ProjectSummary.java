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

import com.opsmatters.media.util.FormatUtils;

import com.opsmatters.media.model.content.ResourceSummary;

/**
 * Class representing a project summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProjectSummary extends ResourceSummary
{
    private String founded = "";
    private String license = "";

    /**
     * Default constructor.
     */
    public ProjectSummary()
    {
    }

    /**
     * Copy constructor.
     */
    public ProjectSummary(ProjectSummary obj)
    {
        super(obj);

        if(obj != null)
        {
            setFounded(obj.getFounded());
            setLicense(obj.getLicense());
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
}