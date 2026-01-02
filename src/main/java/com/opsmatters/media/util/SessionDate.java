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

package com.opsmatters.media.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.*;

/**
 * Methods to calculate the application session date.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SessionDate
{
    private static final int START_HOUR = 8;

    private Instant dt;

    /**
     * Constructor that takes a date.
     */
    public SessionDate(Instant dt)
    {
        this.dt = adjust(dt);
    }

    /**
     * Default constructor.
     */
    public SessionDate()
    {
        this(Instant.now());
    }

    /**
     * Returns the session date.
     */
    public Instant getDate()
    {
        return dt;
    }

    /**
     * Returns the current session date.
     */
    public static Instant get()
    {
        return new SessionDate().getDate();
    }

    /**
     * Returns the session date for the given date.
     */
    public static Instant get(Instant dt)
    {
        return new SessionDate(dt).getDate();
    }

    /**
     * Returns the given date adjusted to a session date.
     */
    private Instant adjust(Instant dt)
    {
        Instant ret = dt;
        int hour = TimeUtils.toDateTimeUTC(ret).getHour();
        if(hour < START_HOUR)
            ret = ret.minus(1, DAYS); // Go back to yesterday if before session start hour
        return ret;
    }

    /**
     * Returns the session date converted to a local date.
     */
    public LocalDate toLocalDate()
    {
        return dt.atZone(ZoneId.of("UTC")).toLocalDate();
    }
}