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
package com.opsmatters.media.db.dao.drupal;

import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.drupal.LogMessage;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the WATCHDOG table in the drupal database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LogMessageDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(LogMessageDAO.class.getName());

    /**
     * The query to use to select the messages from the WATCHDOG table.
     */
    private static final String LIST_SQL =  
      "SELECT WID, UID, TIMESTAMP, TYPE, MESSAGE, SEVERITY, LINK, LOCATION, REFERER, HOSTNAME "
      + "FROM watchdog WHERE TYPE=? AND TIMESTAMP >= ?";

    /**
     * The query to use to get the count of messages from the WATCHDOG table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM watchdog";

    /**
     * Constructor that takes a DAO factory.
     */
    public LogMessageDAO(DrupalDAOFactory factory)
    {
        super(factory, "WATCHDOG");
    }

    /**
     * Returns the log messages from the WATCHDOG table.
     */
    public synchronized List<LogMessage> list(String type, Instant from) throws SQLException
    {
        List<LogMessage> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setString(1, type);
            listStmt.setLong(2, from != null ? from.toEpochMilli()/1000L : 0L);
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<LogMessage>();
            while(rs.next())
            {
                LogMessage message = new LogMessage();
                message.setWid(rs.getLong(1));
                message.setUid(rs.getInt(2));
                message.setCreatedDateMillis(rs.getLong(3)*1000L);
                message.setType(rs.getString(4));
                message.setMessage(rs.getString(5));
                message.setSeverity(rs.getShort(6));
                message.setLink(rs.getString(7));
                message.setLocation(rs.getString(8));
                message.setReferer(rs.getString(9));
                message.setHostname(rs.getString(10));
                ret.add(message);
            }
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
            }
            catch (SQLException ex) 
            {
            } 
        }

        postQuery();

        return ret;
    }

    /**
     * Returns the count of messages from the table.
     */
    public int count() throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(countStmt == null)
            countStmt = prepareStatement(getConnection(), COUNT_SQL);
        clearParameters(countStmt);

        countStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = countStmt.executeQuery();
        rs.next();

        return rs.getInt(1);
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(listStmt);
        listStmt = null;
        closeStatement(countStmt);
        countStmt = null;
    }

    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
}
