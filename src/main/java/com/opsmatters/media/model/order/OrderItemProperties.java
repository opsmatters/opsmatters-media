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

import java.util.Map;
import java.util.LinkedHashMap;
import java.time.Instant;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.Formats;

import static com.opsmatters.media.model.order.OrderItemProperty.*;

/**
 * Class representing the properties for an order item description.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrderItemProperties extends LinkedHashMap<String,String>
{
    /**
     * Default constructor.
     */
    public OrderItemProperties()
    {
    }

    /**
     * Constructor that takes a set of properties.
     */
    public OrderItemProperties(OrderItemProperties properties)
    {
        putAll(properties);
    }

    /**
     * Returns the value of the given property.
     */
    public String get(OrderItemProperty property)
    {
        return get(property.value());
    }

    /**
     * Returns <CODE>true</CODE> if the given property has been set.
     */
    public boolean containsKey(OrderItemProperty property)
    {
        return containsKey(property.value());
    }

    /**
     * Sets the value of the given property.
     */
    public void put(OrderItemProperty property, String value)
    {
        put(property.value(), value);
    }

    /**
     * Removes the given property.
     */
    public void remove(OrderItemProperty property)
    {
        remove(property.value());
    }

    /**
     * Sets the value of the DATE property using the given date.
     */
    public void setDate(Instant dt)
    {
        put(DATE, TimeUtils.toStringUTC(dt, Formats.ORDER_DATE_FORMAT).toUpperCase());
    }

    /**
     * Sets the value of the DOMAIN property using the given site.
     */
    public void setDomain(Site site)
    {
        put(DOMAIN, site.getDomain());
    }
}