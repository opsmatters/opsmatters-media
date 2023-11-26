/*
 * Copyright 2023 Gerald Curley
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

import com.opsmatters.media.model.content.ResourceItem;

/**
 * Class representing a job list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class JobItem extends ResourceItem<Job>
{
    private Job content = new Job();

    /**
     * Default constructor.
     */
    public JobItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public JobItem(JobItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a job.
     */
    public JobItem(Job obj)
    {
        super.set(content);
        copyAttributes(obj);
        content.setConfigured(false);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(JobItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(Job obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public Job get()
    {
        return content;
    }

    /**
     * Returns the job location.
     */
    public String getLocation()
    {
        return content.getLocation();
    }

    /**
     * Sets the job location.
     */
    public void setLocation(String location)
    {
        content.setLocation(location);
    }

    /**
     * Returns the job package.
     */
    public String getPackage()
    {
        return content.getPackage();
    }

    /**
     * Sets the job package.
     */
    public void setPackage(String package_)
    {
        content.setPackage(package_);
    }
}