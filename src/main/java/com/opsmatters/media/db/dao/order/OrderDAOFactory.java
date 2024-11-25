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
import com.opsmatters.media.db.dao.order.contact.ContactDAO;
import com.opsmatters.media.db.dao.order.contact.ContactRateDAO;
import com.opsmatters.media.db.dao.order.product.ProductDAO;

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

        getContactDAO();
        getContactRateDAO();
        getProductDAO();
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
     * Returns the product DAO.
     */
    public ProductDAO getProductDAO()
    {
        if(productDAO == null)
            productDAO = new ProductDAO(this);
        return productDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        contactDAO = null;
        contactRateDAO = null;
        productDAO = null;
    }

    private ContactDAO contactDAO;
    private ContactRateDAO contactRateDAO;
    private ProductDAO productDAO;
}