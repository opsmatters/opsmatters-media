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
import com.opsmatters.media.cache.admin.ImageProviders;
import com.opsmatters.media.cache.content.util.ContentImages;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.admin.ImageProvider;
import com.opsmatters.media.model.admin.ImageProviderType;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Article;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSiteSettings;
import com.opsmatters.media.model.content.Metatag;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.SessionId;

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
    private String attribution = "";

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
        setAttribution(obj.getAttribution());
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
        String metatags = values.length > 19 ? values[19] : null;
        String attribution = values.length > 20 ? values[20] : null;

        // Remove feeds path from image
        if(image.indexOf("/") != -1)
            image = image.substring(image.lastIndexOf("/")+1);

        setCode(code);
        setId(Integer.parseInt(id.substring(id.lastIndexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setTitle(title);
        setSummary(summary);
        setDescription(description);
        setTags(tags);
        setImage(image);
        setImageText(imageText);
        setAuthor(author);
        setAuthorUrl(authorUrl);
        setAuthorEmail(authorEmail);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
        setPromoted(promote != null && promote.equals("1"));
        setNewsletter(newsletter != null && newsletter.equals("1"));
        setFeatured(featured != null && featured.equals("1"));
        setSponsored(sponsored != null && sponsored.equals("1"));
        setAttribution(attribution);

        // Set the metatag fields
        if(metatags != null && metatags.length() > 0)
        {
            JSONObject obj = new JSONObject(metatags);
            setMetaTitle(obj.optString(Metatag.TITLE.value()));
            setMetaDescription(obj.optString(Metatag.DESCRIPTION.value()));
            setCanonicalUrl(obj.optString(Metatag.CANONICAL_URL.value()));
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        ret.putOpt(POST_TYPE.value(), getPostType());
        ret.putOpt(DESCRIPTION.value(), EmojiParser.parseToAliases(getDescription()));
        ret.putOpt(URL.value(), getUrlAlias());
        ret.putOpt(CANONICAL_URL.value(), getCanonicalUrl());
        ret.putOpt(IMAGE.value(), getImage());
        ret.putOpt(IMAGE_SOURCE.value(), getImageSource());
        ret.putOpt(IMAGE_TEXT.value(), getImageText());
        ret.putOpt(AUTHOR.value(), getAuthor());
        ret.putOpt(AUTHOR_URL.value(), getAuthorUrl());
        ret.putOpt(META_TITLE.value(), getMetaTitle());
        ret.putOpt(META_DESCRIPTION.value(), getMetaDescription());
        ret.putOpt(ATTRIBUTION.value(), getAttribution());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);

        setPostType(obj.optString(POST_TYPE.value()));
        setDescription(EmojiParser.parseToUnicode(obj.optString(DESCRIPTION.value())));
        setUrlAlias(obj.optString(URL.value()));
        setCanonicalUrl(obj.optString(CANONICAL_URL.value()));
        setImage(obj.optString(IMAGE.value()));
        setImageSource(obj.optString(IMAGE_SOURCE.value()));
        setImageText(obj.optString(IMAGE_TEXT.value()));
        setAuthor(obj.optString(AUTHOR.value()));
        if(obj.has(AUTHOR_URL.value()))
            setAuthorUrl(obj.optString(AUTHOR_URL.value()));
        else
            setAuthorUrl(obj.optString(AUTHOR_LINK.value())); // deprecated
        setMetaTitle(obj.optString(META_TITLE.value()));
        setMetaDescription(obj.optString(META_DESCRIPTION.value()));
        setAttribution(obj.optString(ATTRIBUTION.value()));
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
        ret.put(AUTHOR_URL, getAuthorUrl());
        ret.put(URL, getUrlAlias());
        ret.put(IMAGE, getImage());
        ret.put(IMAGE_TEXT, getImageText());
        ret.put(METATAGS, getMetatags());
        ret.put(ATTRIBUTION, getAttribution());

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
        post.setPublishedDateAsString(TimeUtils.toStringUTC(SessionId.now(), config.getField(PUBLISHED_DATE)));

        return post;
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite, PostConfig config)
    {
        super.init(organisation, organisationSite, config);

        setAuthorEmail(organisation.getEmail());

        if(organisation != null)
        {
            setImageText(organisation.getImageText());
        }

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

        setPostType(config.getField(POST_TYPE, ""));
    }

    /**
     * Prepare the fields in the content using the given configuration.
     */
    public void prepare(PostConfig config, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getField(PUBLISHED_DATE)));

        formatSummary(config, false, debug);

        PostType guessed = PostType.guess(getTagsList());
        if(guessed != null)
            setPostType(guessed);

        // Use the default author if a content author wasn't found
        if(config.hasField(AUTHOR) && getAuthor().length() == 0)
            setAuthor(config.getField(AUTHOR));

        // Use the default author URL if a content author URL wasn't found
        if(config.hasField(AUTHOR_URL) && getAuthorUrl().length() == 0)
            setAuthorUrl(config.getField(AUTHOR_URL));

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
            setImageText(new String(obj.getImageText() != null ? obj.getImageText() : ""));
            setAuthor(new String(obj.getAuthor() != null ? obj.getAuthor() : ""));
            setAuthorUrl(new String(obj.getAuthorUrl() != null ? obj.getAuthorUrl() : ""));
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
     * Format the post body and summary.
     */
    public void formatSummary(PostConfig config, boolean force, boolean debug)
    {
        if(hasDescription())
        {
            BodyParser parser = new BodyParser(getDescription(), debug);
            if(parser.converted())
                setDescription(parser.formatBody());
            if(getSummary().length() == 0 || force)
                setSummary(parser.formatSummary(getType()));
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
     * Returns <CODE>true</CODE> if the post description has been set.
     */
    public boolean hasDescription()
    {
        return getDetails().hasDescription();
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
     */
    @Override
    public boolean hasImageSource()
    {
        return getDetails().hasImageSource();
    }

    /**
     * Returns the post image text.
     */
    @Override
    public String getImageText()
    {
        return getDetails().getImageText();
    }

    /**
     * Sets the image text.
     */
    public void setImageText(String imageText)
    {
        getDetails().setImageText(imageText);
    }

    /**
     * Returns <CODE>true</CODE> if the post image text has been set.
     */
    @Override
    public boolean hasImageText()
    {
        return getDetails().hasImageText();
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
     * Returns <CODE>true</CODE> if the post url has been set.
     */
    public boolean hasUrl()
    {
        return getUrl() != null && getUrl().length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if this content has metatags to set.
     */
    @Override
    public boolean hasMetatags()
    {
        return hasMetaTitle() || hasMetaDescription() || hasCanonicalUrl();
    }

    /**
     * Add the metatags for this content to the given JSON object.
     */
    @Override
    protected void addMetatags(JSONObject obj)
    {
        if(hasMetaTitle())
        {
            obj.putOpt(Metatag.TITLE.value(),
                String.format("%s | [site:name]", getMetaTitle()));
            obj.putOpt(Metatag.OG_TITLE.value(),
                getMetaTitle());
        }

        if(hasMetaDescription())
        {
            obj.putOpt(Metatag.DESCRIPTION.value(),
                getMetaDescription());
            obj.putOpt(Metatag.OG_DESCRIPTION.value(),
                getMetaDescription());
        }

        if(hasCanonicalUrl())
        {
            obj.putOpt(Metatag.CANONICAL_URL.value(),
                getCanonicalUrl());
        }
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
     * Returns the author URL of the post.
     */
    public String getAuthorUrl()
    {
        return getDetails().getAuthorUrl();
    }

    /**
     * Sets the author URL of the post.
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

    /**
     * Returns the attribution of the post.
     */
    public String getAttribution()
    {
        return attribution;
    }

    /**
     * Sets the attribution of the post.
     */
    public void setAttribution(String attribution)
    {
        this.attribution = attribution;
    }

    /**
     * Sets the attribution of the post using the image filename.
     */
    public void setAttribution()
    {
        if(!hasAttribution())
        {
            ImageProvider provider = ImageProviders.getByFilename(getImage());
            if(provider != null && provider.getType() == ImageProviderType.ATTRIBUTED)
                setAttribution(provider.getAttribution());
        }
    }

    /**
     * Returns <CODE>true</CODE> if the attribution has been set.
     */
    public boolean hasAttribution()
    {
        return getAttribution() != null && getAttribution().length() > 0;
    }
}