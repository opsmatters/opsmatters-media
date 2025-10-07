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
import com.opsmatters.media.db.dao.order.product.ProductTextDAO;
import com.opsmatters.media.db.dao.order.contact.ContactDAO;
import com.opsmatters.media.db.dao.order.contact.ContactProfileDAO;
import com.opsmatters.media.db.dao.order.contact.ContactProductDAO;
import com.opsmatters.media.db.dao.order.contact.ContactPersonDAO;
import com.opsmatters.media.db.dao.order.contact.CompanyDAO;

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
        getProductTextDAO();
        getContactDAO();
        getContactProfileDAO();
        getContactProductDAO();
        getContactPersonDAO();
        getOrderDAO();
        getOrderItemDAO();
        getCompanyDAO();
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
     * Returns the product text DAO.
     */
    public ProductTextDAO getProductTextDAO()
    {
        if(productTextDAO == null)
            productTextDAO = new ProductTextDAO(this);
        return productTextDAO;
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
     * Returns the contact profile DAO.
     */
    public ContactProfileDAO getContactProfileDAO()
    {
        if(contactProfileDAO == null)
            contactProfileDAO = new ContactProfileDAO(this);
        return contactProfileDAO;
    }

    /**
     * Returns the contact product DAO.
     */
    public ContactProductDAO getContactProductDAO()
    {
        if(contactProductDAO == null)
            contactProductDAO = new ContactProductDAO(this);
        return contactProductDAO;
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
     * Returns the company DAO.
     */
    public CompanyDAO getCompanyDAO()
    {
        if(companyDAO == null)
            companyDAO = new CompanyDAO(this);
        return companyDAO;
    }

    /**
     * Returns the order DAO.
     */
    public OrderDAO getOrderDAO()
    {
        if(orderDAO == null)
            orderDAO = new OrderDAO(this);
        return orderDAO;
    }

    /**
     * Returns the order item DAO.
     */
    public OrderItemDAO getOrderItemDAO()
    {
        if(orderItemDAO == null)
            orderItemDAO = new OrderItemDAO(this);
        return orderItemDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        productDAO = null;
        productTextDAO = null;
        contactDAO = null;
        contactProfileDAO = null;
        contactProductDAO = null;
        contactPersonDAO = null;
        companyDAO = null;
        orderDAO = null;
        orderItemDAO = null;
    }

    private ProductDAO productDAO;
    private ProductTextDAO productTextDAO;
    private ContactDAO contactDAO;
    private ContactProfileDAO contactProfileDAO;
    private ContactProductDAO contactProductDAO;
    private ContactPersonDAO contactPersonDAO;
    private CompanyDAO companyDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
}