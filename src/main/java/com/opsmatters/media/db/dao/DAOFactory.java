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

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;

/**
 * The base class for all data access object factories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class DAOFactory
{
    /**
     * Constructor that takes a database driver and a connection.
     */
    public DAOFactory(JDBCDatabaseDriver driver, JDBCDatabaseConnection conn)
    {
        this.driver = driver;
        this.conn = conn;
    }

    /**
     * Returns the DAO from the list with the given name.
     */
    public BaseDAO getDAO(String name)
    {
        BaseDAO ret = null;
        for(BaseDAO dao : daoList)
        {
            if(dao.getTableName().equals(name))
                ret = dao;
        }
        return ret;
    }

    /**
     * Register the given DAO with this factory.
     */
    public void register(BaseDAO dao)
    {
        synchronized(daoList)
        {
            if(getDAO(dao.getTableName()) == null)
                daoList.add(dao);
        }
    }

    /**
     * Close any DAOs associated with this DAO factory.
     */
    public void close()
    {
        for(BaseDAO dao : daoList)
            close(dao);

        synchronized(daoList)
        {
            daoList.clear();
        }
    }

    /**
     * Returns <CODE>true</CODE> if the table for a DAO is missing from the database.
     */
    public boolean hasMissingTable()
    {
        boolean ret = false;
        for(BaseDAO dao : daoList)
        {
            if(ret = !dao.hasTable())
                break;
        }
        return ret;
    }

    /**
     * Create any application tables that are missing from the database.
     */
    public void createTables()
    {
        for(BaseDAO dao : daoList)
        {
            dao.checkTable();
        }
    }

    /**
     * Close the given DAO.
     */
    protected void close(BaseDAO dao)
    {
        if(dao != null)
            dao.close();
    }

    /**
     * Returns the driver associated with this DAO factory.
     */
    public JDBCDatabaseDriver getDriver()
    {
        return driver;
    }

    /**
     * Returns the connection associated with this DAO factory.
     */
    public JDBCDatabaseConnection getConnection()
    {
        return conn;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is a constraint violation.
     */
    public boolean isConstraintViolation(Exception ex)
    {
        return driver != null && driver.isConstraintViolation(ex);
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by inserting a value too large for a BLOB.
     */
    public boolean isDataTooLongException(Exception ex)
    {
        return driver != null && driver.isDataTooLongException(ex);
    }

    private JDBCDatabaseDriver driver;
    private JDBCDatabaseConnection conn;
    private List<BaseDAO> daoList = new ArrayList<BaseDAO>();
}
