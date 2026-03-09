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

package com.opsmatters.media.client.payment.paypal;

import org.json.JSONArray;

/**
 * Represents the phone object for a PayPal invoice.
 */
public class Phones extends JSONArray
{
    /**
     * Default constructor.
     */
    public Phones() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Phones(JSONArray obj) 
    {
        for(int i = 0; i < obj.length(); i++)
            put(new Phone(obj.getJSONObject(i)));
    }
}