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

package com.opsmatters.media.model.content.crawler.field;

/**
 * Represents the output method for a HTML element.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ElementOutput
{
    HTML("html"),
    TEXT("text"),
    OWN_TEXT("ownText"); 

    private String value;

    /**
     * Constructor that takes the output value.
     * @param value The value for the output
     */
    ElementOutput(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the output.
     * @return The value of the output.
     */
    public String value()
    {
        return value;
    }

   /**
     * Returns the output for the given value.
     * @param value The output value
     * @return The output for the given value
     */
    public static ElementOutput fromValue(String value)
    {
        ElementOutput[] outputs = values();
        for(ElementOutput output : outputs)
        {
            if(output.value().equals(value))
                return output;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of outputs.
     * @param value The output value
     * @return <CODE>true</CODE> if the given value is contained in the list of outputs
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}