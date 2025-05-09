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
package com.opsmatters.media.client.social;

/**
 * Class that represents a facet for a Bluesky post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Facet
{
    private FacetType type;
    private int start = -1;
    private int end = -1;
    private String text = null;

    public Facet(FacetType type)
    {
        this.type = type;
    }

    public FacetType getType()
    {
        return type;
    }

    public int getStart()
    {
        return start;
    }

    public void setStart(int start)
    {
        this.start = start;
    }

    public int getEnd()
    {
        return end;
    }

    public void setEnd(int end)
    {
        this.end = end;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        if(type == FacetType.MENTION || type == FacetType.HASHTAG)
            text = text.substring(1); // Strip # or @
        this.text = text;
    }
}