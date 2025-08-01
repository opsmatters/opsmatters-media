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
package com.opsmatters.media.model.content.post;

import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.cache.organisation.OrganisationSites;
import com.opsmatters.media.cache.content.util.ContentImages;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Article;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSiteSettings;
import com.opsmatters.media.model.content.LinkedContent;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.SessionId;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing a roundup post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupPost extends Article<RoundupPostDetails> implements LinkedContent
{
    /**
     * Default constructor.
     */
    public RoundupPost()
    {
        setDetails(new RoundupPostDetails());
    }

    /**
     * Constructor that takes a roundup post.
     */
    public RoundupPost(RoundupPost obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a site and code.
     */
    public RoundupPost(Site site, String code)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(RoundupPost obj)
    {
        super.copyAttributes(obj);
        setContentDetails(obj.getDetails());
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public RoundupPost(Site site, String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        setSiteId(site.getId());

        String id = values[0];
        String pubdate = values[1];
        String title = values[2];
        String summary = values[3];
        String url = values[4];
        String organisation = values[5];
        String tags = values[6];
        String image = values[7];
        String imageText = values[8];
        String imageTitle = values[9];
        String author = values[10];
        String authorUrl = values[11];
        String authorEmail = values[12];
        String createdBy = values[13];
        String published = values[14];
        String promote = values[15];
        String newsletter = values[16];
        String featured = values.length > 17 ? values[17] : null;
        String sponsored = values.length > 18 ? values[18] : null;

        // Remove feeds path from image
        if(image.indexOf("/") != -1)
            image = image.substring(image.lastIndexOf("/")+1);

        setCode(code);
        setId(Integer.parseInt(id.substring(id.lastIndexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setTitle(title);
        setSummary(summary);
        setUrl(url, false);
        setTags(tags);
        setImage(image);
        setAuthor(author);
        setAuthorUrl(authorUrl);
        setAuthorEmail(authorEmail);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
        setPromoted(promote != null && promote.equals("1"));
        setNewsletter(newsletter != null && newsletter.equals("1"));
        setFeatured(featured != null && featured.equals("1"));
        setSponsored(sponsored != null && sponsored.equals("1"));
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        ret.putOpt(URL.value(), getUrl());
        ret.putOpt(IMAGE.value(), getImage());
        ret.putOpt(IMAGE_SOURCE.value(), getImageSource());
        ret.putOpt(AUTHOR.value(), getAuthor());
        ret.putOpt(AUTHOR_URL.value(), getAuthorUrl());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);

        setUrl(obj.optString(URL.value()), false);
        setImage(obj.optString(IMAGE.value()));
        setImageSource(obj.optString(IMAGE_SOURCE.value()));
        setAuthor(obj.optString(AUTHOR.value()));
        if(obj.has(AUTHOR_URL.value()))
            setAuthorUrl(obj.optString(AUTHOR_URL.value()));
        else
            setAuthorUrl(obj.optString(AUTHOR_LINK.value())); // deprecated
    }

    /**
     * Returns the set of output fields for the content.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(URL, getUrl());
        ret.put(AUTHOR, getAuthor());
        ret.put(AUTHOR_URL, getAuthorUrl());
        ret.put(IMAGE, getImage());

        return ret;
    }

    /**
     * Copies any external attributes of the given object.
     */
    @Override
    public void copyExternalAttributes(Content obj)
    {
        if(obj instanceof RoundupPost)
        {
            RoundupPost post = (RoundupPost)obj;
            if(post.hasImageSource())
                setImageSource(post.getImageSource());
        }
    }

    /**
     * Returns a new post with defaults.
     */
    public static RoundupPost getDefault(Organisation organisation, OrganisationSite organisationSite, RoundupPostConfig config)
        throws DateTimeParseException
    {
        RoundupPost post = new RoundupPost();

        post.init();
        post.setSiteId(organisationSite.getSiteId());
        post.setTitle("New Roundup");
        post.setPublishedDateAsString(TimeUtils.toStringUTC(SessionId.now(), config.getPublishedDateField(organisationSite.isSponsor())));

        return post;
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite,
        RoundupPostConfig config, CrawlerWebPage page)
    {
        super.init(organisation, organisationSite, config);

        setAuthorEmail(organisation.getEmail());

        if(organisationSite != null)
        {
            ContentSiteSettings settings = organisationSite.getSettings(getType());
            if(settings != null)
            {
                setPromoted(settings.isPromoted());
                setFeatured(settings.isFeatured());
                setSponsored(settings.isSponsored());
            }
        }

        if(page.hasField(TAGS))
            setTags(page.getField(TAGS, ""));
        if(page.hasField(NEWSLETTER))
            setNewsletter(page.getField(NEWSLETTER, "0").equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the content using the given configuration.
     */
    public void prepare(RoundupPostConfig config, CrawlerWebPage page, boolean debug)
        throws DateTimeParseException
    {
        OrganisationSite organisationSite = OrganisationSites.get(config.getCode());
        setPublishedDateAsString(getPublishedDateAsString(config.getPublishedDateField(organisationSite.isSponsor())));

        // Use the default author if a content author wasn't found
        if(getAuthor().length() == 0)
        {
            if(page.hasField(AUTHOR))
                setAuthor(page.getField(AUTHOR));
            else if(config.hasField(AUTHOR))
                setAuthor(config.getField(AUTHOR));
        }

        // Use the default author URL if a content author URL wasn't found
        if(getAuthorUrl().length() == 0)
        {
            if(page.hasField(AUTHOR_URL))
                setAuthorUrl(page.getField(AUTHOR_URL));
            else if(config.hasField(AUTHOR_URL))
                setAuthorUrl(config.getField(AUTHOR_URL));
        }

        // Use the default image if a content image wasn't found
        ContentImage image = ContentImages.get(ImageType.BANNER, config.getCode());
        if(image != null && getImage().length() == 0)
            setImage(image.getFilename());
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
     * Sets the roundup details from a teaser.
     */
    @Override
    public void setTeaserDetails(RoundupPostDetails obj)
    {
        super.setTeaserDetails(obj);

        if(obj != null)
        {
            setUrl(new String(obj.getUrl()), false);
            setImageSource(new String(obj.getImageSource() != null ? obj.getImageSource() : ""));
            setImage(new String(obj.getImage() != null ? obj.getImage() : ""));
            setAuthor(new String(obj.getAuthor() != null ? obj.getAuthor() : ""));
            setAuthorUrl(new String(obj.getAuthorUrl() != null ? obj.getAuthorUrl() : ""));
        }
    }

    /**
     * Sets the roundup details.
     */
    @Override
    public void setContentDetails(RoundupPostDetails obj)
    {
        if(obj != null)
        {
            setTeaserDetails(obj);
            setConfigured(true);
        }
    }

    /**
     * Returns the URL of the roundup.
     */
    public String getUrl()
    {
        return getDetails().getUrl();
    }

    /**
     * Sets the URL of the roundup.
     */
    public void setUrl(String url)
    {
        setUrl(url, false);
    }

    /**
     * Sets the URL of the roundup.
     */
    public void setUrl(String url, boolean removeParameters)
    {
        getDetails().setUrl(url, removeParameters);
    }

    /**
     * Returns <CODE>true</CODE> if the URL has been set.
     */
    public boolean hasUrl()
    {
        return getDetails().hasUrl();
    }

    /**
     * Returns the image source.
     */
    @Override
    public String getImageSource()
    {
        return getDetails().getImageSource();
    }

    /**
     * Sets the image source.
     */
    public void setImageSource(String imageSource)
    {
        getDetails().setImageSource(imageSource);
    }

    /**
     * Returns <CODE>true</CODE> if the roundup image source has been set.
     */
    @Override
    public boolean hasImageSource()
    {
        return getDetails().hasImageSource();
    }

    /**
     * Returns the image name.
     */
    @Override
    public String getImage()
    {
        return getDetails().getImage();
    }

    /**
     * Sets the image name.
     */
    @Override
    public void setImage(String image)
    {
        getDetails().setImage(image);
    }

    /**
     * Sets the image name.
     */
    @Override
    public void setImageFromPath(String prefix, String path)
    {
        getDetails().setImageFromPath(prefix, path);
    }

    /**
     * Returns <CODE>true</CODE> if the roundup image has been set.
     */
    @Override
    public boolean hasImage()
    {
        return getDetails().hasImage();
    }

    /**
     * Returns the author of the roundup.
     */
    public String getAuthor()
    {
        return getDetails().getAuthor();
    }

    /**
     * Sets the author of the roundup.
     */
    public void setAuthor(String author)
    {
        getDetails().setAuthor(author);
    }

    /**
     * Returns <CODE>true</CODE> if the author has been set.
     */
    public boolean hasAuthor()
    {
        return getAuthor() != null && getAuthor().length() > 0;
    }

    /**
     * Returns the author URL of the roundup.
     */
    public String getAuthorUrl()
    {
        return getDetails().getAuthorUrl();
    }

    /**
     * Sets the author URL of the roundup.
     */
    public void setAuthorUrl(String authorUrl)
    {
        getDetails().setAuthorUrl(authorUrl);
    }

    /**
     * Returns <CODE>true</CODE> if the author URL has been set.
     */
    public boolean hasAuthorUrl()
    {
        return getAuthorUrl() != null && getAuthorUrl().length() > 0;
    }
}