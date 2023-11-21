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

import com.opsmatters.media.model.content.ResourceDetails;

/**
 * Class representing a tool.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ToolDetails extends ResourceDetails
{
    private String pricing = "";
    private String website = "";
    private String download = "";

    /**
     * Default constructor.
     */
    public ToolDetails()
    {
    }

    /**
     * Copy constructor.
     */
    public ToolDetails(ToolDetails obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ToolDetails obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setUrl(obj.getUrl());
            setPricing(obj.getPricing());
            setWebsite(obj.getWebsite());
            setDownload(obj.getDownload());
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

    /**
     * Returns the tool website.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * Sets the tool website.
     */
    public void setWebsite(String website)
    {
        this.website = website;
    }

    /**
     * Returns <CODE>true</CODE> if the tool website has been set.
     */
    public boolean hasWebsite()
    {
        return website != null && website.length() > 0;
    }

    /**
     * Returns the tool download URL.
     */
    public String getDownload()
    {
        return download;
    }

    /**
     * Sets the tool download URL.
     */
    public void setDownload(String download)
    {
        this.download = download;
    }

    /**
     * Returns <CODE>true</CODE> if the tool download URL has been set.
     */
    public boolean hasDownload()
    {
        return download != null && download.length() > 0;
    }
}