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

/**
 * Represents a contact rating.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ContactRating
{
    NON_PAYER("Non Payer", -10),
    BAD_PAYER("Bad Payer", -3),
    LATE_PAYER("Late Payer", -2),
    SUSPECT("Suspect", -1),
    NEUTRAL("Neutral", 0),
    UNDEFINED("Undefined", 0),
    OCCASIONAL("Occasional", 1),
    REGULAR("Regular", 10),
//GERALD: are these needed?
//    GOOD("Good"), // Pseudo status
//    VERY_GOOD("Very Good"), // Pseudo status
//    BAD("Bad"), // Pseudo status
//    VERY_BAD("Very Bad"), // Pseudo status
    ALL("All"); // Pseudo status

    private String value;
    private int precedence;

    /**
     * Constructor that takes the type value and precedence.
     * @param value The value for the type
     * @param precedence The precedence for the type
     */
    ContactRating(String value, int precedence)
    {
        this.value = value;
        this.precedence = precedence;
    }

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     */
    ContactRating(String value)
    {
        this.value = value;
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
     * Returns the precedence of the type.
     * @return The precedence of the type.
     */
    public int precedence()
    {
        return precedence;
    }

    /**
     * Returns <CODE>true</CODE> if the precedence is positive.
     * @return <CODE>true</CODE> if the precedence is positive
     */
    public boolean good()
    {
        return precedence > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the precedence is very positive.
     * @return <CODE>true</CODE> if the precedence is very positive
     */
    public boolean veryGood()
    {
        return precedence >= 10;
    }

    /**
     * Returns <CODE>true</CODE> if the precedence is negative.
     * @return <CODE>true</CODE> if the precedence is negative
     */
    public boolean bad()
    {
        return precedence < 0;
    }

    /**
     * Returns <CODE>true</CODE> if the precedence is very negative.
     * @return <CODE>true</CODE> if the precedence is very negative
     */
    public boolean veryBad()
    {
        return precedence <= -10;
    }

    /**
     * Returns the CSS class for the rating.
     * @return the CSS class for the rating
     */
    public String css()
    {
        String ret = "";
        if(veryBad())
            ret = "rating-very-bad";
        else if(bad())
            ret = "rating-bad";
        else if(veryGood())
            ret = "rating-very-good";
        else if(good())
            ret = "rating-good";

        return ret;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static ContactRating fromValue(String value)
    {
        ContactRating[] types = values();
        for(ContactRating type : types)
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
     * Returns a list of the contact ratings.
     */
    public static List<ContactRating> toList()
    {
        List<ContactRating> ret = new ArrayList<ContactRating>();

        ret.add(UNDEFINED);
        ret.add(NON_PAYER);
        ret.add(BAD_PAYER);
        ret.add(LATE_PAYER);
        ret.add(SUSPECT);
        ret.add(NEUTRAL);
        ret.add(OCCASIONAL);
        ret.add(REGULAR);
 
        return ret;
    }
}