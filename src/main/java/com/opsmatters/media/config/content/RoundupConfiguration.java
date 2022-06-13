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

package com.opsmatters.media.config.content;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.RoundupArticle;

/**
 * Class that represents the configuration for roundup content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupConfiguration extends ContentConfiguration<RoundupArticle>
{
    private static final Logger logger = Logger.getLogger(RoundupConfiguration.class.getName());

    public static final String PAGES = "pages";

    private List<WebPageConfiguration> pages = new ArrayList<WebPageConfiguration>();

    /**
     * Default constructor.
     */
    public RoundupConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public RoundupConfiguration(RoundupConfiguration obj)
    {
        super(obj != null ? obj.getName() : null);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(RoundupConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(WebPageConfiguration page : obj.getPages())
                addPage(new WebPageConfiguration(page));
        }
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.ROUNDUP;
    }

    /**
     * Returns the pages for this configuration.
     */
    public List<WebPageConfiguration> getPages()
    {
        return pages;
    }

    /**
     * Sets the pages for this configuration.
     */
    public void setPages(List<WebPageConfiguration> pages)
    {
        this.pages = pages;
    }

    /**
     * Adds a page for this configuration.
     */
    public void addPage(WebPageConfiguration page)
    {
        this.pages.add(page);
    }

    /**
     * Returns the number of pages.
     */
    public int numPages()
    {
        return pages.size();
    }

    /**
     * Returns the page at the given index.
     */
    public WebPageConfiguration getPage(int i)
    {
        return pages.get(i);
    }

    /**
     * Returns the page with the given name.
     */
    public WebPageConfiguration getPage(String name)
    {
        WebPageConfiguration ret = null;
        for(WebPageConfiguration page : getPages())
        {
            if(page.getName().equals(name))
            {
                ret = page;
                break;
            }
        }
        return ret;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        super.parseDocument(map);
        if(map.containsKey(PAGES))
        {
            List<Map<String,Object>> pages = (List<Map<String,Object>>)map.get(PAGES);
            for(Map<String,Object> page : pages)
            {
                for(Map.Entry<String,Object> entry : page.entrySet())
                {
                    WebPageConfiguration config = new WebPageConfiguration(entry.getKey());
                    config.parseDocument((Map<String,Object>)entry.getValue());
                    addPage(config);
                }
            }
        }
    }
}