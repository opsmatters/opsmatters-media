/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.cache.admin;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.Shortcut;

/**
 * Class representing the list of shortcuts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Shortcuts implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Shortcuts.class.getName());

    private static Map<String,Shortcut> idMap = new LinkedHashMap<String,Shortcut>();

    private static Comparator comparator = new Comparator<Shortcut>()
      {
          public int compare(Shortcut arg1, Shortcut arg2)
          {
              int i1 = arg1.getPosition();
              int i2 = arg2.getPosition();
              if(i1 == i2)
                  return 0;
              else if (i1 > i2)
                  return 1;
              else
                  return -1;
          }
      };

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Shortcuts()
    {
    }

    /**
     * Returns <CODE>true</CODE> if shortcuts have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of shortcuts.
     */
    public static void load(List<Shortcut> shortcuts)
    {
        initialised = false;

        clear();
        for(Shortcut shortcut : shortcuts)
        {
            add(shortcut);
        }

        logger.info("Loaded "+size()+" shortcuts");

        initialised = true;
    }

    /**
     * Clears the shortcuts.
     */
    public static void clear()
    {
        idMap.clear();
    }

    /**
     * Adds the given shortcut.
     */
    public static void add(Shortcut shortcut)
    {
        idMap.put(shortcut.getId(), shortcut);
    }

    /**
     * Removes the given shortcut.
     */
    public static void remove(Shortcut shortcut)
    {
        idMap.remove(shortcut.getId());
    }

    /**
     * Returns the list of shortcuts.
     */
    public static List<Shortcut> list()
    {
        List<Shortcut> ret = new ArrayList<Shortcut>(idMap.values());

        // Sort shortcuts by position
        Collections.sort(ret, comparator);

        return ret;
    }

    /**
     * Returns the count of shortcuts.
     */
    public static int size()
    {
        return idMap.size();
    }
}