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

/**
 * Class that represents an external image platform.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImagePlatform extends ExternalPlatform
{
    private String tag = "";
    private ImagePlatformType type;
    private String attribution = "";

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
        setType(ImagePlatformType.FREE);
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
            setType(obj.getType());
            setAttribution(obj.getAttribution());
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
     * Returns <CODE>true</CODE> if the given the tag for the platform has been set.
     */
    public boolean hasTag()
    {
        return getTag() != null && getTag().length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the given filename contains the platform tag.
     */
    public boolean matchesFilename(String filename)
    {
        return hasTag() && filename != null && filename.indexOf(getTag()) != -1;
    }

    /**
     * Returns the platform type.
     */
    public ImagePlatformType getType()
    {
        return type;
    }

    /**
     * Sets the platform type.
     */
    public void setType(ImagePlatformType type)
    {
        this.type = type;
    }

    /**
     * Sets the platform type.
     */
    public void setType(String type)
    {
        setType(ImagePlatformType.valueOf(type));
    }

    /**
     * Returns the attribution for the platform.
     */
    public String getAttribution()
    {
        return attribution;
    }

    /**
     * Sets the attribution for the platform.
     */
    public void setAttribution(String attribution)
    {
        this.attribution = attribution;
    }
}