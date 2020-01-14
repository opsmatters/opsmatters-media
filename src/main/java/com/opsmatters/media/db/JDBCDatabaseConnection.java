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

import java.util.*;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.logging.Logger;
import com.opsmatters.media.exception.MissingParameterException;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;
import com.opsmatters.media.db.dao.social.SocialDAOFactory;
import com.opsmatters.media.util.StringUtils;

/**
 * Encapsulates the definition of a connection to a database using a JDBC-compliant driver.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class JDBCDatabaseConnection
{
    private static final Logger logger = Logger.getLogger(JDBCDatabaseConnection.class.getName());

    private String databaseName = "";
    private String hostname = "";
    private int port = 0;
    private boolean security = false;
    private String otherHostnames = "";
    private String username = "";
    private String passwd = "";
    private String driverType = "";
    private String name = "";
    private String connectionParameters = "";
    private int connectTimeout = 0;
    private Connection conn;
    private DatabaseMetaData data;
    private JDBCDatabaseDriver driver;
    private ContentDAOFactory contentDAOFactory;
    private SocialDAOFactory socialDAOFactory;
    private int lastConnectionStatus = NOT_CONNECTED;
    private Exception connectException;
    private boolean debug = false;

    /**
     * The "Error" status code.
     */
    public static final int ERROR = -1;

    /**
     * The "Not Connected" status code.
     */
    public static final int NOT_CONNECTED = 0;

    /**
     * The "Connected" status code.
     */
    public static final int CONNECTED = 2;

    /**
     * Protected constructor.
     */
    public JDBCDatabaseConnection()
    {
    }

    /**
     * Returns the name of the database.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the database to connect to.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns <CODE>true</CODE> if debug is enabled for this database.
     */
    public boolean debug()
    {
        return debug;
    }

    /**
     * Set to <CODE>true</CODE> if debug is enabled for this database.
     */
    public void setDebug(boolean b)
    {
        debug = b;
    }

    /**
     * Returns the underlying JDBC connection.
     */
    public Connection getConnection()
    {
        return conn;
    }

    /**
     * Opens a connection to the database using the given properties.
     * @param p a set of properties containing the connection parameters
     * @param verbose <CODE>true</CODE> if log entries should be written during the connection
     */
    public boolean connect(ConnectionProperties p, boolean verbose) 
        throws Exception
    {
        boolean ret = true;
        conn = null;

        setConnectionStatus(NOT_CONNECTED);

        // Get the driver type
        driverType = p.getType();
        if(driverType == null || driverType.length() == 0)
        {
            ret = false;
            logger.severe("JDBC driver class name not defined");
        }

        // Get the driver
        if(ret)
        {
            driver = JDBCDatabaseDriver.getDriver(driverType);
            if(driver == null)
                throw new MissingParameterException("driver not found for driver type '"+driverType+"'");

            try
            {
                Class.forName(driver.getClassName());
            }
            catch(ClassNotFoundException ex)
            {
                ret = false;
                String msg = "Driver Class not found in classpath: "+driver.getClassName();
                connectException = new MissingParameterException(msg);
                logger.severe(msg);
            }
        }

        // Get the hostname
        if(ret)
        {
            hostname = p.getHostname();
            if((hostname == null || hostname.length() == 0) && !driver.isEmbedded())
            {
                ret = false;
                String msg = "JDBC hostname name not defined";
                connectException = new MissingParameterException(msg);
                logger.severe(msg);
            }
        }

        // Get the port number
        if(ret)
        {
            port = p.getPort();
            if(port == 0 && !driver.isEmbedded())
            {
                ret = false;
                String msg = "JDBC port not defined";
                connectException = new MissingParameterException(msg);
                logger.severe(msg);
            }
        }

        // Get the database name
        if(ret)
        {
            databaseName = p.getDatabaseName();
            if(databaseName != null && databaseName.length() > 0)
            {
                name = new String(databaseName);
            }
            else if(driver.hasDefaultDatabase())
            {
                // Using default database
                name = driver.getDefaultDatabaseName();
            }
            else
            {
                ret = false;
                String msg = "JDBC database name not defined";
                connectException = new MissingParameterException(msg);
                logger.severe(msg);
            }
        }

        // Get the username
        if(ret)
        {
            username = p.getUsername();
            if((username == null || username.length() == 0) && !driver.allowEmptyUsername())
            {
                ret = false;
                String msg = "JDBC username not defined";
                connectException = new MissingParameterException(msg);
                logger.severe(msg);
            }
        }

        // Get the password
        if(ret)
        {
            passwd = p.getPassword();
            if((passwd == null || passwd.length() == 0) && !driver.allowEmptyPassword())
            {
                ret = false;
                String msg = "JDBC password not defined";
                connectException = new MissingParameterException(msg);
                logger.severe(msg);
            }
        }

        // Get the connect timeout
        if(ret)
        {
            connectTimeout = p.getConnectTimeout();
        }

        // Connect to the database
        if(ret)
        {
            // Get the other hostnames in the cluster
            otherHostnames = p.getOtherHostnames();

            // Get the connection properties
            connectionParameters = p.getParameters();

            try
            {
                connectInternal(verbose);
                if(connectException != null)
                {
                    String msg = "Reconnected to database '"+getName()+"' successfully";
                    logger.info(msg);
                    connectException = null;
                }
            }
            catch(Exception e)
            {
                if(connectException == null)
                {
                    String sqlsuffix = "";
                    if(e instanceof SQLException)
                    {
                        SQLException sqle = (SQLException)e;
                        sqlsuffix = " code="+sqle.getErrorCode()+" state="+sqle.getSQLState();
                    }

                    connectException = e;
                    String msg = "Unable to connect to database '"
                        +getName()+"': "+e.getClass().getName()+": "+e.getMessage()+sqlsuffix;
                    logger.severe(msg);
                    setConnectionStatus(ERROR);
                }
                return false;
            }

            // Show the banner with the version info
            try
            {
                if(verbose)
                    showBanner();
            }
            catch(AbstractMethodError e)
            {
                // Doesn't work on SQL Server
            }
        }

        createDAOFactories();
        createTables();

        return ret;
    }

    /**
     * Connect to the database.
     */
    protected void connectInternal(boolean log) throws Exception
    {
        // Build the connection string
        String url = JDBCDatabaseDriver.getConnectionString(driverType, hostname, 
            port, databaseName, username, passwd, otherHostnames, connectionParameters);

        if(debug())
        {
            logger.info("connecting to database '"+getName()
                +"' with user '"+username+"' using: "+url);
        }

        if(connectTimeout > 0)
            DriverManager.setLoginTimeout(connectTimeout);
        conn = DriverManager.getConnection(url, username, passwd);
        conn.setAutoCommit(true);
        data = conn.getMetaData();

        // Update the connection status
        setConnectionStatus(isConnectionValid() ? CONNECTED : NOT_CONNECTED);

        if(log && getConnectionStatus() == CONNECTED)
        {
            logger.info("Connected to database '"+getName()+"' successfully");
        }
    }

    /**
     * Returns <CODE>true</CODE> if an application table is missing from the database.
     */
    protected boolean hasMissingTable() 
    {
        return contentDAOFactory.hasMissingTable() || socialDAOFactory.hasMissingTable();
    }

    /**
     * Create any application tables that are missing from the database.
     */
    protected void createTables() 
    {
        contentDAOFactory.createTables();
        socialDAOFactory.createTables();
    }

    /**
     * Create an object containing all the provider-specific SQL statements.
     */
    protected void createDAOFactories()
    {
        if(driver != null)
        {
            contentDAOFactory = new ContentDAOFactory(driver, this);
            socialDAOFactory = new SocialDAOFactory(driver, this);
        }
    }

    /**
     * Returns the content DAO factory.
     */
    public ContentDAOFactory getContentDAOFactory()
    {
        return contentDAOFactory;
    }

    /**
     * Returns the social media DAO factory.
     */
    public SocialDAOFactory getSocialDAOFactory()
    {
        return socialDAOFactory;
    }

    /**
     * Returns <CODE>true</CODE> if the dao factory exists.
     */
    protected boolean hasDAOFactories()
    {
        return contentDAOFactory != null && socialDAOFactory != null;
    }

    /**
     * Close any factory objects.
     */
    protected void closeDAOFactories()
    {
        if(contentDAOFactory != null)
            contentDAOFactory.close();
        if(socialDAOFactory != null)
            socialDAOFactory.close();
    }

    /**
     * Sets the connection status.
     */
    private void setConnectionStatus(int status) 
    {
        lastConnectionStatus = status;
    }

    /**
     * Returns the connection status.
     */
    private int getConnectionStatus() 
    {
        return lastConnectionStatus;
    }

    /**
     * Returns the last exception raised during a failed connection attempt.
     */
    public Exception getConnectException()
    {
        return connectException;
    }

    /**
     * Returns <CODE>true</CODE> if the database is currently connected.
     */
    public boolean isConnected() throws Exception
    {
        boolean ret = false;

        if(conn != null)
        {
            try
            {
                ret = isConnectionValid();
                setConnectionStatus(ret ? CONNECTED : NOT_CONNECTED);
            }
            catch(Exception e)
            {
                setConnectionStatus(ERROR);
                throw e;
            }
        }

        return ret;
    }

    /**
     * Closes the connection to the database (if one exists).
     * @param verbose <CODE>true</CODE> if log entries should be written during the connection
     * @param force <CODE>true</CODE> force the connection closed without checking the current status
     */
    public void close(boolean verbose) 
    {
        boolean isConnected = false;

        try
        {
            isConnected = isConnected();
        }
        catch(Exception e)
        {
        }

        try
        {
            if(isConnected)
            {
                if(isConnected && verbose)
                    logger.info("Attempting to disconnect from database '"+getName()+"' ...");
                closeDAOFactories();
                if(conn != null)
                    conn.close();
                if(isConnected && verbose)
                    logger.info("Disconnected from database '"+getName()+"' successfully");
            }
        }
        catch(Exception e)
        {
            logger.severe("Error disconnecting from database '"+getName()+"': "
                +e.getClass().getName()+": "+e.getMessage());
        }

        setConnectionStatus(NOT_CONNECTED);
    }

    /**
     * Shows a banner containing version and other information for the database.
     */
    private void showBanner()
    {
        if(data != null)
        {
            try
            {
               logger.info("Database: "+data.getDatabaseProductVersion().replace('\n', ' ')
                   +" "+data.getDatabaseMajorVersion()+"."+data.getDatabaseMinorVersion());
               logger.info("Database Driver: "+data.getDriverName()+" "+data.getDriverVersion());
               logger.info("JDBC: "+data.getJDBCMajorVersion()+"."+data.getJDBCMinorVersion());
            }
            catch(SQLException e)
            {
            }
        }
    }

    /**
     * Returns the name of the database driver.
     */
    public String getDriverInfo() throws SQLException
    {
        return data != null ? data.getDriverName()+" "+data.getDriverVersion() : "";
    }

    /**
     * Returns the database driver.
     */
    public JDBCDatabaseDriver getDriver()
    {
        return driver;
    }

    /**
     * Returns the database metadata.
     */
    public DatabaseMetaData getMetaData()
    {
        return data;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is not caused by a connection error.
     */
    public static boolean isSyntaxError(Throwable e)
    {
        String msg = "";
        if(e.getMessage() != null)
            msg = e.getMessage().toLowerCase();
        return msg.length() > 0
            && msg.indexOf("shutdown") == -1 // Not for shutdown or disconnect errors
            && msg.indexOf("inconsistent state") == -1
            && msg.indexOf("reset") == -1
            && msg.indexOf("socket") == -1
            && msg.indexOf("abort") == -1
            && msg.indexOf("closed") == -1;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by a disconnection.
     */
    public static boolean isDisconnectError(Throwable e)
    {
        String msg = StringUtils.serialize(e);
        return msg.indexOf("shutdown") != -1
            || msg.indexOf("inconsistent state") != -1
            || msg.indexOf("reset") != -1
            || msg.indexOf("socket") != -1
            || msg.indexOf("abort") != -1
            || msg.indexOf("Closed") != -1
            || msg.indexOf("closed") != -1;
    }

    /**
     * Returns <CODE>true</CODE> if the connection is valid.
     */
    public boolean isConnectionValid() throws Exception
    {
        return driver != null ? !conn.isClosed() : false;
    }

    /**
     * Stop the thread when garbage collecting this class.
     */
    protected void finalize() throws Throwable
    {
        close(false);
        super.finalize();
    }
}