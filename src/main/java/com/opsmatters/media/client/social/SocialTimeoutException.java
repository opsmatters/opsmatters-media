/*
 * Copyright 2025 Gerald Curley
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

package com.opsmatters.media.client.social;

import java.net.SocketTimeoutException;

/**
 * Wrapper class for a generic timeout exception when sending a social post.
 *
 * @author Gerald Curley (opsmatters)
 */
public class SocialTimeoutException extends SocketTimeoutException
{
    /**
     * Constructor that takes a message.
     */
    public SocialTimeoutException(String message)
    {
        this(message, null);
    }

    /**
     * Constructor that takes a message and a nested exception.
     */
    public SocialTimeoutException(String message, SocketTimeoutException e)
    {
        super(message);
        initCause(e);
    }
}