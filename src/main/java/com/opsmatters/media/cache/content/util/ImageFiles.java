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
package com.opsmatters.media.cache.content.util;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.util.ImageFile;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing the set of image files.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImageFiles extends StaticCache
{
    private static final Logger logger = Logger.getLogger(ImageFiles.class.getName());

    private static Map<String,ImageFile> filenameMap = new LinkedHashMap<String,ImageFile>();

    /**
     * Private constructor.
     */
    private ImageFiles()
    {
    }

    /**
     * Loads the set of files.
     */
    public static void load(List<ImageFile> files)
    {
        setInitialised(false);

        filenameMap.clear();
        for(ImageFile file : files)
        {
            add(file);
        }

        logger.info("Loaded "+size()+" image files");

        setInitialised(true);
    }

    /**
     * Adds the given file.
     */
    public static void add(ImageFile file)
    {
        filenameMap.put(file.getFilename(), file);
    }

    /**
     * Returns the file for the given filename (if one exists).
     */
    public static ImageFile get(String filename)
    {
        return filenameMap.get(filename);
    }

    /**
     * Deletes the given file.
     */
    public static void delete(ImageFile file)
    {
        filenameMap.remove(file.getFilename());
    }

    /**
     * Returns the list of files.
     */
    public static List<ImageFile> list()
    {
        return new ArrayList<ImageFile>(filenameMap.values());
    }

    /**
     * Returns the count of files.
     */
    public static int size()
    {
        return filenameMap.size();
    }
}