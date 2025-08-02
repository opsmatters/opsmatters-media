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

package com.opsmatters.media.model.order.product;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the id of a product text.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ProductTextId
{
    INVOICE_ITEM("INV-ITM"),
    DELIVERED("EML-DEL"),
    SUSPECT("EML-SUS"),
    UNPAID("EML-UNP"),
    CANCELLED("EML-CAN"),
    ALL("All"); // Pseudo status

    private String code;

    /**
     * Constructor that takes the type code.
     * @param code The code for the type
     */
    ProductTextId(String code)
    {
        this.code = code;
    }

    /**
     * Returns the code of the type.
     * @return The code of the type.
     */
    public String toString()
    {
        return code();
    }

    /**
     * Returns the code of the type.
     * @return The code of the type.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static ProductTextId fromCode(String code)
    {
        ProductTextId[] types = values();
        for(ProductTextId type : types)
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
     * Returns a list of the text keys.
     */
    public static List<ProductTextId> toList()
    {
        List<ProductTextId> ret = new ArrayList<ProductTextId>();

        ret.add(INVOICE_ITEM);
        ret.add(DELIVERED);
        ret.add(SUSPECT);
        ret.add(UNPAID);
        ret.add(CANCELLED);
 
        return ret;
    }
}