/*
 * Copyright 2023 Gerald Curley
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

import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;

/**
 * A timezone with a name for display in a list.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class NamedTimeZone extends SimpleTimeZone
{
    private String name = "";

    /**
     * Constructor that takes a timezone.
     * @param tz The timezone
     */
    public NamedTimeZone(TimeZone tz)
    {
        super(tz.getRawOffset(), tz.getID());
        setName();
    }

    /**
     * Returns the id of the timezone.
     * @return The id of the timezone
     */
    public String getId()
    {
        return super.getID();
    }

    /**
     * Returns the name of the timezone.
     * @return The name of the timezone
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the string value of the timezone.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Sets the name of the timezone.
     */
    private void setName()
    {
        long hours = TimeUnit.MILLISECONDS.toHours(getRawOffset());
        long minutes = Math.abs(TimeUnit.MILLISECONDS.toMinutes(getRawOffset())
            -TimeUnit.HOURS.toMinutes(hours));
        this.name = String.format("(GMT%+d:%02d) %s", hours, minutes, getId());
    }
}