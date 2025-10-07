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
import com.opsmatters.media.cache.admin.Parameters;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.order.Currency;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.FormatUtils;

import static com.opsmatters.media.model.admin.ParameterType.*;
import static com.opsmatters.media.model.admin.ParameterName.*;

/**
 * Class representing a contact.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Contact extends BaseEntity
{
    private String name = "";
    private ContactType type = ContactType.UNDEFINED;
    private String code = "";
    private String email = ""; // Only for search
    private String website = "";
    private String salutation = "";
    private String notes = "";
    private Currency currency = Currency.UNDEFINED;
    private ContactStatus status = ContactStatus.NEW;
    private SuspendReason reason = SuspendReason.NONE;
    private ContactRating rating = ContactRating.UNDEFINED;
    private boolean deliveryEmail = false;
    private boolean completionEmail = false;

    /**
     * Default constructor.
     */
    public Contact()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public Contact(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setType(ContactType.BUYER);
        setName(name);
        setSalutation();
        setCurrency(Parameters.get(ORDER, CURRENCY).getValue());
        setRating(ContactRating.NEUTRAL);
        setDeliveryEmail(true);
        setCompletionEmail(true);
    }

    /**
     * Copy constructor.
     */
    public Contact(Contact obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Contact obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setType(obj.getType());
            setCode(obj.getCode());
            setWebsite(obj.getWebsite());
            setSalutation(obj.getSalutation());
            setNotes(obj.getNotes());
            setCurrency(obj.getCurrency());
            setStatus(obj.getStatus());
            setReason(obj.getReason());
            setRating(obj.getRating());
            setDeliveryEmail(obj.hasDeliveryEmail());
            setCompletionEmail(obj.hasCompletionEmail());
        }
    }

    /**
     * Returns the contact name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the contact name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the contact name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the contact salutation.
     */
    public String getSalutation()
    {
        return salutation;
    }

    /**
     * Sets the contact salutation.
     */
    public void setSalutation(String salutation)
    {
        this.salutation = salutation;
    }

    /**
     * Sets the default contact salutation.
     */
    public void setSalutation()
    {
        setSalutation(FormatUtils.getSalutation(getName()));
    }

    /**
     * Returns <CODE>true</CODE> if the contact salutation has been set.
     */
    public boolean hasSalutation()
    {
        return getSalutation() != null && getSalutation().length() > 0;
    }

    /**
     * Returns the type.
     */
    public ContactType getType()
    {
        return type;
    }

    /**
     * Sets the type.
     */
    public void setType(String type)
    {
        setType(ContactType.valueOf(type));
    }

    /**
     * Sets the type.
     */
    public void setType(ContactType type)
    {
        this.type = type;
    }

    /**
     * Returns the contact organisation.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the contact organisation.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the contact email.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the contact email.
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Returns the contact website.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * Sets the contact website.
     */
    public void setWebsite(String website)
    {
        this.website = website;
    }

    /**
     * Returns <CODE>true</CODE> if the contact website has been set.
     */
    public boolean hasWebsite()
    {
        return getWebsite() != null && getWebsite().length() > 0;
    }

    /**
     * Returns the notes.
     */
    public String getNotes()
    {
        return notes;
    }

    /**
     * Sets the notes.
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    /**
     * Adds a note to the notes.
     */
    public void addNote(String note)
    {
        String notes = getNotes();
        if(notes == null)
            notes = "";
        else if(notes.length() > 0)
            notes += "\n";
        notes += note;
        setNotes(notes);
    }

    /**
     * Returns the payment currency.
     */
    public Currency getCurrency()
    {
        return currency;
    }

    /**
     * Sets the payment currency.
     */
    public void setCurrency(String currency)
    {
        setCurrency(Currency.fromCode(currency));
    }

    /**
     * Sets the payment currency.
     */
    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    /**
     * Returns the contact status.
     */
    public ContactStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the contact status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == ContactStatus.ACTIVE;
    }

    /**
     * Returns <CODE>true</CODE> if the contact status is SUSPENDED.
     */
    public boolean isSuspended()
    {
        return status == ContactStatus.SUSPENDED;
    }

    /**
     * Sets the contact status.
     */
    public void setStatus(String status)
    {
        setStatus(ContactStatus.valueOf(status));
    }

    /**
     * Sets the contact status.
     */
    public void setStatus(ContactStatus status)
    {
        this.status = status;
    }

    /**
     * Returns the suspension reason.
     */
    public SuspendReason getReason()
    {
        return reason;
    }

    /**
     * Sets the suspension reason.
     */
    public void setReason(String reason)
    {
        setReason(SuspendReason.valueOf(reason));
    }

    /**
     * Sets the suspension reason.
     */
    public void setReason(SuspendReason reason)
    {
        this.reason = reason;
    }

    /**
     * Returns the rating.
     */
    public ContactRating getRating()
    {
        return rating;
    }

    /**
     * Sets the rating.
     */
    public void setRating(String rating)
    {
        setRating(ContactRating.valueOf(rating));
    }

    /**
     * Sets the rating.
     */
    public void setRating(ContactRating rating)
    {
        this.rating = rating;
    }

    /**
     * Returns <CODE>true</CODE> if this contact requires a delivery email.
     */
    public boolean hasDeliveryEmail()
    {
        return deliveryEmail;
    }

    /**
     * Returns <CODE>true</CODE> if this contact requires a delivery email.
     */
    public Boolean getDeliveryEmailObject()
    {
        return Boolean.valueOf(deliveryEmail);
    }

    /**
     * Set to <CODE>true</CODE> if this contact requires a delivery email.
     */
    public void setDeliveryEmail(boolean deliveryEmail)
    {
        this.deliveryEmail = deliveryEmail;
    }

    /**
     * Set to <CODE>true</CODE> if this contact requires a delivery email.
     */
    public void setDeliveryEmailObject(Boolean deliveryEmail)
    {
        setDeliveryEmail(deliveryEmail != null && deliveryEmail.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if this contact sends a completion email.
     */
    public boolean hasCompletionEmail()
    {
        return completionEmail;
    }

    /**
     * Returns <CODE>true</CODE> if this contact sends a completion email.
     */
    public Boolean getCompletionEmailObject()
    {
        return Boolean.valueOf(completionEmail);
    }

    /**
     * Set to <CODE>true</CODE> if this contact sends a completion email.
     */
    public void setCompletionEmail(boolean completionEmail)
    {
        this.completionEmail = completionEmail;
    }

    /**
     * Set to <CODE>true</CODE> if this contact sends a completion email.
     */
    public void setCompletionEmailObject(Boolean completionEmail)
    {
        setCompletionEmail(completionEmail != null && completionEmail.booleanValue());
    }
}