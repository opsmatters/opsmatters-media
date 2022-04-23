/*
 * Copyright 2020 Gerald Curley
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

package com.opsmatters.media.model.admin;

/**
 * Represents the name of an application parameter.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum AppParameterName
{
    DRAFT_POST_INTERVAL("draft-post-interval"),
    PREPARED_POST_INTERVAL("prepared-post-interval"),
    INIT_DELAY("init-delay"),
    BATCH_DELAY("batch-delay"),
    ITEM_DELAY("item-delay"),
    STALLED_INTERVAL("stalled-interval"),
    SCAN_INTERVAL("scan-interval"),
    REVIEW_INTERVAL("review-interval"),
    MIN_AGE("min-age"),
    MAX_BATCH_SIZE("max-batch-size"),
    MAX_WAITING("max-waiting"),
    MAX_ERRORS("max-errors"),
    MAX_RESULTS("max-results"),
    MAX_RETRIES("max-retries"),
    MAX_CHANGED("max-changed"),
    MAX_REVIEW("max-review"),
    SUB_LEASE("sub-lease"),
    SUB_EXPIRY("sub-expiry"),
    PUBLISHER("publisher"),
    DIRECTOR("director"),
    STATUS("status"),
    SESSION("session"),
    CONFIG_CHANGED("config-changed"),
    CONFIG_UPDATED("config-updated"),
    ORGANISATION_CHANGED("organisation-changed"),
    ORGANISATION_UPDATED("organisation-updated"),
    ERROR_COUNT_ERROR("error-count-error"),
    CHANGED_COUNT_ERROR("changed-count-error"),
    REVIEW_COUNT_ERROR("review-count-error"),
    STALLED_ERROR("stalled-error"),
    MIN_WEBINAR_DURATION("min-webinar-duration");

    private String value;

    /**
     * Constructor that takes the name value.
     * @param value The value for the name
     */
    AppParameterName(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the name.
     * @return The value of the name.
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
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static AppParameterName fromValue(String value)
    {
        AppParameterName[] types = values();
        for(AppParameterName type : types)
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