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
package com.opsmatters.media.model.content;

/**
 * Class representing a tool summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ToolSummary extends ResourceSummary
{
    private String pricing = "";

    /**
     * Default constructor.
     */
    public ToolSummary()
    {
    }

    /**
     * Copy constructor.
     */
    public ToolSummary(ToolSummary obj)
    {
        super(obj);

        if(obj != null)
        {
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