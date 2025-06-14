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

/**
 * Class representing an entity managed by the system.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ManagedEntity extends BaseEntity
{
    private String id = "";

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ManagedEntity obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setId(obj.getId());
//            setCreatedDate(obj.getCreatedDate());
//            setUpdatedDate(obj.getUpdatedDate());
        }
    }

    /**
     * Returns the entity id.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the entity id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the entity id.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns <CODE>true</CODE> if the entity id has been set.
     */
    public boolean hasId()
    {
        return id != null && id.length() > 0;
    }
}