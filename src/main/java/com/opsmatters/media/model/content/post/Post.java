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
import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.cache.content.util.ContentImages;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Article;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.util.TimeUtils;

import static com.opsmatters.media.model.content.FieldName.*;
import static com.opsmatters.media.model.content.post.PostType.*;

/**
 * Class representing a post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Post extends Article<PostDetails>
{
    private String postType = "";
    private String urlAlias = "";
    private String basePath = "";

    /**
     * Default constructor.
     */
    public Post()
    {
        setDetails(new PostDetails());
    }

    /**
     * Constructor that takes a post.
     */
    public Post(Post obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a site and code.
     */
    public Post(Site site, String code)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Post obj)
    {
        super.copyAttributes(obj);
        setContentDetails(obj.getDetails());
        setPostType(new String(obj.getPostType() != null ? obj.getPostType() : ""));
        setBasePath(obj.getBasePath());
        setUrlAlias(new String(obj.getUrlAlias() != null ? obj.getUrlAlias() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public Post(Site site, String code, String[] values) throws DateTimeParseException
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
    public Post(JSONObject obj)
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

        setPostType(obj.optString(POST_TYPE.value()));
        setDescription(EmojiParser.parseToUnicode(obj.optString(DESCRIPTION.value())));
        setUrlAlias(obj.optString(URL.value()));
        setCanonicalUrl(obj.optString(CANONICAL_URL.value()));
        setImage(obj.optString(IMAGE.value()));
        setImageSource(obj.optString(IMAGE_SOURCE.value()));
        setAuthor(obj.optString(AUTHOR.value()));
        setAuthorLink(obj.optString(AUTHOR_LINK.value()));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(POST_TYPE.value(), getPostType());
        ret.putOpt(DESCRIPTION.value(), EmojiParser.parseToAliases(getDescription()));
        ret.putOpt(URL.value(), getUrlAlias());
        ret.putOpt(CANONICAL_URL.value(), getCanonicalUrl());
        ret.putOpt(IMAGE.value(), getImage());
        ret.putOpt(IMAGE_SOURCE.value(), getImageSource());
        ret.putOpt(AUTHOR.value(), getAuthor());
        ret.putOpt(AUTHOR_LINK.value(), getAuthorLink());

        return ret;
    }

    /**
     * Returns the set of output fields for the content.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(POST_TYPE, getPostType());
        ret.put(DESCRIPTION, EmojiParser.parseToHtmlDecimal(getDescription()));
        ret.put(AUTHOR, getAuthor());
        ret.put(AUTHOR_LINK, getAuthorLink());
        ret.put(URL, getUrlAlias());
        ret.put(CANONICAL_URL, getCanonicalUrl());
        ret.put(IMAGE, getImage());
        ret.put(METATAGS, getMetatags());

        return ret;
    }

    /**
     * Copies any external attributes of the given object.
     */
    @Override
    public void copyExternalAttributes(Content obj)
    {
        if(obj instanceof Post)
        {
            Post post = (Post)obj;
            if(post.hasUrlAlias())
                setUrlAlias(post.getUrlAlias());
        }
    }

    /**
     * Returns a new post with defaults.
     */
    public static Post getDefault(Organisation organisation, OrganisationSite organisationSite, PostConfig config)
        throws DateTimeParseException
    {
        Post post = new Post();

        post.init();
        post.setSiteId(organisationSite.getSiteId());
        post.setPublishedDateAsString(TimeUtils.toStringUTC(config.getField(PUBLISHED_DATE)));
        post.setSocial(organisationSite.hasSocial());

        return post;
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite, PostConfig config)
    {
        super.init(organisation, organisationSite, config);

        setCreatorEmail(organisation.getEmail());

        String promote = config.getField(PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);

        String featured = config.getField(FEATURED);
        setFeatured(featured == null || featured.equals("0") ? false : true);

        String sponsored = config.getField(SPONSORED);
        setSponsored(sponsored == null || sponsored.equals("0") ? false : true);

        setPostType(config.getField(POST_TYPE, ""));

        setSocial(organisationSite.hasSocial());
    }

    /**
     * Prepare the fields in the content using the given configuration.
     */
    public void prepare(PostConfig config, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getField(PUBLISHED_DATE)));

        PostType guessed = PostType.guess(getTagsList());
        if(guessed != null)
            setPostType(guessed);

        // Use the default author if a content author wasn't found
        if(config.hasField(AUTHOR) && getAuthor().length() == 0)
            setAuthor(config.getField(AUTHOR));

        // Use the default author link if a content author link wasn't found
        if(config.hasField(AUTHOR_LINK) && getAuthorLink().length() == 0)
            setAuthorLink(config.getField(AUTHOR_LINK));

        // Use the default image if a content image wasn't found
        ContentImage image = ContentImages.get(ImageType.BANNER, config.getCode());
        if(image != null && getImage().length() == 0)
            setImage(image.getFilename());

        // Clear social flag if the content is old
        if(hasSocial())
            setSocial(isRecent());
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
     * Sets the post details from a teaser.
     */
    @Override
    public void setTeaserDetails(PostDetails obj)
    {
        super.setTeaserDetails(obj);

        if(obj != null)
        {
            setImageSource(new String(obj.getImageSource() != null ? obj.getImageSource() : ""));
            setImage(new String(obj.getImage() != null ? obj.getImage() : ""));
            setAuthor(new String(obj.getAuthor() != null ? obj.getAuthor() : ""));
            setAuthorLink(new String(obj.getAuthorLink() != null ? obj.getAuthorLink() : ""));
        }
    }

    /**
     * Sets the post details.
     */
    @Override
    public void setContentDetails(PostDetails obj)
    {
        if(obj != null)
        {
            setTeaserDetails(obj);
            setDescription(new String(obj.getDescription() != null ? obj.getDescription() : ""));
            setConfigured(true);
        }
    }

    /**
     * Returns the post type.
     */
    public String getPostType()
    {
        return postType;
    }

    /**
     * Sets the post type.
     */
    public void setPostType(String postType)
    {
        this.postType = postType;
    }

    /**
     * Sets the post type.
     */
    public void setPostType(PostType postType)
    {
        setPostType(postType != null ? postType.value() : "");
    }

    /**
     * Returns the post description.
     */
    public String getDescription()
    {
        return getDetails().getDescription();
    }

    /**
     * Sets the post description.
     */
    public void setDescription(String description)
    {
        getDetails().setDescription(description);
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
     * Returns <CODE>true</CODE> if the post image has been set.
     */
    @Override
    public boolean hasImage()
    {
        return getDetails().hasImage();
    }

    /**
     * Returns the post image source.
     * <p>
     * Always returns <CODE>null</CODE>.
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
     * Returns <CODE>true</CODE> if the post image source has been set.
     * <p>
     * Always returns <CODE>false</CODE>.
     */
    @Override
    public boolean hasImageSource()
    {
        return getDetails().hasImageSource();
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
        return getDetails().getAuthor();
    }

    /**
     * Sets the author of the post.
     */
    public void setAuthor(String author)
    {
        getDetails().setAuthor(author);
    }

    /**
     * Returns the author link of the post.
     */
    public String getAuthorLink()
    {
        return getDetails().getAuthorLink();
    }

    /**
     * Sets the author link of the post.
     */
    public void setAuthorLink(String authorLink)
    {
        getDetails().setAuthorLink(authorLink);
    }

    /**
     * Returns <CODE>true</CODE> if the author link has been set.
     */
    public boolean hasAuthorLink()
    {
        return getAuthorLink() != null && getAuthorLink().length() > 0;
    }
}