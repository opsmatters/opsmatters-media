/*
 * Copyright 2021 Gerald Curley
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
package com.opsmatters.media.model.drupal;

import com.opsmatters.media.model.BaseItem;

/**
 * Class representing a drupal taxonomy term.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TaxonomyTerm extends BaseItem
{
    // The vocabulary types
    public static final String TAGS = "tags";
    public static final String FEATURES = "features";
    public static final String TECHNOLOGIES = "technologies";
    public static final String PRICINGS = "pricing_model";
    public static final String EVENT_TYPES = "activity_type";
    public static final String VIDEO_TYPES = "video_type";
    public static final String PUBLICATION_TYPES = "publication_type";
    public static final String ORGANISATIONS = "organisation";

    private String name = "";
    private String type = "";
    private int tid = -1;
    private String description = "";

    /**
     * Default constructor.
     */
    public TaxonomyTerm()
    {
    }

    /**
     * Copy constructor.
     */
    public TaxonomyTerm(TaxonomyTerm obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(TaxonomyTerm obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setType(obj.getType());
            setTid(obj.getTid());
            setDescription(obj.getDescription());
        }
    }

    /**
     * Returns the term name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the term name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the term name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the term type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the term type.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns the term id.
     */
    public int getTid()
    {
        return tid;
    }

    /**
     * Sets the term id.
     */
    public void setTid(int tid)
    {
        this.tid = tid;
    }

    /**
     * Returns the term description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the term description.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
}