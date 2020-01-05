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

/**
 * Class that encapsulates the attributes of a database driver.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DatabaseDriver
{
    private String type = "";
    private String name = "";
    private String className = "";
    private boolean embedded = false;
    private boolean clustered = false;
    private boolean allowEmptyUsername = false;
    private boolean allowEmptyPassword = false;
    private String connectionParameters = "";
    private boolean defaultDatabase = false;
    private String defaultDatabaseName = "";

    /**
     * Empty constructor.
     */
    public DatabaseDriver()
    {
    }

    /**
     * Constructor that takes a type, name and driver class.
     */
    public DatabaseDriver(String type, String name, String className)
    {
        setType(type);
        setName(name);
        setClassName(className);
    }

    /**
     * Returns the name of the driver.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Sets the type of the driver.
     */
    public void setType(String s)
    {
        if(s != null)
            type = new String(s);
    }

    /**
     * Returns the type of the driver.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the name of the driver.
     */
    public void setName(String s)
    {
        if(s != null)
            name = new String(s);
    }

    /**
     * Returns the name of the driver.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the class name of the driver.
     */
    public void setClassName(String s)
    {
        if(s != null)
            className = new String(s);
    }

    /**
     * Returns the class name of the driver.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Set to <CODE>true</CODE> if the this is an embedded (in-memory) driver.
     */
    public void setEmbedded(boolean b)
    {
        embedded = b;
        if(embedded)
            setName(getName()+" (Embedded)");
    }

    /**
     * Returnd <CODE>true</CODE> if the this is an embedded (in-memory) driver.
     */
    public boolean isEmbedded()
    {
        return embedded;
    }

    /**
     * Set to <CODE>true</CODE> if this driver supports clustering.
     */
    public void setClustered(boolean b)
    {
        clustered = b;
    }

    /**
     * Set to <CODE>true</CODE> if this driver supports clustering.
     */
    public boolean isClustered()
    {
        return clustered;
    }

    /**
     * Set to <CODE>true</CODE> if this driver should allow an empty username for the connection.
     */
    public void setAllowEmptyUsername(boolean b)
    {
        allowEmptyUsername = b;
    }

    /**
     * Returns <CODE>true</CODE> if this driver should allow an empty username for the connection.
     */
    public boolean allowEmptyUsername()
    {
        return allowEmptyUsername;
    }

    /**
     * Set to <CODE>true</CODE> if this driver should allow an empty password for the connection.
     */
    public void setAllowEmptyPassword(boolean b)
    {
        allowEmptyPassword = b;
    }

    /**
     * Returns <CODE>true</CODE> if this driver should allow an empty password for the connection.
     */
    public boolean allowEmptyPassword()
    {
        return allowEmptyPassword;
    }

    /**
     * Sets the connection parameters for this driver.
     */
    public void setConnectionParameters(String s)
    {
        connectionParameters = s;
    }

    /**
     * Returns the connection parameters for this driver.
     */
    public String getConnectionParameters()
    {
        return connectionParameters;
    }

    /**
     * Set to <CODE>true</CODE> if this driver can have a default database.
     */
    public void setDefaultDatabase(boolean b)
    {
        defaultDatabase = b;
    }

    /**
     * Returns <CODE>true</CODE> if this driver can have a default database.
     */
    public boolean hasDefaultDatabase()
    {
        return defaultDatabase;
    }

    /**
     * Sets the default database name for this driver.
     */
    public void setDefaultDatabaseName(String s)
    {
        defaultDatabaseName = s;
    }

    /**
     * Returns the default database name for this driver.
     */
    public String getDefaultDatabaseName()
    {
        return defaultDatabaseName;
    }
}