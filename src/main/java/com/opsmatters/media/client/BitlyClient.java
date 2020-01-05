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
import net.swisstech.bitly.model.Response;
import net.swisstech.bitly.model.v3.ShortenResponse;

/**
 * Class that represents a connection to bit.ly for shortening URLs.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BitlyClient
{
    private static final Logger logger = Logger.getLogger(BitlyClient.class.getName());

    public static final String AUTH = ".bitly";

    private static net.swisstech.bitly.BitlyClient client;
    private String accessToken = "";

    /**
     * Returns a new bitly client using an access token.
     */
    static public BitlyClient newClient() throws IOException
    {
        BitlyClient ret = new BitlyClient();

        // get the access token
        ret.configure();

        return ret;
    }

    /**
     * Configure the client.
     */
    public void configure() throws IOException
    {
        String directory = System.getProperty("om-config.auth", ".");

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

        // Authenticate using access token
        if(accessToken != null && accessToken.length() > 0)
            client = new net.swisstech.bitly.BitlyClient(accessToken);
    }

    /**
     * Shortens the given URL.
     */
    public String shortenUrl(String logUrl) throws IOException
    {
        String ret = null;

        Response<ShortenResponse> response = client.shorten()
            .setLongUrl(logUrl)
            .call();

        if(response.status_code == 200)
        {
            ret = response.data.url;
            logger.info("Shortened URL: " + response.data.url);
        }
        else
        {
            throw new IOException("Bitly Error "+response.status_code+": "+response.status_txt);
        }

        return ret;
    }
}