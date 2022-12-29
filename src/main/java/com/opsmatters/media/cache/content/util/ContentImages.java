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
package com.opsmatters.media.cache.content.util;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;

/**
 * Class representing the set of content images.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentImages
{
    private static Map<ImageType,Map<String,ContentImage>> types = new LinkedHashMap<ImageType,Map<String,ContentImage>>();

    private static boolean initialised = false;

    // Static image files
    public static String PLACEHOLDER_IMAGE = "placeholder-image.jpg";
    public static String PLACEHOLDER_THUMB = "placeholder-thumb.jpg";
    public static String PLACEHOLDER_LOGO = "placeholder-logo.jpg";

    /**
     * Private constructor.
     */
    private ContentImages()
    {
    }

    /**
     * Returns <CODE>true</CODE> if images have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of images.
     */
    public static void load(List<ContentImage> images)
    {
        initialised = false;

        types.clear();
        for(ContentImage image : images)
        {
            if(!image.getType().multiple())
            {
                add(image);
            }
        }

        initialised = true;
    }

    /**
     * Sets the given images.
     */
    public static void set(List<ContentImage> images)
    {
        for(ContentImage image : images)
            add(image);
    }

    /**
     * Adds the given image.
     */
    private static void add(ContentImage image)
    {
        Map<String,ContentImage> map = types.get(image.getType());
        if(map == null)
        {
            map = new LinkedHashMap<String,ContentImage>();
            types.put(image.getType(), map);
        }

        map.put(image.getCode(), image);
    }

    /**
     * Returns the list of images with the given type.
     */
    public static List<ContentImage> get(ImageType type)
    {
        List<ContentImage> ret = new ArrayList<ContentImage>();
        Map<String,ContentImage> map = types.get(type);
        if(map != null)
            ret.addAll(map.values());
        return ret;
    }

    /**
     * Returns the image with the given type and code.
     */
    public static ContentImage get(ImageType type, String code)
    {
        ContentImage ret = null;
        Map<String,ContentImage> map = types.get(type);
        if(map != null)
            ret = map.get(code);
        return ret;
    }

    /**
     * Returns the banner image for the given content (if one exists).
     */
    public static ContentImage get(Content content)
    {
        ContentImage ret = null;

        ContentImage banner = ContentImages.get(ImageType.BANNER, content.getCode());
        if(banner != null && content.getImage().equals(banner.getFilename()))
        {
            ret = banner;
        }

        return ret;
    }

    /**
     * Adds the given image.
     */
    public static void put(ContentImage image)
    {
        add(image);
    }

    /**
     * Adds all the given images.
     */
    public static void putAll(List<ContentImage> images)
    {
        for(ContentImage image : images)
            put(image);
    }

    /**
     * Removes the given image.
     */
    public static void remove(ContentImage image)
    {
        delete(image);
    }

    /**
     * Deletes the given image.
     */
    private static void delete(ContentImage image)
    {
        Map<String,ContentImage> map = types.get(image.getType());
        if(map != null)
            map.remove(image.getCode());
    }
}