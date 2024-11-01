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

import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;

/**
 * Class that represents an external event platform.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventPlatform extends ExternalPlatform
{
    private String domain = "";
    private String page = "";
    private CrawlerWebPage crawlerPage;

    /**
     * Default constructor.
     */
    public EventPlatform()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public EventPlatform(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public EventPlatform(EventPlatform obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(EventPlatform obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setDomain(obj.getDomain());
            setPage(obj.getPage());
        }
    }

    /**
     * Returns the domain for the platform.
     */
    public String getDomain()
    {
        return domain;
    }

    /**
     * Sets the domain for the platform.
     */
    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    /**
     * Returns the page for the platform.
     */
    public String getPage()
    {
        return page;
    }

    /**
     * Sets the page for the platform.
     */
    public void setPage(String page)
    {
        this.page = page;

        setCrawlerPage();
    }

    /**
     * Returns <CODE>true</CODE> if the page for the platform have been configured.
     */
    public boolean hasPage()
    {
        return page != null && page.length() > 0;
    }

    /**
     * Returns the crawler page for the platform.
     */
    public CrawlerWebPage getCrawlerPage()
    {
        return crawlerPage;
    }

    /**
     * Returns the crawler page for the platform.
     */
    private void setCrawlerPage()
    {
        crawlerPage = null;

        if(hasPage())
        {
            crawlerPage = CrawlerWebPage.builder(getCode())
                .parse(new Yaml().load(getPage()))
                .build();
        }
    }

    /**
     * Returns <CODE>true</CODE> if the crawler page for the platform have been configured.
     */
    public boolean hasCrawlerPage()
    {
        return crawlerPage != null;
    }
}