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

package com.opsmatters.media.model.content.organisation;

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.content.ContentType;

/**
 * Represents the sets of organisation listing tabs.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum OrganisationTabs
{
    NONE(-1),
    ALL(0),
    N(1),
    V(2),
    NV(10),
    NE(20),
    NP(30),
    NVP(40),
    NVE(50),
    NPE(60);

    private int content;

    /**
     * Constructor that takes the tabs content.
     * @param content The content for the tabs
     */
    OrganisationTabs(int content)
    {
        this.content = content;
    }

    /**
     * Returns the content of the tabs.
     * @return The content of the tabs.
     */
    public int content()
    {
        return content;
    }

    /**
     * Returns the tabs for the given content.
     * @param content The tabs content
     * @return The tabs for the given content
     */
    public static OrganisationTabs fromContent(int content)
    {
        for(OrganisationTabs tabs : values())
        {
            if(tabs.content() == content)
                return tabs;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of tabs.
     * @param value The tabs value
     * @return <CODE>true</CODE> if the given value is contained in the list of tabs
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }

    /**
     * Returns <CODE>true</CODE> if the given content type is contained in the list of tabs.
     * @param type The content type
     * @return <CODE>true</CODE> if the given content type is contained in the list of tabs
     */
    public boolean contains(ContentType type)
    {
        boolean ret = false;

        if(this != NONE)
        {
            switch(type)
            {
                case VIDEO:
                    ret = this == ALL || name().indexOf("V") != -1;
                    break;
                case ROUNDUP:
                case POST:
                    ret = this == ALL || name().indexOf("N") != -1;
                    break;
                case EVENT:
                    ret = this == ALL || name().indexOf("E") != -1;
                    break;
                case PUBLICATION:
                    ret = this == ALL || name().indexOf("P") != -1;
                    break;
            }
        }

        return ret;
    }

    /**
     * Returns a list of the tabs.
     */
    public static List<OrganisationTabs> toList()
    {
        List<OrganisationTabs> ret = new ArrayList<OrganisationTabs>();

        ret.add(ALL);
        ret.add(N);
        ret.add(V);
        ret.add(NV);
        ret.add(NE);
        ret.add(NP);
        ret.add(NVP);
        ret.add(NVE);
        ret.add(NPE);
        ret.add(NONE);

        return ret;
    }
}