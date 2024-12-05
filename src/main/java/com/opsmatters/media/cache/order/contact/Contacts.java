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
package com.opsmatters.media.cache.order.contact;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.contact.Contact;

/**
 * Class representing the list of contacts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Contacts implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Contacts.class.getName());

    private static Map<String,Contact> idMap = new LinkedHashMap<String,Contact>();
    private static Map<String,Contact> nameMap = new TreeMap<String,Contact>();
    private static Map<String,Contact> emailMap = new LinkedHashMap<String,Contact>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Contacts()
    {
    }

    /**
     * Returns <CODE>true</CODE> if contacts have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of contacts.
     */
    public static void load(List<Contact> contacts)
    {
        initialised = false;

        clear();
        for(Contact contact : contacts)
        {
            add(contact);
        }

        logger.info("Loaded "+size()+" contacts");

        initialised = true;
    }

    /**
     * Clears the contacts.
     */
    public static void clear()
    {
        idMap.clear();
        nameMap.clear();
        emailMap.clear();
    }

    /**
     * Returns the contact with the given id.
     */
    public static Contact getById(String id)
    {
        return idMap.get(id);
    }

    /**
     * Returns the contact with the given name.
     */
    public static Contact getByName(String name)
    {
        return name != null ? nameMap.get(name) : null;
    }

    /**
     * Returns the contact with the given email.
     */
    public static Contact getByEmail(String email)
    {
        return email != null ? emailMap.get(email) : null;
    }

    /**
     * Adds the contact with the given name.
     */
    public static void add(Contact contact)
    {
        idMap.put(contact.getId(), contact);
        nameMap.put(contact.getName(), contact);
        emailMap.put(contact.getBillingEmail(), contact);
    }

    /**
     * Removes the contact with the given name.
     */
    public static void remove(Contact contact)
    {
        idMap.remove(contact.getId());
        nameMap.remove(contact.getName());
        emailMap.remove(contact.getBillingEmail());
    }

    /**
     * Returns the count of contacts.
     */
    public static int size()
    {
        return idMap.size();
    }

    /**
     * Returns the list of contacts.
     */
    public static List<Contact> list()
    {
        List<Contact> ret = new ArrayList<Contact>();
        for(Contact contact : nameMap.values())
        {
            if(contact.isActive())
                ret.add(contact);
        }

        return ret;
    }
}