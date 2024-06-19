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
package com.opsmatters.media.db.dao.logging;

import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.db.dao.DAOFactory;

/**
 * The class for all log data access object factories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LogDAOFactory extends DAOFactory
{
    /**
     * Constructor that takes a database driver and connection.
     */
    public LogDAOFactory(JDBCDatabaseDriver driver, JDBCDatabaseConnection conn)
    {
        super(driver, conn);

        getLogErrorDAO();
    }

    /**
     * Returns the log error DAO.
     */
    public LogErrorDAO getLogErrorDAO()
    {
        if(logErrorDAO == null)
            logErrorDAO = new LogErrorDAO(this);
        return logErrorDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        logErrorDAO = null;
    }

    private LogErrorDAO logErrorDAO;
}
