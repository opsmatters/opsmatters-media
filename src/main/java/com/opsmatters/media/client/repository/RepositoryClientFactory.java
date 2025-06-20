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

package com.opsmatters.media.client.repository;

import java.io.IOException;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.RepositoryProviderId;
import com.opsmatters.media.model.content.project.ProjectConfig;

/**
 * Factory class to create a client for a repository provider.
 *
 * @author Gerald Curley (opsmatters)
 */
public class RepositoryClientFactory
{
    private static final Logger logger = Logger.getLogger(RepositoryClientFactory.class.getName());

    /**
     * Private constructor.
     */
    private RepositoryClientFactory()
    {
    }

    /**
     * Returns a client for the given provider.
     */
    public static RepositoryClient newClient(RepositoryProviderId providerId, ProjectConfig config)
        throws IOException
    {
        switch(providerId)
        {
            case GITHUB:
                return GitHubClient.newClient(config);
        }

        throw new IllegalArgumentException("Repository provider id not found: "+providerId);
    }
}