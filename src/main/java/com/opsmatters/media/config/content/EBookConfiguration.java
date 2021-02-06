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

package com.opsmatters.media.config.content;

import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.EBookResource;

/**
 * Class that represents a YAML configuration for e-books.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EBookConfiguration extends PublicationConfiguration<EBookResource>
{
    /**
     * Default constructor.
     */
    public EBookConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public EBookConfiguration(EBookConfiguration obj)
    {
        super(obj != null ? obj.getName() : null);

        if(obj != null)
        {
            copyAttributes(obj);
            for(WebPageConfiguration page : obj.getPages())
                addPage(new WebPageConfiguration(page));
        }
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.EBOOK;
    }
}