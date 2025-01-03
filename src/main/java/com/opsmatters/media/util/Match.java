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

/**
 * Defines a result of a regex string match.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Match
{
    private int start = -1;
    private int end = -1;
    private String text;

    Match(int start, int end, String text)
    {
        this.start = start;
        this.end = end;
        this.text = text;
    }

    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
    }

    public String getText()
    {
        return text;
    }
}