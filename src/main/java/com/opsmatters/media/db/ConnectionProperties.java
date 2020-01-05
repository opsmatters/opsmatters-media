/*
 * Copyright 2019 Gerald Curley
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
package com.opsmatters.media.db;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

/**
 * Defines the properties of a connection.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ConnectionProperties extends Properties
{
    /**
     * The name of the "connection.name" property.
     */
    public static final String NAME = "connection.name";

    /**
     * The name of the "connection.protocol" property.
     */
    public final static String PROTOCOL = "connection.protocol";

    /**
     * The name of the "connection.type" property.
     */
    public final static String TYPE = "connection.type";

    /**
     * The name of the "connection.database-name" property.
     */
    public final static String DATABASE_NAME = "connection.database-name";

    /**
     * The name of the "connection.hostname" property.
     */
    public final static String HOSTNAME = "connection.hostname";

    /**
     * The name of the "connection.port" property.
     */
    public final static String PORT = "connection.port";

    /**
     * The name of the "connection.admin-port" property.
     */
    public final static String ADMIN_PORT = "connection.admin-port";

    /**
     * The name of the "connection.other-hostnames" property.
     */
    public final static String OTHER_HOSTNAMES = "connection.other-hostnames";

    /**
     * The name of the "connection.username" property.
     */
    public final static String USERNAME = "connection.username";

    /**
     * The name of the "connection.password" property.
     */
    public final static String PASSWORD = "connection.password";

    /**
     * The name of the "connection.parameters" property.
     */
    public final static String PARAMETERS = "connection.parameters";

    /**
     * The name of the "connection.security" property.
     */
    public final static String SECURITY = "connection.security";

    /**
     * The name of the "connection.keyfile" property.
     */
    public final static String KEYFILE = "connection.keyfile";

    /**
     * The name of the "connection.directory" property.
     */
    public final static String DIRECTORY = "connection.directory";

    /**
     * The name of the "connection.connectTimeout" property.
     */
    public final static String CONNECT_TIMEOUT = "connection.connectTimeout";

    /**
     * Default constructor.
     */
    public ConnectionProperties()
    {
    }

    /**
     * Creates the object from the builder.
     */
    private ConnectionProperties(Builder builder)
    {
        setType(builder.type);
        setProtocol(builder.protocol);
        setName(builder.name);
        setDatabaseName(builder.databaseName);
        setHostname(builder.hostname);
        setPort(builder.port);
        setAdminPort(builder.adminPort);
        setUsername(builder.username);
        setPassword(builder.password);
        setOtherHostnames(builder.otherHostnames);
        setParameters(builder.parameters);
        setKeyfile(builder.keyfile);
        setDirectory(builder.directory);
        setConnectTimeout(builder.connectTimeout);
    }

    /**
     * Returns the builder.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Returns the builder.
     */
    public static Builder builder(String hostname, int port)
    {
        return new Builder(hostname, port);
    }

    /**
     * Returns the protocol for the connection.
     */
    public String getProtocol()
    {
        return getProperty(PROTOCOL);
    }

    /**
     * Sets the protocol for the connection.
     */
    public void setProtocol(String protocol)
    {
        if(protocol != null)
            setProperty(PROTOCOL, new String(protocol));
        else
            remove(PROTOCOL);
    }

    /**
     * Returns the type for the connection.
     */
    public String getType()
    {
        return getProperty(TYPE);
    }

    /**
     * Sets the type for the connection.
     */
    public void setType(String type)
    {
        if(type != null)
            setProperty(TYPE, new String(type));
        else
            remove(TYPE);
    }

    /**
     * Returns the name for the connection.
     */
    public String getName()
    {
        return getProperty(NAME);
    }

    /**
     * Sets the name for the connection.
     */
    public void setName(String name)
    {
        if(name != null)
            setProperty(NAME, new String(name));
        else
            remove(NAME);
    }

    /**
     * Returns the database name for the connection.
     */
    public String getDatabaseName()
    {
        return getProperty(DATABASE_NAME);
    }

    /**
     * Sets the database name for the connection.
     */
    public void setDatabaseName(String databaseName)
    {
        if(databaseName != null)
            setProperty(DATABASE_NAME, new String(databaseName));
        else
            remove(DATABASE_NAME);
    }

    /**
     * Returns the hostname for the connection.
     */
    public String getHostname()
    {
        return getProperty(HOSTNAME);
    }

    /**
     * Sets the hostname for the connection.
     */
    public void setHostname(String hostname)
    {
        if(hostname != null)
            setProperty(HOSTNAME, new String(hostname));
        else
            remove(HOSTNAME);
    }

    /**
     * Returns the port for the connection.
     */
    public int getPort()
    {
        return Integer.parseInt(getProperty(PORT, "0"));
    }

    /**
     * Sets the port for the connection.
     */
    public void setPort(int port)
    {
        if(port > 0)
            setProperty(PORT, Integer.toString(port));
        else
            remove(PORT);
    }

    /**
     * Sets the port for the connection.
     */
    public void setPort(String port)
    {
        if(port != null && port.length() > 0)
            setProperty(PORT, port);
        else
            remove(PORT);
    }

    /**
     * Returns the admin port for the connection.
     */
    public int getAdminPort()
    {
        return Integer.parseInt(getProperty(ADMIN_PORT, "0"));
    }

    /**
     * Sets the admin port for the connection.
     */
    public void setAdminPort(int port)
    {
        if(port > 0)
            setProperty(ADMIN_PORT, Integer.toString(port));
        else
            remove(ADMIN_PORT);
    }

    /**
     * Sets the admin port for the connection.
     */
    public void setAdminPort(String port)
    {
        if(port != null && port.length() > 0)
            setProperty(ADMIN_PORT, port);
        else
            remove(ADMIN_PORT);
    }

    /**
     * Returns <CODE>true</CODE> if the connection should use https.
     */
    public boolean useSecurity()
    {
        return Boolean.parseBoolean(getProperty(SECURITY, ""));
    }

    /**
     * Set to <CODE>true</CODE> if the connection should use https.
     */
    public void setSecurity(boolean security)
    {
        setProperty(SECURITY, Boolean.toString(security));
        if(security && getProtocol() != null && !getProtocol().endsWith("s"))
            setProtocol(getProtocol()+"s");
    }

    /**
     * Returns the username for the connection.
     */
    public String getUsername()
    {
        return getProperty(USERNAME);
    }

    /**
     * Sets the username for the connection.
     */
    public void setUsername(String username)
    {
        if(username != null)
            setProperty(USERNAME, new String(username));
        else
            remove(USERNAME);
    }

    /**
     * Returns the password for the connection.
     */
    public String getPassword()
    {
        return getProperty(PASSWORD);
    }

    /**
     * Sets the password for the connection.
     */
    public void setPassword(String password)
    {
        if(password != null)
            setProperty(PASSWORD, new String(password));
        else
            remove(PASSWORD);
    }

    /**
     * Set to <CODE>true</CODE> if the connection should use authentication.
     */
    public boolean useAuthentication()
    {
        return getUsername() != null && getUsername().length() > 0;
    }

    /**
     * Returns the other hostnames for the connection.
     */
    public String getOtherHostnames()
    {
        return getProperty(OTHER_HOSTNAMES);
    }

    /**
     * Returns <CODE>true</CODE> if there are other hostnames for the connection.
     */
    public boolean hasOtherHostnames()
    {
        return getOtherHostnames() != null && getOtherHostnames().length() > 0;
    }

    /**
     * Sets the other hostnames for the connection.
     */
    public void setOtherHostnames(String otherHostnames)
    {
        if(otherHostnames != null)
            setProperty(OTHER_HOSTNAMES, new String(otherHostnames));
        else
            remove(OTHER_HOSTNAMES);
    }

    /**
     * Returns the connection parameters for the connection.
     */
    public String getParameters()
    {
        return getProperty(PARAMETERS);
    }

    /**
     * Sets the connection parameters for the connection.
     */
    public void setParameters(String parameters)
    {
        if(parameters != null)
            setProperty(PARAMETERS, new String(parameters));
        else
            remove(PARAMETERS);
    }

    /**
     * Returns the keyfile for the connection.
     */
    public String getKeyfile()
    {
        return getProperty(KEYFILE);
    }

    /**
     * Sets the keyfile for the connection.
     */
    public void setKeyfile(String keyfile)
    {
        if(keyfile != null)
            setProperty(KEYFILE, new String(keyfile));
        else
            remove(KEYFILE);
    }

    /**
     * Returns the directory for the connection.
     */
    public String getDirectory()
    {
        return getProperty(DIRECTORY);
    }

    /**
     * Sets the directory for the connection.
     */
    public void setDirectory(String directory)
    {
        if(directory != null)
            setProperty(DIRECTORY, new String(directory));
        else
            remove(DIRECTORY);
    }

    /**
     * Returns the connect timeout for the connection (in seconds).
     */
    public int getConnectTimeout()
    {
        return Integer.parseInt(getProperty(CONNECT_TIMEOUT, "0"));
    }

    /**
     * Sets the connect timeout for the connection (in seconds).
     */
    public void setConnectTimeout(int connectTimeout)
    {
        if(connectTimeout > 0)
            setProperty(CONNECT_TIMEOUT, Integer.toString(connectTimeout));
        else
            remove(CONNECT_TIMEOUT);
    }

    /**
     * Returns the URL for the connection.
     */
    public String getURL()
    {
        StringBuffer buff = new StringBuffer();
        buff.append(getProtocol());
        buff.append("://");
        buff.append(getHostname());
        buff.append(":");
        buff.append(getPort());
        if(getOtherHostnames() != null && getOtherHostnames().length() > 0)
        {
            buff.append(",");
            buff.append(getOtherHostnames());
        }
        buff.append("/");
        buff.append(getDatabaseName());
        if(getParameters() != null && getParameters().length() > 0)
        {
            buff.append("?");
            buff.append(getParameters());
        }
        return buff.toString();
    }

    /**
     * A host with a name and port.
     */
    public class Host
    {
        Host(String hostname, int port)
        {
            this.hostname = hostname;
            this.port = port;
        }

        public String getHostname()
        {
            return hostname;
        }

        public int getPort()
        {
            return port;
        }

        public String toString()
        {
            return hostname+":"+port;
        }

        String hostname;
        int port;
    }

    /**
     * Returns a list of the hosts for the connection.
     */
    public List<Host> getOtherHosts()
    {
        List<Host> ret = new ArrayList<Host>();

        if(hasOtherHostnames())
        {
            String[] array = getOtherHostnames().split(",");
            for(int i = 0; i < array.length; i++)
            {
                String hostname = array[i];
                int port = getPort();
                int pos = hostname.indexOf(":");
                if(pos != -1)
                {
                    String portStr = hostname.substring(pos+1).trim();
                    hostname = hostname.substring(0, pos);
                    port = Integer.parseInt(portStr);
                }               
                ret.add(new Host(hostname, port));
            }
        }

        return ret;
    }

    /**
     * Makes ConnectionPropties construction easier.
     */
    public static class Builder 
    {
        private String type;
        private String protocol = "http";
        private String name;
        private String databaseName;
        private String hostname;
        private int port = 0;
        private int adminPort = 0;
        private boolean security = false;
        private String username;
        private String password;
        private String otherHostnames;
        private String parameters;
        private String keyfile;
        private String directory;
        private int connectTimeout = 0;

        /**
         * Default constructor.
         */
        public Builder()
        {
        }

        /**
         * Constructor that takes a hostname and port.
         */
        public Builder(String hostname, int port)
        {
            this.hostname = hostname;
            this.port = port;
        }

        /**
         * Sets the type for the connection.
         */
        public Builder type(String type)
        {
            this.type = type;
            return this;
        }

        /**
         * Sets the protocol for the connection.
         */
        public Builder protocol(String protocol)
        {
            this.protocol = protocol;
            return this;
        }

        /**
         * Sets the name for the connection.
         */
        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        /**
         * Sets the database name for the connection.
         */
        public Builder databaseName(String databaseName)
        {
            this.databaseName = databaseName;
            return this;
        }

        /**
         * Sets the hostname for the connection.
         */
        public Builder hostname(String hostname)
        {
            this.hostname = hostname;
            return this;
        }

        /**
         * Sets the port for the connection.
         */
        public Builder port(int port)
        {
            this.port = port;
            return this;
        }

        /**
         * Sets the admin port for the connection.
         */
        public Builder adminPort(int port)
        {
            this.adminPort = port;
            return this;
        }

        /**
         * Set to <CODE>true</CODE> if the connection should use https.
         */
        public Builder security(boolean security)
        {
            this.security = security;
            return this;
        }

        /**
         * Sets the username for the connection.
         */
        public Builder username(String username)
        {
            this.username = username;
            return this;
        }

        /**
         * Sets the password for the connection.
         */
        public Builder password(String password)
        {
            this.password = password;
            return this;
        }

        /**
         * Sets the username and password for the connection.
         */
        public Builder credentials(String username, String password)
        {
            this.username = username;
            this.password = password;
            return this;
        }

        /**
         * Sets the other hostnames for the connection.
         */
        public Builder otherHostnames(String otherHostnames)
        {
            this.otherHostnames = otherHostnames;
            return this;
        }

        /**
         * Sets the connection parameters for the connection.
         */
        public Builder parameters(String parameters)
        {
            this.parameters = parameters;
            return this;
        }

        /**
         * Sets the keyfile for the connection.
         */
        public Builder keyfile(String keyfile)
        {
            this.keyfile = keyfile;
            return this;
        }

        /**
         * Sets the directory for the connection.
         */
        public Builder directory(String directory)
        {
            this.directory = directory;
            return this;
        }

        /**
         * Sets the connect timeout for the connection.
         */
        public Builder connectTimeout(int connectTimeout)
        {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public ConnectionProperties build()
        {
            return new ConnectionProperties(this);           
        }
    }
}