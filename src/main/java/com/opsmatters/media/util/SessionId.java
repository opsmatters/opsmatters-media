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
    private int id, yesterday = -1;

    private static SessionId _session = new SessionId();

    /**
     * Constructor that takes a date.
     */
    private SessionId(Instant dt)
    {
        setId(SessionDate.get(dt));
    }

    /**
     * Default constructor that uses the current system date.
     */
    private SessionId()
    {
        this(Instant.now());
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
     * Sets the current and yesterday session id from an instant.
     */
    private void setId(Instant dt)
    {
        setId(toId(dt));
        setYesterday(toId(dt.minus(1, DAYS)));
    }

    /**
     * Returns yesterday's session id.
     */
    private int getYesterday()
    {
        return yesterday;
    }

    /**
     * Sets yesterday's session id.
     */
    private void setYesterday(int yesterday)
    {
        this.yesterday = yesterday;
    }

    /**
     * Returns yesterday's session id.
     */
    public static int yesterday()
    {
        return _session.getYesterday();
    }

    /**
     * Returns the current session id.
     */
    public static int get()
    {
        return _session.getId();
    }

    /**
     * Sets the current session id.
     */
    public static void set(int id)
    {
        _session.setId(toInstant(id));
    }

    /**
     * Returns the session id for the given date.
     */
    public static int id(Instant dt)
    {
        return new SessionId(dt).getId();
    }

    /**
     * Returns the session id for the current system date.
     */
    public static int id()
    {
        return id(Instant.now());
    }

    /**
     * Returns the given id as an instant.
     */
    private static Instant toInstant(int id)
    {
        return id > 0 ? TimeUtils.toInstantUTC(Integer.toString(id), Formats.SESSION_FORMAT) : null;
    }

    /**
     * Returns the given instant as an id.
     */
    private static int toId(Instant dt)
    {
        return Integer.parseInt(TimeUtils.toStringUTC(dt, Formats.SESSION_FORMAT));
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
}