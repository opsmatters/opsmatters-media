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

package com.opsmatters.media.model.content.util;

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
    UNSPLASH("unsplash", "Unsplash", true),
    PEXELS("pexels", "Pexels", true),
    PIXABAY("pixabay", "Pixabay", true),
    PXHERE("pxhere", "Pxhere", true),
    PXFUEL("pxfuel", "Pxfuel", true),
    FREEPIK("freepik", "Freepik", true),
    STOCKVAULT("stockvault", "Stockvault", true),
    STOCKSNAP("stocksnap", "StockSnap", true),
    ISTOCK("istock", "iStock", false),
    GETTY("getty", "Getty", false),
    SHUTTERSTOCK("shutterstock", "Shutterstock", false),
    ADOBE("adobe", "Adobe", false),
    DREAMSTIME("dreamstime", "Dreamstime", false),
    ALAMY("alamy", "Alamy", false),
    FREEPIK_PREMIUM("freepik-premium", "Freepik Premium", false);

    private String code;
    private String value;
    private boolean free = false;

    /**
     * Constructor that takes the provider information.
     * @param code The code for the provider
     * @param value The value for the provider
     * @param free <CODE>true</CODE> if the provider is a copyright-free source
     */
    ImageProvider(String code, String value, boolean free)
    {
        this.code = code;
        this.value = value;
        this.free = free;
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
     * Returns <CODE>true</CODE> if the provider is a copyright-free source.
     * @return <CODE>true</CODE> if the provider is a copyright-free source
     */
    public boolean free()
    {
        return free;
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
     * Returns the provider for the given filename.
     * @param filename The filename
     * @return The provider for the given filename
     */
    public static ImageProvider fromFilename(String filename)
    {
        ImageProvider[] providers = values();
        for(ImageProvider provider : providers)
        {
            if(filename.indexOf(provider.code()) != -1)
                return provider;
        }
        return null;
    }

    /**
     * Returns the provider for the given code.
     * @param code The provider code
     * @return The provider for the given code
     */
    public static ImageProvider fromCode(String code)
    {
        ImageProvider[] providers = values();
        for(ImageProvider provider : providers)
        {
            if(provider.code().equals(code))
                return provider;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given code is contained in the list of providers.
     * @param code The provider code
     * @return <CODE>true</CODE> if the given code is contained in the list of providers
     */
    public static boolean contains(String code)
    {
        return fromCode(code) != null;
    }

    /**
     * Returns a list of the providers.
     */
    public static List<ImageProvider> toList(boolean free)
    {
        List<ImageProvider> ret = new ArrayList<ImageProvider>();

        ImageProvider[] providers = values();
        for(ImageProvider provider : providers)
        {
            if(provider.free() == free)
                ret.add(provider);
        }

        return ret;
    }
}