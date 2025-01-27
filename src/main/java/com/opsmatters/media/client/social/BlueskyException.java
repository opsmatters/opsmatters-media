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

import java.io.IOException;
import org.json.JSONObject;

/**
 * Wrapper class for a Bluesky error.
 *
 * @author Gerald Curley (opsmatters)
 */
public class BlueskyException extends IOException
{
    private int code;
    private String error = "";
    private String message = "";

    /**
     * Constructor that takes a status code and JSON response.
     */
    public BlueskyException(int code, JSONObject obj)
    {
        super(obj.toString());

        this.code = code;
        this.error = obj.optString("error");
        this.message = obj.optString("message");
    }

    /**
     * Constructor that takes an exception.
     */
    public BlueskyException(Throwable e)
    {
        super(e);
        this.message = e.getMessage();
    }

    public int getCode()
    {
        return code;
    }

    public String getError()
    {
        return error;
    }

    public String getMessage()
    {
        return message;
    }
}