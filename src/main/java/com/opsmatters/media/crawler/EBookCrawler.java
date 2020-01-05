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
import java.util.logging.Logger;
import java.time.format.DateTimeParseException;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.opsmatters.media.model.content.ContentField;
import com.opsmatters.media.model.content.ContentFields;
import com.opsmatters.media.model.content.PublicationSummary;
import com.opsmatters.media.model.content.PublicationDetails;
import com.opsmatters.media.model.content.Fields;
import com.opsmatters.media.config.content.EBookConfiguration;
import com.opsmatters.media.config.content.WebPageConfiguration;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a crawler for ebooks.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EBookCrawler extends ContentCrawler<PublicationSummary>
{
    private static final Logger logger = Logger.getLogger(EBookCrawler.class.getName());

    private EBookConfiguration config;

    /**
     * Constructor that takes a web page configuration.
     */
    public EBookCrawler(EBookConfiguration config, WebPageConfiguration page)
    {
        super(page);
        this.config = config;
    }

    /**
     * Returns the ebook configuration of the crawler.
     */
    public EBookConfiguration getConfig()
    {
        return config;
    }

    /**
     * Create the ebook summary from a selected node.
     */
    @Override
    public PublicationSummary getContentSummary(DomNode root, ContentFields fields)
        throws DateTimeParseException
    {
        PublicationSummary content = new PublicationSummary();
        if(fields.hasValidator())
            validateContent(content, fields.getValidator(), root, "teaser");
        if(content.isValid())
        {
            if(debug() && fields.hasValidator())
                logger.info("Validated ebook content: "+fields.getValidator());
            populateSummaryFields(this.page, root, fields, content, "teaser");
            if(fields.hasUrl())
            {
                ContentField field = fields.getUrl();
                String url = getAnchor(field, root, page, "teaser", field.removeParameters());
                if(url != null)
                    content.setUrl(url, field.removeParameters());
            }
        }

        return content;
    }

    /**
     * Create an ebook content item from the given url.
     */
    public PublicationDetails getEBook(String url)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        return getEBook(new PublicationSummary(url, removeParameters()));
    }

    /**
     * Populate the given ebook content.
     */
    public PublicationDetails getEBook(PublicationSummary summary)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        ContentFields fields = getContentFields();
        PublicationDetails content = new PublicationDetails(summary);
        HtmlPage page = getPage(content.getUrl(), isContentJavaScriptEnabled());
        if(!fields.hasRoot())
            throw new IllegalArgumentException("Root empty for ebook content");

        // Wait for the javascript to load for the content
        loadPage(page, getContentLoading());

        // Trace to see the ebook page
        if(trace(page))
            logger.info("ebook-page="+page.asXml());

        DomNode root = page.querySelector(fields.getRoot());
        if(root != null)
        {
            if(debug())
                logger.info("Root found for ebook content: "+fields.getRoot());
            populateSummaryFields(page, root, fields, content, "content");
        }
        else
        {
            logger.severe("Root not found for ebook content: "+fields.getRoot());
            throw new IllegalArgumentException("Root not found for ebook content");
        }

        // Trace to see the ebook root node
        if(trace(root))
            logger.info("ebook-node="+root.asXml());

        // Default the published date to today if not found
        if(!fields.hasPublishedDate() && content.getPublishedDate() == null)
        {
            content.setPublishedDateAsString(TimeUtils.toMidnightStringUTC());
            if(debug())
                logger.info("Defaulting published date: "+content.getPublishedDateAsString());
        }

        if(root != null && fields.hasBody())
        {
            String body = getBody(fields.getBody(), root, page, "content");
            if(body != null)
                content.setDescription(body);
        }

        return content;
    }

    /**
     * Populate the content fields from the given node.
     */
    private void populateSummaryFields(HtmlPage page, DomNode root, 
        ContentFields fields, PublicationSummary content, String type)
        throws DateTimeParseException
    {
        if(fields.hasTitle())
        {
            ContentField field = fields.getTitle();
            String title = getElements(field, root, page, type, field.isMultiple(), field.getSeparator());
            if(title != null)
                content.setTitle(title);
        }

        if(fields.hasPublishedDate())
        {
            ContentField field = fields.getPublishedDate();
            String publishedDate = getElement(field, root, page, type);
            if(publishedDate != null)
            {
                try
                {
                    try
                    {
                        // Try the 1st date pattern
                        content.setPublishedDateAsString(publishedDate, field.getDatePattern());
                    }
                    catch(DateTimeParseException e)
                    {
                        // If the 1st date pattern fails, try the 2nd format
                        if(field.hasDatePattern2())
                            content.setPublishedDateAsString(publishedDate, field.getDatePattern2());
                        else
                            throw e;
                    }
                }
                catch(DateTimeParseException e)
                {
                    logger.severe(StringUtils.serialize(e));
                    logger.warning("Unparseable published date, using default instead: "+publishedDate);
                    content.setPublishedDateAsString(TimeUtils.toMidnightStringUTC());
                }
            }
        }
    }
}