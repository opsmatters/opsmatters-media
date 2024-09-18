/*
 * Copyright 2024 Gerald Curley
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

import com.opsmatters.media.model.platform.PlatformConfig;

/**
 * Represents a configuration type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ConfigType
{
    CONTENT("Content", "content",
        PlatformConfig.getS3Config().getContentConfigBucket()),
    CHART("Chart", "chart",
        PlatformConfig.getS3Config().getChartConfigBucket()),
    PLATFORM("Platform", "platform",
        PlatformConfig.getS3Config().getPlatformConfigBucket());

    private String value;
    private String tag;
    private String bucket;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     * @param tag The tag for the type
     * @param bucket The S3 bucket for the type
     */
    ConfigType(String value, String tag, String bucket)
    {
        this.value = value;
        this.tag = tag;
        this.bucket = bucket;
    }

    /**
     * Returns the value of the type.
     * @return The value of the type.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the type.
     * @return The value of the type.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the tag of the type.
     * @return The tag of the type.
     */
    public String tag()
    {
        return tag;
    }

    /**
     * Returns the S3 bucket of the type.
     * @return The S3 bucket of the type.
     */
    public String bucket()
    {
        return bucket;
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