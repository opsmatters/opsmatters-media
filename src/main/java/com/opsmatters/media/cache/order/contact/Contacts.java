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
import com.opsmatters.media.model.order.contact.ContactPerson;

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
    private static Map<String,ContactPerson> personIdMap = new TreeMap<String,ContactPerson>();
    private static Map<String,ContactPerson> personNameMap = new TreeMap<String,ContactPerson>();
    private static Map<String,ContactPerson> personEmailMap = new TreeMap<String,ContactPerson>();
    private static Map<String,Map<String,ContactPerson>> personContactMap = new LinkedHashMap<String,Map<String,ContactPerson>>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Contacts()
    {
    }

    /**
     * Returns <CODE>true</CODE> if contacts and persons have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of contacts and persons.
     */
    public static void load(List<Contact> contacts, List<ContactPerson> persons)
    {
        initialised = false;

        clear();
        for(Contact contact : contacts)
        {
            add(contact);
        }

        logger.info("Loaded "+size()+" contacts");

        for(ContactPerson person : persons)
        {
            add(person);
        }

        logger.info("Loaded "+personNameMap.size()+" contact persons");

        initialised = true;
    }

    /**
     * Clears the contacts and persons.
     */
    public static void clear()
    {
        idMap.clear();
        nameMap.clear();
        emailMap.clear();
        personIdMap.clear();
        personNameMap.clear();
        personEmailMap.clear();
        personContactMap.clear();
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
     * Returns the contact person with the given id.
     */
    public static ContactPerson getPersonById(String id)
    {
        return id != null ? personIdMap.get(id) : null;
    }

    /**
     * Returns the contact person with the given name.
     */
    public static ContactPerson getPersonByName(String name)
    {
        return name != null ? personNameMap.get(name) : null;
    }

    /**
     * Returns the contact person with the given email.
     */
    public static ContactPerson getPersonByEmail(String email)
    {
        return email != null ? personEmailMap.get(email) : null;
    }

    /**
     * Returns the contact for the given person.
     */
    public static Contact getByPerson(ContactPerson person)
    {
        return person != null ? getById(person.getContactId()) : null;
    }

    /**
     * Adds the given contact.
     */
    public static void add(Contact contact)
    {
        idMap.put(contact.getId(), contact);
        nameMap.put(contact.getName(), contact);
        emailMap.put(contact.getBillingEmail(), contact);
    }

    /**
     * Adds the given contact person.
     */
    public static void add(ContactPerson person)
    {
        personIdMap.put(person.getId(), person);
        personNameMap.put(person.getName(), person);
        personEmailMap.put(person.getEmail(), person);

        Map<String,ContactPerson> persons = personContactMap.get(person.getContactId());
        if(persons == null)
        {
            persons = new LinkedHashMap<String,ContactPerson>();
            personContactMap.put(person.getContactId(), persons);
        }

        persons.put(person.getId(), person);
    }

    /**
     * Removes the given contact.
     */
    public static void remove(Contact contact)
    {
        idMap.remove(contact.getId());
        nameMap.remove(contact.getName());
        emailMap.remove(contact.getBillingEmail());
    }

    /**
     * Removes the given contact person.
     */
    public static void remove(ContactPerson person)
    {
        personIdMap.remove(person.getId());
        personNameMap.remove(person.getName());
        personEmailMap.remove(person.getEmail());
        personContactMap.get(person.getContactId()).remove(person.getId());
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

    /**
     * Returns the list of contact persons.
     */
    public static List<ContactPerson> listPersons()
    {
        List<ContactPerson> ret = new ArrayList<ContactPerson>();
        for(ContactPerson person : personNameMap.values())
        {
            if(person.isEnabled())
                ret.add(person);
        }

        return ret;
    }

    /**
     * Returns the list of contact persons for the given contact.
     */
    public static List<ContactPerson> listPersons(Contact contact)
    {
        List<ContactPerson> ret = new ArrayList<ContactPerson>();
        Map<String,ContactPerson> persons = personContactMap.get(contact.getId());
        if(persons != null)
        {
            for(ContactPerson person : persons.values())
            {
                if(person.isEnabled())
                    ret.add(person);
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given name matches the given contact or its list of persons.
     */
    public static boolean matches(Contact contact, String name)
    {
        boolean ret = false;

        name = name.toLowerCase();

        if(contact.getName().toLowerCase().indexOf(name) != -1)
        {
            ret = true;
        }
        else // Next, check the persons for a match
        {
            Map<String,ContactPerson> persons = personContactMap.get(contact.getId());
            if(persons != null)
            {
                for(ContactPerson person : persons.values())
                {
                    if(person.isEnabled()
                        && person.getName().toLowerCase().indexOf(name) != -1)
                    {
                        ret = true;
                        break;
                    }
                }
            }
        }

        return ret;
    }
}