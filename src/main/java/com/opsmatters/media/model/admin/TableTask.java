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
package com.opsmatters.media.model.admin;

import java.time.Instant;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a database table task to be executed.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TableTask extends Task
{
    private String countQuery = "";
    private String updateQuery = "";

    /**
     * Default constructor.
     */
    public TableTask()
    {
        setType(TaskType.TABLE_UPDATE);
    }

    /**
     * Copy constructor.
     */
    public TableTask(TableTask obj)
    {
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a name.
     */
    public TableTask(String name)
    {
        this();
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
        setInterval(1);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(TableTask obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCountQuery(obj.getCountQuery());
            setUpdateQuery(obj.getUpdateQuery());
        }
    }

    /**
     * Returns the count query.
     */
    public String getCountQuery()
    {
        return countQuery;
    }

    /**
     * Sets the count query.
     */
    public void setCountQuery(String countQuery)
    {
        this.countQuery = countQuery;
    }

    /**
     * Returns <CODE>true</CODE> if the count query has been set.
     */
    public boolean hasCountQuery()
    {
        return countQuery != null && countQuery.length() > 0;
    }

    /**
     * Returns the update query.
     */
    public String getUpdateQuery()
    {
        return updateQuery;
    }

    /**
     * Sets the update query.
     */
    public void setUpdateQuery(String updateQuery)
    {
        this.updateQuery = updateQuery;
    }

    /**
     * Returns <CODE>true</CODE> if the update query has been set.
     */
    public boolean hasUpdateQuery()
    {
        return updateQuery != null && updateQuery.length() > 0;
    }
}