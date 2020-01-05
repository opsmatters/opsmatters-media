/*
 * Copyright 2019 Gerald Curley
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
package com.opsmatters.media.model.content;

/**
 * Class representing a publication summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PublicationSummary extends ResourceSummary
{
    /**
     * Default constructor.
     */
    public PublicationSummary()
    {
    }

    /**
     * Constructor that takes a url.
     */
    public PublicationSummary(String url, boolean removeParameters)
    {
        setUrl(url, removeParameters);
    }

    /**
     * Copy constructor.
     */
    public PublicationSummary(PublicationSummary obj)
    {
        super(obj);
    }

    /**
     * Returns the publication url.
     */
    @Override
    public String getUniqueId()
    {
        return getUrl();
    }
}