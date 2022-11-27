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
package com.opsmatters.media.crawler;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.time.format.DateTimeParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.opsmatters.media.model.content.event.EventSummary;
import com.opsmatters.media.model.content.event.EventDetails;
import com.opsmatters.media.model.content.event.EventConfig;
import com.opsmatters.media.model.content.crawler.ContentLoading;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing a crawler for events.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventCrawler extends WebPageCrawler<EventSummary>
{
    private static final Logger logger = Logger.getLogger(EventCrawler.class.getName());

    private EventConfig config;

    /**
     * Constructor that takes a web page configuration.
     */
    public EventCrawler(EventConfig config,CrawlerWebPage page)
    {
        super(page);
        this.config = config;
    }

    /**
     * Returns the event configuration of the crawler.
     */
    public EventConfig getConfig()
    {
        return config;
    }

    /**
     * Create an event teaser from the selected node.
     */
    @Override
    protected EventSummary getTeaser(Element root, Fields fields)
        throws DateTimeParseException
    {
        EventSummary content = new EventSummary();
        if(fields.hasValidator())
            validateContent(content, fields.getValidator(), root, "teaser");
        if(content.isValid())
        {
            if(debug() && fields.hasValidator())
                logger.info("Validated event content: "+fields.getValidator());
            populateSummaryFields(root, fields, content, "teaser");
            if(fields.hasUrl())
            {
                Field field = fields.getUrl();
                String url = getAnchor(field, root, "teaser", field.removeParameters());
                if(url != null)
                    content.setUrl(url, field.removeParameters());
            }
        }

        return content;
    }

    /**
     * Create an event content item from the given url.
     */
    @Override
    public EventDetails getContent(String url)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        return getContent(new EventSummary(url, removeParameters()));
    }

    /**
     * Populate the given event content.
     */
    @Override
    public EventDetails getContent(EventSummary summary)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        EventDetails content = new EventDetails(summary);
        List<Fields> articles = getPage().getArticles().getFields(hasRootError());

        loadArticlePage(content.getUrl());

        // Trace to see the page
        if(trace(getDriver()))
            logger.info("content-page="+getPageSource());

        Element root = null;
        Document doc = Jsoup.parse(getPageSource("body"));
        doc.outputSettings().prettyPrint(false);

        for(Fields fields : articles)
        {
            if(!fields.hasRoot())
                throw new IllegalArgumentException("Root empty for event content");

            Elements elements = doc.select(fields.getRoot());
            if(elements.size() > 0)
            {
                root = elements.get(0);
                if(debug())
                    logger.info("Root found for event content: "+fields.getRoot());
                populateSummaryFields(root, fields, content, "content");
            }
            else
            {
                logger.warning("Root not found for event content: "+fields.getRoot());
                continue;
            }

            // Trace to see the content root node
            if(trace(root))
                logger.info("event-node="+root.html());

            content.setPublishedDate(TimeUtils.truncateTimeUTC());

            boolean hasTime = false;
            if(content.getStartDate() != null)
                hasTime = TimeUtils.hasTime(content.getStartDateMillis());

            long starttm = 0L;

            if(fields.hasStartTime())
            {
                Field field = fields.getStartTime();
                String start = getElements(field, root, "content");
                if(start != null)
                {
                    try
                    {
                        // Try each date pattern
                        DateTimeParseException ex = null;
                        for(String datePattern : field.getDatePatterns())
                        {
                            try
                            {
                                starttm = TimeUtils.toMillisTime(start, datePattern);
                                ex = null;
                                break;
                            }
                            catch(DateTimeParseException e)
                            {
                                ex = e;
                            }
                        }

                        if(ex != null)
                            throw ex;

                        if(debug())
                            logger.info("Found start time: "+starttm);
                    }
                    catch(DateTimeParseException e)
                    {
                        logger.severe(StringUtils.serialize(e));
                        logger.warning("Unparseable start time: "+start);
                    }
                }
            }

            // If no start time was found, use the default
            if(!hasTime && starttm == 0L && config.getFields().containsKey(START_TIME))
            {
                String start = config.getFields().get(START_TIME);
                starttm = TimeUtils.toMillisTime(start, Formats.SHORT_TIME_FORMAT);
                if(debug())
                    logger.info("Found default start time: "+starttm);
            }

            if(fields.hasTimeZone())
            {
                String timezone = getElements(fields.getTimeZone(), root, "content");
                if(timezone != null)
                    content.setTimeZone(timezone);
            }

            if(root != null && fields.hasBody())
            {
                String body = getBody(fields.getBody(), root, "content", debug());
                if(body != null)
                    content.setDescription(body);
            }

            // Add the start time if it is a separate field
            if(starttm > 0L && content.getStartDateMillis() > 0L)
            {
                content.setStartDateMillis(content.getStartDateMillis()+starttm);
                if(debug())
                    logger.info("Added start time: "+content.getStartDateAsString());
            }

            if(root != null)
                break;
        }

        if(root == null)
            throw new IllegalArgumentException("Root not found for event content");

        return content;
    }

    /**
     * Populate the content fields from the given node.
     */
    private void populateSummaryFields(Element root, 
        Fields fields, EventSummary content, String type)
        throws DateTimeParseException
    {
        if(fields.hasTitle())
        {
            Field field = fields.getTitle();
            String title = getElements(field, root, type);
            if(title != null && title.length() > 0)
            {
                // Event title should always start with the organisation name
                if(!title.startsWith(config.getName()))
                {
                    if(debug())
                        logger.info("Adding organisation to event title: "+config.getName());
                    content.setTitle(String.format("%s: %s", config.getName(), title));
                    if(debug())
                        logger.info("Added organisation to event title: "+content.getTitle());
                }
                else
                {
                    content.setTitle(title);
                }
            }
        }

        if(fields.hasStartDate())
        {
            Field field = fields.getStartDate();
            String startDate = getElements(field, root, type);
            if(startDate != null)
            {
                // Remove whitespace from date
                startDate = startDate.trim().replaceAll("\\s+"," ");

                try
                {
                    // Try each date pattern
                    DateTimeParseException ex = null;
                    for(String datePattern : field.getDatePatterns())
                    {
                        try
                        {
                            content.setStartDateAsString(startDate, datePattern);
                            ex = null;
                            break;
                        }
                        catch(DateTimeParseException e)
                        {
                            ex = e;
                        }
                    }

                    if(ex != null)
                        throw ex;
                }
                catch(DateTimeParseException e)
                {
                    logger.severe(StringUtils.serialize(e));
                    logger.warning("Unparseable start date: "+startDate+" code="+config.getCode());
                }
            }
        }
    }
}