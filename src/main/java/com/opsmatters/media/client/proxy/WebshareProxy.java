package com.opsmatters.media.client.proxy;

import org.json.JSONObject;

/**
 * Represents a proxy from Webshare.
 */
public class WebshareProxy extends JSONObject implements java.io.Serializable
{
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PROXY_ADDRESS = "proxy_address";
    private static final String PORT = "port";
    private static final String VALID = "valid";
    private static final String COUNTRY_CODE = "country_code";
    private static final String CITY_NAME = "city_name";
    private static final String CREATED_AT = "created_at";
    private static final String LAST_VERIFICATION = "last_verification";

    /**
     * Default constructor.
     */
    public WebshareProxy() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public WebshareProxy(JSONObject obj) 
    {
        if(obj.has(ID))
            setId(obj.optString(ID));
        if(obj.has(USERNAME))
            setUsername(obj.optString(USERNAME));
        if(obj.has(PASSWORD))
            setPassword(obj.optString(PASSWORD));
        if(obj.has(PROXY_ADDRESS))
            setProxyAddress(obj.optString(PROXY_ADDRESS));
        if(obj.has(PORT))
            setPort(obj.optInt(PORT));
        if(obj.has(VALID))
            setValid(obj.optBoolean(VALID));
        if(obj.has(COUNTRY_CODE))
            setCountryCode(obj.optString(COUNTRY_CODE));
        if(obj.has(CITY_NAME))
            setCityName(obj.optString(CITY_NAME));
        if(obj.has(CREATED_AT))
            setCreatedAt(obj.optString(CREATED_AT));
        if(obj.has(LAST_VERIFICATION))
            setLastVerification(obj.optString(LAST_VERIFICATION));
    }

    /**
     * Returns the proxy id.
     */
    public String getId() 
    {
        return optString(ID);
    }

    /**
     * Sets the proxy id.
     */
    public void setId(String id) 
    {
        put(ID, id);
    }

    /**
     * Returns the proxy username.
     */
    public String getUsername() 
    {
        return optString(USERNAME);
    }

    /**
     * Sets the proxy username.
     */
    public void setUsername(String username) 
    {
        put(USERNAME, username);
    }

    /**
     * Returns the proxy password.
     */
    public String getPassword() 
    {
        return optString(PASSWORD);
    }

    /**
     * Sets the proxy password.
     */
    public void setPassword(String password) 
    {
        put(PASSWORD, password);
    }

    /**
     * Returns the proxy address.
     */
    public String getProxyAddress() 
    {
        return optString(PROXY_ADDRESS);
    }

    /**
     * Sets the proxy address.
     */
    public void setProxyAddress(String proxyAddress) 
    {
        put(PROXY_ADDRESS, proxyAddress);
    }

    /**
     * Returns the proxy port.
     */
    public int getPort() 
    {
        return optInt(PORT);
    }

    /**
     * Sets the proxy port.
     */
    public void setPort(int port) 
    {
        put(PORT, port);
    }

    /**
     * Returns <CODE>true</CODE> if the proxy is valid.
     */
    public boolean isValid() 
    {
        return optBoolean(VALID);
    }

    /**
     * Set to <CODE>true</CODE> if the proxy is valid.
     */
    public void setValid(boolean valid) 
    {
        put(VALID, valid);
    }

    /**
     * Returns the proxy country code.
     */
    public String getCountryCode() 
    {
        return optString(COUNTRY_CODE);
    }

    /**
     * Sets the proxy country code.
     */
    public void setCountryCode(String countryCode) 
    {
        put(COUNTRY_CODE, countryCode);
    }

    /**
     * Returns the proxy city name.
     */
    public String getCityName() 
    {
        return optString(CITY_NAME);
    }

    /**
     * Sets the proxy city name.
     */
    public void setCityName(String cityName) 
    {
        put(CITY_NAME, cityName);
    }

    /**
     * Returns the proxy created date.
     */
    public String getCreatedAt() 
    {
        return optString(CREATED_AT);
    }

    /**
     * Sets the proxy created date.
     */
    public void setCreatedAt(String createdAt) 
    {
        put(CREATED_AT, createdAt);
    }

    /**
     * Returns the proxy last verification date.
     */
    public String getLastVerification() 
    {
        return optString(LAST_VERIFICATION);
    }

    /**
     * Sets the proxy last verification date.
     */
    public void setLastVerification(String lastVerification) 
    {
        put(LAST_VERIFICATION, lastVerification);
    }
}