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
package com.opsmatters.media.crawler.parser;

import java.util.regex.Pattern;

import static com.opsmatters.media.crawler.parser.ElementType.*;
import static com.opsmatters.media.crawler.parser.ElementDisplay.*;

/**
 * Class representing a body element.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BodyElement
{
    // Pattern to detect URLs in strings
    private static final String TIMESTAMP_REGEX = "^\\s*\\d{1,2}:\\d{2}[\\s|-]+.*";
    private static final Pattern timestampPattern = Pattern.compile(TIMESTAMP_REGEX);

    private String tag = null;
    private ElementType type;
    private String listType;
    private StringBuilder text = new StringBuilder();
    private boolean strong = true;
    private ElementDisplay display = INLINE;
    private boolean hasBR = false;

    /**
     * Constructor that takes a tag, value and strong flag.
     */
    public BodyElement(String tag, String str, boolean strong)
    {
        this.strong = strong;
        this.tag = tag;

        if(isBlock(tag))
            display = BLOCK;

        // Remove any leading <br>
        if(str.startsWith("<br>"))
        {
            hasBR = true;
            str = str.substring("<br>".length()).trim();
        }

        setType(tag, str);
        text.append(str);
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
    public void setType(String tag, String text)
    {
        if(tag.startsWith("h"))
            type = TITLE;
        else if(tag.equals("blockquote"))
            type = QUOTE;
        else if(tag.equals("pre"))
            type = PRE;
        else if(tag.equals("ul") || tag.equals("ol") || tag.equals("li"))
            type = LIST;
        else if(tag.equals("table"))
            type = TABLE;
        else if(tag.equals("figure"))
            type = FIGURE;
        else if(tag.equals("iframe"))
            type = IFRAME;
        else if(timestampPattern.matcher(text).matches()) // looks for nn:nn timestamp
            type = TIMESTAMP;
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
     * Returns the list type of the element.
     */
    public String getListType()
    {
        return listType;
    }

    /**
     * Sets the list type of the element.
     */
    public void setListType(String listType)
    {
        this.listType = listType;
    }

    /**
     * Returns the text of the element.
     */
    public String getText()
    {
        return text.toString();
    }

    /**
     * Returns <CODE>true</CODE> if the element text starts with <br>.
     */
    public boolean hasBR()
    {
        return hasBR;
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
     * Returns <CODE>true</CODE> if the given tag is a block element.
     */
    public boolean isBlock(String tag)
    {
        return tag.equals("p") || tag.startsWith("h")
            || tag.equals("blockquote") || tag.equals("pre")
            || tag.equals("ul") || tag.equals("ol")
            || tag.equals("li") || tag.equals("table")
            || tag.equals("figure") || tag.equals("iframe")
            || tag.equals("aside");
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
        append(element.getText());
    }

    /**
     * Append the given string to the element.
     */
    public void append(String str)
    {
        if(str != null && str.length() > 0)
        {
            // Check if the string starts with a line break
            boolean hasBR = false;
            if(str.startsWith("<br>"))
            {
                hasBR = true;
                str = str.substring("<br>".length());
            }

            if(text.length() > 0
                && needsSpace(str)
                && (text.charAt(text.length()-1) != '\n' || hasBR))
            {
                if(hasBR)
                    appendBR();
                text.append(" ");
            }

            text.append(str);
        }
    }

    /**
     * Returns <CODE>true</CODE> if the given string needs a space before appending.
     */
    private boolean needsSpace(String str)
    {
        boolean ret = false;
        if(str.length() > 0)
        {
            char c = str.charAt(0);
            ret = Character.isLetterOrDigit(c);
        }
        return ret;
    }

    /**
     * Append a &lt;br&gt; to the text.
     */
    public void appendBR()
    {
        text.append("<br>");
    }
}