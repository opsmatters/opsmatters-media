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

package com.opsmatters.media.model;

/**
 * Represents the environments for a site.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum SiteEnv
{
    STAGE("stage", "Stage"),
    PROD("prod", "Production");

    private String code;
    private String value;

    /**
     * Constructor that takes the env code and value.
     * @param code The code for the env
     * @param value The value for the env
     */
    SiteEnv(String code, String value)
    {
        this.code = code;
        this.value = value;
    }

    /**
     * Returns the value of the env.
     * @return The value of the env.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the env.
     * @return The code of the env.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the env.
     * @return The value of the env.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static SiteEnv fromValue(String value)
    {
        SiteEnv[] types = values();
        for(SiteEnv type : types)
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