/*
 * Copyright 2021 Gerald Curley
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
package com.opsmatters.media.model.social;

/**
 * Class representing a social media post template.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class PostTemplate extends PostSource
{
    public static final String DEFAULT = "New Template";

    private boolean isDefault = false;

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PostTemplate obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setDefault(obj.isDefault());
        }
    }

    /**
     * Returns <CODE>true</CODE> if this template is the default for the content type.
     */
    public boolean isDefault()
    {
        return isDefault;
    }

    /**
     * Returns <CODE>true</CODE> if this template is the default for the content type.
     */
    public Boolean getDefaultObject()
    {
        return Boolean.valueOf(isDefault());
    }

    /**
     * Set to <CODE>true</CODE> if this template is the default for the content type.
     */
    public void setDefault(boolean isDefault)
    {
        this.isDefault = isDefault;
    }

    /**
     * Set to <CODE>true</CODE> if this template is the default for the content type.
     */
    public void setDefaultObject(Boolean isDefault)
    {
        setDefault(isDefault != null && isDefault.booleanValue());
    }
}