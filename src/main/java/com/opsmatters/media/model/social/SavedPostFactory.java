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

package com.opsmatters.media.model.social;

import java.util.logging.Logger;

/**
 * Factory class to create an instance of a saved post depending on the type.
 *
 * @author Gerald Curley (opsmatters)
 */
public class SavedPostFactory
{
    private static final Logger logger = Logger.getLogger(SavedPostFactory.class.getName());

    /**
     * Private constructor.
     */
    private SavedPostFactory()
    {
    }

    /**
     * Returns a saved post for the given type.
     */
    public static SavedPost newInstance(PostType type)
    {
        switch(type)
        {
            case CONTENT:
                return new SavedContentPost();
            case STANDARD:
                return new SavedStandardPost();
        }

        throw new IllegalArgumentException("Post type not found: "+type);
    }
}