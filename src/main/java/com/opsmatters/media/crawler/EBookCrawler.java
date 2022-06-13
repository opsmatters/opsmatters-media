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
import com.opsmatters.media.model.content.PublicationSummary;
import com.opsmatters.media.model.content.PublicationDetails;
import com.opsmatters.media.config.content.EBookConfiguration;
import com.opsmatters.media.config.content.WebPageConfiguration;
import com.opsmatters.media.config.content.ContentField;
import com.opsmatters.media.config.content.ContentFields;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a crawler for ebooks.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EBookCrawler extends WebPageCrawler<PublicationSummary>
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
     * Create the ebook summary from the selected node.
     */
    @Override
    public PublicationSummary getContentSummary(Element root, ContentFields fields)
        throws DateTimeParseException
    {
        PublicationSummary content = new PublicationSummary();
        if(fields.hasValidator())
            validateContent(content, fields.getValidator(), root, "teaser");
        if(content.isValid())
        {
            if(debug() && fields.hasValidator())
                logger.info("Validated ebook content: "+fields.getValidator());
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
        PublicationDetails content = new PublicationDetails(summary);
        List<ContentFields> articles = getArticleFields();

        configureImplicitWait(getArticleLoading());
        loadPage(content.getUrl(), getArticleLoading());
        configureExplicitWait(getArticleLoading());

        // Scroll the page if configured
        configureMovement(getArticleLoading());

        // Wait for the page to load
        configureSleep(getArticleLoading());

        // Trace to see the page
        if(trace(getDriver()))
            logger.info("content-page="+getPageSource());

        Element root = null;
        Document doc = Jsoup.parse(getPageSource("body"));
        doc.outputSettings().prettyPrint(false);

        for(ContentFields fields : articles)
        {
            if(!fields.hasRoot())
                throw new IllegalArgumentException("Root empty for ebook content");

            Elements elements = doc.select(fields.getRoot());
            if(elements.size() > 0)
            {
                root = elements.get(0);
                if(debug())
                    logger.info("Root found for ebook content: "+fields.getRoot());
                populateSummaryFields(root, fields, content, "content");
            }
            else
            {
                logger.warning("Root not found for ebook content: "+fields.getRoot());
                continue;
            }

            // Trace to see the content root node
            if(trace(root))
                logger.info("ebook-node="+root.html());

            // Default the published date to today if not found
            if(!fields.hasPublishedDate() && content.getPublishedDate() == null)
            {
                content.setPublishedDate(TimeUtils.truncateTimeUTC());
                if(debug())
                    logger.info("Defaulting published date: "+content.getPublishedDateAsString());
            }

            if(root != null && fields.hasBody())
            {
                String body = getBody(fields.getBody(), root, "content", debug());
                if(body != null)
                    content.setDescription(body);
            }

            if(root != null)
                break;
        }

        if(root == null)
            throw new IllegalArgumentException("Root not found for ebook content");

        return content;
    }

    /**
     * Populate the content fields from the given node.
     */
    private void populateSummaryFields(Element root, 
        ContentFields fields, PublicationSummary content, String type)
        throws DateTimeParseException
    {
        if(fields.hasTitle())
        {
            ContentField field = fields.getTitle();
            String title = getElements(field, root, type);
            if(title != null && title.length() > 0)
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
                    logger.warning("Unparseable published date: "+publishedDate);
                }
            }
        }

        if(fields.hasImage())
        {
            ContentField field = fields.getImage();
            String src = getImageSrc(field, root, type);
            if(src != null && src.length() > 0)
            {
                content.setImageFromPath(getImagePrefix(), src);
                content.setImageSource(getBasePath(), encodeUrl(src), field.removeParameters());

                if(debug())
                {
                    String name = fields.getImage().getName();
                    logger.info("Image source for "+name+": "+content.getImageSource());
                    logger.info("Image for "+name+": "+content.getImage());
                }
            }
        }
    }
}