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

    public static final String IMAGE_PREFIX = "image-prefix";
    public static final String IMAGE_FORMAT = "image-format";
    public static final String MAX_LENGTH = "max-length";
    public static final String MIN_LENGTH = "min-length";
    public static final String MIN_PARAGRAPH = "min-paragraph";
    public static final String PAGES = "pages";

    private String imagePrefix = "";
    private String imageFormat = "";
    private int maxLength = 0;
    private int minLength = 0;
    private int minParagraph = 0;
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
            setImagePrefix(obj.getImagePrefix());
            setImageFormat(obj.getImageFormat());
            setMaxLength(obj.getMaxLength());
            setMinLength(obj.getMinLength());
            setMinParagraph(obj.getMinParagraph());
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
     * Returns the image prefix for this configuration.
     */
    public String getImagePrefix()
    {
        return imagePrefix;
    }

    /**
     * Sets the image prefix for this configuration.
     */
    public void setImagePrefix(String imagePrefix)
    {
        this.imagePrefix = imagePrefix;
    }

    /**
     * Returns the image format for this configuration.
     */
    public String getImageFormat()
    {
        return imageFormat;
    }

    /**
     * Sets the image format for this configuration.
     */
    public void setImageFormat(String imageFormat)
    {
        this.imageFormat = imageFormat;
    }

    /**
     * Returns the max summary length for this configuration.
     */
    public int getMaxLength()
    {
        return maxLength;
    }

    /**
     * Sets the max summary length for this configuration.
     */
    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

    /**
     * Returns the min length for this configuration.
     */
    public int getMinLength()
    {
        return minLength;
    }

    /**
     * Sets the min length for this configuration.
     */
    public void setMinLength(int minLength)
    {
        this.minLength = minLength;
    }

    /**
     * Returns the min paragraph length for this configuration.
     */
    public int getMinParagraph()
    {
        return minParagraph;
    }

    /**
     * Sets the min paragraph length for this configuration.
     */
    public void setMinParagraph(int minParagraph)
    {
        this.minParagraph = minParagraph;
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
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Object doc)
    {
        if(doc instanceof Map)
        {
            Map map = (Map)doc;

            super.parseDocument(map);
            if(map.containsKey(IMAGE_PREFIX))
                setImagePrefix((String)map.get(IMAGE_PREFIX));
            if(map.containsKey(IMAGE_FORMAT))
                setImageFormat((String)map.get(IMAGE_FORMAT));
            if(map.containsKey(MAX_LENGTH))
                setMaxLength((Integer)map.get(MAX_LENGTH));
            if(map.containsKey(MIN_LENGTH))
                setMinLength((Integer)map.get(MIN_LENGTH));
            if(map.containsKey(MIN_PARAGRAPH))
                setMinParagraph((Integer)map.get(MIN_PARAGRAPH));
            if(map.containsKey(PAGES))
            {
                List<Map<String,Object>> pages = (List<Map<String,Object>>)map.get(PAGES);
                for(Map<String,Object> page : pages)
                {
                    for(Map.Entry<String,Object> entry : page.entrySet())
                    {
                        WebPageConfiguration config = new WebPageConfiguration(entry.getKey());
                        config.parseDocument(entry.getValue());
                        addPage(config);
                    }
                }
            }
        }
    }
}