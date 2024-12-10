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

import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import com.opsmatters.media.cache.admin.Parameters;
import com.opsmatters.media.cache.order.contact.Contacts;
import com.opsmatters.media.cache.order.contact.Companies;
import com.opsmatters.media.model.OwnedEntity;
import com.opsmatters.media.model.order.contact.Contact;
import com.opsmatters.media.model.order.contact.Company;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.SessionId;

import static com.opsmatters.media.model.admin.ParameterType.*;
import static com.opsmatters.media.model.admin.ParameterName.*;

/**
 * Class representing an order.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Order extends OwnedEntity
{
    private String contactId = "";
    private String contactName = ""; // Used as a filter
    private int week, year = -1;
    private PaymentMethod method = PaymentMethod.UNDEFINED;
    private PaymentMode mode = PaymentMode.UNDEFINED;
    private String notes = "";
    private OrderStatus status = OrderStatus.NEW;
    private CancelReason reason = CancelReason.NONE;
    private Invoice invoice = new Invoice();

    /**
     * Default constructor.
     */
    public Order()
    {
    }

    /**
     * Constructor that takes a contact.
     */
    public Order(Contact contact)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setContactId(contact.getId());
        setPaymentMethod(contact.getPaymentMethod());
        setPaymentMode(contact.getPaymentMode());

        LocalDate dt = SessionId.now().atZone(ZoneId.of("UTC")).toLocalDate();
        setWeek(dt.get(WeekFields.ISO.weekOfWeekBasedYear()));
        setYear(dt.getYear());

        // Set the email and additional info for an invoice
        if(getPaymentMode() == PaymentMode.INVOICE)
        {
            String email = contact.getBillingEmail();
            String note = Parameters.get(ORDER, INVOICE_NOTE).getValue();

            if(contact.hasCompanyId())
            {
                Company company = Companies.getById(contact.getCompanyId());
                if(company != null)
                {
                    if(company.hasAdditionalInfo())
                        note = String.format("%s\n\n%s", company.getAdditionalInfo(), note);
                    if(company.hasBillingEmail())
                        email = company.getBillingEmail();
                }
            }

            getInvoice().setEmail(email);
            getInvoice().setNote(note);
            getInvoice().setCurrency(contact.getCurrency());
            getInvoice().setStatus(InvoiceStatus.NEW);
        }
    }

    /**
     * Copy constructor.
     */
    public Order(Order obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Order obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setContactId(obj.getContactId());
            setPaymentMethod(obj.getPaymentMethod());
            setPaymentMode(obj.getPaymentMode());
            setNotes(obj.getNotes());
            setStatus(obj.getStatus());
            setReason(obj.getReason());
            setWeek(obj.getWeek());
            setYear(obj.getYear());
            getInvoice().copyAttributes(obj.getInvoice());
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

        // Set the contact name
        Contact contact = Contacts.getById(contactId);
        setContactName(contact != null ? contact.getName() : "");
    }

    /**
     * Returns the contact name.
     */
    public String getContactName()
    {
        return contactName;
    }

    /**
     * Sets the contact name.
     */
    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    /**
     * Returns the week number.
     */
    public int getWeek()
    {
        return week;
    }

    /**
     * Sets the week number.
     */
    public void setWeek(int week)
    {
        this.week = week;
    }

    /**
     * Returns the year number.
     */
    public int getYear()
    {
        return year;
    }

    /**
     * Sets the year number.
     */
    public void setYear(int year)
    {
        this.year = year;
    }

    /**
     * Returns the payment method.
     */
    public PaymentMethod getPaymentMethod()
    {
        return method;
    }

    /**
     * Sets the payment method.
     */
    public void setPaymentMethod(String method)
    {
        setPaymentMethod(PaymentMethod.valueOf(method));
    }

    /**
     * Sets the payment method.
     */
    public void setPaymentMethod(PaymentMethod method)
    {
        this.method = method;
    }

    /**
     * Returns the payment mode.
     */
    public PaymentMode getPaymentMode()
    {
        return mode;
    }

    /**
     * Sets the payment mode.
     */
    public void setPaymentMode(String mode)
    {
        setPaymentMode(PaymentMode.valueOf(mode));
    }

    /**
     * Sets the payment mode.
     */
    public void setPaymentMode(PaymentMode mode)
    {
        this.mode = mode;
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
     * Returns the order status.
     */
    public OrderStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the order status.
     */
    public void setStatus(String status)
    {
        setStatus(OrderStatus.valueOf(status));
    }

    /**
     * Sets the order status.
     */
    public void setStatus(OrderStatus status)
    {
        this.status = status;
    }

    /**
     * Returns the cancellation reason.
     */
    public CancelReason getReason()
    {
        return reason;
    }

    /**
     * Sets the cancellation reason.
     */
    public void setReason(String reason)
    {
        setReason(CancelReason.valueOf(reason));
    }

    /**
     * Sets the cancellation reason.
     */
    public void setReason(CancelReason reason)
    {
        this.reason = reason;
    }

    /**
     * Returns the invoice.
     */
    public Invoice getInvoice()
    {
        return invoice;
    }
}