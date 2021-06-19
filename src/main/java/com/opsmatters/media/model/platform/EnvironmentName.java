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
public enum EnvironmentName
{
    APP_PROD("app-prod", "App Prod", true),
    APP_STAGE("app-stage", "App Stage", true),
    IMAGE("image", "Image", true),
    DIRECTOR("director", "Director", true),
    PUBLISHER("publisher", "Publisher", true),
    STAGE("stage", "Stage", false),
    PROD("prod", "Production", false);

    private String code;
    private String value;
    private boolean internal;

    /**
     * Constructor that takes the environment code and value.
     * @param code The code for the environment
     * @param value The value for the environment
     * @param internal <CODE>true</CODE> if this is an internal environment
     */
    EnvironmentName(String code, String value, boolean internal)
    {
        this.code = code;
        this.value = value;
        this.internal = internal;
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
     * Returns <CODE>true</CODE> if this is an internal environment.
     * @return <CODE>true</CODE> if this is an internal environment.
     */
    public boolean internal()
    {
        return internal;
    }

    /**
     * Returns <CODE>true</CODE> if this is a drupal environment.
     * @return <CODE>true</CODE> if this is a drupal environment.
     */
    public boolean drupal()
    {
        return !internal;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
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
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}