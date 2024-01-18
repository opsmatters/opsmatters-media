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

/**
 * Represents the environments for a platform.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum EnvironmentId
{
    DIRECTOR("director", "Director", false),
    PUBLISHER("publisher", "Publisher", false),
    IMAGE("image", "Image", false),
    LOCAL("local", "Local", false),
    STAGE_DB("app-stage", "Stage", false),
    PROD_DB("app-prod", "Production", false),
    STAGE("stage", "Stage", true),
    PROD("prod", "Production", true);

    private String code;
    private String value;
    private boolean site;

    /**
     * Constructor that takes the environment code and value.
     * @param code The code for the environment
     * @param value The value for the environment
     * @param site <CODE>true</CODE> if the environment is associated with a site
     */
    EnvironmentId(String code, String value, boolean site)
    {
        this.code = code;
        this.value = value;
        this.site = site;
    }

    /**
     * Returns the value of the environment.
     * @return The value of the environment.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the environment.
     * @return The code of the environment.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the environment.
     * @return The value of the environment.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns <CODE>true</CODE> if the environment is associated with a site.
     * @return <CODE>true</CODE> if the environment is associated with a site.
     */
    public boolean site()
    {
        return site;
    }

    /**
     * Returns the environment name for the given value.
     * @param value The environment value
     * @return The environment name for the given value
     */
    public static EnvironmentId fromValue(String value)
    {
        EnvironmentId[] types = values();
        for(EnvironmentId type : types)
        {
            if(type.value().equals(value))
                return type;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of environment names.
     * @param value The environment value
     * @return <CODE>true</CODE> if the given value is contained in the list of environment names
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}