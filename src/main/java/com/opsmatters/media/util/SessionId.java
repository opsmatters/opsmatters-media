/*
 * Copyright 2022 Gerald Curley
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
 * Class to manage the current app session id.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SessionId
{
    private int id = -1;

    private static SessionId _session = new SessionId();

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private SessionId()
    {
        setId(Integer.parseInt(TimeUtils.toStringUTC(Formats.SESSION_FORMAT)));
    }

    /**
     * Returns the current session id.
     */
    private int getId()
    {
        return id;
    }

    /**
     * Sets the current session id.
     */
    private void setId(int id)
    {
        this.id = id;
    }

    /**
     * Sets the current session id.
     */
    public static void set(int id)
    {
        _session.setId(id);
    }

    /**
     * Returns the current session id.
     */
    public static int get()
    {
        return _session.getId();
    }

    /**
     * Returns the session id for the current system date.
     */
    public static int now()
    {
        return new SessionId().getId();
    }
}