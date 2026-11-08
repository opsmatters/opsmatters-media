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
package com.opsmatters.media.model.content.util;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a hyperlink.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Hyperlink extends BaseEntity
{
    private static final String HREF = "href";
    private static final String REL = "rel";
    private static final String TARGET = "target";

    private String anchor = "";
    private Map<String,String> attrs = new HashMap<String,String>();

    /**
     * Default constructor.
     */
    public Hyperlink()
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
    }

    /**
     * Constructor that takes an anchor.
     */
    public Hyperlink(String anchor)
    {
        this();
        setAnchor(anchor);
    }

    /**
     * Copy constructor.
     */
    public Hyperlink(Hyperlink obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Hyperlink obj)
    {
        if(obj != null)
        {
            setAnchor(obj.getAnchor());
            getAttrs().putAll(obj.getAttrs());
        }
    }

    /**
     * Returns the anchor.
     */
    public String toString()
    {
        return getAnchor();
    }

    /**
     * Returns the anchor.
     */
    public String getAnchor()
    {
        return anchor;
    }

    /**
     * Sets the anchor.
     */
    public void setAnchor(String anchor)
    {
        this.anchor = anchor;
    }

    /**
     * Returns <CODE>true</CODE> if the anchor has been set.
     */
    public boolean hasAnchor()
    {
        return getAnchor() != null && getAnchor().length() > 0;
    }

    /**
     * Returns the link attributes.
     */
    public Map<String,String> getAttrs()
    {
        return attrs;
    }

    /**
     * Returns the attribute with the given name.
     */
    public String getAttr(String name)
    {
        String value = attrs.get(name);
        return value != null ? value : "";
    }

    /**
     * Sets the attribute with the given name.
     */
    public void setAttr(String name, String value)
    {
        attrs.put(name, value);
    }

    /**
     * Returns the rel.
     */
    public String getRel()
    {
        return getAttr(REL);
    }

    /**
     * Returns <CODE>true</CODE> if the rel has been set.
     */
    public boolean hasRel()
    {
        return getRel() != null && getRel().length() > 0;
    }

    /**
     * Returns the target.
     */
    public String getTarget()
    {
        return getAttr(TARGET);
    }

    /**
     * Returns <CODE>true</CODE> if the target has been set.
     */
    public boolean hasTarget()
    {
        return getTarget() != null && getTarget().length() > 0;
    }

    /**
     * Returns the href.
     */
    public String getHref()
    {
        return getAttr(HREF);
    }

    /**
     * Returns <CODE>true</CODE> if the href has been set.
     */
    public boolean hasHref()
    {
        return getHref() != null && getHref().length() > 0;
    }
}