/*
 * Copyright 2023 Gerald Curley
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

package com.opsmatters.media.model.monitor;

import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.monitor.post.RoundupPostMonitor;
import com.opsmatters.media.model.monitor.video.VideoMonitor;
import com.opsmatters.media.model.monitor.event.EventMonitor;
import com.opsmatters.media.model.monitor.publication.PublicationMonitor;

/**
 * Factory class to create an instance of a draft post depending on the type.
 *
 * @author Gerald Curley (opsmatters)
 */
public class ContentMonitorFactory
{
    private static final Logger logger = Logger.getLogger(ContentMonitorFactory.class.getName());

    /**
     * Private constructor.
     */
    private ContentMonitorFactory()
    {
    }

    /**
     * Returns a content monitor for the given type.
     */
    public static ContentMonitor newInstance(ContentType type)
    {
        switch(type)
        {
            case ROUNDUP:
                return new RoundupPostMonitor();
            case VIDEO:
                return new VideoMonitor();
            case EVENT:
                return new EventMonitor();
            case PUBLICATION:
                return new PublicationMonitor();
        }

        throw new IllegalArgumentException("Monitor type not found: "+type);
    }
}