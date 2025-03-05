/*
 * Copyright 2022 Gerald Curley
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
package com.opsmatters.media.db.dao.content.util;

import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.db.dao.DAOFactory;

/**
 * The class for all content util data access object factories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentUtilDAOFactory extends DAOFactory
{
    /**
     * Constructor that takes a database driver and connection.
     */
    public ContentUtilDAOFactory(JDBCDatabaseDriver driver, JDBCDatabaseConnection conn)
    {
        super(driver, conn);

        getNoteDAO();
        getContentImageDAO();
        getTaxonomyTermDAO();
        getContentProxyDAO();
    }

    /**
     * Returns the note DAO.
     */
    public NoteDAO getNoteDAO()
    {
        if(noteDAO == null)
            noteDAO = new NoteDAO(this);
        return noteDAO;
    }

    /**
     * Returns the content image DAO.
     */
    public ContentImageDAO getContentImageDAO()
    {
        if(contentImageDAO == null)
            contentImageDAO = new ContentImageDAO(this);
        return contentImageDAO;
    }

    /**
     * Returns the taxonomy term DAO.
     */
    public TaxonomyTermDAO getTaxonomyTermDAO()
    {
        if(taxonomyTermDAO == null)
            taxonomyTermDAO = new TaxonomyTermDAO(this);
        return taxonomyTermDAO;
    }

    /**
     * Returns the content proxy DAO.
     */
    public ContentProxyDAO getContentProxyDAO()
    {
        if(contentProxyDAO == null)
            contentProxyDAO = new ContentProxyDAO(this);
        return contentProxyDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        noteDAO = null;
        contentImageDAO = null;
        taxonomyTermDAO = null;
        contentProxyDAO = null;
    }

    private NoteDAO noteDAO;
    private ContentImageDAO contentImageDAO;
    private TaxonomyTermDAO taxonomyTermDAO;
    private ContentProxyDAO contentProxyDAO;
}