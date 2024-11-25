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
 * Represents the payment method.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum PaymentMethod
{
    UNDEFINED("Undefined"),
    PAYPAL("PayPal"),
    PAYONEER("Payoneer"),
    STRIPE("Stripe"),
    BANK_TRANSFER("Bank Transfer"),
    ALL("All"); // Pseudo status

    private String value;

    /**
     * Constructor that takes the method value.
     * @param value The value for the method
     */
    PaymentMethod(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the method.
     * @return The value of the method.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the method.
     * @return The value of the method.
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
    public static PaymentMethod fromValue(String value)
    {
        PaymentMethod[] types = values();
        for(PaymentMethod type : types)
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
     * Returns a list of the payment methods.
     */
    public static List<PaymentMethod> toList()
    {
        List<PaymentMethod> ret = new ArrayList<PaymentMethod>();

        ret.add(UNDEFINED);
        ret.add(PAYPAL);
        ret.add(PAYONEER);
        ret.add(STRIPE);
        ret.add(BANK_TRANSFER);

        return ret;
    }
}