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

package com.opsmatters.media.client.social;

import java.util.logging.Logger;
import com.opsmatters.media.model.provider.SocialProviderId;
import com.opsmatters.media.model.social.SocialChannel;

/**
 * Factory class to create a client for a social media provider.
 *
 * @author Gerald Curley (opsmatters)
 */
public class SocialClientFactory
{
    private static final Logger logger = Logger.getLogger(SocialClientFactory.class.getName());

    /**
     * Private constructor.
     */
    private SocialClientFactory()
    {
    }

    /**
     * Returns a client for the given provider.
     */
    public static SocialClient newClient(SocialChannel channel) throws Exception
    {
        SocialProviderId providerId = channel.getProviderId();

        switch(providerId)
        {
            case TWITTER:
                return TwitterClient.newClient(channel);
            case FACEBOOK:
                return FacebookClient.newClient(channel);
            case LINKEDIN:
                return LinkedInClient.newClient(channel);
            case BLUESKY:
                return BlueskyClient.newClient(channel);
        }

        throw new IllegalArgumentException("Social provider id not found: "+providerId);
    }
}