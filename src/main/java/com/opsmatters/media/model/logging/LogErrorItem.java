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
package com.opsmatters.media.model.logging;

/**
 * Class representing a log error item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LogErrorItem extends LogEventItem<LogError>
{
    private LogError content = new LogError();

    /**
     * Default constructor.
     */
    public LogErrorItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public LogErrorItem(LogErrorItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a log error.
     */
    public LogErrorItem(LogError obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(LogErrorItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(LogError obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public LogError get()
    {
        return content;
    }

    /**
     * Returns the error code.
     */
    public ErrorCode getCode()
    {
        return content.getCode();
    }

    /**
     * Sets the error code.
     */
    public void setCode(String code)
    {
        content.setCode(code);
    }

    /**
     * Sets the error code.
     */
    public void setCode(ErrorCode code)
    {
        content.setCode(code);
    }

    /**
     * Returns the error entity code.
     */
    public String getEntityCode()
    {
        return content.getEntityCode();
    }

    /**
     * Sets the error entity code.
     */
    public void setEntityCode(String entityCode)
    {
        content.setEntityCode(entityCode);
    }

    /**
     * Returns the error entity name.
     */
    public String getEntityName()
    {
        return content.getEntityName();
    }

    /**
     * Sets the error entity name.
     */
    public void setEntityName(String entityName)
    {
        content.setEntityName(entityName);
    }

    /**
     * Returns the error organisation name.
     */
    public String getOrganisation()
    {
        return content.getOrganisation();
    }

    /**
     * Sets the error organisation name.
     */
    public void setOrganisation(String organisation)
    {
        content.setOrganisation(organisation);
    }

    /**
     * Returns the error status.
     */
    public ErrorStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the error status.
     */
    public void setStatus(String status)
    {
        content.setStatus(status);
    }

    /**
     * Sets the error status.
     */
    public void setStatus(ErrorStatus status)
    {
        content.setStatus(status);
    }
}