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
import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.config.content.PostConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.config.content.util.ContentImages;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a post article.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PostArticle extends Article
{
    private PostDetails details = new PostDetails();
    private String urlAlias = "";
    private String basePath = "";

    /**
     * Default constructor.
     */
    public PostArticle()
    {
        setContentDetails(details);
    }

    /**
     * Constructor that takes a post.
     */
    public PostArticle(PostArticle obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a post.
     */
    public PostArticle(Site site, String code, PostDetails obj)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
        setPostDetails(obj);
    }

    /**
     * Constructor that takes a post summary.
     */
    public PostArticle(Site site, String code, PostSummary obj)
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
    public void copyAttributes(PostArticle obj)
    {
        super.copyAttributes(obj);

        setPostDetails(obj.getPostDetails());
        setBasePath(obj.getBasePath());
        setUrlAlias(new String(obj.getUrlAlias() != null ? obj.getUrlAlias() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public PostArticle(Site site, String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        setSiteId(site.getId());

        String id = values[0];
        String pubdate = values[1];
        String title = values[2];
        String summary = values[3];
        String description = values[4];
//        String link = values[5];
//        String linkText = values[6];
        String canonicalUrl = values[5];
        String organisation = values[6];
        String tags = values[7];
        String image = values[8];
        String imageText = values[9];
        String imageTitle = values[10];
        String author = values[11];
        String authorLink = values[12];
        String creatorEmail = values[13];
        String createdBy = values[14];
        String published = values[15];
        String promote = values[16];
        String newsletter = values[17];
        String featured = values.length > 18 ? values[18] : null;
        String sponsored = values.length > 19 ? values[19] : null;


        // Remove feeds path from image
        if(image.indexOf("/") != -1)
            image = image.substring(image.lastIndexOf("/")+1);

        setCode(code);
        setId(Integer.parseInt(id.substring(id.lastIndexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setTitle(title);
        setSummary(summary);
        setDescription(description);
        setCanonicalUrl(canonicalUrl);
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
    public PostArticle(JSONObject obj)
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

        setDescription(EmojiParser.parseToUnicode(obj.optString(Fields.DESCRIPTION)));
        setUrlAlias(obj.optString(Fields.URL));
        setCanonicalUrl(obj.optString(Fields.CANONICAL_URL));
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

        ret.putOpt(Fields.DESCRIPTION, EmojiParser.parseToAliases(getDescription()));
        ret.putOpt(Fields.URL, getUrlAlias());
        ret.putOpt(Fields.CANONICAL_URL, getCanonicalUrl());
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

        ret.put(Fields.DESCRIPTION, EmojiParser.parseToHtmlDecimal(getDescription()));
        ret.put(Fields.AUTHOR, getAuthor());
        ret.put(Fields.AUTHOR_LINK, getAuthorLink());
        ret.put(Fields.URL, getUrlAlias());
        ret.put(Fields.CANONICAL_URL, getCanonicalUrl());
        ret.put(Fields.IMAGE, getImage());
        ret.put(Fields.METATAGS, getMetatags());

        return ret;
    }

    /**
     * Copies any external attributes of the given object.
     */
    @Override
    public void copyExternalAttributes(ContentItem obj)
    {
        if(obj instanceof PostArticle)
        {
            PostArticle article = (PostArticle)obj;
            if(article.hasUrlAlias())
                setUrlAlias(article.getUrlAlias());
        }
    }

    /**
     * Returns a new article with defaults.
     */
    public static PostArticle getDefault(Organisation organisation, OrganisationSite organisationSite, PostConfiguration config)
        throws DateTimeParseException
    {
        PostArticle article = new PostArticle();

        article.init();
        article.setSiteId(organisationSite.getSiteId());
        article.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));
        article.setSocial(organisationSite.hasSocial());

        return article;
    }

    /**
     * Use the given organisation to set defaults for the content.
     */
    public void init(Organisation organisation)
    {
        if(organisation != null)
        {
            setCreatorEmail(organisation.getEmail());
        }
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite, PostConfiguration config)
    {
        super.init(organisation, organisationSite, config);

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
    public void prepare(PostConfiguration config, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));

        // Use the default author if a content author wasn't found
        if(config.hasField(Fields.AUTHOR) && getAuthor().length() == 0)
            setAuthor(config.getField(Fields.AUTHOR));

        // Use the default author link if a content author link wasn't found
        if(config.hasField(Fields.AUTHOR_LINK) && getAuthorLink().length() == 0)
            setAuthorLink(config.getField(Fields.AUTHOR_LINK));

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
        return ContentType.POST;
    }

    /**
     * Returns the post details.
     */
    public PostDetails getPostDetails()
    {
        return details;
    }

    /**
     * Sets the post details.
     */
    public void setPostDetails(PostDetails obj)
    {
        setContentSummary(obj);
        setDescription(new String(obj.getDescription() != null ? obj.getDescription() : ""));
        setContentDetails(true);
    }

    /**
     * Sets the post details from a summary.
     */
    public void setContentSummary(PostSummary obj)
    {
        super.setContentSummary(obj);
        setImageSource(new String(obj.getImageSource() != null ? obj.getImageSource() : ""));
        setImage(new String(obj.getImage() != null ? obj.getImage() : ""));
        setAuthor(new String(obj.getAuthor() != null ? obj.getAuthor() : ""));
        setAuthorLink(new String(obj.getAuthorLink() != null ? obj.getAuthorLink() : ""));
    }

    /**
     * Returns the post description.
     */
    public String getDescription()
    {
        return details.getDescription();
    }

    /**
     * Sets the post description.
     */
    public void setDescription(String description)
    {
        details.setDescription(description);
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
     * Returns <CODE>true</CODE> if the post image has been set.
     */
    @Override
    public boolean hasImage()
    {
        return details.hasImage();
    }

    /**
     * Returns the post image source.
     * <p>
     * Always returns <CODE>null</CODE>.
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
     * Returns <CODE>true</CODE> if the post image source has been set.
     * <p>
     * Always returns <CODE>false</CODE>.
     */
    @Override
    public boolean hasImageSource()
    {
        return details.hasImageSource();
    }

    /**
     * Returns the URL alias for the post.
     */
    public String getUrlAlias()
    {
        return urlAlias;
    }

    /**
     * Sets the URL alias for the post.
     */
    public void setUrlAlias(String urlAlias)
    {
        this.urlAlias = urlAlias;
    }

    /**
     * Returns <CODE>true</CODE> if the URL alias for the post has been set.
     */
    public boolean hasUrlAlias()
    {
        return urlAlias != null && urlAlias.length() > 0;
    }

    /**
     * Returns the base path for the URL alias.
     */
    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Sets the base path for the URL alias.
     */
    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    /**
     * Returns the URL for the post.
     */
    public String getUrl()
    {
        return getBasePath()+getUrlAlias();
    }

    /**
     * Sets the URL for the post.
     */
    public void setUrl(String url)
    {
        String path = getBasePath();
        if(url.startsWith(path))
            url = url.substring(path.length());
        setUrlAlias(url);
    }

    /**
     * Returns <CODE>true</CODE> if this content has metatags to set.
     */
    @Override
    public boolean hasMetatags()
    {
        return hasCanonicalUrl();
    }

    /**
     * Add the metatags for this content to the given JSON object.
     */
    @Override
    protected void addMetatags(JSONObject obj)
    {
        if(hasCanonicalUrl())
            obj.putOpt("canonical_url", getCanonicalUrl());
    }

    /**
     * Returns the author of the post.
     */
    public String getAuthor()
    {
        return details.getAuthor();
    }

    /**
     * Sets the author of the post.
     */
    public void setAuthor(String author)
    {
        details.setAuthor(author);
    }

    /**
     * Returns the author link of the post.
     */
    public String getAuthorLink()
    {
        return details.getAuthorLink();
    }

    /**
     * Sets the author link of the post.
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