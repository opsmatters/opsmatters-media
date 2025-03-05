/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.model.content.util;

import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.client.proxy.WebshareProxy;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.Formats;

/**
 * Class representing a content proxy.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentProxy extends BaseEntity
{
    private String host = "";
    private int port = -1;
    private String username = "";
    private String password = "";
    private String countryCode = "";
    private String cityName = "";
    private ProxyStatus status = ProxyStatus.DISABLED;

    /**
     * Default constructor.
     */
    public ContentProxy()
    {
    }

    /**
     * Constructor that takes a Webshare proxy.
     */
    public ContentProxy(WebshareProxy proxy)
    {
        setId(proxy.getId());
        setCreatedDate(TimeUtils.toInstantUTC(proxy.getCreatedAt(), Formats.LONG_ISO8601_FORMAT));
        setHost(proxy.getProxyAddress());
        setPort(proxy.getPort());
        setUsername(proxy.getUsername());
        setPassword(proxy.getPassword());
        setCountryCode(proxy.getCountryCode());
        setCityName(proxy.getCityName());
        setStatus(proxy.isValid() ? ProxyStatus.ACTIVE : ProxyStatus.DISABLED);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentProxy obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setHost(obj.getHost());
            setPort(obj.getPort());
            setUsername(obj.getUsername());
            setPassword(obj.getPassword());
            setCountryCode(obj.getCountryCode());
            setCityName(obj.getCityName());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the host and port.
     */
    public String toString()
    {
        return getHostPort();
    }

    /**
     * Returns the host.
     */
    public String getHost()
    {
        return host;
    }

    /**
     * Sets the host.
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * Returns the port.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Sets the port.
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * Returns the host and port ("<host>:<port>").
     */
    public String getHostPort()
    {
        return String.format("%s:%d", host, port);
    }

    /**
     * Returns the username.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Returns the password.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Returns the country code.
     */
    public String getCountryCode()
    {
        return countryCode;
    }

    /**
     * Sets the country code.
     */
    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    /**
     * Returns the city name.
     */
    public String getCityName()
    {
        return cityName;
    }

    /**
     * Sets the city name.
     */
    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    /**
     * Returns the proxy status.
     */
    public ProxyStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the proxy is active.
     */
    public boolean isActive()
    {
        return status == ProxyStatus.ACTIVE;
    }

    /**
     * Sets the proxy status.
     */
    public void setStatus(ProxyStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the proxy status.
     */
    public void setStatus(String status)
    {
        setStatus(ProxyStatus.valueOf(status));
    }
}