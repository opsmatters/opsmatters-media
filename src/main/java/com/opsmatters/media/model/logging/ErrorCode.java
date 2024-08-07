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
    E_NONE,
    E_EXCEPTION,
    E_ERROR_PAGE,
    E_PARSE_DATE,
    E_EMPTY_ROOT,
    E_MISSING_SOURCE,
    E_MISSING_ROOT,
    E_MISSING_BODY,
    E_MISSING_SUMMARY,
    E_MISSING_ANCHOR,
    E_MISSING_URL,
    E_MISSING_ELEM,
    E_MISSING_IMAGE,
    E_MISSING_MORE,
    E_MISSING_MOVE,
    E_SUBSCRIBE_FAIL;
}