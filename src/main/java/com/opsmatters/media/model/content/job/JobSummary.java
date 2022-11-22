/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.model.content.job;

import com.opsmatters.media.model.content.ResourceSummary;

/**
 * Class representing a job summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class JobSummary extends ResourceSummary
{
    private String package_ = "";
    private String location = "";

    /**
     * Default constructor.
     */
    public JobSummary()
    {
    }

    /**
     * Copy constructor.
     */
    public JobSummary(JobSummary obj)
    {
        super(obj);

        if(obj != null)
        {
            setUrl(obj.getUrl());
            setPackage(obj.getPackage());
            setLocation(obj.getLocation());
        }
    }

    /**
     * Sets the job url.
     */
    public void setUrl(String url)
    {
        setUrl("", url, true);
    }

    /**
     * Returns the job package.
     */
    public String getPackage()
    {
        return package_;
    }

    /**
     * Sets the job package.
     */
    public void setPackage(String package_)
    {
        this.package_ = package_;
    }

    /**
     * Returns <CODE>true</CODE> if the job package has been set.
     */
    public boolean hasPackage()
    {
        return package_ != null && package_.length() > 0;
    }

    /**
     * Returns the job location.
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Sets the job location.
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * Returns <CODE>true</CODE> if the job location has been set.
     */
    public boolean hasLocation()
    {
        return location != null && location.length() > 0;
    }
}