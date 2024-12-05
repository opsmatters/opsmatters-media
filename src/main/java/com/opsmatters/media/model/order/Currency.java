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
 * Represents the corrency for an order.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum Currency
{
    UNDEFINED("???", "Undefined"),
    US_DOLLAR("USD", "United States dollar"),
    UK_POUND("GBP", "Pound sterling"),
    EURO("EUR", "Euro"),
    ALL("", "All"); // Pseudo status

    private String code;
    private String value;

    /**
     * Constructor that takes the currency code and value.
     * @param code The code for the currency
     * @param value The value for the currency
     */
    Currency(String code, String value)
    {
        this.code = code;
        this.value = value;
    }

    /**
     * Returns the value of the currency.
     * @return The value of the currency.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the currency.
     * @return The code of the currency.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the currency.
     * @return The value of the currency.
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
    public static Currency fromValue(String value)
    {
        Currency[] types = values();
        for(Currency type : types)
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
    public static Currency fromCode(String code)
    {
        Currency[] types = values();
        for(Currency type : types)
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
     * Returns a list of the currencies.
     */
    public static List<Currency> toList()
    {
        List<Currency> ret = new ArrayList<Currency>();

        for(Currency currency : values())
        {
            if(currency.code().length() > 0)
                ret.add(currency);
        }

        return ret;
    }
}