/*
 * Copyright 2023 Gerald Curley
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

/**
 * Represents a taxonomy type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum TaxonomyType
{
    TAGS("Tags", "tags", true),
    FEATURES("Features", "features", true),
    TECHNOLOGIES("Technologies", "technologies", true),
    PRICINGS("Pricings", "pricing_model", true),
    EVENT_TYPES("Event Types", "activity_type", true),
    VIDEO_TYPES("Video Types", "video_type", true),
    PUBLICATION_TYPES("Publication Types", "publication_type", true),
    ORGANISATIONS("Organisations", "organisation", false);

    private String value;
    private String vocabulary;
    private boolean editable;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     * @param vocabulary The vocabulary for the type
     * @param editable <CODE>true</CODE> if the type can be edited
     */
    TaxonomyType(String value, String vocabulary, boolean editable)
    {
        this.value = value;
        this.vocabulary = vocabulary;
        this.editable = editable;
    }

    /**
     * Returns the value of the type.
     * @return The value of the type.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the vocabulary of the type.
     * @return The vocabulary of the type.
     */
    public String vocabulary()
    {
        return vocabulary;
    }

    /**
     * Returns <CODE>true</CODE> if the type can be edited.
     * @return <CODE>true</CODE> if the type can be edited.
     */
    public boolean editable()
    {
        return editable;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static TaxonomyType fromValue(String value)
    {
        TaxonomyType[] types = values();
        for(TaxonomyType type : types)
        {
            if(type.value().equals(value))
                return type;
        }

        return null;
    }

    /**
     * Returns the type for the given vocabularyalue.
     * @param vocabulary The type vocabulary
     * @return The type for the given vocabulary
     */
    public static TaxonomyType fromVocabulary(String vocabulary)
    {
        TaxonomyType[] types = values();
        for(TaxonomyType type : types)
        {
            if(type.vocabulary().equals(vocabulary))
                return type;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}