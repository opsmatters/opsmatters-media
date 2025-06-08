/*
 * Copyright 2025 Gerald Curley
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

package com.opsmatters.media.model.order.contact;

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Represents a contact note.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ContactNote
{
    REMINDER("Reminder Sent", "Sent email reminder for unpaid invoice"),
    SUSPENDED("Suspended", "Suspended due to non-payment"),
    RATING_CHANGE("Rating Change", "Rating changed to '%s'");

    private String value;
    private String text;

    /**
     * Constructor that takes the type value and text.
     * @param value The value for the type
     * @param text The text for the type
     */
    ContactNote(String value, String text)
    {
        this.value = value;
        this.text = text;
    }

    /**
     * Returns the value of the type.
     * @return The value of the type.
     */
    public String toString()
    {
        return value();
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
     * Returns the text of the type.
     * @return The text of the type.
     */
    public String text()
    {
        return text;
    }

    /**
     * Returns the formatted text of the type.
     * @return The formatted text of the type.
     */
    public String format(String... params)
    {
        return String.format("%s: %s",
            TimeUtils.toStringUTC(Formats.DATE_FORMAT),
            String.format(text(), params));
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static ContactNote fromValue(String value)
    {
        ContactNote[] types = values();
        for(ContactNote type : types)
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
     * Returns a list of the contact types.
     */
    public static List<ContactNote> toList()
    {
        List<ContactNote> ret = new ArrayList<ContactNote>();

        ret.add(REMINDER);
        ret.add(SUSPENDED);
 
        return ret;
    }
}