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

package com.opsmatters.media.cache.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Comparator;
import java.util.Collections;
import com.opsmatters.media.util.NamedTimeZone;

/**
 * Lists of timezones that can be searched by various keys.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TimeZones
{
    public static final String DEFAULT = "Universal";

    private static List<NamedTimeZone> timezones = new ArrayList<NamedTimeZone>();
    private static List<String> ids = new ArrayList<String>();
    private static List<String> names =  new ArrayList<String>();
    private static Map<String,NamedTimeZone> idMap = new HashMap<String,NamedTimeZone>();
    private static Map<String,NamedTimeZone> nameMap = new HashMap<String,NamedTimeZone>();

    // Reduced list of "main" timezones
    public static String[] TIMEZONES =
    {
        "Africa/Johannesburg",
        //"America/Argentina/Buenos_Aires",
        "America/Lima",
        "America/Montevideo",
        "Asia/Calcutta",
        "Asia/Istanbul",
        "Asia/Qatar",
        "Asia/Tel_Aviv",
        "Asia/Tokyo",
        "Australia/North",
        "Australia/South",
        "Australia/Sydney",
        "Australia/West",
        "Brazil/East",
        "Canada/Atlantic",
        "Canada/Eastern",
        "Canada/Central",
        "Canada/Mountain",
        "Canada/Newfoundland",
        "Canada/Pacific",
        "Chile/Continental",
        "Europe/Amsterdam",
        "Europe/Belfast",
        "Europe/Berlin",
        "Europe/Brussels",
        "Europe/Bucharest",
        "Europe/Budapest",
        "Europe/Dublin",
        "Europe/Lisbon",
        "Europe/London",
        "Europe/Madrid",
        "Europe/Paris",
        "Europe/Rome",
        "Europe/Vienna",
        "Europe/Zurich",
        "Europe/Warsaw",
        "Mexico/BajaNorte",
        "Mexico/BajaSur",
        "Mexico/General",
        "Pacific/Auckland",
        "US/Central",
        "US/Eastern",
        "US/Mountain",
        "US/Pacific",
        "Greenwich",
        "Universal",
    };

    static
    {
        // Cache all the available timezones
        for(String id : TIMEZONES)
        {
            if((id.length() > 3) && !id.startsWith("Etc"))
            {
                TimeZone tz = TimeZone.getTimeZone(id);
                String tzName = tz.getDisplayName();

                // Don't include zones with generic "GMT+n" descriptions.
                if(!tzName.startsWith("GMT-") && !tzName.startsWith("GMT+"))
                {
                    NamedTimeZone ntz = new NamedTimeZone(tz);
                    timezones.add(ntz);
                    idMap.put(ntz.getId(), ntz);
                    nameMap.put(ntz.getName(), ntz);
                }
            }
        }

        Collections.sort(timezones, new Comparator<NamedTimeZone>()
        {
            public int compare(NamedTimeZone tz1, NamedTimeZone tz2)
            {
                int offset1 = tz1.getRawOffset();
                int offset2 = tz2.getRawOffset();
                if(offset1 == offset2)
                    return tz1.getId().compareTo(tz2.getId());
                else if(offset1 > offset2)
                    return 1;
                else
                    return -1;
            }
        });

        ids.add("");
        names.add("");
        for(NamedTimeZone tz : timezones)
        {
            ids.add(tz.getId());
            names.add(tz.getName());
        }
    }

    /**
     * Private constructor.
     */
    private TimeZones()
    {
    }

    /**
     * Returns the list of IDs for the cached timezones.
     * @return The list of IDs for the cached timezones
     */
    public static List<String> getIds()
    {
        return ids;
    }

    /**
     * Returns the list of names for the cached timezones.
     * @return The list of names for the cached timezones
     */
    public static List<String> getNames()
    {
        return names;
    }

    /**
     * Returns the cached timezone with the given name.
     * @param name The name of the timezone
     * @return The cached timezone with the given name
     */
    public static TimeZone getTimeZone(String name)
    {
        return nameMap.get(name);
    }

    /**
     * Returns the cached timezone with the given ID.
     * @param id The id of the timezone
     * @return The cached timezone with the given ID
     */
    public static TimeZone getTimeZoneById(String id)
    {
        return idMap.get(id);
    }
}