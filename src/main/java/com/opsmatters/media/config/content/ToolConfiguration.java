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

/**
 * Class that represents the configuration for tool content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ToolConfiguration extends ContentConfiguration
{
    private static final Logger logger = Logger.getLogger(ToolConfiguration.class.getName());

    /**
     * Default constructor.
     */
    public ToolConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public ToolConfiguration(ToolConfiguration obj)
    {
        super(obj != null ? obj.getName() : null);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ToolConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
        }
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.TOOL;
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
        }
    }
}