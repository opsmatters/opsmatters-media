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

import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import com.opsmatters.media.util.Match;

/**
 * Class that parses a Bluesky post to create facets.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FacetParser
{
    private char[] chars;
    private int[] indices;
    private List<Facet> facets = new ArrayList<Facet>();

    public FacetParser(String str)
    {
        chars = str.toCharArray();
        indices = new int[chars.length+1];

        // Get the byte index for each char in the string
        int index = 0;
        for(int i = 0; i <= chars.length; i++)
        {
            if(i > 0)
            {
                int len = 1;
                int cp = (int)chars[i-1];
                if(cp > 255)
                {
                    if(cp > 0xD800) // emoji
                    {
                        len = 2;
                    }
                    else // unicode
                    {
                        len = new String(chars, i-1, 1).getBytes(StandardCharsets.UTF_8).length;
                    }
                }

                index += len;
            }

            indices[i] = index;
        }
    }

    public List<Facet> getFacets()
    {
        return facets;
    }

    private Facet create(FacetType type, Match match)
    {
        Facet ret = new Facet(type);
        ret.setStart(indices[match.getStart()]);
        ret.setEnd(indices[match.getEnd()]);
        ret.setText(match.getText());
        return ret;
    }

    private void addMatches(FacetType type, List<Match> matches)
    {
        for(Match match : matches)
            facets.add(create(type, match));
    }

    public void addLinks(List<Match> matches)
    {
        addMatches(FacetType.LINK, matches);
    }

    public void addHashtags(List<Match> matches)
    {
        addMatches(FacetType.HASHTAG, matches);
    }
}