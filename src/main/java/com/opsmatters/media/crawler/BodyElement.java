/*
 * Copyright 2021 Gerald Curley
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
package com.opsmatters.media.crawler;

import static com.opsmatters.media.crawler.ElementType.*;
import static com.opsmatters.media.crawler.ElementDisplay.*;

/**
 * Class representing a body element.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BodyElement
{
    private String tag = null;
    private ElementType type;
    private StringBuilder text = new StringBuilder();
    private boolean strong = true;
    private ElementDisplay display = INLINE;

    /**
     * Constructor that takes a tag, value and strong flag.
     */
    public BodyElement(String tag, String str, boolean strong)
    {
        this.strong = strong;
        this.tag = tag;
        setType(tag);

        if(isBlock(tag))
            display = BLOCK;
        text.append(str);
    }

    /**
     * Returns <CODE>true</CODE> if the given tag is a block element.
     */
    public boolean isBlock(String tag)
    {
        return tag.equals("p") || tag.startsWith("h")
            || tag.equals("blockquote") || tag.equals("pre")
            || tag.equals("ul") || tag.equals("ol")
            || tag.equals("aside");
    }

    /**
     * Returns the tag of the element.
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * Returns the type of the element.
     */
    public ElementType getType()
    {
        return type;
    }

    /**
     * Sets the type of the element.
     */
    public void setType(String tag)
    {
        if(tag.startsWith("h"))
            type = TITLE;
        else if(tag.equals("blockquote"))
            type = QUOTE;
        else if(tag.equals("pre"))
            type = PRE;
        else if(tag.equals("ul") || tag.equals("ol") || tag.equals("li"))
            type = LIST;
        else
            type = TEXT;
    }

    /**
     * Sets the type of the element.
     */
    public void setType(ElementType type)
    {
        this.type = type;
    }

    /**
     * Returns the text of the element.
     */
    public String getText()
    {
        return text.toString();
    }

    /**
     * Returns <CODE>true</CODE> if the element is strong.
     */
    public boolean isStrong()
    {
        return strong;
    }

    /**
     * Returns the display mode of the element.
     */
    public ElementDisplay getDisplay()
    {
        return display;
    }

    /**
     * Returns <CODE>true</CODE> if the display mode is a block element.
     */
    public boolean isBlock()
    {
        return display == BLOCK || display == INLINE_BLOCK;
    }

    /**
     * Sets the display mode of the element.
     */
    public void setDisplay(ElementDisplay display)
    {
        this.display = display;
    }

    /**
     * Append the given text to the element.
     */
    public void append(BodyElement element)
    {
        String elementText = element.getText();
        if(elementText.length() > 0)
        {
            if(text.length() > 0)
                text.append(" ");
            text.append(elementText);
        }
    }
}