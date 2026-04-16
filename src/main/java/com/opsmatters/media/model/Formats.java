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

package com.opsmatters.media.model;

/**
 * A set of formats for dates and times.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Formats
{
    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private Formats()
    {
    }    

    /**
     * The default date format.
     */
    public static final String DATE = "dd-MM-yyyy";

    /**
     * The date format used by order items.
     */
    public static final String ORDER_DATE = "dd/MMM/yyyy";

    /**
     * The default date time format.
     */
    public static final String DATETIME = "dd-MM-yyyy HH:mm:ss";

    /**
     * The default date time format with timezone.
     */
    public static final String DATETIME_TZ = "dd-MM-yyyy HH:mm:ss z";

    /**
     * The date time format that includes milliseconds.
     */
    public static final String LONG_DATETIME = "dd-MM-yyyy HH:mm:ss.SSS";

    /**
     * The date time format that doesn't include seconds.
     */
    public static final String SHORT_DATETIME = "dd-MM-yyyy HH:mm";

    /**
     * The date time format used for content.
     */
    public static final String CONTENT_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * The default time format.
     */
    public static final String TIME = "HH:mm:ss";

    /**
     * The time format that includes milliseconds.
     */
    public static final String LONG_TIME = "HH:mm:ss.SSS";

    /**
     * The time format that doesn't includes seconds.
     */
    public static final String SHORT_TIME = "HH:mm";

    /**
     * The minutes format.
     */
    public static final String MINUTE = "mm:ss";

    /**
     * The minutes format that includes milliseconds.
     */
    public static final String LONG_MINUTE = "mm:ss.SSS";

    /**
     * The date format used by ISO8601.
     */
    public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * The long date format used by ISO8601.
     */
    public static final String LONG_ISO8601 = "yyyy-MM-dd'T'HH:mm:ssX";

    /**
     * The date format used for the session id.
     */
    public static final String SESSION_ID = "yyyyMMdd";
}