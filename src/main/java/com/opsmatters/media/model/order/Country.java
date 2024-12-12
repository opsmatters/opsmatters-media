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

package com.opsmatters.media.model.order;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the country for an order.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum Country
{
    UNDEFINED("??", "Undefined"),
    ARMENIA("AM", "Armenia"),
    AUSTRALIA("AU", "Australia"),
    BULGARIA("BG", "Bulgaria"),
    CANADA("CA", "Canada"),
    CHINA("CN", "China"),
    CYPRUS("CY", "Cyprus"),
    ESTONIA("EE", "Estonia"),
    GEORGIA("GE", "Georgia"),
    GERMANY("DE", "Germany"),
    INDIA("IN", "India"),
    IRELAND("IE", "Ireland"),
    LITHUANIA("LT", "Lithuania"),
    MALTA("MT", "Malta"),
    MONTENEGRO("ME", "Montenegro"),
    NETHERLANDS("NL", "Netherlands"),
    PANAMA("PA", "Panama"),
    POLAND("PL", "Poland"),
    ROMANIA("RO", "Romania"),
    SINGAPORE("SG", "Singapore"),
    SPAIN("ES", "Spain"),
    SWEDEN("SE", "Sweden"),
    SWITZERLAND("CH", "Switzerland"),
    UAE("AE", "United Arab Emirates"),
    UK("GB", "United Kingdom"),
    UKRAINE("UA", "Ukraine"),
    USA("US", "United States"),
    VIETNAM("VN", "Vietnam"),
    ALL("", "All"); // Pseudo status

    private String code;
    private String value;

    /**
     * Constructor that takes the country code and value.
     * @param code The code for the country
     * @param value The value for the country
     */
    Country(String code, String value)
    {
        this.code = code;
        this.value = value;
    }

    /**
     * Returns the value of the country.
     * @return The value of the country.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the country.
     * @return The code of the country.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the country.
     * @return The value of the country.
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
    public static Country fromValue(String value)
    {
        Country[] types = values();
        for(Country type : types)
        {
            if(type.value().equals(value))
                return type;
        }
        return null;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static Country fromCode(String code)
    {
        Country[] types = values();
        for(Country type : types)
        {
            if(type.code().equals(code))
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
     * Returns a list of the countries.
     */
    public static List<Country> toList()
    {
        List<Country> ret = new ArrayList<Country>();

        for(Country country : values())
        {
            if(country.code().length() > 0)
                ret.add(country);
        }

        return ret;
    }
}