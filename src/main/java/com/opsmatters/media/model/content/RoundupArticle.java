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
package com.opsmatters.media.model.content;

import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.config.content.RoundupConfiguration;
import com.opsmatters.media.config.content.WebPageConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.config.content.util.ContentImages;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a roundup post article.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupArticle extends Article implements LinkedContent
{
    private RoundupDetails details = new RoundupDetails();

    /**
     * Default constructor.
     */
    public RoundupArticle()
    {
        setContentDetails(details);
    }

    /**
     * Constructor that takes a roundup post.
     */
    public RoundupArticle(RoundupArticle obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a roundup.
     */
    public RoundupArticle(Site site, String code, RoundupDetails obj)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
        setRoundupDetails(obj);
    }

    /**
     * Constructor that takes a roundup summary.
     */
    public RoundupArticle(Site site, String code, RoundupSummary obj)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
        setContentSummary(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(RoundupArticle obj)
    {
        super.copyAttributes(obj);

        setRoundupDetails(obj.getRoundupDetails());
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public RoundupArticle(Site site, String code, String[] values) throws DateTimeParseException
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
        String authorLink = values[11];
        String creatorEmail = values[12];
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
        setAuthorLink(authorLink);
        setCreatorEmail(creatorEmail);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
        setPromoted(promote != null && promote.equals("1"));
        setNewsletter(newsletter != null && newsletter.equals("1"));
        setFeatured(featured != null && featured.equals("1"));
        setSponsored(sponsored != null && sponsored.equals("1"));
    }

    /**
     * Constructor that takes a JSON object.
     */
    public RoundupArticle(JSONObject obj)
    {
        this();
        fromJson(obj);
    }

    /**
     * Initialise this object using a JSON object.
     */
    public void fromJson(JSONObject obj)
    {
        super.fromJson(obj);

        setUrl(obj.optString(Fields.URL), false);
        setImage(obj.optString(Fields.IMAGE));
        setImageSource(obj.optString(Fields.IMAGE_SOURCE));
        setAuthor(obj.optString(Fields.AUTHOR));
        setAuthorLink(obj.optString(Fields.AUTHOR_LINK));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(Fields.URL, getUrl());
        ret.putOpt(Fields.IMAGE, getImage());
        ret.putOpt(Fields.IMAGE_SOURCE, getImageSource());
        ret.putOpt(Fields.AUTHOR, getAuthor());
        ret.putOpt(Fields.AUTHOR_LINK, getAuthorLink());

        return ret;
    }

    /**
     * Returns the set of output fields for the content.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.URL, getUrl());
        ret.put(Fields.AUTHOR, getAuthor());
        ret.put(Fields.AUTHOR_LINK, getAuthorLink());
        ret.put(Fields.IMAGE, getImage());

        return ret;
    }

    /**
     * Returns a new article with defaults.
     */
    public static RoundupArticle getDefault(Organisation organisation, OrganisationSite organisationSite,
        RoundupConfiguration config) throws DateTimeParseException
    {
        RoundupArticle article = new RoundupArticle();

        article.init();
        article.setSiteId(organisationSite.getSiteId());
        article.setTitle("New Roundup");
        article.setSummary(StringUtils.EMPTY);
        article.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));
        article.setSocial(organisationSite.hasSocial());

        return article;
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite,
        RoundupConfiguration config, WebPageConfiguration page)
    {
        super.init(organisation, organisationSite, config);

        setCreatorEmail(organisation.getEmail());

        if(page.hasField(Fields.TAGS))
            setTags(page.getField(Fields.TAGS, ""));
        if(page.hasField(Fields.NEWSLETTER))
            setNewsletter(page.getField(Fields.NEWSLETTER, "0").equals("0") ? false : true);

        String promote = config.getField(Fields.PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);

        String featured = config.getField(Fields.FEATURED);
        setFeatured(featured == null || featured.equals("0") ? false : true);

        String sponsored = config.getField(Fields.SPONSORED);
        setSponsored(sponsored == null || sponsored.equals("0") ? false : true);

        setSocial(organisationSite.hasSocial());
    }

    /**
     * Prepare the fields in the content using the given configuration.
     */
    public void prepare(RoundupConfiguration config, WebPageConfiguration page, boolean debug)
        throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));

        // Use the default author if a content author wasn't found
        if(getAuthor().length() == 0)
        {
            if(page.hasField(Fields.AUTHOR))
                setAuthor(page.getField(Fields.AUTHOR));
            else if(config.hasField(Fields.AUTHOR))
                setAuthor(config.getField(Fields.AUTHOR));
        }

        // Use the default author link if a content author link wasn't found
        if(getAuthorLink().length() == 0)
        {
            if(page.hasField(Fields.AUTHOR_LINK))
                setAuthorLink(page.getField(Fields.AUTHOR_LINK));
            else if(config.hasField(Fields.AUTHOR_LINK))
                setAuthorLink(config.getField(Fields.AUTHOR_LINK));
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
     * Returns the roundup details.
     */
    public RoundupDetails getRoundupDetails()
    {
        return details;
    }

    /**
     * Sets the roundup details.
     */
    public void setRoundupDetails(RoundupDetails obj)
    {
        setContentSummary(obj);
        setContentDetails(true);
    }

    /**
     * Sets the roundup details from a summary.
     */
    public void setContentSummary(RoundupSummary obj)
    {
        super.setContentSummary(obj);
        setUrl(new String(obj.getUrl()), false);
        setImageSource(new String(obj.getImageSource() != null ? obj.getImageSource() : ""));
        setImage(new String(obj.getImage() != null ? obj.getImage() : ""));
        setAuthor(new String(obj.getAuthor() != null ? obj.getAuthor() : ""));
        setAuthorLink(new String(obj.getAuthorLink() != null ? obj.getAuthorLink() : ""));
    }

    /**
     * Returns the URL of the roundup.
     */
    public String getUrl()
    {
        return details.getUrl();
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
        details.setUrl(url, removeParameters);
    }

    /**
     * Returns <CODE>true</CODE> if the URL has been set.
     */
    public boolean hasUrl()
    {
        return details.hasUrl();
    }

    /**
     * Returns the image source.
     */
    @Override
    public String getImageSource()
    {
        return details.getImageSource();
    }

    /**
     * Sets the image source.
     */
    public void setImageSource(String imageSource)
    {
        details.setImageSource(imageSource);
    }

    /**
     * Returns <CODE>true</CODE> if the roundup image source has been set.
     */
    @Override
    public boolean hasImageSource()
    {
        return details.hasImageSource();
    }

    /**
     * Returns the image name.
     */
    @Override
    public String getImage()
    {
        return details.getImage();
    }

    /**
     * Sets the image name.
     */
    @Override
    public void setImage(String image)
    {
        details.setImage(image);
    }

    /**
     * Sets the image name.
     */
    @Override
    public void setImageFromPath(String prefix, String path)
    {
        details.setImageFromPath(prefix, path);
    }

    /**
     * Returns <CODE>true</CODE> if the roundup image has been set.
     */
    @Override
    public boolean hasImage()
    {
        return details.hasImage();
    }

    /**
     * Returns the author of the roundup.
     */
    public String getAuthor()
    {
        return details.getAuthor();
    }

    /**
     * Sets the author of the roundup.
     */
    public void setAuthor(String author)
    {
        details.setAuthor(author);
    }

    /**
     * Returns <CODE>true</CODE> if the author has been set.
     */
    public boolean hasAuthor()
    {
        return getAuthor() != null && getAuthor().length() > 0;
    }

    /**
     * Returns the author link of the roundup.
     */
    public String getAuthorLink()
    {
        return details.getAuthorLink();
    }

    /**
     * Sets the author link of the roundup.
     */
    public void setAuthorLink(String authorLink)
    {
        details.setAuthorLink(authorLink);
    }

    /**
     * Returns <CODE>true</CODE> if the author link has been set.
     */
    public boolean hasAuthorLink()
    {
        return getAuthorLink() != null && getAuthorLink().length() > 0;
    }
}