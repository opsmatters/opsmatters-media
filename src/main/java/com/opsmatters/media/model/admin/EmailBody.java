/*
 * Copyright 2020 Gerald Curley
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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Class representing an email message body.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EmailBody
{
    public static final String TEXT_STYLE = 
        "font-family:Helvetica Neue, Arial, Sans-serif; font-size:16px; padding:10px;";
    public static final String TABLE_STYLE = 
        "font-family:Helvetica Neue, Arial, Sans-serif; font-size:14px; background:#F8F8F8; margin: 0 0 0 20px;";
    public static final String NAME_STYLE = 
        "background:#E8E8E8; color:#274589; font-weight: 700; padding:10px; vertical-align:top; border:1px solid white;";
    public static final String VALUE_STYLE = 
        "background:#F8F8F8; color:#808080; padding:10px; border:1px solid white; border-collapse:collapse;";

    private List<String> paragraphs = new ArrayList<String>();

    /**
     * Default constructor.
     */
    public EmailBody()
    {
    }

    /**
     * Constructor that takes a text body.
     */
    public EmailBody(String body)
    {
        addParagraph(body);
    }

    /**
     * Returns the subject.
     */
    public String toString()
    {
        return getText();
    }

    /**
     * Adds a new paragraph.
     */
    public EmailBody addParagraph(String paragraph)
    {
        paragraphs.add(toParagraph(paragraph, TEXT_STYLE));
        return this;
    }

    /**
     * Adds a new set of properties for a table.
     */
    public EmailBody addTable(Map<String,Object> map)
    {
        paragraphs.add(toParagraph(map));
        return this;
    }

    /**
     * Adds a new set of properties for a table using an array.
     */
    public EmailBody addTable(String[][] array)
    {
        Map<String,Object> map = new LinkedHashMap<>(array.length);
        for(int i = 0; i < array.length; i++)
        {
            String[] entry = array[i];
            if(entry.length >= 2)
                map.put(entry[0], entry[1]);
        }

        return addTable(map);
    }

    /**
     * Convert the given set of properties to a paragraph.
     */
    private String toParagraph(Map<String,Object> map)
    {
        StringBuilder ret = new StringBuilder();
        if(map.size() > 0)
        {
            ret.append(String.format("<table cellspacing=0 style=\"%s\">", TABLE_STYLE));

            for(Map.Entry<String,Object> entry : map.entrySet())
            {
                ret.append("<tr>");
                ret.append(String.format("<td style=\"%s\">%s</td>", NAME_STYLE, entry.getKey()));
                ret.append(String.format("<td style=\"%s\">%s</td>", VALUE_STYLE, entry.getValue()));
                ret.append("</tr>");
            }

            ret.append("</table>");
        }

        return toParagraph(ret.toString(), null);
    }

    /**
     * Convert the given string to a paragraph with a style.
     */
    private String toParagraph(String text, String style)
    {
        String ret = text;

        // Check if the text is already marked up
        if(!text.startsWith("<p"))
        {
            if(style != null)
                ret= String.format("<p style=\"%s\">%s</p>", style, text);
            else
                ret = String.format("<p>%s</p>", text);
        }

        return ret;
    }

    /**
     * Generates the message text from the paragraphs.
     */
    public String getText()
    {
        StringBuilder ret = new StringBuilder();
        for(String paragraph : paragraphs)
            ret.append(paragraph);
        return ret.toString();
    }
}