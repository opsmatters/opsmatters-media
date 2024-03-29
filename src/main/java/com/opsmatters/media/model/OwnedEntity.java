/*
 * Copyright 2020 Gerald Curley
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
 * Class representing an entity with an owner.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class OwnedEntity extends BaseEntity
{
    private String createdBy = "";

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(OwnedEntity obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCreatedBy(obj.getCreatedBy());
        }
    }

    /**
     * Returns the user that changed the entity.
     */
    public String getCreatedBy()
    {
        return createdBy;
    }

    /**
     * Sets the user that changed the entity.
     */
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }
}