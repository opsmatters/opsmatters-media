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
package com.opsmatters.media.model.content.crawler;

import java.time.Instant;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents an error page for a crawler.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ErrorPage extends BaseEntity
{
    private String name = "";
    private String title = "";
    private String notes = "";
    private ErrorPageType type = ErrorPageType.UNKNOWN;
    private ErrorPageStatus status = ErrorPageStatus.ACTIVE;

    /**
     * Default constructor.
     */
    public ErrorPage()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public ErrorPage(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public ErrorPage(ErrorPage obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ErrorPage obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setTitle(obj.getTitle());
            setNotes(obj.getNotes());
            setType(obj.getType());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the title of the error page.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the error page.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the error page.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the title of the error page.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title of the error page.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the type of the error page.
     */
    public ErrorPageType getType()
    {
        return type;
    }

    /**
     * Sets the type of the error page.
     */
    public void setType(ErrorPageType type)
    {
        this.type = type;
    }

    /**
     * Sets the type of the error page.
     */
    public void setType(String type)
    {
        setType(ErrorPageType.valueOf(type));
    }

    /**
     * Returns the notes for the error page.
     */
    public String getNotes()
    {
        return notes;
    }

    /**
     * Sets the notes for the error page.
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    /**
     * Returns the error page status.
     */
    public ErrorPageStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the error page status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == ErrorPageStatus.ACTIVE;
    }

    /**
     * Sets the error page status.
     */
    public void setStatus(ErrorPageStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the error page status.
     */
    public void setStatus(String status)
    {
        setStatus(ErrorPageStatus.valueOf(status));
    }
}