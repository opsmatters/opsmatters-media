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

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import com.opsmatters.media.db.provider.*;

/**
 * Class that encapsulates the attributes of a JDBC database driver.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class JDBCDatabaseDriver extends DatabaseDriver
{
    /**
     * Empty constructor.
     */
    public JDBCDatabaseDriver()
    {
    }

    /**
     * Constructor that takes a type, name, driver class, URL format and auth format.
     */
    public JDBCDatabaseDriver(String type, String name, String className, String url, String auth)
    {
        super(type, name, className);
        setURLFormat(url);
        setAuthFormat(auth);
        types.put(type, this);
    }

    /**
     * Constructor that takes a type, name, driver class and URL format.
     */
    public JDBCDatabaseDriver(String type, String name, String className, String url)
    {
        this(type, name, className, url, null);
    }

    /**
     * Sets the url format of the driver.
     */
    public void setURLFormat(String s)
    {
        if(s != null)
            url = new String(s);
    }

    /**
     * Returns the url format of the driver.
     */
    public String getURLFormat()
    {
        return url;
    }

    /**
     * Sets the authentication format of the driver.
     */
    public void setAuthFormat(String s)
    {
        if(s != null)
            auth = new String(s);
    }

    /**
     * Returns the authentication format of the driver.
     */
    public String getAuthFormat()
    {
        return auth;
    }

    /**
     * Sets the provider for the driver.
     */
    public void setProvider(DBProvider provider)
    {
        this.provider = provider;
    }

    /**
     * Returns the provider for the driver.
     */
    public DBProvider getProvider()
    {
        return provider;
    }

    /**
     * Set to <CODE>true</CODE> if this driver should close a statement on an exception.
     */
    public void setCloseOnException(boolean b)
    {
        closeOnException = b;
    }

    /**
     * Returns <CODE>true</CODE> if this driver should close a statement on an exception.
     */
    public boolean closeOnException()
    {
        return closeOnException;
    }

    /**
     * Returns the driver of the given type.
     */
    public static JDBCDatabaseDriver getDriver(String type)
    {
        return types.get(type);
    }

    /**
     * Returns the name of the given driver.
     */
    public static String getName(String type)
    {
        String ret = "";
        JDBCDatabaseDriver driver = getDriver(type);
        if(driver != null)
            ret = driver.getName();
        return ret;
    }

    /**
     * Returns the class name of the given driver.
     */
    public static String getClassName(String type)
    {
        return getDriver(type).getClassName();
    }

    /**
     * Returns the url format of the given driver.
     */
    public static String getURLFormat(String type)
    {
        return getDriver(type).getURLFormat();
    }

    /**
     * Returns the driver for the given class name.
     */
    private static JDBCDatabaseDriver getDriverForClassName(String className)
    {
        JDBCDatabaseDriver ret = null;
        for(JDBCDatabaseDriver driver : types.values())
        {
            if(driver.getClassName().equals(className))
            {
                ret = driver;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns the driver for the given name.
     */
    public static JDBCDatabaseDriver getDriverIgnoreCase(String name)
    {
        JDBCDatabaseDriver ret = null;
        name = name.toLowerCase();
        for(JDBCDatabaseDriver driver : types.values())
        {
            if(driver.getName().toLowerCase().equals(name))
            {
                ret = driver;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns type of the given class name.
     */
    public static String getType(String className)
    {
        return getDriverForClassName(className).getType();
    }

    /**
     * Returns the connection string using the given parameters.
     */
    public static String getConnectionString(String type, String hostname, int port, String database, 
        String username, String password, String otherHostnames, String connectionParameters)
    {
        JDBCDatabaseDriver driver = types.get(type);
        StringBuffer buff = new StringBuffer();

        Map<String,String> params = new HashMap<String,String>();
        params.put("hostname", hostname);
        params.put("port", Integer.toString(port));
        params.put("database", database);
        params.put("username", username);
        params.put("password", password);

        // Get the authentication string
        String auth = "";
        if(username != null && username.length() > 0)
        {
            StringSubstitutor substitutor = new StringSubstitutor(params);
            auth = substitutor.replace(driver.getAuthFormat());
        }

        // Get the other hostnames in the cluster
        if(driver.isClustered() && otherHostnames != null && otherHostnames.length() > 0)
        {
            otherHostnames = StringUtils.deleteWhitespace(otherHostnames);
            if(!otherHostnames.startsWith(","))
                otherHostnames = ","+otherHostnames;
        }
        else
        {
            otherHostnames = "";
        }

        params.put("auth", auth);
        params.put("other-hostnames", otherHostnames);
        //params.put("directory", getDirectory());

        StringSubstitutor substitutor = new StringSubstitutor(params);
        buff.append(substitutor.replace(driver.getURLFormat()));

        // Add any connection parameters from the provider
        buff.append(driver.getConnectionParameters());

        // Add any other connection parameters from the server
        if(connectionParameters != null)
            buff.append(connectionParameters);

        return buff.toString();
    }

    /**
     * Returns the connection string using the given parameters.
     */
    public static String getConnectionString(String type, String hostname, int port, String database)
    {
        return getConnectionString(type, hostname, port, database, "", "", null, null);
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is a constraint violation.
     */
    public boolean isConstraintViolation(Exception ex)
    {
        return provider != null && provider.isConstraintViolation(ex);
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by inserting a value too large for a BLOB.
     */
    public boolean isDataTooLongException(Exception ex)
    {
        return provider != null && provider.isDataTooLongException(ex);
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by a tablespace error.
     */
    public boolean isTablespaceError(Exception ex)
    {
        return provider != null && provider.isTablespaceError(ex);
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by an invalid column error.
     */
    public boolean isInvalidColumnError(Exception ex)
    {
        if(provider != null)
            return provider.isInvalidColumnError(ex);
        return DBProvider.isDefaultInvalidColumnError(ex); // use default if no provider
    }

    /**
     * Returns an expression to convert the given date using the default date format (dd-MM-yyyy HH:mm:ss).
     */
    public String getDateConversion(long dt)
    {
        String ret = "";
        if(provider != null)
            ret = provider.getDateConversion(dt);
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the provider can use integers (0,1) for booleans.
     */
    public boolean useIntegerForBoolean()
    {
        boolean ret = true;
        if(provider != null)
            ret = provider.useIntegerForBoolean();
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the provider uses String instead of CLOB.
     */
    public boolean useStringForCLOB()
    {
        boolean ret = false;
        if(provider != null)
            ret = provider.useStringForCLOB();
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the provider uses Bytes instead of BLOB.
     */
    public boolean useBytesForBLOB()
    {
        boolean ret = false;
        if(provider != null)
            ret = provider.useBytesForBLOB();
        return ret;
    }

    private String url = "";
    private String auth = "";
    private DBProvider provider;
    private boolean closeOnException = false;

    public static JDBCDatabaseDriver ORACLE, SQLSERVER, MYSQL, DB2,
        DERBY, DERBY_EMBEDDED, POSTGRESQL, SQLITE, HSQLDB, HSQLDB_EMBEDDED, H2, H2_EMBEDDED;

    public static JDBCDatabaseDriver[] DRIVERS;
    public static String[] CODES;
    private static Map<String,JDBCDatabaseDriver> types = new LinkedHashMap<String,JDBCDatabaseDriver>();

    static
    {
        // Oracle driver definition
        ORACLE = new JDBCDatabaseDriver("oracle", "Oracle", 
            "oracle.jdbc.driver.OracleDriver", 
            "jdbc:oracle:thin:@${hostname}:${port}:${database}");
        ORACLE.setProvider(new OracleProvider());

        // SQL Server driver definition
        SQLSERVER = new JDBCDatabaseDriver("sqlserver", "SQL Server", 
            "com.microsoft.sqlserver.jdbc.SQLServerDriver", 
            "jdbc:sqlserver://${hostname}:${port};database=${database}${auth}",
            ";user=${username};password=${password}");
        SQLSERVER.setProvider(new SQLServerProvider());
        SQLSERVER.setAllowEmptyPassword(true);
        SQLSERVER.setDefaultDatabase(true);
        SQLSERVER.setDefaultDatabaseName(SQLServerProvider.DEFAULT);
        SQLSERVER.setConnectionParameters(";selectMethod=cursor");

        // MySQL driver definition
        MYSQL = new JDBCDatabaseDriver("mysql", "MySQL", 
            "com.mysql.cj.jdbc.Driver", 
            "jdbc:mysql://${hostname}:${port}${other-hostnames}/${database}");
        MYSQL.setClustered(true);
        MYSQL.setProvider(new MySQLProvider());
        MYSQL.setAllowEmptyPassword(true);
        MYSQL.setConnectionParameters("?useSSL=false&sessionVariables=sql_mode='ANSI'&characterEncoding=utf8&allowPublicKeyRetrieval=true");

        // IBM DB2 driver definition
        DB2 = new JDBCDatabaseDriver("db2", "IBM DB2", 
            "com.ibm.db2.jcc.DB2Driver", 
            "jdbc:db2://${hostname}:${port}/${database}");
        DB2.setProvider(new DB2Provider());
        DB2.setConnectionParameters(":driverType=4;fullyMaterializeLobData=true;"
            +"fullyMaterializeInputStreams=true;progressiveStreaming=2;progresssiveLocators=2;");

        // Derby driver definition
        DERBY = new JDBCDatabaseDriver("derby", "Derby", 
            "org.apache.derby.jdbc.ClientDriver", 
            "jdbc:derby://${hostname}:${port}/${database}");
        DERBY.setProvider(new DerbyProvider());
        DERBY.setAllowEmptyUsername(true);
        DERBY.setAllowEmptyPassword(true);

        // Derby Embedded driver definition
        DERBY_EMBEDDED = new JDBCDatabaseDriver("derby-embedded", "Derby", 
            "org.apache.derby.jdbc.EmbeddedDriver", 
            "jdbc:derby:${directory}/${database}");
        DERBY_EMBEDDED.setEmbedded(true);
        DERBY_EMBEDDED.setProvider(new DerbyProvider());
        DERBY_EMBEDDED.setAllowEmptyUsername(true);
        DERBY_EMBEDDED.setAllowEmptyPassword(true);
        DERBY_EMBEDDED.setConnectionParameters(";create=true");

        // PostgreSQL driver definition
        POSTGRESQL = new JDBCDatabaseDriver("postgresql", "PostgreSQL", 
            "org.postgresql.Driver", 
            "jdbc:postgresql://${hostname}:${port}${other-hostnames}/${database}");
        POSTGRESQL.setClustered(true);
        POSTGRESQL.setProvider(new PostgreSQLProvider());
        POSTGRESQL.setAllowEmptyPassword(true);

        // SQLite driver definition
        SQLITE = new JDBCDatabaseDriver("sqlite", "SQLite", 
            "org.sqlite.JDBC", 
            "jdbc:sqlite:${directory}/${database}");
        SQLITE.setEmbedded(true);
        SQLITE.setProvider(new SQLiteProvider());
        SQLITE.setAllowEmptyUsername(true);
        SQLITE.setAllowEmptyPassword(true);
        SQLITE.setCloseOnException(true);

        // HSQLDB driver definition
        HSQLDB = new JDBCDatabaseDriver("hsqldb", "HSQLDB", 
            "org.hsqldb.jdbcDriver", 
            "jdbc:hsqldb:hsql://${hostname}:${port}/${database}");
        HSQLDB.setProvider(new HSQLDBProvider());
        HSQLDB.setAllowEmptyPassword(true);

        // HSQLDB Embedded driver definition
        HSQLDB_EMBEDDED = new JDBCDatabaseDriver("hsqldb-embedded", "HSQLDB", 
            "org.hsqldb.jdbcDriver", 
            "jdbc:hsqldb:file:${directory}/${database}");
        HSQLDB_EMBEDDED.setEmbedded(true);
        HSQLDB_EMBEDDED.setProvider(new HSQLDBProvider());
        HSQLDB_EMBEDDED.setAllowEmptyUsername(true);
        HSQLDB_EMBEDDED.setAllowEmptyPassword(true);

        // H2 driver definition
        H2 = new JDBCDatabaseDriver("h2", "H2", 
            "org.h2.Driver", 
            "jdbc:h2:tcp://${hostname}:${port}${other-hostnames}/${database}");
        H2.setClustered(true);
        H2.setProvider(new H2Provider());
        H2.setAllowEmptyPassword(true);

        // H2 Embedded driver definition
        H2_EMBEDDED = new JDBCDatabaseDriver("h2-embedded", "H2", 
            "org.h2.Driver", 
            "jdbc:h2:file:${directory}/${database}");
        H2_EMBEDDED.setEmbedded(true);
        H2_EMBEDDED.setProvider(new H2Provider());
        H2_EMBEDDED.setAllowEmptyUsername(true);
        H2_EMBEDDED.setAllowEmptyPassword(true);

        DRIVERS = new JDBCDatabaseDriver[types.size()];
        CODES = new String[types.size()];

        // Get the full list of drivers
        List<JDBCDatabaseDriver> drivers = new ArrayList<JDBCDatabaseDriver>(types.values());
        for(int i = 0; i < drivers.size(); i++)
        {
            JDBCDatabaseDriver driver = drivers.get(i);
            DRIVERS[i] = driver;
            CODES[i] = driver.getType();
        }
    }
}