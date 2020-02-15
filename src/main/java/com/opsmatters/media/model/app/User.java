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
package com.opsmatters.media.model.app;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a user.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class User implements java.io.Serializable
{
    private String id = "";
    private Instant createdDate;
    private Instant updatedDate;
    private String name = "";
    private String username = "";
    private String email = "";
    private String role = "";
    private boolean administrator = false;
    private boolean enabled = false;

    /**
     * Default constructor.
     */
    public User()
    {
    }

    /**
     * Constructor that takes a username.
     */
    public User(String username)
    {
        setId(StringUtils.getUUID(null));
        if(StringUtils.isValidEmailAddress(username, false))
            setEmail(username);
        else
            setUsername(username);
        setCreatedDate(Instant.now());
        setEnabled(true);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(User obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setName(obj.getName());
            setUsername(obj.getUsername());
            setEmail(obj.getEmail());
            setRole(obj.getRole());
            setCreatedDate(obj.getCreatedDate());
            setUpdatedDate(obj.getUpdatedDate());
            setAdministrator(obj.isAdministrator());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the username.
     */
    public String toString()
    {
        return getUsername();
    }

    /**
     * Returns the user id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the user id.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns <CODE>true</CODE> if the user id has been set.
     */
    public boolean hasId()
    {
        return id != null && id.length() > 0;
    }

    /**
     * Returns the date the user was created.
     */
    public Instant getCreatedDate()
    {
        return createdDate;
    }

    /**
     * Returns the date the user was created.
     */
    public long getCreatedDateMillis()
    {
        return getCreatedDate() != null ? getCreatedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the user was created.
     */
    public LocalDateTime getCreatedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getCreatedDate());
    }

    /**
     * Returns the date the user was created.
     */
    public String getCreatedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(createdDate, pattern);
    }

    /**
     * Returns the date the user was created.
     */
    public String getCreatedDateAsString()
    {
        return getCreatedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the user was created.
     */
    public void setCreatedDate(Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    /**
     * Sets the date the user was created.
     */
    public void setCreatedDateMillis(long millis)
    {
        if(millis > 0L)
            this.createdDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the user was created.
     */
    public void setCreatedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setCreatedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the user was created.
     */
    public void setCreatedDateAsString(String str) throws DateTimeParseException
    {
        setCreatedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the user was created.
     */
    public void setCreatedDateUTC(LocalDateTime createdDate)
    {
        if(createdDate != null)
            setCreatedDate(TimeUtils.toInstantUTC(createdDate));
    }

    /**
     * Returns the date the user was last updated.
     */
    public Instant getUpdatedDate()
    {
        return updatedDate;
    }

    /**
     * Returns the date the user was last updated.
     */
    public long getUpdatedDateMillis()
    {
        return getUpdatedDate() != null ? getUpdatedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the user was last updated.
     */
    public LocalDateTime getUpdatedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getUpdatedDate());
    }

    /**
     * Returns the date the user was last updated.
     */
    public String getUpdatedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(updatedDate, pattern);
    }

    /**
     * Returns the date the user was last updated.
     */
    public String getUpdatedDateAsString()
    {
        return getUpdatedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the user was last updated.
     */
    public void setUpdatedDate(Instant updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    /**
     * Sets the date the user was last updated.
     */
    public void setUpdatedDateMillis(long millis)
    {
        if(millis > 0L)
            this.updatedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the user was last updated.
     */
    public void setUpdatedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setUpdatedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the user was last updated.
     */
    public void setUpdatedDateAsString(String str) throws DateTimeParseException
    {
        setUpdatedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the user was last updated.
     */
    public void setUpdatedDateUTC(LocalDateTime updatedDate)
    {
        if(updatedDate != null)
            setUpdatedDate(TimeUtils.toInstantUTC(updatedDate));
    }

    /**
     * Returns the user's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the user's name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if the user's name has been set.
     */
    public boolean hasName()
    {
        return name != null && name.length() > 0;
    }

    /**
     * Returns the username.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Returns <CODE>true</CODE> if the username has been set.
     */
    public boolean hasUsername()
    {
        return username != null && username.length() > 0;
    }

    /**
     * Returns the email.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the email.
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Returns <CODE>true</CODE> if the email has been set.
     */
    public boolean hasEmail()
    {
        return email != null && email.length() > 0;
    }

    /**
     * Returns the role.
     */
    public String getRole()
    {
        return role;
    }

    /**
     * Sets the role.
     */
    public void setRole(String role)
    {
        this.role = role;
    }

    /**
     * Returns <CODE>true</CODE> if the role has been set.
     */
    public boolean hasRole()
    {
        return role != null && role.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the user is an administrator.
     */
    public boolean isAdministrator()
    {
        return administrator;
    }

    /**
     * Set to <CODE>true</CODE> if the user is an administrator.
     */
    public void setAdministrator(boolean administrator)
    {
        this.administrator = administrator;
    }

    /**
     * Returns <CODE>true</CODE> if the user is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Set to <CODE>true</CODE> if the user is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}