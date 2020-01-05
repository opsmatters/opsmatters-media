/*
 * Copyright 2018 Gerald Curley
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

/**
 * Represents a repository provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum RepoProvider
{
    GITHUB("github", "GitHub", "https://github.com/%s"),
    GITLAB("gitlab", "GitLab", "https://gitlab.com/%s");

    private String code;
    private String displayName;
    private String repoUrl;

    /**
     * Constructor that takes the code and name.
     * @param code The code for the provider
     * @param displayName The display name of the provider
     * @param repoUrl The repository URL template for the provider
     */
    RepoProvider(String code, String displayName, String repoUrl)
    {
        this.code = code;
        this.displayName = displayName;
        this.repoUrl = repoUrl;
    }

    /**
     * Returns the display name of the provider.
     * @return The display name of the provider.
     */
    public String toString()
    {
        return displayName();
    }

    /**
     * Returns the code of the provider.
     * @return The code of the provider.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the display name of the provider.
     * @return The display name of the provider.
     */
    public String displayName()
    {
        return displayName;
    }

    /**
     * Returns the repository URL template.
     * @return The repository URL template.
     */
    public String repoUrl()
    {
        return repoUrl;
    }

    /**
     * Returns the repo id from the given repo URL.
     * @return The repo id.
     */
    public String getRepoId(String repoUrl)
    {
        String ret = null;
        if(repoUrl != null)
        {
            int pos = repoUrl.indexOf(code);
            ret = repoUrl.substring(repoUrl.indexOf("/", pos)+1);
        }
        return ret;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static RepoProvider fromCode(String code)
    {
        RepoProvider[] types = values();
        for(RepoProvider type : types)
        {
            if(type.code().equals(code))
                return type;
        }
        return null;
    }

    /**
     * Returns the type for the given repo url.
     * @param repoUrl The repo url
     * @return The type for the given repo url
     */
    public static RepoProvider fromRepoUrl(String repoUrl)
    {
        RepoProvider[] types = values();
        for(RepoProvider type : types)
        {
            if(repoUrl.indexOf(type.code()) != -1)
                return type;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given code is contained in the list of types.
     * @param code The type code
     * @return <CODE>true</CODE> if the given code is contained in the list of types
     */
    public static boolean contains(String code)
    {
        return fromCode(code) != null;
    }
}