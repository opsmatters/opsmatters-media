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
import com.opsmatters.media.model.provider.RepositoryProviderId;
import com.opsmatters.media.model.content.project.ProjectDetails;

/**
 * Methods to interact with a repository provider.
 *
 * @author Gerald Curley (opsmatters)
 */
public interface RepositoryClient
{
    /**
     * Close the client.
     */
    public void close();

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public boolean debug();

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebug(boolean debug);

    /**
     * Returns the provider for this client.
     */
    public RepositoryProviderId getProviderId();

    /**
     * Returns the name of the current account.
     */
    public String getName() throws IOException;

    /**
     * Returns the project for the given repository URL.
     */
    public ProjectDetails getProject(String url) throws IOException;
}