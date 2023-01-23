/*
 * Copyright 2021 Gerald Curley
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.drupal.DrupalTaxonomyTerm;

/**
 * DAO that provides operations on the TAXONOMY_TERM_FIELD_DATA table in the drupal database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DrupalTaxonomyTermDAO extends DrupalDAO<DrupalTaxonomyTerm>
{
    private static final Logger logger = Logger.getLogger(DrupalTaxonomyTermDAO.class.getName());

    /**
     * The query to use to select the terms from the TAXONOMY_TERM_FIELD_DATA table.
     */
    private static final String LIST_SQL =  
      "SELECT TID, CHANGED, VID, NAME, DESCRIPTION__VALUE, STATUS "
      + "FROM taxonomy_term_field_data WHERE STATUS=1";

    /**
     * The query to use to get the count of terms from the TAXONOMY_TERM_FIELD_DATA table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM taxonomy_term_field_data";

    /**
     * Constructor that takes a DAO factory.
     */
    public DrupalTaxonomyTermDAO(DrupalDAOFactory factory)
    {
        super(factory, "TAXONOMY_TERM_FIELD_DATA");
    }

    /**
     * Returns the terms from the TAXONOMY_TERM_FIELD_DATA table.
     */
    public synchronized List<DrupalTaxonomyTerm> list() throws SQLException
    {
        List<DrupalTaxonomyTerm> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<DrupalTaxonomyTerm>();
            while(rs.next())
            {
                DrupalTaxonomyTerm term = new DrupalTaxonomyTerm();
                term.setTid(rs.getInt(1));
                term.setCreatedDateMillis(rs.getLong(2)*1000L);
                term.setType(rs.getString(3));
                term.setName(rs.getString(4));
                term.setDescription(rs.getString(5));
                term.setPublished(rs.getBoolean(6));
                ret.add(term);
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
     * Returns the count of terms from the table.
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
