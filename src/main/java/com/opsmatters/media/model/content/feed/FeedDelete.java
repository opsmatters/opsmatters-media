/*
 * Copyright 2026 Gerald Curley
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
package com.opsmatters.media.model.content.feed;

import com.opsmatters.media.model.BaseEntity;

/**
 * Class representing the result of deleting a set of nodes or files created by feeds.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedDelete extends BaseEntity
{
    private String response = "";
    private boolean valid = false;
    private int nodesFoundCount = -1;
    private int nodesDeletedCount = -1;
    private int filesFoundCount = -1;
    private int filesDeletedCount = -1;
    private String errorMessage = "";

    /**
     * Default constructor.
     */
    public FeedDelete()
    {
    }

    /**
     * Copy constructor.
     */
    public FeedDelete(FeedDelete obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FeedDelete obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setResponse(obj.getResponse());
            setValid(obj.isValid());
            setNodesFoundCount(obj.getNodesFoundCount());
            setNodesDeletedCount(obj.getNodesDeletedCount());
            setFilesFoundCount(obj.getFilesFoundCount());
            setFilesDeletedCount(obj.getFilesDeletedCount());
            setErrorMessage(obj.getErrorMessage());
        }
    }

    /**
     * Returns the response.
     */
    public String getResponse()
    {
        return response;
    }

    /**
     * Sets the response.
     */
    public void setResponse(String response)
    {
        this.response = response;
    }

    /**
     * Returns <CODE>true</CODE> if the response was valid.
     */
    public boolean isValid()
    {
        return valid;
    }

    /**
     * Set to <CODE>true</CODE> if the response was valid.
     */
    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    /**
     * Returns the nodes found item count.
     */
    public int getNodesFoundCount()
    {
        return nodesFoundCount;
    }

    /**
     * Sets the nodes found item count.
     */
    public void setNodesFoundCount(int nodesFoundCount)
    {
        this.nodesFoundCount = nodesFoundCount;
    }

    /**
     * Returns the nodes deleted item count.
     */
    public int getNodesDeletedCount()
    {
        return nodesDeletedCount;
    }

    /**
     * Sets the nodes deleted item count.
     */
    public void setNodesDeletedCount(int nodesDeletedCount)
    {
        this.nodesDeletedCount = nodesDeletedCount;
    }

    /**
     * Returns the files found item count.
     */
    public int getFilesFoundCount()
    {
        return filesFoundCount;
    }

    /**
     * Sets the files found item count.
     */
    public void setFilesFoundCount(int filesFoundCount)
    {
        this.filesFoundCount = filesFoundCount;
    }

    /**
     * Returns the files deleted item count.
     */
    public int getFilesDeletedCount()
    {
        return filesDeletedCount;
    }

    /**
     * Sets the files deleted item count.
     */
    public void setFilesDeletedCount(int filesDeletedCount)
    {
        this.filesDeletedCount = filesDeletedCount;
    }

    /**
     * Returns the delete error message.
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Sets the delete error message.
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns <CODE>true</CODE> if the delete error message has been set.
     */
    public boolean hasErrorMessage()
    {
        return errorMessage != null && errorMessage.length() > 0;
    }
}