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

package com.opsmatters.media.client.system;

import java.io.IOException;
import java.util.logging.Logger;
import com.opsmatters.media.model.provider.EmailProviderId;
import com.opsmatters.media.model.system.aws.SesConfig;
import com.opsmatters.media.client.system.aws.AwsSesClient;

/**
 * Factory class to create a client for an email provider.
 *
 * @author Gerald Curley (opsmatters)
 */
public class EmailClientFactory
{
    private static final Logger logger = Logger.getLogger(EmailClientFactory.class.getName());

    /**
     * Private constructor.
     */
    private EmailClientFactory()
    {
    }

    /**
     * Returns a client for the given provider.
     */
    public static EmailClient newClient(EmailProviderId providerId, SesConfig config) throws IOException
    {
        switch(providerId)
        {
            case SES:
                return AwsSesClient.newClient(config);
        }

        throw new IllegalArgumentException("Email provider id not found: "+providerId);
    }
}