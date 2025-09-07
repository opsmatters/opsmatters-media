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
package com.opsmatters.media.model.admin;

import java.time.Instant;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a shortcut.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Shortcut extends BaseEntity
{
    public static final String DEFAULT = "New Shortcut";

    private String name = "";
    private ShortcutType type;
    private String menu = "";
    private String siteId = "";
    private String selection = "";
    private String url = "";
    private String icon = "";
    private int position = 0;
    private ShortcutStatus status = ShortcutStatus.DISABLED;

    /**
     * Default constructor.
     */
    public Shortcut()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public Shortcut(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
        setType(ShortcutType.MENU);
        setStatus(ShortcutStatus.ACTIVE);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Shortcut obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setType(obj.getType());
            setMenu(obj.getMenu());
            setSiteId(obj.getSiteId());
            setSelection(obj.getSelection());
            setUrl(obj.getUrl());
            setIcon(obj.getIcon());
            setPosition(obj.getPosition());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns <CODE>true</CODE> if the shortcut can be displayed.
     */
    public boolean isValid()
    {
        boolean ret = hasIcon();

        if(ret)
        {
            if(getType() == ShortcutType.MENU)
            {
                if(!hasMenu())
                    ret = false;
            }
            else if(getType() == ShortcutType.LINK)
            {
                if(!hasUrl())
                    ret = false;
            }
        }

        return ret;
    }

    /**
     * Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if the name has been set.
     */
    public boolean hasName()
    {
        return name != null && name.length() > 0;
    }

    /**
     * Returns the type.
     */
    public ShortcutType getType()
    {
        return type;
    }

    /**
     * Sets the type.
     */
    public void setType(ShortcutType type)
    {
        this.type = type;
    }

    /**
     * Sets the type.
     */
    public void setType(String type)
    {
        setType(ShortcutType.valueOf(type));
    }

    /**
     * Returns the menu item.
     */
    public String getMenu()
    {
        return menu;
    }

    /**
     * Sets the menu item.
     */
    public void setMenu(String menu)
    {
        this.menu = menu;
    }

    /**
     * Returns <CODE>true</CODE> if the menu item has been set.
     */
    public boolean hasMenu()
    {
        return menu != null && menu.length() > 0;
    }

    /**
     * Returns the site id.
     */
    public String getSiteId()
    {
        return siteId;
    }

    /**
     * Sets the site id.
     */
    public void setSiteId(String siteId)
    {
        this.siteId = siteId;
    }

    /**
     * Returns <CODE>true</CODE> if the site id has been set.
     */
    public boolean hasSiteId()
    {
        return siteId != null && siteId.length() > 0;
    }

    /**
     * Returns the selection.
     */
    public String getSelection()
    {
        return selection;
    }

    /**
     * Sets the selection.
     */
    public void setSelection(String selection)
    {
        this.selection = selection;
    }

    /**
     * Returns <CODE>true</CODE> if the selection has been set.
     */
    public boolean hasSelection()
    {
        return selection != null && selection.length() > 0;
    }

    /**
     * Returns the url.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the url.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns <CODE>true</CODE> if the url has been set.
     */
    public boolean hasUrl()
    {
        return url != null && url.length() > 0;
    }

    /**
     * Returns the icon.
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Sets the icon.
     */
    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    /**
     * Returns <CODE>true</CODE> if the icon has been set.
     */
    public boolean hasIcon()
    {
        return icon != null && icon.length() > 0;
    }

    /**
     * Returns the position.
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Sets the position.
     */
    public void setPosition(int position)
    {
        this.position = position;
    }

    /**
     * Returns the shortcut's status.
     */
    public ShortcutStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the shortcut is active.
     */
    public boolean isActive()
    {
        return status == ShortcutStatus.ACTIVE;
    }

    /**
     * Sets the shortcut's status.
     */
    public void setStatus(ShortcutStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the shortcut's status.
     */
    public void setStatus(String status)
    {
        setStatus(ShortcutStatus.valueOf(status));
    }
}