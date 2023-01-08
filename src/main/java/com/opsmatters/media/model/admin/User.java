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
package com.opsmatters.media.model.admin;

import java.time.Instant;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.AppTimeZone;

/**
 * Class representing a user.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class User extends BaseEntity
{
    private String username = "";
    private String email = "";
    private String firstName = "";
    private String lastName = "";
    private String role = "";
    private String timezone = AppTimeZone.DEFAULT; // UTC
    private boolean admin = false;
    private UserStatus status = UserStatus.DISABLED;
    private TraceObject traceObject = TraceObject.NONE;
    private boolean debug = false;

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
        setStatus(UserStatus.ACTIVE);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(User obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setUsername(obj.getUsername());
            setEmail(obj.getEmail());
            setFirstName(obj.getFirstName());
            setLastName(obj.getLastName());
            setRole(obj.getRole());
            setTimezone(obj.getTimezone());
            setAdmin(obj.isAdmin());
            setStatus(obj.getStatus());
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
     * Returns the user's first name.
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * Sets the user's first name.
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * Returns <CODE>true</CODE> if the user's first name has been set.
     */
    public boolean hasFirstName()
    {
        return firstName != null && firstName.length() > 0;
    }

    /**
     * Returns the user's last name.
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * Sets the user's last name.
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * Returns <CODE>true</CODE> if the user's last name has been set.
     */
    public boolean hasLastName()
    {
        return lastName != null && lastName.length() > 0;
    }

    /**
     * Returns the user's first and last name.
     */
    public String getName()
    {
        return String.format("%s %s", getFirstName(), getLastName());
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
     * Returns the timezone.
     */
    public String getTimezone()
    {
        return timezone;
    }

    /**
     * Sets the timezone.
     */
    public void setTimezone(String timezone)
    {
        this.timezone = timezone;
    }

    /**
     * Returns <CODE>true</CODE> if the timezone has been set.
     */
    public boolean hasTimezone()
    {
        return timezone != null && timezone.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the user is an administrator.
     */
    public boolean isAdmin()
    {
        return admin;
    }

    /**
     * Returns <CODE>true</CODE> if the user is an administrator.
     */
    public Boolean getAdminObject()
    {
        return Boolean.valueOf(admin);
    }

    /**
     * Set to <CODE>true</CODE> if the user is an administrator.
     */
    public void setAdmin(boolean admin)
    {
        this.admin = admin;
    }

    /**
     * Set to <CODE>true</CODE> if the user is an administrator.
     */
    public void setAdminObject(Boolean admin)
    {
        this.admin = admin != null && admin.booleanValue();
    }

    /**
     * Returns the user's status.
     */
    public UserStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the user is active.
     */
    public boolean isActive()
    {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Sets the user's status.
     */
    public void setStatus(UserStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the user's status.
     */
    public void setStatus(String status)
    {
        setStatus(UserStatus.valueOf(status));
    }

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public boolean debug()
    {
        return debug;
    }

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public Boolean getDebugObject()
    {
        return Boolean.valueOf(debug);
    }

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebugObject(Boolean debug)
    {
        this.debug = debug != null && debug.booleanValue();
    }

    /**
     * Returns the object type to be traced.
     */
    public TraceObject getTraceObject()
    {
        return traceObject;
    }

    /**
     * Set the object type to be traced.
     */
    public void setTraceObject(TraceObject traceObject)
    {
        this.traceObject = traceObject;
    }
}