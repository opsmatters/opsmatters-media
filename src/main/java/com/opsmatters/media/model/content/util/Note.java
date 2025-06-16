/*
 * Copyright 2022 Gerald Curley
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
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a note.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Note extends BaseEntity
{
    private NoteType type;
    private String code = "";
    private String value = "";

    /**
     * Default constructor.
     */
    public Note()
    {
    }

    /**
     * Constructor that takes an organisation code and type.
     */
    public Note(String code, NoteType type)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(code);
        setType(type);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Note obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setType(obj.getType());
            setCode(obj.getCode());
            setValue(obj.getValue());
        }
    }

    /**
     * Returns the name.
     */
    public String toString()
    {
        return getType().value();
    }

    /**
     * Returns the organisation code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the organisation code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the type.
     */
    public NoteType getType()
    {
        return type;
    }

    /**
     * Sets the type.
     */
    public void setType(NoteType type)
    {
        this.type = type;
    }

    /**
     * Returns <CODE>true</CODE> if the type has been set.
     */
    public boolean hasType()
    {
        return type != null;
    }

    /**
     * Returns the value.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Returns <CODE>true</CODE> if the value has been set.
     */
    public boolean hasValue()
    {
        return value != null && value.length() > 0;
    }
}