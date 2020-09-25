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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import com.opsmatters.media.model.content.PublicationSummary;
import com.opsmatters.media.model.content.PublicationDetails;
import com.opsmatters.media.config.content.WhitePaperConfiguration;
import com.opsmatters.media.config.content.WebPageConfiguration;
import com.opsmatters.media.config.content.ContentField;
import com.opsmatters.media.config.content.ContentFields;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a crawler for white papers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class WhitePaperCrawler extends WebPageCrawler<PublicationSummary>
{
    private static final Logger logger = Logger.getLogger(WhitePaperCrawler.class.getName());

    private WhitePaperConfiguration config;

    /**
     * Constructor that takes a web page configuration.
     */
    public WhitePaperCrawler(WhitePaperConfiguration config, WebPageConfiguration page)
    {
        super(page);
        this.config = config;
    }

    /**
     * Returns the white paper configuration of the crawler.
     */
    public WhitePaperConfiguration getConfig()
    {
        return config;
    }

    /**
     * Create the white paper summary from a selected node.
     */
    @Override
    public PublicationSummary getContentSummary(WebElement root, ContentFields fields)
        throws DateTimeParseException
    {
        PublicationSummary content = new PublicationSummary();
        if(fields.hasValidator())
            validateContent(content, fields.getValidator(), root, "teaser");
        if(content.isValid())
        {
            if(debug() && fields.hasValidator())
                logger.info("Validated white paper content: "+fields.getValidator());
            populateSummaryFields(root, fields, content, "teaser");
            if(fields.hasUrl())
            {
                ContentField field = fields.getUrl();
                String url = getAnchor(field, root, "teaser", field.removeParameters());
                if(url != null)
                    content.setUrl(url, field.removeParameters());
            }
        }

        return content;
    }

    /**
     * Create a white paper content item from the given url.
     */
    public PublicationDetails getWhitePaper(String url)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        return getWhitePaper(new PublicationSummary(url, removeParameters()));
    }

    /**
     * Populate the given white paper content.
     */
    public PublicationDetails getWhitePaper(PublicationSummary summary)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        ContentFields fields = getContentFields();
        PublicationDetails content = new PublicationDetails(summary);

        configureImplicitWait(getContentLoading());
        loadPage(content.getUrl(), getContentLoading());
        configureExplicitWait(getContentLoading());

        if(!fields.hasRoot())
            throw new IllegalArgumentException("Root empty for white paper content");

        // Trace to see the white paper page
        if(trace(getDriver()))
            logger.info("whitepaper-page="+getDriver().getPageSource());

        WebElement root = null;
        List<WebElement> elements = getDriver().findElements(By.cssSelector(fields.getRoot()));
        if(elements.size() > 0)
        {
            root = elements.get(0);
            if(debug())
                logger.info("Root found for white paper content: "+fields.getRoot());
            populateSummaryFields(root, fields, content, "content");
        }
        else
        {
            logger.severe("Root not found for white paper content: "+fields.getRoot());
            throw new IllegalArgumentException("Root not found for white paper content");
        }

        // Trace to see the whitepaper root node
        if(trace(root))
            logger.info("whitepaper-node="+root.getAttribute("innerHTML"));

        // Default the published date to today if not found
        if(!fields.hasPublishedDate() && content.getPublishedDate() == null)
        {
            content.setPublishedDate(TimeUtils.truncateTimeUTC());
            if(debug())
                logger.info("Defaulting published date: "+content.getPublishedDateAsString());
        }

        if(root != null && fields.hasBody())
        {
            String body = getBody(fields.getBody(), root, "content");
            if(body != null)
                content.setDescription(body);
        }

        return content;
    }

    /**
     * Populate the content fields from the given node.
     */
    private void populateSummaryFields(WebElement root, 
        ContentFields fields, PublicationSummary content, String type)
        throws DateTimeParseException
    {
        if(fields.hasTitle())
        {
            ContentField field = fields.getTitle();
            String title = getElements(field, root, type);
            if(title != null)
                content.setTitle(title);
        }

        if(fields.hasPublishedDate())
        {
            ContentField field = fields.getPublishedDate();
            String publishedDate = getElements(field, root, type);
            if(publishedDate != null)
            {
                try
                {
                    // Try each date pattern
                    DateTimeParseException ex = null;
                    for(String datePattern : field.getDatePatterns())
                    {
                        try
                        {
                            content.setPublishedDateAsString(publishedDate, datePattern);
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
                    logger.warning("Unparseable published date, using default instead: "+publishedDate);
                    content.setPublishedDate(TimeUtils.truncateTimeUTC());
                }
            }
        }
    }
}