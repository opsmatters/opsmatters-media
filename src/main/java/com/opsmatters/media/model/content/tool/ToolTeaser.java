/*
 * Copyright 2019 Gerald Curley
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
package com.opsmatters.media.model.content.tool;

import com.opsmatters.media.model.content.ResourceTeaser;

/**
 * Class representing a tool teaser.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ToolTeaser extends ResourceTeaser
{
    private String pricing = "";

    /**
     * Default constructor.
     */
    public ToolTeaser()
    {
    }

    /**
     * Copy constructor.
     */
    public ToolTeaser(ToolTeaser obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ToolTeaser obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setUrl(obj.getUrl());
            setPricing(obj.getPricing());
        }
    }

    /**
     * Sets the tool url.
     */
    public void setUrl(String url)
    {
        setUrl("", url, true);
    }

    /**
     * Returns the tool pricing.
     */
    public String getPricing()
    {
        return pricing;
    }

    /**
     * Sets the tool pricing.
     */
    public void setPricing(String pricing)
    {
        this.pricing = pricing;
    }
}