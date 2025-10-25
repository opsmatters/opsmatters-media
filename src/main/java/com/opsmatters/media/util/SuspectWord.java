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

package com.opsmatters.media.util;

import java.util.regex.Pattern;

/**
 * Defines a suspect word that needs to be checked in posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SuspectWord
{
    private String text;
    private String expr;
    private Pattern pattern;

    /**
     * Constructor that takes a word.
     */
    public SuspectWord(String text)
    {
        this.text = text;
        this.expr = String.format("\\b%s\\b", text.toLowerCase());
        this.pattern = Pattern.compile(this.expr, Pattern.DOTALL);
    }

    /**
     * Returns the word.
     */
    public String toString()
    {
        return getText();
    }

    /**
     * Returns the word.
     */
    public String getText()
    {
        return text;
    }

    /**
     * Returns the regular expression.
     */
    public String getExpr()
    {
        return expr;
    }

    /**
     * Returns the compiled pattern.
     */
    public Pattern getPattern()
    {
        return pattern;
    }
}