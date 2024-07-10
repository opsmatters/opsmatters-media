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

package com.opsmatters.media.util;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the HTML wrapper elements.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum HtmlElement
{
    NONE(""),
    P("p"),
    DIV("div"),
    FIGURE("figure");

    private String tag;

    /**
     * Constructor that takes the element tag.
     * @param tag The element tag
     */
    HtmlElement(String tag)
    {
        this.tag = tag;
    }

    /**
     * Returns the tag of the element.
     * @return The tag of the element.
     */
    public String toString()
    {
        return name();
    }

    /**
     * Returns the tag of the element.
     * @return The tag of the element.
     */
    public String tag()
    {
        return tag;
    }

    /**
     * Returns the list of elements.
     * @return The list of elements.
     */
    public static List<HtmlElement> toList()
    {
        List<HtmlElement> ret = new ArrayList<HtmlElement>();

        for(HtmlElement element : HtmlElement.values())
            ret.add(element);

        return ret;
    }
}