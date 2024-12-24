package com.opsmatters.media.client.paypal;

import java.util.List;
import org.json.JSONObject;
import com.opsmatters.media.cache.order.contact.Contacts;
import com.opsmatters.media.model.order.Order;
import com.opsmatters.media.model.order.OrderItem;
import com.opsmatters.media.model.order.Sender;
import com.opsmatters.media.model.order.Invoice;
import com.opsmatters.media.model.order.Country;
import com.opsmatters.media.model.order.contact.Contact;
import com.opsmatters.media.model.order.contact.Company;

/**
 * Represents an invoice used with PayPal.
 */
public class PayPalInvoice extends JSONObject implements java.io.Serializable
{
    private static final String ID = "id";
    private static final String STATUS = "status";
    private static final String DETAIL = "detail";
    private static final String INVOICER = "invoicer";
    private static final String PRIMARY_RECIPIENTS = "primary_recipients";
    private static final String ITEMS = "items";
    private static final String CONFIGURATION = "configuration";
    private static final String AMOUNT = "amount";

    private Detail detail;
    private Invoicer invoicer;
    private PrimaryRecipients primaryRecipients;
    private Items items;
    private Configuration configuration;
    private Amount amount;

    /**
     * Default constructor.
     */
    public PayPalInvoice() 
    {
    }

    /**
     * Constructor that takes a sender, order, order items and an optional company.
     */
    public PayPalInvoice(Sender sender, Order order, List<OrderItem> orderItems, Company company) 
    {
        Invoice invoice = order.getInvoice();

        Invoicer invoicer = new Invoicer();
        invoicer.setBusinessName(sender.getBillingName());
        invoicer.setEmailAddress(sender.getBillingEmail());
        invoicer.getName().setGivenName(sender.getGivenName());
        invoicer.getName().setSurname(sender.getSurname());
        invoicer.getAddress().setAddressLine1(sender.getAddressLine1());
        invoicer.getAddress().setAddressLine2(sender.getAddressLine2());
        invoicer.getAddress().setAdminArea1(sender.getAddressArea1());
        invoicer.getAddress().setAdminArea2(sender.getAddressArea2());
        invoicer.getAddress().setPostalCode(sender.getPostalCode());
        if(sender.getCountry() != Country.UNDEFINED)
            invoicer.getAddress().setCountryCode(sender.getCountry().code());
        invoicer.setWebsite(sender.getWebsite());
        invoicer.setLogoUrl(sender.getLogoUrl());
        invoicer.setTaxId(sender.getTaxId());
        invoicer.setAdditionalNotes(sender.getAdditionalNotes());

        if(sender.hasPhoneCode())
        {
            Phone phone = new Phone();
            phone.setCountryCode(sender.getPhoneCode());
            phone.setNationalNumber(sender.getPhoneNumber());
            invoicer.getPhones().put(phone);
        }

        setInvoicer(invoicer);

        PrimaryRecipient recipient = new PrimaryRecipient();
        BillingInfo billingInfo = new BillingInfo();
        billingInfo.setEmailAddress(invoice.getEmail());

        if(company != null)
        {
            billingInfo.setBusinessName(company.getBillingName());
            billingInfo.getName().setGivenName(company.getGivenName());
            billingInfo.getName().setSurname(company.getSurname());
            billingInfo.getAddress().setAddressLine1(company.getAddressLine1());
            billingInfo.getAddress().setAddressLine2(company.getAddressLine2());
            billingInfo.getAddress().setAdminArea1(company.getAddressArea1());
            billingInfo.getAddress().setAdminArea2(company.getAddressArea2());
            billingInfo.getAddress().setPostalCode(company.getPostalCode());
            if(company.getCountry() != Country.UNDEFINED)
                billingInfo.getAddress().setCountryCode(company.getCountry().code());

            if(company.hasPhoneCode())
            {
                Phone phone = new Phone();
                phone.setCountryCode(company.getPhoneCode());
                phone.setNationalNumber(company.getPhoneNumber());
                billingInfo.getPhones().put(phone);
            }
        }

        recipient.setBillingInfo(billingInfo);
        getPrimaryRecipients().put(recipient);

        Detail detail = new Detail();
        detail.setCurrencyCode(order.getCurrency().code());
        detail.getPaymentTerm().setTermType(order.getPaymentTerm().code());
        detail.setNote(invoice.getNote());
        setDetail(detail);

        for(OrderItem orderItem : orderItems)
        {
            if(orderItem.isEnabled())
            {
                Item item = new Item();
                item.setName(orderItem.getName());
                item.setDescription(orderItem.getDescription());
                item.setQuantity(Integer.toString(orderItem.getQuantity()));
                UnitAmount amount = new UnitAmount();
                amount.setCurrencyCode(orderItem.getCurrency().code());
                amount.setValue(String.format("%d.00", orderItem.getPrice()));
                item.setUnitAmount(amount);
                item.setUnitOfMeasure("AMOUNT");
                getItems().put(item);
            }
        }

        Amount amount = new Amount();
        Shipping shipping = new Shipping();
        shipping.getAmount().setValue(String.format("%d.00", 0));
        shipping.getAmount().setCurrencyCode(order.getCurrency().code());
        amount.getBreakdown().setShipping(shipping);
        setAmount(amount);
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public PayPalInvoice(JSONObject obj) 
    {
        if(obj.has(ID))
            setId(obj.optString(ID));
        if(obj.has(STATUS))
            setStatus(obj.optString(STATUS));

        if(obj.has(DETAIL))
            setDetail(new Detail(obj.getJSONObject(DETAIL)));
        if(obj.has(INVOICER))
            setInvoicer(new Invoicer(obj.getJSONObject(INVOICER)));
        if(obj.has(PRIMARY_RECIPIENTS))
            setPrimaryRecipients(new PrimaryRecipients(obj.getJSONArray(PRIMARY_RECIPIENTS)));
        if(obj.has(ITEMS))
            setItems(new Items(obj.getJSONArray(ITEMS)));
        if(obj.has(CONFIGURATION))
            setConfiguration(new Configuration(obj.getJSONObject(CONFIGURATION)));
        if(obj.has(AMOUNT))
            setAmount(new Amount(obj.getJSONObject(AMOUNT)));
    }

    /**
     * Returns the "detail" object for the invoice.
     */
    public Detail getDetail()
    {
        if(detail == null)
            setDetail(new Detail());
        return detail;
    }

    /**
     * Sets the "detail" object for the invoice.
     */
    public void setDetail(Detail detail)
    {
        this.detail = detail;
        put(DETAIL, detail);
    }

    /**
     * Returns the "invoicer" object for the invoice.
     */
    public Invoicer getInvoicer()
    {
        if(invoicer == null)
            setInvoicer(new Invoicer());
        return invoicer;
    }

    /**
     * Sets the "invoicer" object for the invoice.
     */
    public void setInvoicer(Invoicer invoicer)
    {
        this.invoicer = invoicer;
        put(INVOICER, invoicer);
    }

    /**
     * Returns the "primary_recipients" object for the invoice.
     */
    public PrimaryRecipients getPrimaryRecipients()
    {
        if(primaryRecipients == null)
            setPrimaryRecipients(new PrimaryRecipients());
        return primaryRecipients;
    }

    /**
     * Sets the "primary_recipients" object for the invoice.
     */
    public void setPrimaryRecipients(PrimaryRecipients primaryRecipients)
    {
        this.primaryRecipients = primaryRecipients;
        put(PRIMARY_RECIPIENTS, primaryRecipients);
    }

    /**
     * Returns the "items" object for the invoice.
     */
    public Items getItems()
    {
        if(items == null)
            setItems(new Items());
        return items;
    }

    /**
     * Sets the "items" object for the invoice.
     */
    public void setItems(Items items)
    {
        this.items = items;
        put(ITEMS, items);
    }

    /**
     * Returns the "configuration" object for the invoice.
     */
    public Configuration getConfiguration()
    {
        if(configuration == null)
            setConfiguration(new Configuration());
        return configuration;
    }

    /**
     * Sets the "configuration" object for the invoice.
     */
    public void setConfiguration(Configuration configuration)
    {
        this.configuration = configuration;
        put(CONFIGURATION, configuration);
    }

    /**
     * Returns the "amount" object for the invoice.
     */
    public Amount getAmount()
    {
        if(amount == null)
            setAmount(new Amount());
        return amount;
    }

    /**
     * Sets the "amount" object for the invoice.
     */
    public void setAmount(Amount amount)
    {
        this.amount = amount;
        put(AMOUNT, amount);
    }

    /**
     * Returns the invoice id.
     */
    public String getId() 
    {
        return optString(ID);
    }

    /**
     * Sets the invoice id.
     */
    public void setId(String id) 
    {
        put(ID, id);
    }

    /**
     * Returns the invoice status.
     */
    public String getStatus() 
    {
        return optString(STATUS);
    }

    /**
     * Sets the invoice status.
     */
    public void setStatus(String status) 
    {
        put(STATUS, status);
    }
}