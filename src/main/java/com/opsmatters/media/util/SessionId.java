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

import java.time.Instant;

import static java.time.temporal.ChronoUnit.*;

/**
 * Class to manage the current app session id.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SessionId
{
    private static final int START_HOUR = 8;

    private static SessionId _session = new SessionId();

    private int id = -1;

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private SessionId()
    {
        Instant dt = Instant.now();
        int hour = TimeUtils.toDateTimeUTC(dt).getHour();
        if(hour < START_HOUR)
            dt = dt.minus(1, DAYS); // Go back to yesterday if before session start hour
        setId(Integer.parseInt(TimeUtils.toStringUTC(dt, Formats.SESSION_FORMAT)));
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

    /**
     * Returns the number of days difference between the two given ids.
     */
    public static long diff(int id1, int id2)
    {
        Instant dt1 = toInstant(id1);
        Instant dt2 = toInstant(id2);
        return dt1 != null && dt2 != null ? DAYS.between(dt1, dt2) : 0L;
    }

    /**
     * Returns the given id as an instant.
     */
    private static Instant toInstant(int id)
    {
        return id > 0 ? TimeUtils.toInstantUTC(Integer.toString(id), Formats.SESSION_FORMAT) : null;
    }
}