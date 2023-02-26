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

import static com.opsmatters.media.model.platform.EnvironmentType.*;

/**
 * Represents the environments for a platform.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum EnvironmentName
{
    DIRECTOR("director", "Director", APP),
    PUBLISHER("publisher", "Publisher", APP),
    IMAGE("image", "Image", APP),
    LOCAL("local", "Local", APP),
    STAGE_DB("app-stage", "Stage", APP),
    PROD_DB("app-prod", "Production", APP),
    STAGE("stage", "Stage", SITE),
    PROD("prod", "Production", SITE);

    private String code;
    private String value;
    private EnvironmentType type;

    /**
     * Constructor that takes the environment code and value.
     * @param code The code for the environment
     * @param value The value for the environment
     * @param type The type of the environment
     */
    EnvironmentName(String code, String value, EnvironmentType type)
    {
        this.code = code;
        this.value = value;
        this.type = type;
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
     * Returns The type of the environment.
     * @return The type of the environment.
     */
    public EnvironmentType type()
    {
        return type;
    }

    /**
     * Returns <CODE>true</CODE> if this is a APP environment.
     * @return <CODE>true</CODE> if this is a APP environment.
     */
    public boolean app()
    {
        return type == APP;
    }

    /**
     * Returns <CODE>true</CODE> if this is a SITE environment.
     * @return <CODE>true</CODE> if this is a SITE environment.
     */
    public boolean site()
    {
        return type == SITE;
    }

    /**
     * Returns the environment name for the given value.
     * @param value The environment value
     * @return The environment name for the given value
     */
    public static EnvironmentName fromValue(String value)
    {
        EnvironmentName[] types = values();
        for(EnvironmentName type : types)
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