/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.model;

import java.time.Instant;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents a message template.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class MessageTemplate extends BaseEntity
{
    public static final String DEFAULT = "New Template";

    private String code = "";
    private String name = "";
    private String message = "";
    private TemplateStatus status = TemplateStatus.NEW;

    /**
     * Default constructor.
     */
    public MessageTemplate()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public MessageTemplate(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public MessageTemplate(MessageTemplate obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(MessageTemplate obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setName(obj.getName());
            setMessage(obj.getMessage());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the template name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the code for the template.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the code for the template.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns <CODE>true</CODE> if the code for the template has been set.
     */
    public boolean hasCode()
    {
        return getCode() != null && getCode().length() > 0;
    }

    /**
     * Returns the name for the template.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name for the template.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the template message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the template message.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Returns <CODE>true</CODE> if the template message has been set.
     */
    public boolean hasMessage()
    {
        return message != null && message.length() > 0;
    }

    /**
     * Returns the template status.
     */
    public TemplateStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the template status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == TemplateStatus.ACTIVE;
    }

    /**
     * Sets the template status.
     */
    public void setStatus(TemplateStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the template status.
     */
    public void setStatus(String status)
    {
        setStatus(TemplateStatus.valueOf(status));
    }
}