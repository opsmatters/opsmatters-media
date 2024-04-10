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

        getSocialChannelDAO();
        getSocialTemplateDAO();
        getSavedPostDAO();
        getDraftPostDAO();
        getChannelPostDAO();
        getHashtagDAO();
    }

    /**
     * Returns the social channel DAO.
     */
    public SocialChannelDAO getSocialChannelDAO()
    {
        if(socialChannelDAO == null)
            socialChannelDAO = new SocialChannelDAO(this);
        return socialChannelDAO;
    }

    /**
     * Returns the social template DAO.
     */
    public SocialTemplateDAO getSocialTemplateDAO()
    {
        if(socialTemplateDAO == null)
            socialTemplateDAO = new SocialTemplateDAO(this);
        return socialTemplateDAO;
    }

    /**
     * Returns the saved post DAO.
     */
    public SavedPostDAO getSavedPostDAO()
    {
        if(savedPostDAO == null)
            savedPostDAO = new SavedPostDAO(this);
        return savedPostDAO;
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
     * Returns the channel post DAO.
     */
    public ChannelPostDAO getChannelPostDAO()
    {
        if(channelPostDAO == null)
            channelPostDAO = new ChannelPostDAO(this);
        return channelPostDAO;
    }

    /**
     * Returns the hashtag DAO.
     */
    public HashtagDAO getHashtagDAO()
    {
        if(hashtagDAO == null)
            hashtagDAO = new HashtagDAO(this);
        return hashtagDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        socialChannelDAO = null;
        socialTemplateDAO = null;
        savedPostDAO = null;
        draftPostDAO = null;
        channelPostDAO = null;
        hashtagDAO = null;
    }

    private SocialChannelDAO socialChannelDAO;
    private SocialTemplateDAO socialTemplateDAO;
    private SavedPostDAO savedPostDAO;
    private DraftPostDAO draftPostDAO;
    private ChannelPostDAO channelPostDAO;
    private HashtagDAO hashtagDAO;
}
