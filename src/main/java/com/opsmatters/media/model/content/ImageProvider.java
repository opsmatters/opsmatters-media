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

package com.opsmatters.media.model.content;

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.file.CommonFiles;
import com.opsmatters.media.util.FileUtils;

/**
 * Represents an image provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ImageProvider
{
    UNSPLASH("unsplash", "Unsplash"),
    PEXELS("pexels", "Pexels"),
    PIXABAY("pixabay", "Pixabay"),
    PXHERE("pxhere", "Pxhere"),
    FREEPIK("freepik", "Freepik");

    private String code;
    private String value;

    /**
     * Constructor that takes the provider information.
     * @param code The code for the provider
     * @param value The value for the provider
     */
    ImageProvider(String code, String value)
    {
        this.code = code;
        this.value = value;
    }

    /**
     * Returns the code of the provider.
     * @return The code of the provider.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the provider.
     * @return The value of the provider.
     */
    public String value()
    {
        return value;
    }

    /**
     * Adds the provider to the given filename.
     * @return The revised filename.
     */
    public String addProvider(String filename)
    {
        String ret = filename;
        if(filename != null && filename.indexOf(code()) == -1)
        {
            String name = FileUtils.getName(filename);
            String ext = FileUtils.getExtension(filename);
            if(ext.length() == 0)
                ext = CommonFiles.JPG_EXT;
            ret = String.format("%s-%s.%s", name, code, ext);
        }

        return ret;
    }

    /**
     * Returns the type for the given filename.
     * @param filename The filename
     * @return The type for the given filename
     */
    public static ImageProvider fromFilename(String filename)
    {
        ImageProvider[] types = values();
        for(ImageProvider type : types)
        {
            if(filename.indexOf(type.code()) != -1)
                return type;
        }
        return null;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static ImageProvider fromCode(String code)
    {
        ImageProvider[] types = values();
        for(ImageProvider type : types)
        {
            if(type.code().equals(code))
                return type;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given code is contained in the list of types.
     * @param code The type code
     * @return <CODE>true</CODE> if the given code is contained in the list of types
     */
    public static boolean contains(String code)
    {
        return fromCode(code) != null;
    }

    /**
     * Returns a list of the providers.
     */
    public static List<ImageProvider> toList()
    {
        List<ImageProvider> ret = new ArrayList<ImageProvider>();

        ret.add(UNSPLASH);
        ret.add(PEXELS);
        ret.add(PIXABAY);
        ret.add(PXHERE);
        ret.add(FREEPIK);

        return ret;
    }
}