/*
 * Copyright 2023 Gerald Curley
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

import com.opsmatters.media.model.content.ResourceItem;

/**
 * Class representing a tool list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ToolItem extends ResourceItem<Tool>
{
    private Tool content = new Tool();

    /**
     * Default constructor.
     */
    public ToolItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public ToolItem(ToolItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a tool.
     */
    public ToolItem(Tool obj)
    {
        super.set(content);
        copyAttributes(obj);
        content.setConfigured(false);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ToolItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(Tool obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public Tool get()
    {
        return content;
    }

    /**
     * Returns the pricing.
     */
    public String getPricing()
    {
        return content.getPricing();
    }

    /**
     * Sets the pricing.
     */
    public void setPricing(String pricing)
    {
        content.setPricing(pricing);
    }
}