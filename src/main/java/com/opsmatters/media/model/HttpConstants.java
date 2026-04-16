/*
 * Copyright 2026 Gerald Curley
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
 * Constants related to HTTP requests and responses.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class HttpConstants
{
    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private HttpConstants()
    {
    }

    // HTTP header names
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String USER_AGENT = "User-Agent";

    /**
     * The default user agent to use with URLConnections to avoid 403 rejection errors
     */
    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36";

    /**
     * The default read timeout for a HTTP connection in milliseconds.
     */
    public static final int DEFAULT_READ_TIMEOUT = 10000;

    /**
     * The default connect timeout for a HTTP connection in milliseconds.
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 5000;
}