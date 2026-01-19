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
package com.opsmatters.media.cache.system;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.system.Environment;
import com.opsmatters.media.model.system.EnvironmentId;
import com.opsmatters.media.cache.StaticCache;

import static com.opsmatters.media.model.system.EnvironmentId.*;

/**
 * Class representing the set of environments.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Environments extends StaticCache
{
    private static final Logger logger = Logger.getLogger(Environments.class.getName());

    private static Map<EnvironmentId,Environment> environmentMap = new HashMap<EnvironmentId,Environment>();

    /**
     * Private constructor.
     */
    private Environments()
    {
    }

    /**
     * Loads the set of environments.
     */
    public static void load(List<Environment> environments)
    {
        setInitialised(false);

        clear();
        for(Environment environment : environments)
        {
            add(environment);
        }

        logger.info("Loaded "+size()+" environments");

        setInitialised(true);
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
        environmentMap.put(environment.getId(), environment);
    }

    /**
     * Returns the environment for the given id.
     */
    public static Environment get(EnvironmentId id)
    {
        return environmentMap.get(id);
    }

    /**
     * Returns the environment for the given id.
     */
    public static Environment get(String id)
    {
        EnvironmentId env = null;
        if(id != null && id.length() > 0)
            env = EnvironmentId.valueOf(id);
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
                ret.add(site.getEnvironment(STAGE));
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