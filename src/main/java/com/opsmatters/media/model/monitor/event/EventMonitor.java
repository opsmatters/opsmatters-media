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

package com.opsmatters.media.model.monitor.event;

import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import com.opsmatters.media.cache.content.organisation.OrganisationConfigs;
import com.opsmatters.media.crawler.event.EventCrawler;
import com.opsmatters.media.model.content.event.EventConfig;
import com.opsmatters.media.model.content.event.EventDetails;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.monitor.ContentMonitor;
import com.opsmatters.media.model.monitor.ContentSnapshot;

/**
 * Class representing an event monitor.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventMonitor extends ContentMonitor<EventDetails>
{
    private static final Logger logger = Logger.getLogger(EventMonitor.class.getName());

    private static Comparator comparator = new Comparator<EventDetails>()
      {
          public int compare(EventDetails arg1, EventDetails arg2)
          {
              long l1 = arg2.getStartDateMillis();
              long l2 = arg1.getStartDateMillis();
              if(l1 == l2)
                  return 0;
              else if(l1 == 0L) // Sort blank dates to the top
                  return 1;
              else if(l2 == 0L)
                  return -1;
              else if (l1 > l2)
                  return 1;
              else
                  return -1;
          }
      };

    /**
     * Default constructor.
     */
    public EventMonitor()
    {
    }

    /**
     * Copy constructor.
     */
    public EventMonitor(EventMonitor obj)
    {
        copyAttributes(obj);
    }

    /**
     * Executes a check using this monitor.
     */
    @Override
    public ContentSnapshot check(int maxResults, boolean cache, boolean debug)
        throws IOException, IllegalStateException
    {
        ContentSnapshot ret = null;
        EventCrawler crawler = null;
        EventConfig config = OrganisationConfigs.get(getCode()).getEvents();

        try
        {
            CrawlerWebPage page = config.getPage(getName());
            if(page == null)
                throw new IllegalStateException("Event page '"+getName()
                    +"' does not exist for monitor "+getCode());
            crawler = new EventCrawler(config, page);
            crawler.setDebug(debug);
            crawler.setMaxResults(maxResults);
            int count = crawler.processTeasers(cache);

            List<EventDetails> teasers = crawler.getTeasers();
            if(EventDetails.hasStartDate(teasers))
                Collections.sort(teasers, comparator);
            ret = new ContentSnapshot(getContentType(), teasers);
            ret.setLog(crawler.getLog());

            if(crawler.getTitle() != null)
                setTitle(crawler.getTitle());
            setUrl(page.getTeasers().getUrl());
            setSites(page.getSites());
        }
        finally
        {
            if(crawler != null)
                crawler.close();
        }

        return ret;
    }
}