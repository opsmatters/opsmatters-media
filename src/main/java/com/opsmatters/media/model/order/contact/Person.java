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
package com.opsmatters.media.model.order.contact;

import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.FormatUtils;

/**
 * Represents a person with a name.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Person extends BaseEntity
{
    private String name = "";
    private String salutation = "";

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Person obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setSalutation(obj.getSalutation());
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
     * Returns the salutation.
     */
    public String getSalutation()
    {
        return salutation;
    }

    /**
     * Sets the salutation.
     */
    public void setSalutation(String salutation)
    {
        this.salutation = salutation;
    }

    /**
     * Sets the default salutation.
     */
    public void setSalutation()
    {
        setSalutation(FormatUtils.getSalutation(getName()));
    }

    /**
     * Returns <CODE>true</CODE> if the salutation has been set.
     */
    public boolean hasSalutation()
    {
        return getSalutation() != null && getSalutation().length() > 0;
    }
}