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
import net.fellbaum.jemoji.EmojiManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.opsmatters.media.crawler.WebPageCrawler;
import com.opsmatters.media.cache.content.Teasers;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.post.RoundupPostDetails;
import com.opsmatters.media.model.content.post.RoundupPostConfig;
import com.opsmatters.media.model.content.crawler.ContentLoading;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;
import com.opsmatters.media.model.logging.LogEventCategory;
import com.opsmatters.media.model.logging.LogError;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.FormatUtils;

import static com.opsmatters.media.model.content.crawler.CrawlerStatus.*;
import static com.opsmatters.media.model.content.crawler.DocumentFormat.*;
import static com.opsmatters.media.model.logging.LogEventCategory.*;
import static com.opsmatters.media.model.logging.ErrorCode.*;

/**
 * Class representing a crawler for roundup posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupPostCrawler extends WebPageCrawler<RoundupPostDetails>
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
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.ROUNDUP;
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
    protected RoundupPostDetails getTeaser(Element root, Fields fields)
        throws DateTimeParseException
    {
        RoundupPostDetails teaser = new RoundupPostDetails();
        if(fields.hasValidator())
            validateContent(teaser, fields.getValidator(), root, TEASER);
        if(teaser.isValid())
        {
            if(debug() && fields.hasValidator())
                logger.info("Validated roundup teaser: "+fields.getValidator());
            populateTeaserFields(root, fields, teaser, TEASER);
            if(fields.hasUrl())
            {
                Field field = fields.getUrl();

                String url = null;
                if(getFormat() == HTML)
                    url = getAnchor(field, root, TEASER, field.removeParameters());
                else
                    url = getElements(field, root, TEASER);

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
        return getDetails(new RoundupPostDetails(url, getArticles().removeParameters()));
    }

    /**
     * Returns the processed roundup post derived from the given teaser.
     */
    @Override
    public RoundupPostDetails getDetails(RoundupPostDetails teaser)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        clearResults();
        setStatus(EXECUTING);

        RoundupPostDetails content = new RoundupPostDetails(teaser);
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
                    throw new IllegalArgumentException("Root empty for roundup article");
                }

                Elements elements = doc.select(fields.getRoot());
                if(elements.size() > 0)
                {
                    root = elements.get(0);
                    if(debug())
                        logger.info("Root found for roundup article: "+fields.getRoot());
                    populateTeaserFields(root, fields, content, ARTICLE);
                }
                else
                {
                    if(debug())
                        logger.info("Root not found for roundup article: "+fields.getRoot());
                    log.info(ARTICLE, "Root not found for roundup article: "+fields.getRoot());
                    continue;
                }

                // Trace to see the article root node
                if(trace(root))
                    logger.info("roundup-node="+root.html());

                if(root != null && fields.hasBody())
                {
                    String body = getBodySummary(fields.getBody(), root, ARTICLE, debug());
                    if(body != null)
                        content.setSummary(EmojiManager.removeAllEmojis(body));
                }

                if(root != null)
                {
                    if(isBadPage())
                    {
                        setStatus(ERROR);
                        setErrorCode(E_BAD_PAGE);
                        throw new IllegalArgumentException("Unable to crawl roundup article page");
                    }

                    break;
                }
            }

            if(root == null)
            {
                setErrorCode(E_MISSING_ROOT);
                throw new IllegalArgumentException("Root not found for roundup article");
            }

            Teasers.update(getPage().getTeasers().getUrls(), content);
        }

        if(getErrorCode() == E_NONE)
            setStatus(COMPLETED);

        return content;
    }

    /**
     * Populate the teaser fields from the given element.
     */
    private void populateTeaserFields(Element root, Fields fields, RoundupPostDetails teaser, LogEventCategory category)
        throws DateTimeParseException
    {
        if(fields.hasTitle())
        {
            Field field = fields.getTitle();
            String title = getElements(field, root, category);
            if(title != null && title.length() > 0 && !title.equals("Please wait..."))
                teaser.setTitle(EmojiManager.removeAllEmojis(title.trim()));
        }

        if(fields.hasPublishedDate())
        {
            Field field = fields.getPublishedDate();
            String publishedDate = getElements(field, root, category);
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
                    setResult(field, false);

                    if(!field.isOptional())
                    {
                        logger.severe(StringUtils.serialize(e));
                        logger.severe(String.format("Unparseable %s published date: %s code=%s",
                            category.tag(), publishedDate, config.getCode()));

                        log.add(log.warn(E_PARSE_DATE, category)
                            .message(String.format("Unparseable %s published date: %s",
                                category.tag(), publishedDate))
                            .location(this)
                            .entity(config, getPage())
                            .exception(e));
                    }
                }
            }
        }

        if(fields.hasAuthor())
        {
            Field field = fields.getAuthor();
            String author = getElements(field, root, category);
            if(author != null && author.length() > 0)
                teaser.setAuthor(author);
        }

        if(fields.hasAuthorLink())
        {
            Field field = fields.getAuthorLink();
            String authorLink = getAnchor(field, root, category, field.removeParameters());
            if(authorLink != null && authorLink.length() > 0)
                teaser.setAuthorLink(authorLink);
        }

        if(fields.hasImage())
        {
            Field field = fields.getImage();
            String src = getImageSrc(field, root, category);
            if(src != null && src.length() > 0)
            {
                teaser.setImageFromPath(getImagePrefix(), src);
                teaser.setImageSource(getBasePath(field), encodeUrl(src), field.removeParameters());

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