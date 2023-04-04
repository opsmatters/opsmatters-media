/*
 * Copyright 2022 Gerald Curley
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

package com.opsmatters.media.model.content.publication;

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.LinkText;

/**
 * Represents a publication type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum PublicationType
{
    CASE_STUDY("Case Study"),
    CHEAT_SHEET("Cheat Sheet"),
    DATASHEET("Datasheet"),
    EBOOK("EBook"),
    HANDBOOK("Handbook"),
    GUIDE("Guide"),
    REPORT("Report"),
    SOLUTION_BRIEF("Solution Brief"),
    WHITE_PAPER("White Paper");

    private String value;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     */
    PublicationType(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the type.
     * @return The value of the type.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static PublicationType fromValue(String value)
    {
        PublicationType[] types = values();
        for(PublicationType type : types)
        {
            if(type.value().equals(value))
                return type;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }

    /**
     * Guesses the publication type from the given texts.
     */
    public static PublicationType guess(String[] texts)
    {
        PublicationType ret = null;
        for(String text : texts)
        {
            text = text.toLowerCase();
            if(ret == null)
                ret = test(text, WHITE_PAPER);
            if(ret == null)
                ret = test(text, EBOOK);
            if(ret == null)
                ret = test(text, SOLUTION_BRIEF);
            if(ret == null)
                ret = test(text, DATASHEET);
            if(ret == null)
                ret = test(text, HANDBOOK);
            if(ret == null)
                ret = test(text, CHEAT_SHEET);
            if(ret == null)
                ret = test(text, CASE_STUDY);
            if(ret == null)
                ret = test(text, GUIDE);
            if(ret == null)
                ret = test(text, REPORT);

            if(ret != null)
                break;
        }

        return ret;
    }

    /**
     * Returns the type for the given text.
     */
    private static PublicationType test(String text, PublicationType type)
    {
        if(text.indexOf(type.value().toLowerCase()) != -1)
            return type;
        return null;
    }

    /**
     * Returns the link text for the given type value.
     */
    public static String getLinkText(String value)
    {
        String ret = null;
        for(String linkText : LinkText.toList(ContentType.PUBLICATION))
        {
            if(linkText.indexOf(value) != -1)
            {
                ret = linkText;
                break;
            }
        }

        return ret;
    }

    /**
     * Returns a list of the type values.
     */
    public static List<String> toList(String blank)
    {
        List<String> ret = new ArrayList<String>();

        if(blank != null)
            ret.add(blank);
        for(PublicationType type : PublicationType.values())
            ret.add(type.value());

        return ret;
    }
}