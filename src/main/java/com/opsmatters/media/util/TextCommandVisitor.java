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
package com.opsmatters.media.util;

import org.apache.commons.text.diff.CommandVisitor;

/**
 * Custom visitor for text comparison and generating HTML document highlighting the differences.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TextCommandVisitor implements CommandVisitor<Character>
{
    private static final String DELETION = "<span class=\"deletion\">${text}</span>";
    private static final String INSERTION = "<span class=\"insertion\">${text}</span>";

    private static final String BREAK = "<br/>";
    private static final String SPACE = "&nbsp;";
    private static final String AMPERSAND = "&amp;";
    private static final String QUOTE = "&quot;";
    private static final String APOSTROPHE = "&apos;";
    private static final String LESS_THAN = "&lt;";
    private static final String GREATER_THAN = "&gt;";

    private StringBuilder text = new StringBuilder();

    /**
     * Character is present in both strings, so no highlighting.
     */
    @Override
    public void visitKeepCommand(Character c)
    {
        text.append(escape(c));
    }

    /**
     * Character is present in right string but not left, so add green highlighting.
     */
    @Override
    public void visitInsertCommand(Character c)
    {
        text.append(INSERTION.replace("${text}", escape(c)));
    }

    /**
     * Character is present in left string but not right, so add red highlighting.
     */
    @Override
    public void visitDeleteCommand(Character c)
    {
        text.append(DELETION.replace("${text}", escape(c)));
    }

    /**
     * Replace special characters with their HTML equivalents.
     */
    private static String escape(char c)
    {
        String ret = null;
        if(c == '\n')
            ret = BREAK;
        else if(c == '&')
            ret = AMPERSAND;
        else if(c == '"')
            ret = QUOTE;
        else if(c == '\'')
            ret = APOSTROPHE;
        else if(c == '<')
            ret = LESS_THAN;
        else if(c == '>')
            ret = GREATER_THAN;
        else if(c == ' ')
            ret = SPACE;
        else
            ret = Character.toString(c);
        return ret;
    }

    /**
     * Append the given string.
     */
    public void append(String s)
    {
        text.append(s);
    }

    /**
     * Return the diff text in HTML format.
     */
    public String getText()
    {
        return text.toString();
    }
}