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
package com.opsmatters.media.model.feed.atom;

/**
 * Class for categories of Atom feeds and entries.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AtomCategory implements java.io.Serializable
{
    private String term;
    private String scheme;
    private String label;

    AtomCategory()
    {
    }

    public String toString()
    {
        return term;
    }

    public String getTerm()
    {
        return term;
    }

    public void setTerm(String term)
    {
        this.term = term;
    }

    public String getScheme()
    {
        return scheme;
    }

    public void setScheme(String scheme)
    {
        this.scheme = scheme;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
}