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
package com.opsmatters.media.db.dao.social;

import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.db.dao.DAOFactory;

/**
 * The class for all social media data access object factories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialDAOFactory extends DAOFactory
{
    /**
     * Constructor that takes a database driver and connection.
     */
    public SocialDAOFactory(JDBCDatabaseDriver driver, JDBCDatabaseConnection conn)
    {
        super(driver, conn);

        getPostTemplateDAO();
        getDraftPostDAO();
        getPreparedPostDAO();
    }

    /**
     * Returns the post template DAO.
     */
    public PostTemplateDAO getPostTemplateDAO()
    {
        if(postTemplateDAO == null)
            postTemplateDAO = new PostTemplateDAO(this);
        return postTemplateDAO;
    }

    /**
     * Returns the draft post DAO.
     */
    public DraftPostDAO getDraftPostDAO()
    {
        if(draftPostDAO == null)
            draftPostDAO = new DraftPostDAO(this);
        return draftPostDAO;
    }

    /**
     * Returns the prepared post DAO.
     */
    public PreparedPostDAO getPreparedPostDAO()
    {
        if(preparedPostDAO == null)
            preparedPostDAO = new PreparedPostDAO(this);
        return preparedPostDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        postTemplateDAO = null;
        draftPostDAO = null;
        preparedPostDAO = null;
    }

    private PostTemplateDAO postTemplateDAO;
    private DraftPostDAO draftPostDAO;
    private PreparedPostDAO preparedPostDAO;
}
