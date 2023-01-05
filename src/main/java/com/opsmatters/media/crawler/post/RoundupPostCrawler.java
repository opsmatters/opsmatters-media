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
package com.opsmatters.media.crawler.post;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.time.format.DateTimeParseException;
import com.vdurmont.emoji.EmojiParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.opsmatters.media.crawler.WebPageCrawler;
import com.opsmatters.media.model.content.post.RoundupPostTeaser;
import com.opsmatters.media.model.content.post.RoundupPostDetails;
import com.opsmatters.media.model.content.post.RoundupPostConfig;
import com.opsmatters.media.model.content.crawler.ContentLoading;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;

import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.FormatUtils;

/**
 * Class representing a crawler for roundup posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupPostCrawler extends WebPageCrawler<RoundupPostTeaser,RoundupPostDetails>
{
    private static final Logger logger = Logger.getLogger(RoundupPostCrawler.class.getName());

    private RoundupPostConfig config;

    /**
     * Constructor that takes a web page configuration.
     */
    public RoundupPostCrawler(RoundupPostConfig config, CrawlerWebPage page)
    {
        super(config, page);
        this.config = config;
    }

    /**
     * Returns the roundup configuration of the crawler.
     */
    public RoundupPostConfig getConfig()
    {
        return config;
    }

    /**
     * Create a roundup teaser from the given element.
     */
    @Override
    protected RoundupPostTeaser getTeaser(Element root, Fields fields)
        throws DateTimeParseException
    {
        RoundupPostTeaser teaser = new RoundupPostTeaser();
        if(fields.hasValidator())
            validateContent(teaser, fields.getValidator(), root, "teaser");
        if(teaser.isValid())
        {
            if(debug() && fields.hasValidator())
                logger.info("Validated roundup content: "+fields.getValidator());
            populateTeaserFields(root, fields, teaser, "teaser");
            if(fields.hasUrl())
            {
                String url = null;
                Field field = fields.getUrl();
                if(field.generate())
                    url = FormatUtils.generateUrl(getBasePath(), getElements(field, root, "teaser"));
                else
                    url = getAnchor(field, root, "teaser", field.removeParameters());
                if(url != null)
                    teaser.setUrl(url, field.removeParameters());
            }
        }

        return teaser;
    }

    /**
     * Create roundup details from the given url.
     */
    public RoundupPostDetails getDetails(String url)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        return getDetails(new RoundupPostTeaser(url, removeParameters()));
    }

    /**
     * Returns the processed roundup post derived from the given teaser.
     */
    @Override
    public RoundupPostDetails getDetails(RoundupPostTeaser teaser)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        RoundupPostDetails content = new RoundupPostDetails(teaser);
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
                throw new IllegalArgumentException("Root empty for roundup content");

            Elements elements = doc.select(fields.getRoot());
            if(elements.size() > 0)
            {
                root = elements.get(0);
                if(debug())
                    logger.info("Root found for roundup content: "+fields.getRoot());
                populateTeaserFields(root, fields, content, "content");
            }
            else
            {
                logger.warning("Root not found for roundup content: "+fields.getRoot());
                continue;
            }

            // Trace to see the content root node
            if(trace(root))
                logger.info("roundup-node="+root.html());

            if(root != null && fields.hasBody())
            {
                String body = getBodySummary(fields.getBody(), root, "content", config.getSummary(), debug());
                if(body != null)
                    content.setSummary(body);
            }

            if(root != null)
                break;
        }

        if(root == null)
            throw new IllegalArgumentException("Root not found for roundup content");

        return content;
    }

    /**
     * Populate the teaser fields from the given element.
     */
    private void populateTeaserFields(Element root, Fields fields, RoundupPostTeaser teaser, String type)
        throws DateTimeParseException
    {
        if(fields.hasTitle())
        {
            Field field = fields.getTitle();
            String title = getElements(field, root, type);
            if(title != null && title.length() > 0 && !title.equals("Please wait..."))
                teaser.setTitle(EmojiParser.removeAllEmojis(title.trim()));
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
                    logger.warning("Unparseable published date: "+publishedDate+" code="+config.getCode());
                }
            }
        }

        if(fields.hasAuthor())
        {
            Field field = fields.getAuthor();
            String author = getElements(field, root, type);
            if(author != null && author.length() > 0)
                teaser.setAuthor(author);
        }

        if(fields.hasAuthorLink())
        {
            Field field = fields.getAuthorLink();
            String authorLink = getAnchor(field, root, type, field.removeParameters());
            if(authorLink != null && authorLink.length() > 0)
                teaser.setAuthorLink(authorLink);
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

        if(fields.hasBackgroundImage())
        {
            Field field = fields.getBackgroundImage();
            String style = getStyle(field, root, type);
            if(style != null && style.length() > 0)
            {
                String src = getBackgroundImage(style);
                teaser.setImageFromPath(getImagePrefix(), src);
                teaser.setImageSource(getBasePath(), encodeUrl(src), field.removeParameters());

                if(debug())
                {
                    String name = fields.getBackgroundImage().getName();
                    logger.info("Background Image source for "+name+": "+teaser.getImageSource());
                    logger.info("Background Image for "+name+": "+teaser.getImage());
                }
            }
        }
    }
}