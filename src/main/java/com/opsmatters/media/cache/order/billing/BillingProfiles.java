/*
 * Copyright 2024 Gerald Curley
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
package com.opsmatters.media.cache.order.billing;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.billing.BillingProfile;

/**
 * Class representing the list of billing profiles.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BillingProfiles implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(BillingProfiles.class.getName());

    private static Map<String,BillingProfile> profileMap = new LinkedHashMap<String,BillingProfile>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private BillingProfiles()
    {
    }

    /**
     * Returns <CODE>true</CODE> if profiles have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of profiles.
     */
    public static void load(List<BillingProfile> profiles)
    {
        initialised = false;

        clear();
        for(BillingProfile profile : profiles)
        {
            add(profile);
        }

        logger.info("Loaded "+size()+" profiles");

        initialised = true;
    }

    /**
     * Clears the profiles.
     */
    public static void clear()
    {
        profileMap.clear();
    }

    /**
     * Returns the profile with the given id.
     */
    public static BillingProfile get(String id)
    {
        return profileMap.get(id);
    }

    /**
     * Adds the profile with the given id.
     */
    public static void add(BillingProfile profile)
    {
        profileMap.put(profile.getId(), profile);
    }

    /**
     * Removes the profile with the given id.
     */
    public static void remove(BillingProfile profile)
    {
        profileMap.remove(profile.getId());
    }

    /**
     * Returns the count of profiles.
     */
    public static int size()
    {
        return profileMap.size();
    }

    /**
     * Returns the list of profiles.
     */
    public static List<BillingProfile> list()
    {
        List<BillingProfile> ret = new ArrayList<BillingProfile>();
        for(BillingProfile profile : profileMap.values())
        {
            if(profile.isActive())
                ret.add(profile);
        }

        return ret;
    }
}