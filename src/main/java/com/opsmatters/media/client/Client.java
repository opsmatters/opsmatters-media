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
package com.opsmatters.media.client;

/**
 * Base class for all clients.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Client
{
    private boolean debug = false;

    /**
     * Configure the client.
     */
    public abstract void configure() throws Exception;

    /**
     * Create the client using the configured credentials.
     */
    public abstract boolean create() throws Exception;

    /**
     * Close the client.
     */
    public abstract void close();

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public boolean debug()
    {
        return debug;
    }

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }
}