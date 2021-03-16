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
package com.opsmatters.media.client;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import com.opsmatters.bitly.Bitly;

/**
 * Class that represents a connection to bit.ly for shortening URLs.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BitlyClient extends Client
{
    private static final Logger logger = Logger.getLogger(BitlyClient.class.getName());

    public static final String AUTH = ".bitly";
    public static final String DEFAULT_DOMAIN = "bit.ly";

    private Bitly client;
    private String domain = "";
    private String accessToken = "";

    /**
     * Returns a new bitly client using an access token.
     */
    static public BitlyClient newClient(String domain) throws IOException
    {
        BitlyClient ret = new BitlyClient();
        ret.setDomain(domain);

        // Configure and create the bitly client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create bitly client");

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring bitly client");

        String directory = System.getProperty("app.auth", ".");

        File auth = new File(directory, AUTH);
        try
        {
            // Read access token from auth directory
            accessToken = FileUtils.readFileToString(auth, "UTF-8");
        }
        catch(IOException e)
        {
            logger.severe("Unable to read bitly access token: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured bitly client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() 
    {
        if(debug())
            logger.info("Creating bitly client");

        // Authenticate using access token
        if(accessToken != null && accessToken.length() > 0)
            client = new Bitly(accessToken);

        if(debug())
            logger.info("Created bitly client successfully");

        return true;
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
        client = null;
    }

    /**
     * Returns the custom domain.
     */
    public String getDomain()
    {
        return domain;
    }

    /**
     * Sets the custom domain.
     */
    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    /**
     * Shortens the given URL using the given short domain name.
     */
    public String shortenUrl(String longUrl) throws IOException
    {
        return client.bitlinks().shorten(longUrl, getDomain()).get().getLink();
    }
}