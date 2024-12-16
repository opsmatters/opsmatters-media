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
 * Represents the payment term.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum PaymentTerm
{
    UNDEFINED("", "Undefined"),
    ON_RECEIPT("DUE_ON_RECEIPT", "On Receipt"),
    NET10("NET_10", "NET 10"),
    NET30("NET_30", "NET 30"),
    ALL("", "All"); // Pseudo status

    private String code;
    private String value;

    /**
     * Constructor that takes the term value.
     * @param code The value for the term
     * @param value The value for the term
     */
    PaymentTerm(String code, String value)
    {
        this.code = code;
        this.value = value;
    }

    /**
     * Returns the value of the term.
     * @return The value of the term.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the term.
     * @return The code of the term.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the term.
     * @return The value of the term.
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
    public static PaymentTerm fromValue(String value)
    {
        PaymentTerm[] types = values();
        for(PaymentTerm type : types)
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
     * Returns a list of the payment terms.
     */
    public static List<PaymentTerm> toList()
    {
        List<PaymentTerm> ret = new ArrayList<PaymentTerm>();

        ret.add(UNDEFINED);
        ret.add(ON_RECEIPT);
        ret.add(NET10);
        ret.add(NET30);

        return ret;
    }
}