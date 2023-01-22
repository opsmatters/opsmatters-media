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

package com.opsmatters.media.model.content;

import java.util.List;
import java.util.ArrayList;

import static com.opsmatters.media.model.content.ContentType.*;

/**
 * Represents a content link text.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum LinkText
{
    GO_AGENDA("Go to Agenda and Booking", EVENT),
    GO_REGISTRATION("Go to Registration", EVENT),
    GET_CASE_STUDY("Get the Case Study", PUBLICATION),
    GET_DATASHEET("Get the Datasheet", PUBLICATION),
    GET_EBOOK("Get the EBook", PUBLICATION),
    GET_GUIDE("Get the Guide", PUBLICATION),
    GET_HANDBOOK("Get the Handbook", PUBLICATION),
    GET_REPORT("Get the Report", PUBLICATION),
    GET_SOLUTION_BRIEF("Get the Solution Brief", PUBLICATION),
    GET_WHITE_PAPER("Get the White Paper", PUBLICATION),
    DOWNLOAD("Download", TOOL),
    GET_STARTED("Get Started", TOOL),
    VIEW_WEBSITE("View on Website", TOOL),
    LEARN_MORE("Learn More", new ContentType[] {TOOL,JOB});

    private String value;
    private ContentType[] types;

    /**
     * Constructor that takes the text value and content types.
     * @param value The value for the text
     * @param types The content types for the text
     */
    LinkText(String value, ContentType... types)
    {
        this.value = value;
        this.types = types;
    }

    /**
     * Returns the value of the text.
     * @return The value of the text.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the text.
     * @return The value of the text.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the content types for the text.
     * @return The content types for the text.
     */
    public ContentType[] types()
    {
        return types;
    }

    /**
     * Returns the text for the given value.
     * @param value The text value
     * @return The text for the given value
     */
    public static LinkText fromValue(String value)
    {
        LinkText[] types = values();
        for(LinkText type : types)
        {
            if(type.value().equals(value))
                return type;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of texts.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of texts
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }

    /**
     * Returns a list of the texts for the given type.
     */
    public static List<String> toList(ContentType filter)
    {
        List<String> ret = new ArrayList<String>();

        for(LinkText text : LinkText.values())
        {
            for(ContentType type : text.types())
            {
                if(type == filter)
                {
                    ret.add(text.value());
                    break;
                }
            }
        }

        return ret;
    }
}