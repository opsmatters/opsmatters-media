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
import com.opsmatters.media.config.YamlConfiguration;

/**
 * Class that represents a YAML configuration used to parse a content summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SummaryConfiguration extends YamlConfiguration
{
    public static final String MAX_LENGTH = "max-length";
    public static final String MIN_LENGTH = "min-length";
    public static final String MIN_PARAGRAPH = "min-paragraph";

    private int maxLength = 0;
    private int minLength = 0;
    private int minParagraph = 0;

    /**
     * Default constructor.
     */
    public SummaryConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public SummaryConfiguration(SummaryConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SummaryConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setMaxLength(obj.getMaxLength());
            setMinLength(obj.getMinLength());
            setMinParagraph(obj.getMinParagraph());
        }
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
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Object doc)
    {
        if(doc instanceof Map)
        {
            Map map = (Map)doc;

            if(map.containsKey(MAX_LENGTH))
                setMaxLength((Integer)map.get(MAX_LENGTH));
            if(map.containsKey(MIN_LENGTH))
                setMinLength((Integer)map.get(MIN_LENGTH));
            if(map.containsKey(MIN_PARAGRAPH))
                setMinParagraph((Integer)map.get(MIN_PARAGRAPH));
        }
    }
}