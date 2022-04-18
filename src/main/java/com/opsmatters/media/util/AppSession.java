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
 * A set of utility methods to manage the current app session.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AppSession
{
    private int id = -1;

    private static AppSession _session = new AppSession();

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private AppSession()
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
     * Sets the session id.
     */
    public static void id(int id)
    {
        _session.setId(id);
    }

    /**
     * Returns the current session id.
     */
    public static int id()
    {
        return _session.getId();
    }
}