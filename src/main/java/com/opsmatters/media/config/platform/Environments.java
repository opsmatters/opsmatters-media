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
package com.opsmatters.media.config.platform;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.config.platform.Sites;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.platform.Environment;
import com.opsmatters.media.model.platform.EnvironmentName;

/**
 * Class representing the set of environments.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Environments
{
    private static final Logger logger = Logger.getLogger(Environments.class.getName());

    private static Map<EnvironmentName,Environment> environmentMap = new HashMap<EnvironmentName,Environment>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Environments()
    {
    }

    /**
     * Returns <CODE>true</CODE> if environments have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of environments.
     */
    public static void load(List<Environment> environments)
    {
        initialised = false;

        clear();
        for(Environment environment : environments)
        {
            add(environment);
        }

        logger.info("Loaded "+size()+" environments");

        initialised = true;
    }

    /**
     * Clears the environments.
     */
    private static void clear()
    {
        environmentMap.clear();
    }

    /**
     * Adds the given environment.
     */
    private static void add(Environment environment)
    {
        environmentMap.put(environment.getName(), environment);
    }

    /**
     * Returns the environment for the given name.
     */
    public static Environment get(EnvironmentName name)
    {
        return environmentMap.get(name);
    }

    /**
     * Returns the environment for the given name.
     */
    public static Environment get(String name)
    {
        EnvironmentName env = null;
        if(name != null && name.length() > 0)
            env = EnvironmentName.valueOf(name);
        return get(env);
    }

    /**
     * Returns the list of enabled environments.
     */
    public static List<Environment> listStage()
    {
        List<Environment> ret = new ArrayList<Environment>();
        for(Site site : Sites.list())
        {
            if(site.isEnabled())
                ret.add(site.getEnvironment(EnvironmentName.STAGE));
        }

        return ret;
    }

    /**
     * Returns the number of environments.
     */
    public static int size()
    {
        return environmentMap.size();
    }
}