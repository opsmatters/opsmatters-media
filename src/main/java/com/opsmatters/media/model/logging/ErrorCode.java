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

package com.opsmatters.media.model.logging;

/**
 * Represents the code of a log error.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ErrorCode
{
    // Maximum 20 characters
    E_NONE(false),
    E_EXCEPTION(true),
    E_ERROR_PAGE(false),
    E_BAD_PAGE(false),
    E_PARSE_DATE(true),
    E_EMPTY_ROOT(true),
    E_MISSING_SOURCE(false),
    E_MISSING_ROOT(true),
    E_MISSING_BODY(true),
    E_MISSING_SUMMARY(true),
    E_MISSING_ANCHOR(true),
    E_MISSING_URL(true),
    E_MISSING_ELEM(true),
    E_MISSING_IMAGE(false),
    E_MISSING_MORE(true),
    E_MISSING_MOVE(true),
    E_SUBSCRIBE_FAIL(true);

    private boolean persist;

    /**
     * Constructor that takes the persist value.
     * @param persist The persist for the code
     */
    ErrorCode(boolean persist)
    {
        this.persist = persist;
    }

    /**
     * Returns <CODE>true</CODE> if the code should be persisted.
     * @return <CODE>true</CODE> if the code should be persisted
     */
    public boolean persist()
    {
        return persist;
    }
}