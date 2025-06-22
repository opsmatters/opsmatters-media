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

package com.opsmatters.media.model.provider;

/**
 * Represents a repository provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum RepositoryProviderId
{
    GITHUB("GTH", "GitHub", "https://github.com"),
    GITLAB("GTL", "GitLab", "https://gitlab.com");

    private String code;
    private String value;
    private String url;

    /**
     * Constructor that takes the code and name.
     * @param code The code for the provider
     * @param value The value of the provider
     * @param url The base URL for the provider
     */
    RepositoryProviderId(String code, String value, String url)
    {
        this.code = code;
        this.value = value;
        this.url = url;
    }

    /**
     * Returns the value of the provider.
     * @return The value of the provider.
     */
    public String toString()
    {
        return value();
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
     * Returns the value of the provider.
     * @return The value of the provider.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the base URL.
     * @return The base URL.
     */
    public String url()
    {
        return url;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static RepositoryProviderId fromCode(String code)
    {
        RepositoryProviderId[] types = values();
        for(RepositoryProviderId type : types)
        {
            if(type.code().equals(code))
                return type;
        }

        return null;
    }

    /**
     * Returns the type for the given url.
     * @param url The url
     * @return The type for the given url
     */
    public static RepositoryProviderId fromUrl(String url)
    {
        RepositoryProviderId[] types = values();
        for(RepositoryProviderId type : types)
        {
            if(url.startsWith(type.url()))
                return type;
        }

        return null;
    }

    /**
     * Returns the user for the given url.
     * @param url The url
     * @return The user for the given url
     */
    public String getRepoUser(String url)
    {
        String ret = url.substring(this.url.length()+1);
        int pos = ret.indexOf("/");
        if(pos != -1)
            ret = ret.substring(0, pos);
        return ret;
    }

    /**
     * Returns the repo name for the given url.
     * @param url The url
     * @return The repo name for the given url
     */
    public String getRepoName(String url)
    {
        String ret = url;
        int pos = url.lastIndexOf("/");
        if(pos != -1)
            ret = ret.substring(pos+1);
        return ret;
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