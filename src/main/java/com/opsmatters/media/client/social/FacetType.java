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
 * Class that represents the types of facets used in Bluesky posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum FacetType
{
    MENTION("mention", "did"),
    HASHTAG("tag", "tag"),
    LINK("link", "uri");

    String value;
    String attr;

    FacetType(String value, String attr)
    {
        this.value = value;
        this.attr = attr;
    }

    public String value()
    {
        return value;
    }

    public String attr()
    {
        return attr;
    }
}