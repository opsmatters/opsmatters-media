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
package com.opsmatters.media.db.dao.order.contact;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.contact.Company;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the COMPANIES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CompanyDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(Company.class.getName());

    /**
     * The query to use to select a company from the COMPANIES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, BILLING_NAME, BILLING_EMAIL, GIVEN_NAME, SURNAME, ADDRESS_LINE_1, ADDRESS_LINE_2, ADDRESS_AREA_1, ADDRESS_AREA_2, POSTAL_CODE, COUNTRY_CODE, PHONE_CODE, PHONE_NUMBER, ADDITIONAL_INFO, STATUS, CREATED_BY "
      + "FROM COMPANIES WHERE ID=?";

    /**
     * The query to use to insert a company into the COMPANIES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO COMPANIES"
      + "( ID, CREATED_DATE, UPDATED_DATE, NAME, BILLING_NAME, BILLING_EMAIL, GIVEN_NAME, SURNAME, ADDRESS_LINE_1, ADDRESS_LINE_2, ADDRESS_AREA_1, ADDRESS_AREA_2, POSTAL_CODE, COUNTRY_CODE, PHONE_CODE, PHONE_NUMBER, ADDITIONAL_INFO, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a company in the COMPANIES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE COMPANIES SET UPDATED_DATE=?, NAME=?, BILLING_NAME=?, BILLING_EMAIL=?, GIVEN_NAME=?, SURNAME=?, ADDRESS_LINE_1=?, ADDRESS_LINE_2=?, ADDRESS_AREA_1=?, ADDRESS_AREA_2=?, POSTAL_CODE=?, COUNTRY_CODE=?, PHONE_CODE=?, PHONE_NUMBER=?, ADDITIONAL_INFO=?, STATUS=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the companies from the COMPANIES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, BILLING_NAME, BILLING_EMAIL, GIVEN_NAME, SURNAME, ADDRESS_LINE_1, ADDRESS_LINE_2, ADDRESS_AREA_1, ADDRESS_AREA_2, POSTAL_CODE, COUNTRY_CODE, PHONE_CODE, PHONE_NUMBER, ADDITIONAL_INFO, STATUS, CREATED_BY "
      + "FROM COMPANIES ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of companies from the COMPANIES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM COMPANIES";

    /**
     * The query to use to delete a billing from the COMPANIES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM COMPANIES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public CompanyDAO(OrderDAOFactory factory)
    {
        super(factory, "COMPANIES");
    }

    /**
     * Defines the columns and indices for the COMPANIES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("NAME", Types.VARCHAR, 50, true);
        table.addColumn("BILLING_NAME", Types.VARCHAR, 80, false);
        table.addColumn("BILLING_EMAIL", Types.VARCHAR, 50, false);
        table.addColumn("GIVEN_NAME", Types.VARCHAR, 30, false);
        table.addColumn("SURNAME", Types.VARCHAR, 30, false);
        table.addColumn("ADDRESS_LINE_1", Types.VARCHAR, 50, false);
        table.addColumn("ADDRESS_LINE_2", Types.VARCHAR, 50, false);
        table.addColumn("ADDRESS_AREA_1", Types.VARCHAR, 20, false);
        table.addColumn("ADDRESS_AREA_2", Types.VARCHAR, 20, false);
        table.addColumn("POSTAL_CODE", Types.VARCHAR, 20, false);
        table.addColumn("COUNTRY_CODE", Types.VARCHAR, 5, false);
        table.addColumn("PHONE_CODE", Types.VARCHAR, 5, false);
        table.addColumn("PHONE_NUMBER", Types.VARCHAR, 20, false);
        table.addColumn("ADDITIONAL_INFO", Types.VARCHAR, 128, false);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("COMPANIES_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a company from the COMPANIES table by id.
     */
    public synchronized Company getById(String id) throws SQLException
    {
        Company ret = null;

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
                Company company = new Company();
                company.setId(rs.getString(1));
                company.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                company.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                company.setName(rs.getString(4));
                company.setBillingName(rs.getString(5));
                company.setBillingEmail(rs.getString(6));
                company.setGivenName(rs.getString(7));
                company.setSurname(rs.getString(8));
                company.setAddressLine1(rs.getString(9));
                company.setAddressLine2(rs.getString(10));
                company.setAddressArea1(rs.getString(11));
                company.setAddressArea2(rs.getString(12));
                company.setPostalCode(rs.getString(13));
                company.setCountry(rs.getString(14));
                company.setPhoneCode(rs.getString(15));
                company.setPhoneNumber(rs.getString(16));
                company.setAdditionalInfo(rs.getString(17));
                company.setStatus(rs.getString(18));
                company.setCreatedBy(rs.getString(19));
                ret = company;
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
     * Stores the given company in the COMPANIES table.
     */
    public synchronized void add(Company company) throws SQLException
    {
        if(!hasConnection() || company == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, company.getId());
            insertStmt.setTimestamp(2, new Timestamp(company.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(company.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, company.getName());
            insertStmt.setString(5, company.getBillingName());
            insertStmt.setString(6, company.getBillingEmail());
            insertStmt.setString(7, company.getGivenName());
            insertStmt.setString(8, company.getSurname());
            insertStmt.setString(9, company.getAddressLine1());
            insertStmt.setString(10, company.getAddressLine2());
            insertStmt.setString(11, company.getAddressArea1());
            insertStmt.setString(12, company.getAddressArea2());
            insertStmt.setString(13, company.getPostalCode());
            insertStmt.setString(14, company.getCountry().code());
            insertStmt.setString(15, company.getPhoneCode());
            insertStmt.setString(16, company.getPhoneNumber());
            insertStmt.setString(17, company.getAdditionalInfo());
            insertStmt.setString(18, company.getStatus().name());
            insertStmt.setString(19, company.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created company '"+company.getId()+"' in COMPANIES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the billing company exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given company in the COMPANIES table.
     */
    public synchronized void update(Company company) throws SQLException
    {
        if(!hasConnection() || company == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(company.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, company.getName());
        updateStmt.setString(3, company.getBillingName());
        updateStmt.setString(4, company.getBillingEmail());
        updateStmt.setString(5, company.getGivenName());
        updateStmt.setString(6, company.getSurname());
        updateStmt.setString(7, company.getAddressLine1());
        updateStmt.setString(8, company.getAddressLine2());
        updateStmt.setString(9, company.getAddressArea1());
        updateStmt.setString(10, company.getAddressArea2());
        updateStmt.setString(11, company.getPostalCode());
        updateStmt.setString(12, company.getCountry().code());
        updateStmt.setString(13, company.getPhoneCode());
        updateStmt.setString(14, company.getPhoneNumber());
        updateStmt.setString(15, company.getAdditionalInfo());
        updateStmt.setString(16, company.getStatus().name());
        updateStmt.setString(17, company.getCreatedBy());
        updateStmt.setString(18, company.getId());
        updateStmt.executeUpdate();

        logger.info("Updated company '"+company.getId()+"' in COMPANIES");
    }

    /**
     * Adds or Updates the given company in the COMPANIES table.
     */
    public boolean upsert(Company company) throws SQLException
    {
        boolean ret = false;

        Company existing = getById(company.getId());
        if(existing != null)
        {
            update(company);
        }
        else
        {
            add(company);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the companies from the COMPANIES table.
     */
    public synchronized List<Company> list() throws SQLException
    {
        List<Company> ret = null;

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
            ret = new ArrayList<Company>();
            while(rs.next())
            {
                Company company = new Company();
                company.setId(rs.getString(1));
                company.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                company.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                company.setName(rs.getString(4));
                company.setBillingName(rs.getString(5));
                company.setBillingEmail(rs.getString(6));
                company.setGivenName(rs.getString(7));
                company.setSurname(rs.getString(8));
                company.setAddressLine1(rs.getString(9));
                company.setAddressLine2(rs.getString(10));
                company.setAddressArea1(rs.getString(11));
                company.setAddressArea2(rs.getString(12));
                company.setPostalCode(rs.getString(13));
                company.setCountry(rs.getString(14));
                company.setPhoneCode(rs.getString(15));
                company.setPhoneNumber(rs.getString(16));
                company.setAdditionalInfo(rs.getString(17));
                company.setStatus(rs.getString(18));
                company.setCreatedBy(rs.getString(19));
                ret.add(company);
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
     * Returns the count of companies from the COMPANIES table.
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
     * Removes the given company from the COMPANIES table.
     */
    public synchronized void delete(Company company) throws SQLException
    {
        if(!hasConnection() || company == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, company.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted company '"+company.getId()+"' in COMPANIES");
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
