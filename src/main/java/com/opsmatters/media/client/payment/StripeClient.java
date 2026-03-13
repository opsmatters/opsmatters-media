/*
 * Copyright 2026 Gerald Curley
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

package com.opsmatters.media.client.payment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceItem;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.param.InvoiceUpdateParams;
import com.stripe.param.InvoiceItemCreateParams;
import com.stripe.param.InvoiceItemListParams;
import com.stripe.exception.StripeException;
import com.stripe.exception.InvalidRequestException;
import com.opsmatters.media.model.order.Order;
import com.opsmatters.media.model.order.OrderItem;
import com.opsmatters.media.model.order.Sender;
import com.opsmatters.media.model.order.InvoiceStatus;
import com.opsmatters.media.model.order.contact.Contact;
import com.opsmatters.media.model.order.contact.Company;
import com.opsmatters.media.client.Client;

/**
 * Executes Stripe API calls.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class StripeClient extends Client
{
    private static final Logger logger = Logger.getLogger(StripeClient.class.getName());

    public static final String SUFFIX = ".stripe";

    private String apiKey = "";

    /**
     * Returns a new client using the given credentials.
     */
    static public StripeClient newClient() 
        throws IOException
    {
        StripeClient ret = new StripeClient();

        // Configure and create the Stripe client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create Stripe client");

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring Stripe client");

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setApiKey(obj.optString("apiKey"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read Stripe auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured Stripe client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating Stripe client");

        Stripe.apiKey = getApiKey();

        return hasApiKey();
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
    }

    /**
     * Returns the API key for the client.
     */
    public String getApiKey() 
    {
        return apiKey;
    }

    /**
     * Sets the API key for the client.
     */
    public void setApiKey(String apiKey) 
    {
        this.apiKey = apiKey;
    }

    /**
     * Returns <CODE>true</CODE> if the API key has been set.
     */
    public boolean hasApiKey() 
    {
        return apiKey != null && apiKey.length() > 0;
    }

    /**
     * Returns the customer with the given name or email address.
     */
    public Customer getCustomer(Contact contact, String email) throws StripeException
    {
        CustomerSearchParams params = CustomerSearchParams.builder()
            .setQuery(String.format("name: '%s'", contact.getName()))
            .build();

        List<Customer> customers = Customer.search(params).getData();

        // If no customer found, search by email instead
        if(customers.size() == 0)
        {
            params = CustomerSearchParams.builder()
                .setQuery(String.format("email: '%s'", email))
                .build();

            customers.addAll(Customer.search(params).getData());
        }

        return customers.size() > 0 ? customers.get(0) : null;
    }

    /**
     * Creates the customer using the given contact and email.
     */
    public Customer createCustomer(Contact contact, String email) throws StripeException
    {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(contact.getName())
                .setEmail(email)
                .build();

        return Customer.create(params);
    }

    /**
     * Updates the customer using the given contact, company and email.
     */
    public Customer updateCustomer(Customer customer, Contact contact, Company company, String email) throws StripeException
    {
        CustomerUpdateParams params = CustomerUpdateParams.builder()
            .setName(contact.getName())
            .setEmail(email)
            .build();

        Customer ret = customer.update(params);

        if(company != null)
        {
            CustomerUpdateParams.Address addressParams = CustomerUpdateParams.Address.builder()
                .setLine1(company.getAddressLine1())
                .setLine2(company.getAddressLine2())
                .setCity(company.getAddressArea2())
                .setState(company.getAddressArea1())
                .setPostalCode(company.getPostalCode())
                .setCountry(company.getCountry().getCode())
                .build();

            CustomerUpdateParams companyParams = CustomerUpdateParams.builder()
                .setBusinessName(company.getBillingName())
                .setIndividualName(company.getIndividualName())
                .setAddress(addressParams)
                .setPhone(company.getPhoneNumber())
                .build();

            ret = ret.update(companyParams);
        }

        return ret;
    }

    /**
     * Returns the invoice details for the given invoice id.
     */
    public Invoice retrieveInvoice(String invoiceId) throws StripeException
    {
        Invoice ret = null;

        try
        {
            ret = Invoice.retrieve(invoiceId);
        }
        catch(InvalidRequestException e)
        {
            logger.severe("Invoice does not exist: "+invoiceId);
            throw e;
        }

        return ret;
    }

    /**
     * Returns the invoice status for the given invoice id.
     */
    public InvoiceStatus getInvoiceStatus(String invoiceId) throws StripeException
    {
        InvoiceStatus ret = InvoiceStatus.NONE;
        Invoice invoice = retrieveInvoice(invoiceId);
        if(invoice != null)
        {
            String status = invoice.getStatus();

            try
            {
                if(status != null)
                    ret = InvoiceStatus.valueOf(status.toUpperCase());
            }
            catch(IllegalArgumentException e)
            {
                logger.severe("Invalid invoice status: "+status);
            }
        }

        return ret;
    }

    /**
     * Creates a draft invoice using the given customer and order.
     */
    public Invoice createInvoice(Customer customer, Sender sender, Order order) throws StripeException
    {
        if(customer == null)
            throw new IllegalArgumentException("customer null");

        InvoiceCreateParams params = InvoiceCreateParams.builder()
            .setCustomer(customer.getId())
            .setCurrency(order.getCurrency().getCode())
            .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
            .setDaysUntilDue(order.getPaymentTerm().days()*1L)
            .setFooter(getFooter(sender, order))
            .build();

        return Invoice.create(params);
    }

    /**
     * Sends the draft invoice with the given id.
     */
    public Invoice sendInvoice(String invoiceId) throws StripeException
    {
        Invoice ret = null;

        try
        {
            Invoice invoice = retrieveInvoice(invoiceId);
            if(invoice != null)
                ret = invoice.sendInvoice();
        }
        catch(InvalidRequestException e)
        {
            logger.severe("Invoice does not exist: "+invoiceId);
            throw e;
        }

        return ret;
    }

    /**
     * Updates the invoice with the given id.
     */
    public Invoice updateInvoice(String invoiceId, Sender sender, Order order) throws StripeException
    {
        Invoice ret = null;

        InvoiceUpdateParams params = InvoiceUpdateParams.builder()
            .setDaysUntilDue(order.getPaymentTerm().days()*1L)
            .setFooter(getFooter(sender, order))
            .build();

        Invoice invoice = retrieveInvoice(invoiceId);
        if(invoice != null)
            ret = invoice.update(params);

        return ret;
    }

    /**
     * Voids the sent invoice with the given id.
     */
    public Invoice voidInvoice(String invoiceId) throws StripeException
    {
        Invoice ret = null;

        try
        {
            Invoice invoice = retrieveInvoice(invoiceId);
            if(invoice != null)
                ret = invoice.voidInvoice();
        }
        catch(InvalidRequestException e)
        {
            logger.severe("Invoice does not exist: "+invoiceId);
            throw e;
        }

        return ret;
    }

    /**
     * Creates an invoice item for the given invoice using the given order item.
     */
    public InvoiceItem createInvoiceItem(Invoice invoice, OrderItem orderItem) throws StripeException
    {
        InvoiceItemCreateParams params = InvoiceItemCreateParams.builder()
            .setCustomer(invoice.getCustomer())
            .setInvoice(invoice.getId())
            .setDescription(getDescription(orderItem))
            .setAmount(orderItem.getPrice()*orderItem.getQuantity()*100L)
            .setCurrency(orderItem.getCurrency().getCode())
            .build();

        return InvoiceItem.create(params);
    }

    /**
     * Deletes all invoice items from the given invoice.
     */
    public void deleteInvoiceItems(Invoice invoice) throws StripeException
    {
        InvoiceItemListParams params = InvoiceItemListParams.builder()
            .setInvoice(invoice.getId())
            .build();

        List<InvoiceItem> items = InvoiceItem.list(params).getData();
        for(InvoiceItem item : items)
            item.delete();
    }

    /**
     * Returns the description for an invoice item.
     */
    private String getDescription(OrderItem orderItem)
    {
        String ret = orderItem.getName();
        if(orderItem.hasDescription())
            ret += "\n"+orderItem.getDescription();
        return ret;
    }

    /**
     * Returns the footer for an invoice.
     */
    private String getFooter(Sender sender, Order order)
    {
        String ret = order.getInvoice().getNote();
        if(ret.length() > 0)
            ret += "\n\n";
        ret += sender.getCompanyNotes();
        return ret;
    }
}