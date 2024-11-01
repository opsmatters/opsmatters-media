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
package com.opsmatters.media.model.content.publication;

import java.util.List;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Resource;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.LinkText;
import com.opsmatters.media.model.content.ContentSettings;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.field.FieldFilter;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing a publication.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Publication extends Resource<PublicationDetails>
{
    private String publicationType = "";
    private String tags = "";
    private String creatorEmail = "";

    /**
     * Default constructor.
     */
    public Publication()
    {
        setDetails(new PublicationDetails());
    }

    /**
     * Constructor that takes a publication.
     */
    public Publication(Publication obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a site and code.
     */
    public Publication(Site site, String code)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Publication obj)
    {
        super.copyAttributes(obj);
        setContentDetails(obj.getDetails());
        setPublicationType(new String(obj.getPublicationType() != null ? obj.getPublicationType() : ""));
        setTags(new String(obj.getTags() != null ? obj.getTags() : ""));
        setCreatorEmail(new String(obj.getCreatorEmail() != null ? obj.getCreatorEmail() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public Publication(Site site, String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        setSiteId(site.getId());

        // Throw exception if there are not enough columns
        //   - probably means the image, imageText and imageTitle columns are missing
        if(values.length < 20)
            throw new IllegalStateException("Sheet has incorrect number of columns");

        String id = values[0];
        String pubdate = values[1];
        String title = values[2];
        String summary = values[3];
        String description = values[4];
        String url = values[5];
        String linkText = values[6];
        String organisation = values[7];
        String publicationType = values[8];
        String tags = values[9];
        String image = values[10];
        String imageText = values[11];
        String imageTitle = values[12];
        String thumbnail = values[13];
        String thumbnailText = values[14];
        String thumbnailTitle = values[15];
        String creator = values[16];
        String creatorEmail = values[17];
        String createdBy = values[18];
        String published = values[19];
        String promote = values[20];
        String canonicalUrl = values.length > 21 ? values[21] : "";

        // Remove feeds path from image
        if(image.indexOf("/") != -1)
            image = image.substring(image.lastIndexOf("/")+1);

        setCode(code);
        setId(Integer.parseInt(id.substring(id.lastIndexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setTitle(title);
        setSummary(summary);
        setDescription(description);
        setImage(image);
        setUrl(url, false);
        setLinkText(linkText);
        setPublicationType(publicationType);
        setTags(tags);
        setCreatorEmail(creatorEmail);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
        setPromoted(promote != null && promote.equals("1"));
        setCanonicalUrl(canonicalUrl);
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        ret.putOpt(PUBLICATION_TYPE.value(), getPublicationType());
        ret.putOpt(TAGS.value(), getTags());
        ret.putOpt(EMAIL.value(), getCreatorEmail());
        ret.putOpt(CANONICAL_URL.value(), getCanonicalUrl());
        ret.putOpt(IMAGE.value(), getImage());
        ret.putOpt(IMAGE_SOURCE.value(), getImageSource());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);

        setPublicationType(obj.optString(PUBLICATION_TYPE.value()));
        setTags(obj.optString(TAGS.value()));
        setCreatorEmail(obj.optString(EMAIL.value()));
        setCanonicalUrl(obj.optString(CANONICAL_URL.value()));
        setImage(obj.optString(IMAGE.value()));
        setImageSource(obj.optString(IMAGE_SOURCE.value()));
    }

    /**
     * Returns the set of output fields from the publication.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(PUBLICATION_TYPE, getPublicationType());
        ret.put(TAGS, getTags());
        ret.put(EMAIL, getCreatorEmail());
        ret.put(CANONICAL_URL, getCanonicalUrl());
        ret.put(IMAGE, getImage());
        ret.put(METATAGS, getMetatags());

        return ret;
    }

    /**
     * Returns a new publication with defaults.
     */
    public static Publication getDefault(Site site, PublicationConfig config) throws DateTimeParseException
    {
        Publication publication = new Publication();

        publication.init();
        publication.setSiteId(site.getId());
        publication.setTitle("New Publication");
        publication.setDescription(StringUtils.EMPTY);
        publication.setPublishedDateAsString(TimeUtils.toStringUTC(config.getField(PUBLISHED_DATE)));

        return publication;
    }

    /**
     * Use the given configuration to set defaults for the publication.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite,
        PublicationConfig config, CrawlerWebPage page)
    {
        super.init(organisation, organisationSite, config);

        setCreatorEmail(organisation.getEmail());

        if(organisationSite != null)
        {
            ContentSettings settings = organisationSite.getContentSettings(getType());
            if(settings != null)
            {
                setPromoted(settings.isPromoted());
                setTags(settings.getTags());
            }
        }

        if(page.hasField(TAGS))
            setTags(page.getField(TAGS, ""));

        setPublicationType(config.getField(PUBLICATION_TYPE, ""));
        setLinkText(config.getField(LINK_TEXT, ""));
    }

    /**
     * Prepare the fields in the publication using the given configuration.
     */
    public void prepare(PublicationConfig config, CrawlerWebPage page, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getField(PUBLISHED_DATE)));

        formatSummary(config, page.getArticles().getFilters(), false, debug);

        // Attempt to guess the publication type from the text
        String[] texts = new String[] { getTitle(), getDescription(), getUrl() };
        PublicationType guessed = PublicationType.guess(texts);
        if(guessed != null)
            setPublicationType(guessed);
        String linkText = PublicationType.getLinkText(getPublicationType());
        if(linkText != null)
            setLinkText(linkText);
    }

    /**
     * Sets the publication details from a teaser.
     */
    @Override
    public void setTeaserDetails(PublicationDetails obj)
    {
        super.setTeaserDetails(obj);

        if(obj != null)
        {
            setUrl(new String(obj.getUrl()), false);
            setImageSource(new String(obj.getImageSource() != null ? obj.getImageSource() : ""));
            setImage(new String(obj.getImage() != null ? obj.getImage() : ""));
        }
    }

    /**
     * Sets the publication details.
     */
    @Override
    public void setContentDetails(PublicationDetails obj)
    {
        if(obj != null)
        {
            setTeaserDetails(obj);
            setConfigured(true);
        }
    }

    /**
     * Format the publication body and summary.
     */
    public void formatSummary(PublicationConfig config, List<FieldFilter> filters, boolean force, boolean debug)
    {
        if(hasDescription())
        {
            BodyParser parser = new BodyParser(getDescription(), filters, debug);
            if(parser.converted())
                setDescription(parser.formatBody());
            if(getSummary().length() == 0 || force)
                setSummary(parser.formatSummary(getType()));
        }
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.PUBLICATION;
    }

    /**
     * Returns the publication type.
     */
    public String getPublicationType()
    {
        return publicationType;
    }

    /**
     * Sets the publication type.
     */
    public void setPublicationType(String publicationType)
    {
        this.publicationType = publicationType;
    }

    /**
     * Sets the publication type.
     */
    public void setPublicationType(PublicationType publicationType)
    {
        setPublicationType(publicationType.value());
    }

    /**
     * Returns the tags.
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Returns the list of tags.
     */
    public List<String> getTagsList()
    {
        return StringUtils.toList(getTags());
    }

    /**
     * Sets the tags.
     */
    public void setTags(String tags)
    {
        this.tags = tags;
    }

    /**
     * Sets the list of tags.
     */
    public void setTagsList(List<String> tags)
    {
        setTags(StringUtils.fromList(tags));
    }

    /**
     * Returns the creator email.
     */
    public String getCreatorEmail()
    {
        return creatorEmail;
    }

    /**
     * Sets the creator email.
     */
    public void setCreatorEmail(String creatorEmail)
    {
        this.creatorEmail = creatorEmail;
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
     * Returns <CODE>true</CODE> if the publication image has been set.
     */
    @Override
    public boolean hasImage()
    {
        return getDetails().hasImage();
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
     * Returns <CODE>true</CODE> if the publication image source has been set.
     */
    @Override
    public boolean hasImageSource()
    {
        return getDetails().hasImageSource();
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
}