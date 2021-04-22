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
import java.time.Instant;
import com.opsmatters.media.db.dao.admin.NotificationDAO;
import com.opsmatters.media.model.admin.Notification;
import com.opsmatters.media.model.admin.NotificationType;
import com.opsmatters.media.model.admin.NotificationLevel;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.admin.NotificationLevel.*;
import static com.opsmatters.media.model.admin.NotificationStatus.*;

/**
 * Class representing a handler for the notifications.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class NotificationHandler
{
    private static final Logger logger = Logger.getLogger(NotificationHandler.class.getName());

    private static final int DEFAULT_EXPIRY = 1440;

    /**
     * Raturns the Notification DAO.
     */
    public abstract NotificationDAO getNotificationDAO();

    /**
     * Raise a notification.
     */
    private Notification notification(NotificationLevel level,
        NotificationType type, String code, String summary, int expiry)
    {
        Notification ret = null;

        try
        {
            boolean update = false;
            Notification notification = null;
            if(level != INFO)
            {
                List<Notification> notifications = getNotificationDAO().listPending(type, code);
                if(notifications.size() > 0)
                {
                    notification = notifications.get(0);
                    notification.setSummary(summary);
                    update = true;
                }
            }

            if(notification == null)
                notification = new Notification(type, code, summary);

            notification.setLevel(level);
            if(level != INFO)
                notification.setStatus(PENDING);
            else
                notification.setStatus(RESOLVED);
            notification.setExpiry(expiry);

            if(update)
                getNotificationDAO().update(notification);
            else
                getNotificationDAO().add(notification);
            ret = notification;
        }
        catch(SQLException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        return ret;
    }

    /**
     * Raise an error notification.
     */
    public Notification error(NotificationType type, String code, String summary, int expiry)
    {
        return notification(ERROR, type, code, summary, expiry);
    }

    /**
     * Raise an error notification.
     */
    public Notification error(NotificationType type, String code, String summary)
    {
        return error(type, code, summary, 0);
    }

    /**
     * Raise an error notification.
     */
    public Notification error(NotificationType type, String summary)
    {
        return error(type, null, summary);
    }

    /**
     * Raise an error notification.
     */
    public Notification error(NotificationType type, String summary, int expiry)
    {
        return error(type, null, summary, expiry);
    }

    /**
     * Raise a warning notification.
     */
    public Notification warn(NotificationType type, String code, String summary, int expiry)
    {
        return notification(WARN, type, code, summary, expiry);
    }

    /**
     * Raise a warning notification.
     */
    public Notification warn(NotificationType type, String code, String summary)
    {
        return warn(type, code, summary, 0);
    }

    /**
     * Raise a warning notification.
     */
    public Notification warn(NotificationType type, String summary)
    {
        return warn(type, null, summary);
    }

    /**
     * Raise a warning notification.
     */
    public Notification warn(NotificationType type, String summary, int expiry)
    {
        return warn(type, null, summary, expiry);
    }

    /**
     * Raise an info notification.
     */
    public Notification info(NotificationType type, String code, String summary, int expiry)
    {
        return notification(INFO, type, code, summary, expiry);
    }

    /**
     * Raise an info notification.
     */
    public Notification info(NotificationType type, String code, String summary)
    {
        return info(type, code, summary, DEFAULT_EXPIRY);
    }

    /**
     * Raise an info notification.
     */
    public Notification info(NotificationType type, String summary)
    {
        return info(type, null, summary);
    }

    /**
     * Raise an info notification.
     */
    public Notification info(NotificationType type, String summary, int expiry)
    {
        return info(type, null, summary, expiry);
    }

    /**
     * Update a notification.
     */
    public Notification update(NotificationType type, String code, String summary)
    {
        Notification ret = null;

        try
        {
            List<Notification> notifications = getNotificationDAO().listPending(type, code);
            for(Notification notification : notifications)
            {
                notification.setUpdatedDate(Instant.now());
                notification.setSummary(summary);
                getNotificationDAO().update(notification);
                ret = notification;
            }
        }
        catch(SQLException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        return ret;
    }

    /**
     * Update a notification.
     */
    public Notification update(NotificationType type, String summary)
    {
        return update(type, null, summary);
    }

    /**
     * Resolve a notification.
     */
    public Notification resolve(NotificationType type, String code, String summary)
    {
        Notification ret = null;

        try
        {
            List<Notification> notifications = getNotificationDAO().listPending(type, code);
            for(Notification notification : notifications)
            {
                notification.setUpdatedDate(Instant.now());
                notification.setLevel(SUCCESS);
                notification.setSummary(summary);
                notification.setStatus(RESOLVED);
                notification.setExpiry(DEFAULT_EXPIRY);
                getNotificationDAO().update(notification);
                ret = notification;
            }
        }
        catch(SQLException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        return ret;
    }

    /**
     * Resolve a notification.
     */
    public Notification resolve(NotificationType type, String summary)
    {
        return resolve(type, null, summary);
    }

    /**
     * Returns <CODE>true</CODE> if there is a PENDING notification for the given code and type.
     */
    public boolean hasPending(NotificationType type, String code)
    {
        boolean ret = false;

        try
        {
            ret = getNotificationDAO().listPending(type, code).size() > 0;
        }
        catch(SQLException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if there is a PENDING notification for the given type.
     */
    public boolean hasPending(NotificationType type)
    {
        return hasPending(type, null);
    }
}