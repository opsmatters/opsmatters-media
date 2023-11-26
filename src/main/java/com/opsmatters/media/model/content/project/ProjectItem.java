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
package com.opsmatters.media.model.content.project;

import com.opsmatters.media.model.content.ResourceItem;

/**
 * Class representing a project list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProjectItem extends ResourceItem<Project>
{
    private Project content = new Project();

    /**
     * Default constructor.
     */
    public ProjectItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public ProjectItem(ProjectItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a project.
     */
    public ProjectItem(Project obj)
    {
        super.set(content);
        copyAttributes(obj);
        content.setConfigured(false);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ProjectItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(Project obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public Project get()
    {
        return content;
    }

    /**
     * Returns the provider of the project.
     */
    public RepositoryProvider getProvider()
    {
        return content.getProvider();
    }

    /**
     * Returns the project URL.
     */
    public String getUrl()
    {
        return content.getUrl();
    }

    /**
     * Sets the project URL.
     */
    public void setUrl(String url)
    {
        content.setUrl(url);
    }

    /**
     * Returns the project license.
     */
    public String getLicense()
    {
        return content.getLicense();
    }

    /**
     * Sets the project license.
     */
    public void setLicense(OpenSourceLicense license)
    {
        content.setLicense(license);
    }

    /**
     * Sets the project license.
     */
    public void setLicense(String license)
    {
        content.setLicense(license);
    }
}