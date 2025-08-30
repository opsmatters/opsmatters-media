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
import com.opsmatters.media.cache.order.contact.Contacts;
import com.opsmatters.media.model.order.Order;
import com.opsmatters.media.model.order.OrderItem;
import com.opsmatters.media.model.order.contact.Contact;
import com.opsmatters.media.model.order.contact.ContactProduct;
import com.opsmatters.media.model.order.product.Product;

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
    private static Map<String,Map<String,Order>> contactMap = new LinkedHashMap<String,Map<String,Order>>();

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
     * Returns the number of items for the given order.
     */
    public static int getItemCount(Order order)
    {
        int ret = -1;
        Map<String,OrderItem> items = itemMap(order);
        if(items != null)
            ret = items.size();
        return ret;
    }

    /**
     * Returns the list of items for the given order and product.
     */
    public static List<OrderItem> listItems(Order order, Product product)
    {
        List<OrderItem> ret = new ArrayList<OrderItem>();
        Map<String,OrderItem> items = itemMap(order);
        if(items != null)
        {
            for(OrderItem item : items.values())
            {
                if(product == null || item.getProductCode().equals(product.getCode()))
                {
                    ret.add(item);
                }
            }
        }

        return ret;
    }

    /**
     * Returns the list of items for the given order.
     */
    public static List<OrderItem> listItems(Order order)
    {
        return listItems(order, null);
    }

    /**
     * Returns the total amount of the items for the given order.
     */
    public static int getAmount(Order order)
    {
        int ret = 0;
        Map<String,OrderItem> items = itemMap(order);
        if(items != null)
        {
            for(OrderItem item : items.values())
            {
                if(item.isEnabled())
                    ret += (item.getPrice() * item.getQuantity());
            }
        }

        return ret;
    }

    /**
     * Adds the order with the given id.
     */
    public static void add(Order order)
    {
        if(order.isArchived())
            return;

        Order existing = getById(order.getId());
        if(existing != null)
        {
            Map<String,OrderItem> items = itemMap(order);
            remove(existing);
            if(items != null)
                itemMap.put(order.getId(), items);
        }

        orderMap.put(order.getId(), order);

        Map<String,Order> contactOrderMap = contactMap.get(order.getContactId());
        if(contactOrderMap == null)
        {
            contactOrderMap = new LinkedHashMap<String,Order>();
            contactMap.put(order.getContactId(), contactOrderMap);
        }

        contactOrderMap.put(order.getId(), order);
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

        Map<String,Order> contactOrderMap = contactMap.get(order.getContactId());
        if(contactOrderMap != null)
        {
            contactOrderMap.remove(order.getId());
        }
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
        List<Order> ret = new ArrayList<Order>();
        Map<String,Order> contactOrderMap = contactMap.get(contact.getId());
        if(contactOrderMap != null)
            ret.addAll(contactOrderMap.values());
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the contact for the order or any of its products have a delivery email.
     */
    public static boolean hasDeliveryEmail(Order order)
    {
        boolean ret = false;
        if(order != null)
        {
            Contact contact = Contacts.getById(order.getContactId());
            if(contact != null)
            {
                ret = contact.hasDeliveryEmail();

                if(!ret)
                {
                    List<OrderItem> items = listItems(order);
                    if(items != null)
                    {
                        for(OrderItem item : items)
                        {
                            ContactProduct product = Contacts.getProduct(contact, item.getProductCode());
                            if(product != null && product.hasDeliveryEmail())
                            {
                                ret = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return ret;
    }
}