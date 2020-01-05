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
package com.opsmatters.media.model.content;

import com.opsmatters.media.util.FormatUtils;

/**
 * Class representing a project summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProjectSummary extends ResourceSummary
{
    private String repoId = "";
    private RepoProvider provider;
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
            setRepoId(obj.getRepoId());
        }
    }

    /**
     * Returns the repository ID.
     */
    public String getRepoId()
    {
        return repoId;
    }

    /**
     * Sets the repository ID.
     */
    public void setRepoId(String repoId)
    {
        this.repoId = repoId;
    }

    /**
     * Returns <CODE>true</CODE> if the repository ID has been set.
     */
    public boolean hasRepoId()
    {
        return repoId != null && repoId.length() > 0;
    }

    /**
     * Returns the repository provider.
     */
    public RepoProvider getProvider()
    {
        return provider;
    }

    /**
     * Sets the repository provider.
     */
    public void setProvider(RepoProvider provider)
    {
        this.provider = provider;
    }

    /**
     * Returns the url of the repository.
     */
    public String getRepoUrl()
    {
        return provider != null ? String.format(provider.repoUrl(), repoId) : null;
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