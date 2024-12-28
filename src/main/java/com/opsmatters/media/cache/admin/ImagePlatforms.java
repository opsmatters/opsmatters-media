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
package com.opsmatters.media.cache.admin;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.ImagePlatform;

/**
 * Class representing the list of image platforms.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImagePlatforms implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(ImagePlatforms.class.getName());

    private static Map<String,ImagePlatform> platformMap = new LinkedHashMap<String,ImagePlatform>();

    private static boolean initialised = false;

    private static Comparator comparator = new Comparator<ImagePlatform>()
    {
        public int compare(ImagePlatform arg1, ImagePlatform arg2)
        {
            return arg1.getTag().compareTo(arg2.getTag());
        }
    };

    /**
     * Private constructor.
     */
    private ImagePlatforms()
    {
    }

    /**
     * Returns <CODE>true</CODE> if platforms have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of platforms.
     */
    public static void load(List<ImagePlatform> platforms)
    {
        initialised = false;

        clear();
        for(ImagePlatform platform : platforms)
        {
            add(platform);
        }

        logger.info("Loaded "+size()+" image platforms");

        initialised = true;
    }

    /**
     * Clears the image platforms.
     */
    public static void clear()
    {
        platformMap.clear();
    }

    /**
     * Returns the image platform with the given code.
     */
    public static ImagePlatform get(String code)
    {
        return platformMap.get(code);
    }

    /**
     * Adds the image platform with the given code.
     */
    public static void add(ImagePlatform platform)
    {
        platformMap.put(platform.getCode(), platform);
    }

    /**
     * Removes the image platform with the given code.
     */
    public static void remove(ImagePlatform platform)
    {
        platformMap.remove(platform.getCode());
    }

    /**
     * Returns the list of image platforms.
     */
    public static List<ImagePlatform> list()
    {
        return new ArrayList<ImagePlatform>(platformMap.values());
    }

    /**
     * Returns the list of free or paid image platforms.
     */
    public static List<ImagePlatform> list(boolean free)
    {
        List<ImagePlatform> ret = new ArrayList<ImagePlatform>();
        for(ImagePlatform platform : platformMap.values())
        {
            if(platform.isFree() == free
                && platform.isActive())
            {
                ret.add(platform);
            }
        }

        Collections.sort(ret, comparator);

        return ret;
    }

    /**
     * Returns the count of image platforms.
     */
    public static int size()
    {
        return platformMap.size();
    }

    /**
     * Returns <CODE>true</CODE> if the given text contains a supported web image file.
     * @param text The text to be checked
     * @return <CODE>true</CODE> if the given text contains a PNG, JPG, GIF, WEBP or SVG file
     */
    public static boolean containsImage(String text)
    {
        boolean ret = false;
        if(text != null)
        {
            for(ImagePlatform platform : platformMap.values())
            {
                if(text.indexOf(platform.getTag()) != -1)
                {
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }
}