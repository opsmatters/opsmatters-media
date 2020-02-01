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
package com.opsmatters.media.db.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Clob;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;
import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.db.provider.DBProvider;
import com.opsmatters.media.util.StringUtils;

/**
 * The base class for all data access objects.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class BaseDAO
{
    private static final Logger logger = Logger.getLogger(BaseDAO.class.getName());

    protected static Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    /**
     * The timeout for database queries (in seconds).
     */
    public static int QUERY_TIMEOUT = 60;

    /**
     * Constructor that takes a DAO Factory.
     */
    public BaseDAO(DAOFactory factory, String tableName)
    {
        this.factory = factory;
        setTableName(tableName);

        // Postgres changes all table names to lower case
        DBProvider provider = getDriver().getProvider();
        if(!provider.isCaseSensitive())
             setTableName(getTableName().toLowerCase());

        hasTable = tableExists(getTableName());
        factory.register(this);
        table.setName(tableName);
    }

    /**
     * Returns the name of this DAO.
     */
    public String toString()
    {
        return getTableName();
    }

    /**
     * Sets the name of the database table.
     */
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    /**
     * Returns the name of the database table.
     */
    public String getTableName()
    {
        return tableName;
    }

    /**
     * Returns the driver associated with this DAO.
     */
    public JDBCDatabaseDriver getDriver()
    {
        return factory.getDriver();
    }

    /**
     * Returns the connection associated with this DAO.
     */
    public JDBCDatabaseConnection getDatabaseConnection()
    {
        return factory.getConnection();
    }

    /**
     * Returns the connection associated with this DAO.
     */
    public Connection getConnection()
    {
        return getDatabaseConnection().getConnection();
    }

    /**
     * Returns <CODE>true</CODE> if the database is currently connected.
     */
    public boolean isConnected() throws Exception
    {
        return getDatabaseConnection().isConnected();
    }

    /**
     * Returns the metadata associated with the database for this DAO.
     */
    public DatabaseMetaData getMetaData()
    {
        return getDatabaseConnection().getMetaData();
    }

    /**
     * Close any resources associated with this DAO.
     */
    protected abstract void close();

    /**
     * Close the given prepared statement.
     */
    protected void closeStatement(PreparedStatement stmt)
    {
        try
        {
            if(stmt != null)
                stmt.close();
        }
        catch(Exception e)
        {
        }
    }

    /**
     * Create the table if it doesnt exist.
     */
    public void checkTable()
    {
        if(getDatabaseConnection().isInternal())
        {
            if(!table.isInitialised())
                defineTable();

            if(getTableName() != null && !hasTable)
                createTable();
        }
    }

    /**
     * Defines the columns for the table.
     */
    protected void defineTable()
    {
    }

    /**
     * Returns the table definition for this DAO.
     */
    public DBTable getTable()
    {
        return table;
    }

    /**
     * Sets the table definition for this DAO.
     */
    public void setTable(DBTable table)
    {
        this.table = table;
    }

    /**
     * Creates the table and indices.
     */
    public void createTable()
    {
        Connection conn = getConnection();
        if(conn != null)
        {
            try
            {
                DBProvider provider = getDriver().getProvider();
                String sql = table.getTableSQL(provider);
                String[] indices = table.getIndicesSQL(provider);
                Statement stmt = conn.createStatement();

                try
                {
                    if(sql != null)
                    {
                        stmt.executeUpdate(sql);
                        logger.info(getTableName()+" table created in database");

                        if(indices != null && indices.length > 0)
                        {
                            for(int i = 0; i < indices.length; i++)
                                stmt.execute(indices[i]);
                            logger.info(indices.length+" indices created in database");
                        }
                    }
                }
                catch(SQLException e)
                {
                    stmt.close();
                    throw e;
                }

                stmt.close();
                hasTable = true;
            }
            catch(Exception e)
            {
                logger.severe(StringUtils.serialize(e));
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the table for this DAO exists.
     */
    public boolean hasTable()
    {
        return hasTable;
    }

    /**
     * Returns <CODE>true</CODE> if the database has the given table.
     */
    private boolean tableExists(String table)
    {
        boolean ret = false;

        ResultSet rs = null;
        try
        {
            if(table != null && table.length() > 0)
            {
                DatabaseMetaData data = getMetaData();
                if(isConnected() && data != null)
                {
                    String[] types = {"TABLE"};
                    rs = data.getTables(null, null, table, types); 
                    ret = rs.next(); 
                }
            }
        }
        catch(Exception e) 
        {
        } 

        try
        {
            if(rs != null)
                rs.close();
        }
        catch(Exception e) 
        {
        } 

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the database table has the given column.
     */
    public boolean hasColumn(String column)
    {
        boolean ret = false;

        ResultSet rs = null;
        try
        {
            DatabaseMetaData data = getMetaData();
            if(isConnected() && data != null)
            {
                rs = data.getColumns(null, null, getTableName(), column); 
                ret = rs.next(); 
            }
        }
        catch(Exception e) 
        {
        } 

        try
        {
            if(rs != null)
                rs.close();
        }
        catch(Exception e) 
        {
        } 

        return ret;
    }

    /**
     * Alter the table if it requires changes.
     * <P>
     * Implemented by super-class.
     */
    public void alterTable()
    {
    }

    /**
     * Drops the database table for this DAO.
     */
    public void dropTable()
    {
        Connection conn = getConnection();
        if(conn != null && getTableName() != null)
        {
            Statement stmt = null;
            try
            {
                stmt = conn.createStatement();
                if(getTableName() != null)
                {
                    stmt.executeUpdate("drop table "+getTableName());
                    logger.info(getTableName()+" dropped from database");
                    hasTable = false;
                }
            }
            catch(SQLException e)
            {
                logger.severe(StringUtils.serialize(e));
            }
            finally
            {
                try
                {
                    if(stmt != null)
                        stmt.close();
                }
                catch(SQLException e)
                {
                }
            }
        }
    }

    /**
     * Returns the string value of the given CLOB column.
     */
    protected String getClob(ResultSet rs, int col) throws SQLException
    {
        String ret = "";
        if(getDriver().useStringForCLOB())
        {
            ret = rs.getString(col);
        }
        else
        {
            Clob clob = rs.getClob(col);
            ret = clob.getSubString(1, (int)clob.length());
        }
        return ret;
    }

    /**
     * Returns the byte value of the given BLOB column.
     */
    protected Blob getBlob(ResultSet rs, int col) throws SQLException
    {
        Blob ret = null;
        if(getDriver().useBytesForBLOB())
        {
            byte[] bytes = rs.getBytes(col);
            ret = new SerialBlob(bytes);
        }
        else
        {
            ret = rs.getBlob(col);
        }
        return ret;
    }

    /**
     * Clear the parameters of the given prepared statement.
     */
    protected void clearParameters(PreparedStatement stmt)
    {
        try
        {
            if(stmt != null)
                stmt.clearParameters();
        }
        catch(SQLException e)
        {
            // SQLite has bug that causes this to throw
            //   a "statement is not executing" error
        }
    }

    /**
     * Returns a prepared statement for the given query.
     */
    protected PreparedStatement prepareStatement(Connection conn, String query) throws SQLException
    {
        query = getDriver().getProvider().quoteReservedWords(query);
        return conn.prepareStatement(query);
    }

    /**
     * Returns <CODE>true</CODE> if the table exists and has a connection.
     */
    protected boolean hasConnection()
    {
        return hasTable() && getConnection() != null;
    }

    /**
     * Prepare the connection for a query execution.
     */
    protected void preQuery() throws SQLException
    {
        if(getDriver().isEmbedded())
            getConnection().setAutoCommit(false); // Required by Derby or it throws errors
    }

    /**
     * Prepare the connection after a query execution.
     */
    protected void postQuery() throws SQLException
    {
        if(getDriver().isEmbedded())
            getConnection().setAutoCommit(true);
    }

    private DAOFactory factory;
    private String tableName;
    private boolean hasTable = false;
    protected DBTable table = new DBTable();
}
