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

package com.opsmatters.media.client.payment.paypal;

import org.json.JSONObject;

/**
 * Represents the metadata object for a PayPal invoice.
 */
public class Metadata extends JSONObject
{
    private static final String CREATE_TIME = "create_time";
    private static final String RECIPIENT_VIEW_URL = "recipient_view_url";
    private static final String INVOICER_VIEW_URL = "invoicer_view_url";

    /**
     * Default constructor.
     */
    public Metadata() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Metadata(JSONObject obj) 
    {
        if(obj.has(CREATE_TIME))
            setCreateTime(obj.optString(CREATE_TIME));
        if(obj.has(RECIPIENT_VIEW_URL))
            setRecipientViewUrl(obj.optString(RECIPIENT_VIEW_URL));
        if(obj.has(INVOICER_VIEW_URL))
            setInvoicerViewUrl(obj.optString(INVOICER_VIEW_URL));
    }

    /**
     * Returns the create time.
     */
    public String getCreateTime() 
    {
        return optString(CREATE_TIME);
    }

    /**
     * Sets the create time.
     */
    public void setCreateTime(String createTime) 
    {
        put(CREATE_TIME, createTime);
    }

    /**
     * Returns the recipient view url.
     */
    public String getRecipientViewUrl() 
    {
        return optString(RECIPIENT_VIEW_URL);
    }

    /**
     * Sets the recipient view url.
     */
    public void setRecipientViewUrl(String recipientViewUrl) 
    {
        put(RECIPIENT_VIEW_URL, recipientViewUrl);
    }

    /**
     * Returns the invoicer view url.
     */
    public String getInvoicerViewUrl() 
    {
        return optString(INVOICER_VIEW_URL);
    }

    /**
     * Sets the invoicer view url.
     */
    public void setInvoicerViewUrl(String invoicerViewUrl) 
    {
        put(INVOICER_VIEW_URL, invoicerViewUrl);
    }
}