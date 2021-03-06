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

import java.util.List;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.config.content.PublicationConfiguration;
import com.opsmatters.media.config.content.WebPageConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a publication resource.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class PublicationResource extends Resource
{
    private PublicationDetails details = new PublicationDetails();
    private String tags = "";
    private String creatorEmail = "";

    /**
     * Default constructor.
     */
    public PublicationResource()
    {
        setContentDetails(details);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PublicationResource obj)
    {
        super.copyAttributes(obj);

        setPublicationDetails(obj.getPublicationDetails());
        setTags(new String(obj.getTags() != null ? obj.getTags() : ""));
        setCreatorEmail(new String(obj.getCreatorEmail() != null ? obj.getCreatorEmail() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public PublicationResource(Site site, String code, String[] values) throws DateTimeParseException
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
        String tags = values[8];
        String image = values[9];
        String imageText = values[10];
        String imageTitle = values[11];
        String thumbnail = values[12];
        String thumbnailText = values[13];
        String thumbnailTitle = values[14];
        String creator = values[15];
        String creatorEmail = values[16];
        String createdBy = values[17];
        String published = values[18];
        String promote = values[19];
        String canonicalUrl = values.length > 20 ? values[20] : "";

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
        setTags(tags);
        setCreatorEmail(creatorEmail);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
        setPromoted(promote != null && promote.equals("1"));
        setCanonicalUrl(canonicalUrl);
    }

    /**
     * Initialise this object using a JSON object.
     */
    public void fromJson(JSONObject obj)
    {
        super.fromJson(obj);

        setTags(obj.optString(Fields.TAGS));
        setCreatorEmail(obj.optString(Fields.EMAIL));
        setCanonicalUrl(obj.optString(Fields.CANONICAL_URL));
        setImage(obj.optString(Fields.IMAGE));
        setImageSource(obj.optString(Fields.IMAGE_SOURCE));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(Fields.TAGS, getTags());
        ret.putOpt(Fields.EMAIL, getCreatorEmail());
        ret.putOpt(Fields.CANONICAL_URL, getCanonicalUrl());
        ret.putOpt(Fields.IMAGE, getImage());
        ret.putOpt(Fields.IMAGE_SOURCE, getImageSource());

        return ret;
    }

    /**
     * Returns the set of output fields from the resource.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.TAGS, getTags());
        ret.put(Fields.EMAIL, getCreatorEmail());
        ret.put(Fields.CANONICAL_URL, getCanonicalUrl());
        ret.put(Fields.IMAGE, getImage());
        ret.put(Fields.METATAGS, getMetatags());

        return ret;
    }

    /**
     * Use the given organisation to set defaults for the resource.
     */
    public void init(Organisation organisation)
    {
        setCreatorEmail(organisation.getEmail());
    }

    /**
     * Use the given configuration to set defaults for the resource.
     */
    public void init(PublicationConfiguration config, WebPageConfiguration page)
    {
        super.init(config);

        setTags(config.getField(Fields.TAGS, ""));
        if(page.hasField(Fields.TAGS))
            setTags(page.getField(Fields.TAGS, ""));

        setLinkText(config.getField(Fields.LINK_TEXT, ""));

        String promote = config.getField(Fields.PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the resource using the given configuration.
     */
    public void prepare(PublicationConfiguration config) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));
        setDescription(FormatUtils.getFormattedDescription(getDescription()));
        setSummary(FormatUtils.getFormattedSummary(getDescription(), config.getSummary()));
    }

    /**
     * Returns the publication details.
     */
    public PublicationDetails getPublicationDetails()
    {
        return details;
    }

    /**
     * Sets the publication details.
     */
    public void setPublicationDetails(PublicationDetails obj)
    {
        setContentSummary(obj);
        setContentDetails(true);
    }

    /**
     * Sets the publication details from a summary.
     */
    public void setContentSummary(PublicationSummary obj)
    {
        super.setContentSummary(obj);
        setUrl(new String(obj.getUrl()), false);
        setImageSource(new String(obj.getImageSource() != null ? obj.getImageSource() : ""));
        setImagePrefix(new String(obj.getImagePrefix() != null ? obj.getImagePrefix() : ""));
        setImage(new String(obj.getImage() != null ? obj.getImage() : ""));
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
    public void setImageFromPath(String path)
    {
        details.setImageFromPath(path);
    }

    /**
     * Returns <CODE>true</CODE> if the publication image has been set.
     */
    @Override
    public boolean hasImage()
    {
        return details.hasImage();
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
     * Returns <CODE>true</CODE> if the publication image source has been set.
     */
    @Override
    public boolean hasImageSource()
    {
        return details.hasImageSource();
    }

    /**
     * Returns the image prefix.
     */
    @Override
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