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
package com.opsmatters.media.crawler.publication;

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
import com.opsmatters.media.model.content.publication.PublicationTeaser;
import com.opsmatters.media.model.content.publication.PublicationDetails;
import com.opsmatters.media.model.content.publication.PublicationConfig;
import com.opsmatters.media.model.content.crawler.ContentLoading;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a crawler for publications.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PublicationCrawler extends WebPageCrawler<PublicationTeaser,PublicationDetails>
{
    private static final Logger logger = Logger.getLogger(PublicationCrawler.class.getName());

    private PublicationConfig config;

    /**
     * Constructor that takes a web page configuration.
     */
    public PublicationCrawler(PublicationConfig config, CrawlerWebPage page)
    {
        super(config, page);
        this.config = config;
    }

    /**
     * Returns the publication configuration of the crawler.
     */
    public PublicationConfig getConfig()
    {
        return config;
    }

    /**
     * Create the publication teaser from the given element.
     */
    @Override
    protected PublicationTeaser getTeaser(Element root, Fields fields)
        throws DateTimeParseException
    {
        PublicationTeaser teaser = new PublicationTeaser();
        if(fields.hasValidator())
            validateContent(teaser, fields.getValidator(), root, "teaser");
        if(teaser.isValid())
        {
            if(debug() && fields.hasValidator())
                logger.info("Validated publication teaser: "+fields.getValidator());
            populateTeaserFields(root, fields, teaser, "teaser");
            if(fields.hasUrl())
            {
                Field field = fields.getUrl();
                String url = getAnchor(field, root, "teaser", field.removeParameters());
                if(url != null)
                    teaser.setUrl(url, field.removeParameters());
            }
        }

        return teaser;
    }

    /**
     * Create publication details from the given url.
     */
    public PublicationDetails getDetails(String url)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        return getDetails(new PublicationTeaser(url, getArticles().removeParameters()));
    }

    /**
     * Returns the processed publication derived from the given teaser.
     */
    @Override
    public PublicationDetails getDetails(PublicationTeaser teaser)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        PublicationDetails content = new PublicationDetails(teaser);
        List<Fields> articles = getPage().getArticles().getFields(hasRootError());

        loadArticlePage(content.getUrl());

        // Trace to see the page
        if(trace(getDriver()))
            logger.info("article-page="+getPageSource());

        Element root = null;
        Document doc = Jsoup.parse(getPageSource("body"));
        doc.outputSettings().prettyPrint(false);

        for(Fields fields : articles)
        {
            if(!fields.hasRoot())
                throw new IllegalArgumentException("Root empty for publication article");

            Elements elements = doc.select(fields.getRoot());
            if(elements.size() > 0)
            {
                root = elements.get(0);
                if(debug())
                    logger.info("Root found for publication article: "+fields.getRoot());
                populateTeaserFields(root, fields, content, "article");
            }
            else
            {
                logger.warning("Root not found for publication article: "+fields.getRoot());
                log.warn("Root not found for publication article: "+fields.getRoot());
                continue;
            }

            // Trace to see the article root node
            if(trace(root))
                logger.info("publication-node="+root.html());

            // Default the published date to today if not found
            if(!fields.hasPublishedDate() && content.getPublishedDate() == null)
            {
                content.setPublishedDate(TimeUtils.truncateTimeUTC());
                if(debug())
                    logger.info("Defaulting published date: "+content.getPublishedDateAsString());
            }

            if(root != null && fields.hasBody())
            {
                String body = getBody(fields.getBody(), root, "article", debug());
                if(body != null)
                    content.setDescription(body);
            }

            if(root != null)
                break;
        }

        if(root == null)
            throw new IllegalArgumentException("Root not found for publication article");

        Teasers.update(getPage().getTeasers().getUrls(), content);

        return content;
    }

    /**
     * Populate the teaser fields from the given element.
     */
    private void populateTeaserFields(Element root, Fields fields, PublicationTeaser teaser, String type)
        throws DateTimeParseException
    {
        if(fields.hasTitle())
        {
            Field field = fields.getTitle();
            String title = getElements(field, root, type);
            if(title != null && title.length() > 0)
                teaser.setTitle(title);
        }

        if(fields.hasPublishedDate())
        {
            Field field = fields.getPublishedDate();
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
                            teaser.setPublishedDateAsString(publishedDate, datePattern);
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
                    logger.severe(String.format("Unparseable %s published date: %s code=%s",
                        type, publishedDate, config.getCode()));
                    log.error(String.format("Unparseable %s published date: %s",
                        type, publishedDate));
                }
            }
        }

        if(fields.hasImage())
        {
            Field field = fields.getImage();
            String src = getImageSrc(field, root, type);
            if(src != null && src.length() > 0)
            {
                teaser.setImageFromPath(getImagePrefix(), src);
                teaser.setImageSource(getBasePath(), encodeUrl(src), field.removeParameters());

                if(debug())
                {
                    String name = fields.getImage().getName();
                    logger.info("Image source for "+name+": "+teaser.getImageSource());
                    logger.info("Image for "+name+": "+teaser.getImage());
                }
            }
        }
    }
}