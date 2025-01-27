package com.opsmatters.media.client.paypal;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import com.opsmatters.media.client.ApiClient;
import com.opsmatters.media.model.order.InvoiceStatus;

/**
 * Executes PayPal API calls using a http client.
 */
public class PayPalClient extends ApiClient
{
    private static final Logger logger = Logger.getLogger(PayPalClient.class.getName());

    public static final String SUFFIX = ".paypal";

    private static final String BASE_URL = "https://api-m.paypal.com";

    /**
     * Returns a new client using the given credentials.
     */
    static public PayPalClient newClient() 
        throws IOException
    {
        PayPalClient ret = PayPalClient._builder()
            .url(BASE_URL)
            .build();

        // Configure and create the PayPal client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create PayPal client: "+ret.getUrl());

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring Paypal client");

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setClientId(obj.optString("clientId"));
            setSecretKey(obj.optString("secretKey"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read PayPal auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured PayPal client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating PayPal client");

        if(!super.create())
        {
            logger.severe("Unable to create PayPal client");
            return false;
        }

        clearBearerToken();

        Map<String,String> params = new HashMap<String,String>();
        params.put("grant_type", "client_credentials");
        String response = post(String.format("%s/v1/oauth2/token", BASE_URL), params);

        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            setBearerToken(obj.optString("access_token"));
        }
        else // Invalid JSON response
        {
            logger.severe("Invalid JSON response for PayPal authenticate: "+response);
        }

        return hasBearerToken();
    }

    /**
     * Returns the client ID for the client.
     */
    public String getClientId() 
    {
        return getUsername();
    }

    /**
     * Sets the client ID for the client.
     */
    public void setClientId(String clientId) 
    {
        setUsername(clientId);
    }

    /**
     * Returns the secret key for the client.
     */
    public String getSecretKey() 
    {
        return getPassword();
    }

    /**
     * Sets the secret key for the client.
     */
    public void setSecretKey(String secretKey) 
    {
        setPassword(secretKey);
    }

    /**
     * Creates a draft invoice with the given details.
     */
    public String createDraftInvoice(PayPalInvoice invoice) throws IOException
    {
        String ret = null;
        String response = post(String.format("%s/v2/invoicing/invoices", BASE_URL),
            "application/json", invoice.toString().getBytes("UTF-8"));
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("href"))
            {
                String href = obj.optString("href");
                ret = href.substring(href.lastIndexOf("/")+1);
            }
            else // Invoice id not found
            {
                logger.severe("Invoice id not found for paypal create invoice: "+response);
            }
        }
        else // Invalid JSON response
        {
            logger.severe("Invalid JSON response for paypal create invoice: "+response);
        }

        return ret;
    }

    /**
     * Sends the draft invoice with the given id.
     */
    public boolean sendInvoice(String invoiceId) throws IOException
    {
        JSONObject obj = new JSONObject();
        obj.put("send_to_invoicer", true);

        String response = post(String.format("%s/v2/invoicing/invoices/%s/send", BASE_URL, invoiceId),
            "application/json", obj.toString());
        return getStatusLine().getStatusCode() == 200;
    }

    /**
     * Updates the invoice with the given id.
     */
    public String updateInvoice(String invoiceId, PayPalInvoice invoice) throws IOException
    {
        String ret = null;
        String response = put(String.format("%s/v2/invoicing/invoices/%s", BASE_URL, invoiceId),
            "application/json", invoice.toString().getBytes("UTF-8"));
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("href"))
            {
                String href = obj.optString("href");
                ret = href.substring(href.lastIndexOf("/")+1);
            }
            else // Invoice id not found
            {
                logger.severe("Invoice id not found for paypal update invoice: "+response);
            }
        }
        else // Invalid JSON response
        {
            logger.severe("Invalid JSON response for paypal update invoice: "+response);
        }

        return ret;
    }

    /**
     * Returns the invoice details for the given invoice id.
     */
    public PayPalInvoice getInvoiceDetails(String invoiceId) throws IOException
    {
        PayPalInvoice ret = null;
        String response = get(String.format("%s/v2/invoicing/invoices/%s", BASE_URL, invoiceId));
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.optString("name").equals("RESOURCE_NOT_FOUND"))
            {
                logger.severe("Invoice does not exist: "+invoiceId);
            }
            else
            {
                ret = new PayPalInvoice(obj);
            }
        }
        else // Invalid JSON response
        {
            logger.severe("Invalid JSON response for paypal show invoice: "+response);
        }

        return ret;
    }

    /**
     * Returns the invoice status for the given invoice id.
     */
    public InvoiceStatus getInvoiceStatus(String invoiceId) throws IOException
    {
        InvoiceStatus ret = InvoiceStatus.NONE;
        PayPalInvoice invoice = getInvoiceDetails(invoiceId);
        if(invoice != null)
        {
            String status = invoice.getStatus();

            try
            {
                if(status != null)
                    ret = InvoiceStatus.valueOf(status);
            }
            catch(IllegalArgumentException e)
            {
                logger.severe("Invalid invoice status: "+status);
            }
        }

        return ret;
    }

    /**
     * Cancels the sent invoice with the given id.
     */
    public boolean cancelSentInvoice(String invoiceId) throws IOException
    {
        JSONObject obj = new JSONObject();
        obj.put("send_to_invoicer", true);
        obj.put("send_to_recipient", true);

        post(String.format("%s/v2/invoicing/invoices/%s/cancel", BASE_URL, invoiceId),
            "application/json", obj.toString(), false);
        return getStatusLine().getStatusCode() == 204;
    }

    /**
     * Records a payment against the given invoice.
     */
    public String recordInvoicePayment(String invoiceId, PayPalPayment payment) throws IOException
    {
        String ret = null;
        String response = post(String.format("%s/v2/invoicing/invoices/%s/payments", BASE_URL, invoiceId),
            "application/json", payment.toString());
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("payment_id"))
            {
                ret = obj.optString("payment_id");
            }
            else // Invoice id not found
            {
                logger.severe("Payment id not found for paypal record payment: "+response);
            }
        }
        else // Invalid JSON response
        {
            logger.severe("Invalid JSON response for paypal record payment: "+response);
        }

        return ret;
    }

    /**
     * Returns a builder for the client.
     * @return The builder instance.
     */
    public static Builder _builder()
    {
        return new Builder();
    }

    /**
     * Builder to make client construction easier.
     */
    public static class Builder
    {
        private PayPalClient client = new PayPalClient();

        /**
         * Sets the base url for the client.
         * @param url The base url for the client
         * @return This object
         */
        public Builder url(String url)
        {
            client.setUrl(url);
            return this;
        }

        /**
         * Returns the configured client instance
         * @return The client instance
         */
        public PayPalClient build()
        {
            return client;
        }
    }
}