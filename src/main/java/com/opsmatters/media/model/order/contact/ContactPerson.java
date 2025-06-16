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
package com.opsmatters.media.model.order.contact;

import java.time.Instant;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.FormatUtils;

/**
 * Class representing a contact email.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContactPerson extends BaseEntity
{
    private String contactId = "";
    private String name = "";
    private String email = "";
    private String salutation = "";
    private boolean enabled = true;

    /**
     * Default constructor.
     */
    public ContactPerson()
    {
    }

    /**
     * Constructor that takes a contact.
     */
    public ContactPerson(Contact contact)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setContactId(contact.getId());
    }

    /**
     * Copy constructor.
     */
    public ContactPerson(ContactPerson obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContactPerson obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setContactId(obj.getContactId());
            setName(obj.getName());
            setEmail(obj.getEmail());
            setSalutation(obj.getSalutation());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the contact id.
     */
    public String getContactId()
    {
        return contactId;
    }

    /**
     * Sets the contact id.
     */
    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    /**
     * Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the email.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the email.
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Returns the salutation.
     */
    public String getSalutation()
    {
        return salutation;
    }

    /**
     * Sets the salutation.
     */
    public void setSalutation(String salutation)
    {
        this.salutation = salutation;
    }

    /**
     * Sets the default salutation.
     */
    public void setSalutation()
    {
        setSalutation(FormatUtils.getSalutation(getName()));
    }

    /**
     * Returns <CODE>true</CODE> if the salutation has been set.
     */
    public boolean hasSalutation()
    {
        return getSalutation() != null && getSalutation().length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the person is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Returns <CODE>true</CODE> if this person is enabled.
     */
    public Boolean getEnabledObject()
    {
        return Boolean.valueOf(isEnabled());
    }

    /**
     * Set to <CODE>true</CODE> if the person is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Set to <CODE>true</CODE> if this person is enabled.
     */
    public void setEnabledObject(Boolean enabled)
    {
        setEnabled(enabled != null && enabled.booleanValue());
    }
}