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
import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.model.content.RoundupSummary;
import com.opsmatters.media.model.content.RoundupDetails;
import com.opsmatters.media.config.content.RoundupConfiguration;
import com.opsmatters.media.config.content.WebPageConfiguration;
import com.opsmatters.media.config.content.ContentField;
import com.opsmatters.media.config.content.ContentFields;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.FileUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.FormatUtils;

/**
 * Class representing a crawler for roundup posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupCrawler extends WebPageCrawler<RoundupSummary>
{
    private static final Logger logger = Logger.getLogger(RoundupCrawler.class.getName());

    private RoundupConfiguration config;

    /**
     * Constructor that takes a web page configuration.
     */
    public RoundupCrawler(RoundupConfiguration config, WebPageConfiguration page)
    {
        super(page);
        this.config = config;
    }

    /**
     * Returns the roundup configuration of the crawler.
     */
    public RoundupConfiguration getConfig()
    {
        return config;
    }

    /**
     * Returns the image extension for this configuration.
     */
    public String getImageExt()
    {
        return config.getImageExt();
    }

    /**
     * Returns the image prefix for this configuration.
     */
    public String getImagePrefix()
    {
        return config.getImagePrefix();
    }

    /**
     * Create an roundup summary from a selected node.
     */
    @Override
    public RoundupSummary getContentSummary(WebElement root, ContentFields fields)
        throws DateTimeParseException
    {
        RoundupSummary content = new RoundupSummary();
        if(fields.hasValidator())
            validateContent(content, fields.getValidator(), root, "teaser");
        if(content.isValid())
        {
            if(debug() && fields.hasValidator())
                logger.info("Validated roundup content: "+fields.getValidator());
            populateSummaryFields(root, fields, content, "teaser");
            if(fields.hasUrl())
            {
                String url = null;
                ContentField field = fields.getUrl();
                if(field.generate())
                    url = FormatUtils.generateUrl(getBasePath(), getElements(field, root, "teaser"));
                else
                    url = getAnchor(field, root, "teaser", field.removeParameters());
                if(url != null)
                    content.setUrl(url, field.removeParameters());
            }
        }

        return content;
    }

    /**
     * Create a roundup content item from the given url.
     */
    public RoundupDetails getRoundup(String url)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        return getRoundup(new RoundupSummary(url, removeParameters()));
    }

    /**
     * Populate the given roundup content.
     */
    public RoundupDetails getRoundup(RoundupSummary summary)
        throws IOException, IllegalArgumentException, DateTimeParseException
    {
        RoundupDetails content = new RoundupDetails(summary);
        List<ContentFields> articles = getArticleFields();

        configureImplicitWait(getArticleLoading());
        loadPage(content.getUrl(), getArticleLoading());
        configureExplicitWait(getArticleLoading());

        // Trace to see the roundup page
        if(trace(getDriver()))
            logger.info("roundup-page="+getDriver().getPageSource());

        WebElement root = null;
        for(ContentFields fields : articles)
        {
            if(!fields.hasRoot())
                throw new IllegalArgumentException("Root empty for roundup content");
            List<WebElement> elements = getDriver().findElements(By.cssSelector(fields.getRoot()));
            if(elements.size() > 0)
            {
                root = elements.get(0);
                if(debug())
                    logger.info("Root found for roundup content: "+fields.getRoot());
                populateSummaryFields(root, fields, content, "content");
            }
            else
            {
                logger.warning("Root not found for roundup content: "+fields.getRoot());
                continue;
            }

            // Trace to see the content root node
            if(trace(root))
                logger.info("roundup-node="+root.getAttribute("innerHTML"));

            if(root != null && fields.hasBody())
            {
                String body = getBodySummary(fields.getBody(), root, "content", config.getSummary(), debug());
                if(body != null)
                    content.setSummary(body);
            }

            // Apply the custom image name if defined
            String imageFormat = config.getImageFormat();
            if(imageFormat != null && imageFormat.length() > 0 && content.hasImage())
                content.setImageFromPath(getFormatSubstitutor().replace(imageFormat));

            if(root != null)
                break;
        }

        if(root == null)
            throw new IllegalArgumentException("Root not found for roundup content");

        return content;
    }

    /**
     * Sets the roundup properties to be used in field formats.
     * <P>
     * Properties supported:
     * <ul>
     * <li> <strong>url-context</strong>: the last content part of the roundup URL.
     * <li> <strong>image-name</strong>: the name part of the image filename.
     * <li> <strong>image-ext</strong>: the extension part of the image filename.
     * </ul>
     */
    private void setFormatProperties(RoundupSummary content)
    {
        setFormatProperty(URL_CONTEXT, StringUtils.getUrlContext(content.getUrl()));
        setFormatProperty(IMAGE_NAME, FileUtils.getName(content.getImage()));
        setFormatProperty(IMAGE_EXT, FileUtils.getExtension(content.getImage()), getImageExt());
    }

    /**
     * Populate the content fields from the given node.
     */
    private void populateSummaryFields(WebElement root, 
        ContentFields fields, RoundupSummary content, String type)
        throws DateTimeParseException
    {
        content.setImagePrefix(getImagePrefix());

        if(fields.hasTitle())
        {
            ContentField field = fields.getTitle();
            String title = getElements(field, root, type);
            if(title != null && title.length() > 0)
            {
                title = processTitle(title);
                content.setTitle(EmojiParser.removeAllEmojis(title.trim()));
            }
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
                    logger.warning("Unparseable published date: "+publishedDate+" code="+config.getCode());
                }
            }
        }

        if(fields.hasAuthor())
        {
            ContentField field = fields.getAuthor();
            String author = getElements(field, root, type);
            if(author != null && author.length() > 0)
                content.setAuthor(author);
        }

        if(fields.hasAuthorLink())
        {
            ContentField field = fields.getAuthorLink();
            String authorLink = getAnchor(field, root, type, field.removeParameters());
            if(authorLink != null && authorLink.length() > 0)
                content.setAuthorLink(authorLink);
        }

        if(fields.hasImage())
        {
            ContentField field = fields.getImage();
            String src = getImageSrc(field, root, type);
            if(src != null && src.length() > 0)
            {
                content.setImageFromPath(src);
                content.setImageSource(getBasePath(), encodeUrl(src), field.removeParameters());

                if(debug())
                {
                    String name = fields.getImage().getName();
                    logger.info("Image source for "+name+": "+content.getImageSource());
                    logger.info("Image for "+name+": "+content.getImage());
                }
            }
        }

        if(fields.hasBackgroundImage())
        {
            ContentField field = fields.getBackgroundImage();
            String style = getStyle(field, root, type);
            if(style != null && style.length() > 0)
            {
                String src = getBackgroundImage(style);
                content.setImageFromPath(src);
                content.setImageSource(getBasePath(), encodeUrl(src), field.removeParameters());

                if(debug())
                {
                    String name = fields.getBackgroundImage().getName();
                    logger.info("Background Image source for "+name+": "+content.getImageSource());
                    logger.info("Background Image for "+name+": "+content.getImage());
                }
            }
        }

        // Set the format properties from the content
        setFormatProperties(content);
    }
}