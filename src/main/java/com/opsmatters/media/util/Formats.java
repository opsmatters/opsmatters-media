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

package com.opsmatters.media.util;

/**
 * A set of formats for dates and times.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Formats
{
    /**
     * The default date time format.
     */
    public static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    /**
     * The default date time format with timezone.
     */
    public static final String DATETIME_TZ_FORMAT = "dd-MM-yyyy HH:mm:ss z";

    /**
     * The date time format that includes milliseconds.
     */
    public static final String LONG_DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss.SSS";

    /**
     * The date time format that doesn't include seconds.
     */
    public static final String SHORT_DATETIME_FORMAT = "dd-MM-yyyy HH:mm";

    /**
     * The minutes format.
     */
    public static final String MINUTE_FORMAT = "mm:ss";

    /**
     * The minutes format that includes milliseconds.
     */
    public static final String LONG_MINUTE_FORMAT = "mm:ss.SSS";

    /**
     * The hours format that includes milliseconds.
     */
    public static final String LONG_HOUR_FORMAT = "HH:mm:ss.SSS";

    /**
     * The default date format.
     */
    public static final String DATE_FORMAT = "dd-MM-yyyy";

    /**
     * The default time format.
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * The minutes format that doesn't includes seconds.
     */
    public static final String SHORT_TIME_FORMAT = "HH:mm";

    /**
     * The date time format used by shortcuts.
     */
    public static final String SHORTCUT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    /**
     * The date format used by shortcuts.
     */
    public static final String SHORTCUT_DATE_FORMAT = "dd/MM/yyyy";

    /**
     * The date format used by ISO8601.
     */
    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * The date format used for content.
     */
    public static final String CONTENT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * The date format used for a session.
     */
    public static final String SESSION_FORMAT = "yyyyMMdd";
}