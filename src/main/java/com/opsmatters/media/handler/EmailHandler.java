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
package com.opsmatters.media.handler;

import java.util.List;
import java.util.logging.Logger;
import java.sql.SQLException;
import com.opsmatters.media.db.dao.admin.UserDAO;
import com.opsmatters.media.db.dao.admin.EmailDAO;
import com.opsmatters.media.model.DeliveryStatus;
import com.opsmatters.media.model.admin.User;
import com.opsmatters.media.model.admin.Email;
import com.opsmatters.media.model.admin.EmailType;

/**
 * Class representing a handler for emails.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class EmailHandler
{
    private static final Logger logger = Logger.getLogger(EmailHandler.class.getName());

    private String from;

    /**
     * Returns the Email DAO.
     */
    public abstract EmailDAO getEmailDAO();

    /**
     * Returns the User DAO.
     */
    public abstract UserDAO getUserDAO();

    /**
     * Returns the from address.
     */
    public String getFrom()
    {
        return from;
    }

    /**
     * Sets the from address.
     */
    public void setFrom(String from)
    {
        this.from = from;
    }

    /**
     * Enqueue an email for sending.
     */
    public void enqueue(Email email) throws SQLException
    {
        email.setStatus(DeliveryStatus.PENDING);
        getEmailDAO().add(email);
    }

    /**
     * Enqueue an alert email.
     */
    public void alert(Email email) throws SQLException
    {
        email.setType(EmailType.ALERT);
        create(email);
    }

    /**
     * Enqueue a resolution email.
     */
    public void resolved(Email email) throws SQLException
    {
        email.setType(EmailType.RESOLVED);
        create(email);
    }

    /**
     * Create and enqueue an email notification.
     */
    private void create(Email email) throws SQLException
    {
        email.setFrom(getFrom());

        // Set the subject prefix based on the type
        if(email.getType().prefix().length() > 0)
            email.setSubject(String.format("%s: %s", email.getType().prefix(), email.getSubject()));

        // Send to all admins
        List<User> users = getUserDAO().list();
        for(User user : users)
        {
            if(user.isActive() && user.isAdmin())
                email.addRecipient(user.getEmail());
        }

        enqueue(email);
    }
}