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
import com.opsmatters.media.file.CommonFiles;
import com.opsmatters.media.config.content.RoundupConfiguration;
import com.opsmatters.media.config.content.WebPageConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a roundup post article.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupArticle extends Article
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
    public RoundupArticle(String code, RoundupDetails obj)
    {
        this();
        init();
        setCode(code);
        setRoundupDetails(obj);
    }

    /**
     * Constructor that takes a roundup summary.
     */
    public RoundupArticle(String code, RoundupSummary obj)
    {
        this();
        init();
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
    public RoundupArticle(String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

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
    public static RoundupArticle getDefault(RoundupConfiguration config) throws DateTimeParseException
    {
        RoundupArticle article = new RoundupArticle();

        article.init();
        article.setTitle("New Roundup");
        article.setSummary(StringUtils.EMPTY);
        article.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));
        article.setImagePrefix(config.getImagePrefix());
        article.setSocial(true);

        return article;
    }

    /**
     * Use the given organisation listing to set defaults for the content.
     */
    public void init(OrganisationListing listing)
    {
        setCreatorEmail(listing.getEmail());
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(RoundupConfiguration config, WebPageConfiguration page)
    {
        super.init(config);

        setTags(config.getField(Fields.TAGS, ""));
        if(page.hasField(Fields.TAGS))
            setTags(page.getField(Fields.TAGS, ""));

        String promote = config.getField(Fields.PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);

        String newsletter = config.getField(Fields.NEWSLETTER);
        setNewsletter(newsletter == null || newsletter.equals("0") ? false : true);

        String featured = config.getField(Fields.FEATURED);
        setFeatured(featured == null || featured.equals("0") ? false : true);

        String sponsored = config.getField(Fields.SPONSORED);
        setSponsored(sponsored == null || sponsored.equals("0") ? false : true);

        setSocial(true);
    }

    /**
     * Prepare the fields in the content using the given configuration.
     */
    public void prepare(RoundupConfiguration config) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));

        // Use the default author if a content author wasn't found
        if(config.hasField(Fields.AUTHOR) && getAuthor().length() == 0)
            setAuthor(config.getField(Fields.AUTHOR));

        // Use the default author link if a content author link wasn't found
        if(config.hasField(Fields.AUTHOR_LINK) && getAuthorLink().length() == 0)
            setAuthorLink(config.getField(Fields.AUTHOR_LINK));

        // Use the default image if a content image wasn't found
        if(config.hasField(Fields.IMAGE) && getImage().length() == 0)
            setImage(config.getField(Fields.IMAGE));
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
        setImagePrefix(new String(obj.getImagePrefix() != null ? obj.getImagePrefix() : ""));
        setImage(new String(obj.getImage() != null ? obj.getImage() : ""));
        setImageRefresh(obj.isImageRefresh());
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
     * Returns the image source.
     */
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
    public boolean hasImageSource()
    {
        return details.hasImageSource();
    }

    /**
     * Returns the image name.
     */
    public String getImage()
    {
        return details.getImage();
    }

    /**
     * Returns <CODE>true</CODE> if the image name has a JPG extension.
     */
    public boolean isJpgImage()
    {
        String image = getImage() != null ? getImage().toLowerCase() : "";
        return image.endsWith("."+CommonFiles.JPG_EXT) || image.endsWith("."+CommonFiles.JPEG_EXT);
    }

    /**
     * Returns <CODE>true</CODE> if the image name has a PNG extension.
     */
    public boolean isPngImage()
    {
        String image = getImage() != null ? getImage().toLowerCase() : "";
        return image.endsWith("."+CommonFiles.PNG_EXT);
    }

    /**
     * Returns <CODE>true</CODE> if the image name has a GIF extension.
     */
    public boolean isGifImage()
    {
        String image = getImage() != null ? getImage().toLowerCase() : "";
        return image.endsWith("."+CommonFiles.GIF_EXT);
    }

    /**
     * Sets the image name.
     */
    public void setImage(String image)
    {
        details.setImage(image);
    }

    /**
     * Sets the image name.
     */
    public void setImageFromPath(String path)
    {
        details.setImageFromPath(path);
    }

    /**
     * Returns <CODE>true</CODE> if the roundup image has been set.
     */
    public boolean hasImage()
    {
        return details.hasImage();
    }

    /**
     * Returns the image prefix.
     */
    public String getImagePrefix()
    {
        return details.getImagePrefix();
    }

    /**
     * Sets the image prefix.
     */
    public void setImagePrefix(String imagePrefix)
    {
        details.setImagePrefix(imagePrefix);
    }

    /**
     * Returns <CODE>true</CODE> if the image needs to be refreshed after being rendered.
     */
    public boolean isImageRefresh()
    {
        return details.isImageRefresh();
    }

    /**
     * Set to <CODE>true</CODE> if the image needs to be refreshed after being rendered.
     */
    public void setImageRefresh(boolean imageRefresh)
    {
        details.setImageRefresh(imageRefresh);
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
}