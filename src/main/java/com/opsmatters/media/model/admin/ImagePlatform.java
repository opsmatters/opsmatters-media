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
package com.opsmatters.media.model.admin;

import com.opsmatters.media.file.FileFormat;
import com.opsmatters.media.util.FileUtils;

/**
 * Class that represents an external image platform.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImagePlatform extends ExternalPlatform
{
    private String tag = "";
    private boolean free = false;

    /**
     * Default constructor.
     */
    public ImagePlatform()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public ImagePlatform(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public ImagePlatform(ImagePlatform obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ImagePlatform obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTag(obj.getTag());
            setFree(obj.isFree());
        }
    }

    /**
     * Returns the tag for the platform.
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * Sets the tag for the platform.
     */
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    /**
     * Returns <CODE>true</CODE> if the platform provides free images.
     */
    public boolean isFree()
    {
        return free;
    }

    /**
     * Returns <CODE>true</CODE> if the platform provides free images.
     */
    public Boolean getFreeObject()
    {
        return Boolean.valueOf(free);
    }

    /**
     * Set to <CODE>true</CODE> if the platform provides free images.
     */
    public void setFree(boolean free)
    {
        this.free = free;
    }

    /**
     * Set to <CODE>true</CODE> if the platform provides free images.
     */
    public void setFreeObject(Boolean free)
    {
        this.free = free != null && free.booleanValue();
    }

    /**
     * Adds the platform tag to the given filename.
     * @return The revised filename.
     */
    public String appendTag(String filename)
    {
        String ret = filename;
        if(filename != null && filename.indexOf(getTag()) == -1)
        {
            String name = FileUtils.getName(filename);
            String ext = FileUtils.getExtension(filename);
            if(ext.length() == 0)
                ext = FileFormat.JPG.value();
            ret = String.format("%s-%s.%s", name, getTag(), ext);
        }

        return ret;
    }
}