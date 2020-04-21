/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.db.dao.monitor;

import java.util.logging.Logger;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.DAOFactory;

/**
 * DAO that provides operations on a monitor table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class MonitorDAO<T> extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(MonitorDAO.class.getName());

    /**
     * Constructor that takes a DAO factory and a table name.
     */
    public MonitorDAO(DAOFactory factory, String tableName)
    {
        super(factory, tableName);
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
    }
}
