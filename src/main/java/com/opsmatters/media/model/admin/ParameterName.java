/*
 * Copyright 2020 Gerald Curley
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

package com.opsmatters.media.model.admin;

/**
 * Represents the name of an application parameter.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ParameterName
{
    SESSION("session"),
    DASHBOARD("dashboard"),
    STATUS("status"),
    INIT_DELAY("init-delay"),
    BATCH_DELAY("batch-delay"),
    ITEM_DELAY("item-delay"),
    STALLED_INTERVAL("stalled-interval"),
    SCAN_INTERVAL("scan-interval"),
    MIN_AGE("min-age"),
    MAX_BATCH_SIZE("max-batch-size"),
    MAX_WAITING("max-waiting"),
    MAX_ERRORS("max-errors"),
    MAX_RESULTS("max-results"),
    MAX_RETRIES("max-retries"),
    MAX_CHANGES("max-changes"),
    MAX_ALERTS("max-alerts"),
    MAX_INACTIVE("max-inactive"),
    SUB_LEASE("sub-lease"),
    SUB_EXPIRY("sub-expiry"),
    ORGANISATION_CHANGED("organisation-changed"),
    ORGANISATION_UPDATED("organisation-updated"),
    SETTINGS_CHANGED("settings-changed"),
    SETTINGS_UPDATED("settings-updated"),
    IMAGE_CHANGED("image-changed"),
    IMAGE_UPDATED("image-updated"),
    ERROR_COUNT_ERROR("error-count-error"),
    CHANGE_COUNT_ERROR("change-count-error"),
    ALERT_COUNT_ERROR("alert-count-error"),
    STALLED_ERROR("stalled-error"),
    MAX_DRAFT_POST_AGE("max_draft-post-age"),
    MAX_CHANNEL_POST_AGE("max-channel-post-age"),
    MIN_WEBINAR_DURATION("min-webinar-duration"),
    SHUTDOWN("shutdown"),
    PAYMENT_METHOD("payment-method"),
    PAYMENT_MODE("payment-mode"),
    PAYMENT_TERM("payment-term"),
    CURRENCY("currency"),
    SENDER_GIVEN_NAME("sender-given-name"),
    SENDER_SURNAME("sender-surname"),
    SENDER_BILLING_NAME("sender-billing-name"),
    SENDER_BILLING_EMAIL("sender-billing-email"),
    SENDER_ADDRESS_LINE_1("sender-address-line-1"),
    SENDER_ADDRESS_LINE_2("sender-address-line-2"),
    SENDER_ADDRESS_AREA_1("sender-address-area-1"),
    SENDER_ADDRESS_AREA_2("sender-address-area-2"),
    SENDER_POSTAL_CODE("sender-postal-code"),
    SENDER_COUNTRY("sender-country"),
    SENDER_PHONE_CODE("sender-phone-code"),
    SENDER_PHONE_NUMBER("sender-phone-number"),
    SENDER_WEBSITE("sender-website"),
    SENDER_LOGO("sender-logo"),
    SENDER_TAX_ID("sender-tax-id"),
    SENDER_COMPANY_NOTES("sender-company-notes"),
    SENDER_ADDITIONAL_NOTES("sender-additional-notes"),
    INVOICE_NOTE("invoice-note");

    private String value;

    /**
     * Constructor that takes the name value.
     * @param value The value for the name
     */
    ParameterName(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the name.
     * @return The value of the name.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the status.
     * @return The value of the status.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static ParameterName fromValue(String value)
    {
        ParameterName[] types = values();
        for(ParameterName type : types)
        {
            if(type.value().equals(value))
                return type;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}