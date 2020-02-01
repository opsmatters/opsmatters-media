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
package com.opsmatters.media.db.dao.drupal;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.drupal.FeedsItem;

/**
 * DAO that provides operations on the FEEDS tables in the drupal database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedsDAO extends DrupalDAO<FeedsItem>
{
    private static final Logger logger = Logger.getLogger(DrupalDAO.class.getName());

    /**
     * The query to use to select a feeds items from the FEEDS tables using the item GUID and type.
     */
    private static final String GET_ITEM_SQL =  
      "SELECT FF.FID, NFI.FEEDS_ITEM_GUID, NFI.ENTITY_ID, NFI.FEEDS_ITEM_IMPORTED, NFI.BUNDLE, PA.PATH, PA.ALIAS "
      + "FROM feeds_feed FF, node__feeds_item NFI, path_alias PA "
      + "WHERE NFI.FEEDS_ITEM_TARGET_ID=FF.FID "
      + "AND FF.TITLE=CONCAT(?,' Feed') "
      + "AND NFI.FEEDS_ITEM_GUID=? "
      + "AND PA.PATH=CONCAT('/node/',NFI.ENTITY_ID) "
      + "AND PA.LANGCODE='en' "
      + "AND PA.STATUS=1";

    /**
     * The query to use to get the count of feeds from the FEEDS_FEED table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM feeds_feed";

    /**
     * Constructor that takes a DAO factory.
     */
    public FeedsDAO(DrupalDAOFactory factory)
    {
        super(factory, "FEEDS_FEED");
    }

    /**
     * Returns a feeds item from the FEEDS tables by the item GUID and types.
     */
    public FeedsItem getItem(String type, String guid) throws SQLException
    {
        FeedsItem ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getItemStmt == null)
            getItemStmt = prepareStatement(getConnection(), GET_ITEM_SQL);
        clearParameters(getItemStmt);

        ResultSet rs = null;

        try
        {
            getItemStmt.setString(1, type);
            getItemStmt.setString(2, guid);
            getItemStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getItemStmt.executeQuery();
            while(rs.next())
            {
                FeedsItem item = new FeedsItem();
                item.setFeedId(rs.getInt(1));
                item.setGuid(rs.getString(2));
                item.setEntityId(rs.getInt(3));
                item.setImportedDateMillis(rs.getInt(4)*1000L);
                item.setBundle(rs.getString(5));
                item.setPath(rs.getString(6));
                item.setAlias(rs.getString(7));
                ret = item;
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
     * Returns the count of feeds from the table.
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
        closeStatement(getItemStmt);
        getItemStmt = null;
        closeStatement(countStmt);
        countStmt = null;
    }

    private PreparedStatement getItemStmt;
    private PreparedStatement countStmt;
}
