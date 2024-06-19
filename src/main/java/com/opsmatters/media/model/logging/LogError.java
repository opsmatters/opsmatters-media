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

import java.time.Instant;
import org.json.JSONObject;
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.logging.LogErrorProperty.*;

/**
 * Defines a log error.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LogError extends LogEvent
{
    private ErrorCode code;
    private String source = "";
    private String organisation = "";
    private String location = "";
    private String exception = "";
    private String stacktrace = "";
    private ErrorStatus status;

    /**
     * Default constructor.
     */
    public LogError()
    {
    }

    /**
     * Constructor that takes a code, type and category.
     * @param code The code of the error
     * @param type The type of the error
     * @param category The category of the error
     * @param level The level of the error
     */
    public LogError(ErrorCode code, EventType type, EventCategory category, EventLevel level)
    {
        init();
        setCode(code);
        setType(type);
        setCategory(category);
        setLevel(level);
    }

    /**
     * Copy constructor.
     */
    public LogError(LogError obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(LogError obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setSource(obj.getSource());
            setOrganisation(obj.getOrganisation());
            setLocation(obj.getLocation());
            setException(obj.getException());
            setStacktrace(obj.getStacktrace());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Initialise the error.
     */
    @Override
    protected void init()
    {
        super.init();
        setLevel(EventLevel.ERROR);
        setStatus(ErrorStatus.NEW);
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(MESSAGE.value(), getMessage());
        ret.putOpt(LOCATION.value(), getLocation());
        ret.putOpt(EXCEPTION.value(), getException());
        ret.putOpt(STACKTRACE.value(), getStacktrace());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setMessage(obj.optString(MESSAGE.value()));
        setLocation(obj.optString(LOCATION.value()));
        setException(obj.optString(EXCEPTION.value()));
        setStacktrace(obj.optString(STACKTRACE.value()));
    }

    /**
     * Returns the code of the error.
     * @return The code of the error
     */
    public ErrorCode getCode()
    {
        return code;
    }

    /**
     * Sets the code of the error.
     * @param code The code of the error
     */
    public void setCode(String code)
    {
        setCode(ErrorCode.valueOf(code));
    }

    /**
     * Sets the code of the error.
     * @param code The code of the error
     */
    public void setCode(ErrorCode code)
    {
        this.code = code;
    }

    /**
     * Returns the source of the error.
     * @return The source of the error
     */
    public String getSource()
    {
        return source;
    }

    /**
     * Sets the source of the error.
     * @param source The source of the error
     */
    public void setSource(String source)
    {
        this.source = source;

        Organisation organisation = Organisations.get(source);
        setOrganisation(organisation != null ? organisation.getName() : "");
    }

    /**
     * Returns <CODE>true</CODE> if the source of the error has been set.
     * @return <CODE>true</CODE> if the source of the error has been set
     */
    public boolean hasSource()
    {
        return source != null && source.length() > 0;
    }

    /**
     * Returns the error organisation name.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the error organisation name.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns <CODE>true</CODE> if the error organisation name has been set.
     */
    public boolean hasOrganisation()
    {
        return organisation != null && organisation.length() > 0;
    }

    /**
     * Returns the location of the error.
     * @return The location of the error
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Sets the location of the error.
     * @param source The location of the error
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * Sets the location of the error.
     * @param source The location of the error
     */
    public void setLocation(Class clazz)
    {
        setLocation(clazz.getName());
    }

    /**
     * Returns the exception class of the error.
     * @return The exception class of the error
     */
    public String getException()
    {
        return exception;
    }

    /**
     * Sets the exception class of the error.
     * @param exception The exception class of the error
     */
    public void setException(String exception)
    {
        this.exception = exception;
    }

    /**
     * Sets the exception class and stacktrace of the error.
     * @param exception The exception of the error
     */
    public void setException(Throwable e)
    {
        if(!hasMessage() && e.getMessage() != null)
            setMessage(e.getMessage());
        setException(e.getClass().getName());
        setStacktrace(StringUtils.serialize(e));
    }

    /**
     * Returns the stacktrace of the error.
     * @return The stacktrace of the error
     */
    public String getStacktrace()
    {
        return stacktrace;
    }

    /**
     * Sets the stacktrace of the error.
     * @param stacktrace The stacktrace of the error
     */
    public void setStacktrace(String stacktrace)
    {
        this.stacktrace = stacktrace;
    }

    /**
     * Returns the status of the error.
     * @return The status of the error
     */
    public ErrorStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the status of the error.
     * @param status The status of the error
     */
    public void setStatus(String status)
    {
        setStatus(ErrorStatus.valueOf(status));
    }

    /**
     * Sets the status of the error.
     * @param status The status of the error
     */
    public void setStatus(ErrorStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the status of the error for the given user.
     * @param status The status of the error
     * @param username The name of the user that set the status
     */
    public void setStatus(ErrorStatus status, String username)
    {
        setStatus(status);
        setUpdatedDate(Instant.now());
        setCreatedBy(username);
    }

    /**
     * Returns a builder for the error.
     * @param code The code of the error
     * @param type The type of the error
     * @param category The category of the error
     * @param level The level of the error
     * @return The builder instance.
     */
    public static Builder builder(ErrorCode code, EventType type, EventCategory category, EventLevel level)
    {
        return new Builder(code, type, category, level);
    }

    /**
     * Builder to make error construction easier.
     */
    public static class Builder
    {
        private LogError error = null;

        /**
         * Constructor that takes a code, type, category and level.
         * @param code The code of the error
         * @param type The type of the error
         * @param category The category of the error
         * @param level The level of the error
         */
        public Builder(ErrorCode code, EventType type, EventCategory category, EventLevel level)
        {
            error = new LogError(code, type, category, level);
        }

        /**
         * Sets the message for the error.
         * @param message The message for the error
         * @return This object
         */
        public Builder message(String message)
        {
            error.setMessage(message);
            return this;
        }

        /**
         * Sets the exception for the error.
         * @param e The exception for the error
         * @return This object
         */
        public Builder exception(Throwable e)
        {
            error.setException(e);
            return this;
        }

        /**
         * Sets the location and source for the error.
         * @param obj The object with the error
         * @param source The code of the entity with the error
         * @return This object
         */
        public Builder locate(Object obj, String source)
        {
            error.setLocation(obj.getClass());
            error.setSource(source);
            return this;
        }

        /**
         * Returns the configured error instance
         * @return The error instance
         */
        public LogError build()
        {
            return error;
        }
    }
}