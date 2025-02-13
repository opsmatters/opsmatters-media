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
package com.opsmatters.media.crawler.event;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.time.format.DateTimeParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.opsmatters.media.crawler.WebPageCrawler;
import com.opsmatters.media.cache.content.Teasers;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.event.EventDetails;
import com.opsmatters.media.model.content.event.EventConfig;
import com.opsmatters.media.model.content.crawler.ContentLoading;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;
import com.opsmatters.media.model.logging.LogEventCategory;
import com.opsmatters.media.model.logging.LogError;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

import static com.opsmatters.media.model.content.FieldName.*;
import static com.opsmatters.media.model.logging.LogEventCategory.*;
import static com.opsmatters.media.model.logging.ErrorCode.*;

/**
 * Class representing a crawler for events.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventCrawler extends WebPageCrawler<EventDetails>
{
    private static final Logger logger = Logger.getLogger(EventCrawler.class.getName());

    private EventConfig config;

    /**
     * Constructor that takes a web page configuration.
     */
    public EventCrawler(EventConfig config, CrawlerWebPage page)
    {
        super(config, page);
        this.config = config;
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.EVENT;
    }

    /**
     * Returns the event configuration of the crawler.
     */
    public EventConfig getConfig()
    {
        return config;
    }

    /**
     * Create an event teaser from the given element.
     */
    @Override
    protected EventDetails getTeaser(Element root, Fields fields)
        throws DateTimeParseException
    {
        EventDetails teaser = new EventDetails();
        if(fields.hasValidator())
            validateContent(teaser, fields.getValidator(), root, TEASER);
        if(teaser.isValid())
        {
            if(debug() && fields.hasValidator())
                logger.info("Validated event teaser: "+fields.getValidator());
            populateTeaserFields(root, fields, teaser, TEASER);
            if(fields.hasUrl())
            {
                Field field = fields.getUrl();
                String url = getAnchor(field, root, TEASER, field.removeParameters());
                if(url != null)
                    teaser.setUrl(url, field.removeParameters());
            }
        }

        return teaser;
    }

    /**
     * Create event details from the given url.
     */
    public EventDetails getDetails(String url)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        return getDetails(new EventDetails(url, getArticles().removeParameters()));
    }

    /**
     * Returns the processed event derived from the given teaser.
     */
    @Override
    public EventDetails getDetails(EventDetails teaser)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        EventDetails content = new EventDetails(teaser);
        List<Fields> articles = getPage().getArticles().getFields(getErrorCode() == E_MISSING_ROOT);

        loadArticlePage(content.getUrl());

        // Trace to see the page
        if(trace(getDriver()))
            logger.info("article-page="+getPageSource(ARTICLE));

        if(getErrorCode() != E_ERROR_PAGE)
        {
            Element root = null;
            Document doc = Jsoup.parse(getPageSource("body", ARTICLE));
            doc.outputSettings().prettyPrint(false);

            for(Fields fields : articles)
            {
                if(!fields.hasRoot())
                {
                    setErrorCode(E_EMPTY_ROOT);
                    throw new IllegalArgumentException("Root empty for event article");
                }

                Elements elements = doc.select(fields.getRoot());
                if(elements.size() > 0)
                {
                    root = elements.get(0);
                    if(debug())
                        logger.info("Root found for event article: "+fields.getRoot());
                    populateTeaserFields(root, fields, content, ARTICLE);
                }
                else
                {
                    if(debug())
                        logger.info("Root not found for event article: "+fields.getRoot());
                    log.info(ARTICLE, "Root not found for event article: "+fields.getRoot());
                    continue;
                }

                // Trace to see the article root node
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
                    String start = getElements(field, root, ARTICLE);
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

                            log.add(log.warn(E_PARSE_DATE, ARTICLE)
                                .message(String.format("Unparseable %s start time: %s",
                                    ARTICLE.tag(), start))
                                .location(this)
                                .entity(config, getPage())
                                .exception(e));
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
                    String timezone = getElements(fields.getTimeZone(), root, ARTICLE);
                    if(timezone != null)
                        content.setTimeZone(timezone);
                }

                if(root != null && fields.hasBody())
                {
                    String body = getBody(fields.getBody(), root, ARTICLE, debug());
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
            {
                setErrorCode(E_MISSING_ROOT);
                throw new IllegalArgumentException("Root not found for event article");
            }

            Teasers.update(getPage().getTeasers().getUrls(), content);
        }

        return content;
    }

    /**
     * Populate the teaser fields from the given element.
     */
    private void populateTeaserFields(Element root, Fields fields, EventDetails teaser, LogEventCategory category)
        throws DateTimeParseException
    {
        if(fields.hasTitle())
        {
            Field field = fields.getTitle();
            String title = getElements(field, root, category);
            if(title != null && title.length() > 0)
            {
                teaser.setTitle(title);
            }
        }

        if(fields.hasStartDate())
        {
            Field field = fields.getStartDate();
            String startDate = getElements(field, root, category);
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
                            teaser.setStartDateAsString(startDate, datePattern);
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
                    if(!field.isOptional())
                    {
                        logger.severe(StringUtils.serialize(e));
                        logger.severe(String.format("Unparseable %s start date: %s code=%s",
                            category.tag(), startDate, config.getCode()));

                        log.add(log.warn(E_PARSE_DATE, category)
                            .message(String.format("Unparseable %s start date: %s",
                                category.tag(), startDate))
                            .location(this)
                            .entity(config, getPage())
                            .exception(e));
                    }
                }
            }
        }
    }
}