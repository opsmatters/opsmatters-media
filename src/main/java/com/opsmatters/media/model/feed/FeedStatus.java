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

package com.opsmatters.media.model.feed;

/**
 * Represents the status of a feed.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum FeedStatus
{
    NEW("New", "glyphicon-unchecked", "", 1),
    PENDING("Pending", "glyphicon-check", "status-warn", -1),
    SUBMITTED("Submitted", "glyphicon-log-in", "status-warn", -1),
    DEPLOYING("Deploying", "glyphicon-log-out", "status-warn", -1),
    EXECUTING("Executing", "glyphicon-cog", "status-warn", -1),
    ERROR("Error", "glyphicon-exclamation-sign", "status-error", 1),
    COMPLETED("Completed", "glyphicon-ok-circle", "status-success", 1),
    WAITING("Waiting", "", "", 0), // Pseudo status
    PROCESSED("Processed", "", "", 0), // Pseudo status
    ALL("All", "", "", 0); // Pseudo status

    private String value;
    private String icon;
    private String css;
    private int state;

    /**
     * Constructor that takes the status value.
     * @param value The value for the status
     * @param icon The glyphicon for the status
     * @param css The css class for the status
     * @param state The state for the status
     */
    FeedStatus(String value, String icon, String css, int state)
    {
        this.value = value;
        this.icon = icon;
        this.css = css;
        this.state = state;
    }

    /**
     * Returns <CODE>true<CODE> if the feed state is WAITING.
     * @return <CODE>true<CODE> if the feed state is WAITING.
     */
    public boolean isWaiting()
    {
        return state < 0;
    }

    /**
     * Returns <CODE>true<CODE> if the feed state is PROCESSED.
     * @return <CODE>true<CODE> if the feed state is PROCESSED.
     */
    public boolean isProcessed()
    {
        return state > 0;
    }

    /**
     * Returns the value of the status.
     * @return The value of the status.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the status.
     * @return The value of the status.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the glyphicon of the status.
     * @return The glyphicon of the status.
     */
    public String icon()
    {
        return icon;
    }

    /**
     * Returns the css class of the status.
     * @return The css class of the status.
     */
    public String css()
    {
        return css;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static FeedStatus fromValue(String value)
    {
        FeedStatus[] types = values();
        for(FeedStatus type : types)
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