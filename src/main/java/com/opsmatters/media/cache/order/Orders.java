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
package com.opsmatters.media.cache.order;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.Order;
import com.opsmatters.media.model.order.OrderItem;
import com.opsmatters.media.model.order.contact.Contact;

/**
 * Class representing the list of orders.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Orders implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Orders.class.getName());

    private static Map<String,Order> orderMap = new LinkedHashMap<String,Order>();
    private static Map<String,Map<String,OrderItem>> itemMap = new LinkedHashMap<String,Map<String,OrderItem>>();
    private static Map<String,List<Order>> contactMap = new LinkedHashMap<String,List<Order>>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Orders()
    {
    }

    /**
     * Returns <CODE>true</CODE> if orders have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of orders.
     */
    public static void load(List<Order> orders, List<OrderItem> items)
    {
        initialised = false;

        clear();
        for(Order order : orders)
        {
            add(order);
        }

        logger.info("Loaded "+size()+" orders");

        int count = 0;
        for(OrderItem item : items)
        {
            add(item);
            ++count;
        }

        logger.info("Loaded "+count+" order items");

        initialised = true;
    }

    /**
     * Clears the orders.
     */
    public static void clear()
    {
        orderMap.clear();
        itemMap.clear();
        contactMap.clear();
    }

    /**
     * Returns the order with the given id.
     */
    public static Order getById(String id)
    {
        return orderMap.get(id);
    }

    /**
     * Returns the items for the given order.
     */
    public static Map<String,OrderItem> itemMap(Order order)
    {
        return itemMap.get(order.getId());
    }

    /**
     * Adds the order with the given id.
     */
    public static void add(Order order)
    {
        orderMap.put(order.getId(), order);

        List<Order> list = contactMap.get(order.getContactId());
        if(list == null)
        {
            list = new ArrayList<Order>();
            contactMap.put(order.getContactId(), list);
        }

        list.add(order);
    }

    /**
     * Adds the order item with the given id.
     */
    public static void add(OrderItem item)
    {
        Map<String,OrderItem> map = itemMap.get(item.getOrderId());
        if(map == null)
        {
            map = new LinkedHashMap<String,OrderItem>();
            itemMap.put(item.getOrderId(), map);
        }

        map.put(item.getId(), item);
    }

    /**
     * Removes the order with the given id.
     */
    public static void remove(Order order)
    {
        orderMap.remove(order.getId());
        itemMap.remove(order.getId());
    }

    /**
     * Removes the order item with the given id.
     */
    public static void remove(OrderItem item)
    {
        Map<String,OrderItem> map = itemMap.get(item.getOrderId());
        if(map != null)
            map.remove(item.getId());
    }

    /**
     * Returns the count of orders.
     */
    public static int size()
    {
        return orderMap.size();
    }

    /**
     * Returns the list of orders for the given contact.
     */
    public static List<Order> list(Contact contact)
    {
        return contactMap.get(contact.getId());
    }
}