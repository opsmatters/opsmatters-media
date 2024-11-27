/*
 * Copyright 2024 Gerald Curley
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
package com.opsmatters.media.db.dao.order.billing;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.billing.BillingProfile;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the BILLING_PROFILES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BillingProfileDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(BillingProfile.class.getName());

    /**
     * The query to use to select a profile from the BILLING_PROFILES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EMAIL, GIVEN_NAME, SURNAME, COMPANY_NAME, ADDRESS_LINE_1, ADDRESS_LINE_2, ADDRESS_AREA_1, ADDRESS_AREA_2, POSTAL_CODE, COUNTRY, PHONE_CODE, PHONE_NUMBER, ADDITIONAL_INFO, STATUS, CREATED_BY "
      + "FROM BILLING_PROFILES WHERE ID=?";

    /**
     * The query to use to insert a profile into the BILLING_PROFILES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO BILLING_PROFILES"
      + "( ID, CREATED_DATE, UPDATED_DATE, EMAIL, GIVEN_NAME, SURNAME, COMPANY_NAME, ADDRESS_LINE_1, ADDRESS_LINE_2, ADDRESS_AREA_1, ADDRESS_AREA_2, POSTAL_CODE, COUNTRY, PHONE_CODE, PHONE_NUMBER, ADDITIONAL_INFO, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a profile in the BILLING_PROFILES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE BILLING_PROFILES SET UPDATED_DATE=?, EMAIL=?, GIVEN_NAME=?, SURNAME=?, COMPANY_NAME=?, ADDRESS_LINE_1=?, ADDRESS_LINE_2=?, ADDRESS_AREA_1=?, ADDRESS_AREA_2=?, POSTAL_CODE=?, COUNTRY=?, PHONE_CODE=?, PHONE_NUMBER=?, ADDITIONAL_INFO=?, STATUS=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the profiles from the BILLING_PROFILES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EMAIL, GIVEN_NAME, SURNAME, COMPANY_NAME, ADDRESS_LINE_1, ADDRESS_LINE_2, ADDRESS_AREA_1, ADDRESS_AREA_2, POSTAL_CODE, COUNTRY, PHONE_CODE, PHONE_NUMBER, ADDITIONAL_INFO, STATUS, CREATED_BY "
      + "FROM BILLING_PROFILES ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of profiles from the BILLING_PROFILES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM BILLING_PROFILES";

    /**
     * The query to use to delete a billing from the BILLING_PROFILES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM BILLING_PROFILES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public BillingProfileDAO(OrderDAOFactory factory)
    {
        super(factory, "BILLING_PROFILES");
    }

    /**
     * Defines the columns and indices for the BILLING_PROFILES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("EMAIL", Types.VARCHAR, 50, true);
        table.addColumn("GIVEN_NAME", Types.VARCHAR, 30, false);
        table.addColumn("SURNAME", Types.VARCHAR, 30, false);
        table.addColumn("COMPANY_NAME", Types.VARCHAR, 50, false);
        table.addColumn("ADDRESS_LINE_1", Types.VARCHAR, 30, false);
        table.addColumn("ADDRESS_LINE_2", Types.VARCHAR, 30, false);
        table.addColumn("ADDRESS_AREA_1", Types.VARCHAR, 20, false);
        table.addColumn("ADDRESS_AREA_2", Types.VARCHAR, 20, false);
        table.addColumn("POSTAL_CODE", Types.VARCHAR, 20, false);
        table.addColumn("COUNTRY", Types.VARCHAR, 5, false);
        table.addColumn("PHONE_CODE", Types.VARCHAR, 5, false);
        table.addColumn("PHONE_NUMBER", Types.VARCHAR, 20, false);
        table.addColumn("ADDITIONAL_INFO", Types.VARCHAR, 50, false);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("BILLING_PROFILES_PK", new String[] {"ID"});
        table.addIndex("BILLING_PROFILES_EMAIL_IDX", new String[] {"EMAIL"});
        table.setInitialised(true);
    }

    /**
     * Returns a profile from the BILLING_PROFILES table by id.
     */
    public synchronized BillingProfile getById(String id) throws SQLException
    {
        BillingProfile ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByIdStmt == null)
            getByIdStmt = prepareStatement(getConnection(), GET_BY_ID_SQL);
        clearParameters(getByIdStmt);

        ResultSet rs = null;

        try
        {
            getByIdStmt.setString(1, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                BillingProfile profile = new BillingProfile();
                profile.setId(rs.getString(1));
                profile.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                profile.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                profile.setEmail(rs.getString(4));
                profile.setGivenName(rs.getString(5));
                profile.setSurname(rs.getString(6));
                profile.setCompanyName(rs.getString(7));
                profile.setAddressLine1(rs.getString(8));
                profile.setAddressLine2(rs.getString(9));
                profile.setAddressArea1(rs.getString(10));
                profile.setAddressArea2(rs.getString(11));
                profile.setPostalCode(rs.getString(12));
                profile.setCountry(rs.getString(13));
                profile.setPhoneCode(rs.getString(14));
                profile.setPhoneNumber(rs.getString(15));
                profile.setAdditionalInfo(rs.getString(16));
                profile.setStatus(rs.getString(17));
                profile.setCreatedBy(rs.getString(18));
                ret = profile;
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
     * Stores the given profile in the BILLING_PROFILES table.
     */
    public synchronized void add(BillingProfile profile) throws SQLException
    {
        if(!hasConnection() || profile == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, profile.getId());
            insertStmt.setTimestamp(2, new Timestamp(profile.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(profile.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, profile.getEmail());
            insertStmt.setString(5, profile.getGivenName());
            insertStmt.setString(6, profile.getSurname());
            insertStmt.setString(7, profile.getCompanyName());
            insertStmt.setString(8, profile.getAddressLine1());
            insertStmt.setString(9, profile.getAddressLine2());
            insertStmt.setString(10, profile.getAddressArea1());
            insertStmt.setString(11, profile.getAddressArea2());
            insertStmt.setString(12, profile.getPostalCode());
            insertStmt.setString(13, profile.getCountry().code());
            insertStmt.setString(14, profile.getPhoneCode());
            insertStmt.setString(15, profile.getPhoneNumber());
            insertStmt.setString(16, profile.getAdditionalInfo());
            insertStmt.setString(17, profile.getStatus().name());
            insertStmt.setString(18, profile.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created billing profile '"+profile.getId()+"' in BILLING_PROFILES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the billing profile exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given profile in the BILLING_PROFILES table.
     */
    public synchronized void update(BillingProfile profile) throws SQLException
    {
        if(!hasConnection() || profile == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(profile.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, profile.getEmail());
        updateStmt.setString(3, profile.getGivenName());
        updateStmt.setString(4, profile.getSurname());
        updateStmt.setString(5, profile.getCompanyName());
        updateStmt.setString(6, profile.getAddressLine1());
        updateStmt.setString(7, profile.getAddressLine2());
        updateStmt.setString(8, profile.getAddressArea1());
        updateStmt.setString(9, profile.getAddressArea2());
        updateStmt.setString(10, profile.getPostalCode());
        updateStmt.setString(11, profile.getCountry().code());
        updateStmt.setString(12, profile.getPhoneCode());
        updateStmt.setString(13, profile.getPhoneNumber());
        updateStmt.setString(14, profile.getAdditionalInfo());
        updateStmt.setString(15, profile.getStatus().name());
        updateStmt.setString(16, profile.getCreatedBy());
        updateStmt.setString(17, profile.getId());
        updateStmt.executeUpdate();

        logger.info("Updated billing profile '"+profile.getId()+"' in BILLING_PROFILES");
    }

    /**
     * Returns the profiles from the BILLING_PROFILES table.
     */
    public synchronized List<BillingProfile> list() throws SQLException
    {
        List<BillingProfile> ret = null;

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
            ret = new ArrayList<BillingProfile>();
            while(rs.next())
            {
                BillingProfile profile = new BillingProfile();
                profile.setId(rs.getString(1));
                profile.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                profile.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                profile.setEmail(rs.getString(4));
                profile.setGivenName(rs.getString(5));
                profile.setSurname(rs.getString(6));
                profile.setCompanyName(rs.getString(7));
                profile.setAddressLine1(rs.getString(8));
                profile.setAddressLine2(rs.getString(9));
                profile.setAddressArea1(rs.getString(10));
                profile.setAddressArea2(rs.getString(11));
                profile.setPostalCode(rs.getString(12));
                profile.setCountry(rs.getString(13));
                profile.setPhoneCode(rs.getString(14));
                profile.setPhoneNumber(rs.getString(15));
                profile.setAdditionalInfo(rs.getString(16));
                profile.setStatus(rs.getString(17));
                profile.setCreatedBy(rs.getString(18));
                ret.add(profile);
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
     * Returns the count of profiles from the BILLING_PROFILES table.
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
     * Removes the given profile from the BILLING_PROFILES table.
     */
    public synchronized void delete(BillingProfile profile) throws SQLException
    {
        if(!hasConnection() || profile == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, profile.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted billing profile '"+profile.getId()+"' in BILLING_PROFILES");
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listStmt);
        listStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
