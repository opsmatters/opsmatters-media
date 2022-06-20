/*
 * Copyright 2022 Gerald Curley
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
package com.opsmatters.media.config.platform;

import java.util.logging.Logger;
import com.opsmatters.media.model.platform.aws.S3Settings;
import com.opsmatters.media.model.platform.aws.SesSettings;

/**
 * Class representing the platform settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Platform
{
    private static final Logger logger = Logger.getLogger(Platform.class.getName());

    private static S3Settings s3;
    private static SesSettings ses;

    /**
     * Private constructor.
     */
    private Platform()
    {
    }

    /**
     * Returns the S3 settings.
     */
    public static S3Settings getS3Settings()
    {
        return s3;
    }

    /**
     * Returns the SES settings.
     */
    public static SesSettings getSesSettings()
    {
        return ses;
    }

    /**
     * Sets the S3 settings.
     */
    public static void set(S3Settings s3)
    {
        Platform.s3 = s3;
    }

    /**
     * Sets the SES settings.
     */
    public static void set(SesSettings ses)
    {
        Platform.ses = ses;
    }
}