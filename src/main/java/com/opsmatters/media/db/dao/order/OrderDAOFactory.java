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
package com.opsmatters.media.db.dao.order;

import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.db.dao.DAOFactory;
import com.opsmatters.media.db.dao.order.product.ProductDAO;
import com.opsmatters.media.db.dao.order.contact.CompanyDAO;
import com.opsmatters.media.db.dao.order.contact.ContactDAO;
import com.opsmatters.media.db.dao.order.contact.ContactRateDAO;
import com.opsmatters.media.db.dao.order.contact.ContactPersonDAO;

/**
 * The class for all order management data access object factories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrderDAOFactory extends DAOFactory
{
    /**
     * Constructor that takes a database driver and connection.
     */
    public OrderDAOFactory(JDBCDatabaseDriver driver, JDBCDatabaseConnection conn)
    {
        super(driver, conn);

        getProductDAO();
        getCompanyDAO();
        getContactDAO();
        getContactRateDAO();
        getContactPersonDAO();
    }

    /**
     * Returns the product DAO.
     */
    public ProductDAO getProductDAO()
    {
        if(productDAO == null)
            productDAO = new ProductDAO(this);
        return productDAO;
    }

    /**
     * Returns the company DAO.
     */
    public CompanyDAO getCompanyDAO()
    {
        if(companyDAO == null)
            companyDAO = new CompanyDAO(this);
        return companyDAO;
    }

    /**
     * Returns the contact DAO.
     */
    public ContactDAO getContactDAO()
    {
        if(contactDAO == null)
            contactDAO = new ContactDAO(this);
        return contactDAO;
    }

    /**
     * Returns the contact rate DAO.
     */
    public ContactRateDAO getContactRateDAO()
    {
        if(contactRateDAO == null)
            contactRateDAO = new ContactRateDAO(this);
        return contactRateDAO;
    }

    /**
     * Returns the contact person DAO.
     */
    public ContactPersonDAO getContactPersonDAO()
    {
        if(contactPersonDAO == null)
            contactPersonDAO = new ContactPersonDAO(this);
        return contactPersonDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        productDAO = null;
        companyDAO = null;
        contactDAO = null;
        contactRateDAO = null;
        contactPersonDAO = null;
    }

    private ProductDAO productDAO;
    private CompanyDAO companyDAO;
    private ContactDAO contactDAO;
    private ContactRateDAO contactRateDAO;
    private ContactPersonDAO contactPersonDAO;
}