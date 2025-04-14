/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.model.organisation;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import org.json.JSONObject;
import com.opsmatters.media.model.OwnedEntity;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.FieldName;
import com.opsmatters.media.model.content.FieldSource;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSettings;
import com.opsmatters.media.model.content.video.VideoConfig;
import com.opsmatters.media.model.content.event.EventConfig;
import com.opsmatters.media.model.content.publication.PublicationConfig;
import com.opsmatters.media.model.content.post.PostConfig;
import com.opsmatters.media.model.content.post.RoundupPostConfig;
import com.opsmatters.media.model.content.project.ProjectConfig;
import com.opsmatters.media.model.content.tool.ToolConfig;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;
import static com.opsmatters.media.model.content.ContentType.*;

/**
 * Class representing an organisation.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Organisation extends OwnedEntity implements FieldSource
{
    public static final String DEFAULT_CODE = "???";

    private String code = "";
    private String name = "";
    private String website = "";
    private String email = "";
    private String handle = "";
    private String hashtag = "";
    private String tracking = "";
    private String filePrefix = "";
    private String imagePrefix = "";
    private String imageText = "";
    private Map<ContentType,ContentSettings> settings = new HashMap<ContentType,ContentSettings>();

    /**
     * Default constructor.
     */
    public Organisation()
    {
    }

    /**
     * Copy constructor.
     */
    public Organisation(Organisation obj)
    {
        copyAttributes(obj);
    }

    /**
     * Returns the name of the organisation.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Organisation obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setName(obj.getName());
            setWebsite(new String(obj.getWebsite() != null ? obj.getWebsite() : ""));
            setEmail(new String(obj.getEmail() != null ? obj.getEmail() : ""));
            setHandle(new String(obj.getHandle() != null ? obj.getHandle() : ""));
            setHashtag(new String(obj.getHashtag() != null ? obj.getHashtag() : ""));
            setTracking(new String(obj.getTracking() != null ? obj.getTracking() : ""));
            setFilePrefix(new String(obj.getFilePrefix() != null ? obj.getFilePrefix() : ""));
            setImagePrefix(new String(obj.getImagePrefix() != null ? obj.getImagePrefix() : ""));
            setImageText(new String(obj.getImageText() != null ? obj.getImageText() : ""));
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(WEBSITE.value(), getWebsite());
        ret.putOpt(EMAIL.value(), getEmail());
        ret.putOpt(HASHTAG.value(), getHashtag());
        ret.putOpt(TRACKING.value(), getTracking());
        ret.putOpt(HANDLE.value(), getHandle());
        ret.putOpt(FILE_PREFIX.value(), getFilePrefix());
        ret.putOpt(IMAGE_PREFIX.value(), getImagePrefix());
        ret.putOpt(IMAGE_TEXT.value(), getImageText());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setWebsite(obj.optString(WEBSITE.value()));
        setEmail(obj.optString(EMAIL.value()));
        setHashtag(obj.optString(HASHTAG.value()));
        setTracking(obj.optString(TRACKING.value()));
        setHandle(obj.optString(HANDLE.value()));
        setFilePrefix(obj.optString(FILE_PREFIX.value()));
        setImagePrefix(obj.optString(IMAGE_PREFIX.value()));
        setImageText(obj.optString(IMAGE_TEXT.value()));
    }

    /**
     * Returns the fields required by other objects.
     */
    public FieldMap getFields()
    {
        FieldMap ret = new FieldMap();

        ret.put(WEBSITE, getWebsite());
        ret.put(EMAIL, getEmail());
        ret.put(HANDLE, getHandle());
        ret.put(HASHTAG, getHashtag());
        ret.put(TRACKING, getTracking());
        ret.put(IMAGE_TEXT, getImageText());

        return ret;
    }

    /**
     * Returns a new organisation with defaults.
     */
    public static Organisation getDefault()
    {
        Organisation organisation = new Organisation();

        organisation.setId(StringUtils.getUUID(null));
        organisation.setCode(DEFAULT_CODE);
        organisation.setName("New Organisation");
        organisation.setCreatedDate(Instant.now());

        return organisation;
    }

    /**
     * Returns the organisation code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the organisation code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation code has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
    }

    /**
     * Returns the organisation name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the name normalized for a URL context.
     */
    public String getNormalisedName()
    {
        String ret = getName();
        if(ret != null)
        {
            ret = ret.toLowerCase()
                .replaceAll(" ","-")
                .replaceAll("\\.","-")
                .replaceAll("&|:","");
        }

        return ret;
    }

    /**
     * Sets the organisation name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation name has been set.
     */
    public boolean hasName()
    {
        return name != null && name.length() > 0;
    }

    /**
     * Returns the URL for the organisation.
     */
    public String getUrl(String basePath)
    {
        return String.format("%s/organisations/%s",
            basePath, getNormalisedName());
    }

    /**
     * Returns the organisation's website.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * Sets the organisation's website.
     */
    public void setWebsite(String website)
    {
        this.website = website;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a website address.
     */
    public boolean hasWebsite()
    {
        return website != null && website.length() > 0;
    }

    /**
     * Returns the organisation's email.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the organisation's email.
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Returns the organisation's social handle.
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Sets the organisation's social handle.
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a social handle.
     */
    public boolean hasHandle()
    {
        return handle != null && handle.length() > 0;
    }

    /**
     * Returns the organisation's social hashtag.
     */
    public String getHashtag()
    {
        return hashtag;
    }

    /**
     * Sets the organisation's social hashtag.
     */
    public void setHashtag(String hashtag)
    {
        this.hashtag = hashtag;
    }

    /**
     * Returns the organisation's tracking string.
     */
    public String getTracking()
    {
        return tracking;
    }

    /**
     * Sets the organisation's tracking string.
     */
    public void setTracking(String tracking)
    {
        this.tracking = tracking;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a tracking string.
     */
    public boolean hasTracking()
    {
        return tracking != null && tracking.length() > 0;
    }

    /**
     * Returns the file prefix.
     */
    public String getFilePrefix()
    {
        return filePrefix;
    }

    /**
     * Sets the file prefix.
     */
    public void setFilePrefix(String filePrefix)
    {
        this.filePrefix = filePrefix;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a file prefix.
     */
    public boolean hasFilePrefix()
    {
        return filePrefix != null && filePrefix.length() > 0;
    }

    /**
     * Returns the image prefix.
     */
    public String getImagePrefix()
    {
        return imagePrefix;
    }

    /**
     * Sets the image prefix.
     */
    public void setImagePrefix(String imagePrefix)
    {
        this.imagePrefix = imagePrefix;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has an image prefix.
     */
    public boolean hasImagePrefix()
    {
        return imagePrefix != null && imagePrefix.length() > 0;
    }

    /**
     * Returns the organisation's image text.
     */
    public String getImageText()
    {
        return imageText;
    }

    /**
     * Sets the organisation's image text.
     */
    public void setImageText(String imageText)
    {
        this.imageText = imageText;
    }

    /**
     * Returns the content settings.
     */
    public Map<ContentType,ContentSettings> getSettings()
    {
        return settings;
    }

    /**
     * Returns a list of the content settings.
     */
    public List<ContentSettings> getSettingsList()
    {
        return new ArrayList<ContentSettings>(settings.values());
    }

    /**
     * Returns <CODE>true</CODE>if the organisation has content settings.
     */
    public boolean hasSettings()
    {
        return settings != null && settings.size() > 0;
    }

    /**
     * Returns the number of content settings.
     */
    public int numSettings()
    {
        return settings.size();
    }

    /**
     * Returns the settings for the given content type.
     */
    public ContentSettings getSettings(ContentType type)
    {
        return settings.get(type);
    }

    /**
     * Adds the given content settings.
     */
    public void setSettings(ContentSettings settings)
    {
        if(settings.hasContentConfig())
            settings.getContentConfig().setOrganisation(this);
        this.settings.put(settings.getType(), settings);
    }

    /**
     * Removes the given content settings.
     */
    public void removeSettings(ContentSettings settings)
    {
        this.settings.remove(settings.getType());
    }

    /**
     * Sets the content settings.
     */
    public void setSettings(Map<ContentType,ContentSettings> map)
    {
        this.settings.clear();
        for(ContentSettings settings : map.values())
            setSettings(settings);
    }

    /**
     * Clears the content settings.
     */
    public void clearSettings()
    {
        settings.clear();
    }

    /**
     * Returns <CODE>true</CODE> if the config of the given type has been set.
     */
    public boolean hasContentConfig(ContentType type)
    {
        ContentSettings settings = getSettings(type);
        return settings != null && settings.hasContentConfig();
    }

    /**
     * Returns <CODE>true</CODE> if the roundup config has been set.
     */
    public boolean hasRoundupPostConfig()
    {
        return hasContentConfig(ROUNDUP);
    }

    /**
     * Returns the roundup config.
     */
    public RoundupPostConfig getRoundupPostConfig()
    {
        return hasRoundupPostConfig() ? getSettings(ROUNDUP).getRoundupPostConfig() : null;
    }

    /**
     * Returns <CODE>true</CODE> if the video config has been set.
     */
    public boolean hasVideoConfig()
    {
        return hasContentConfig(VIDEO);
    }

    /**
     * Returns the video config.
     */
    public VideoConfig getVideoConfig()
    {
        return hasVideoConfig() ? getSettings(VIDEO).getVideoConfig() : null;
    }

    /**
     * Returns <CODE>true</CODE> if the event config has been set.
     */
    public boolean hasEventConfig()
    {
        return hasContentConfig(EVENT);
    }

    /**
     * Returns the event config.
     */
    public EventConfig getEventConfig()
    {
        return hasEventConfig() ? getSettings(EVENT).getEventConfig() : null;
    }

    /**
     * Returns <CODE>true</CODE> if the publication config has been set.
     */
    public boolean hasPublicationConfig()
    {
        return hasContentConfig(PUBLICATION);
    }

    /**
     * Returns the publication config.
     */
    public PublicationConfig getPublicationConfig()
    {
        return hasPublicationConfig() ? getSettings(PUBLICATION).getPublicationConfig() : null;
    }

    /**
     * Returns <CODE>true</CODE> if the post config has been set.
     */
    public boolean hasPostConfig()
    {
        return hasContentConfig(POST);
    }

    /**
     * Returns the post config.
     */
    public PostConfig getPostConfig()
    {
        return hasPostConfig() ? getSettings(POST).getPostConfig() : null;
    }

    /**
     * Returns <CODE>true</CODE> if the project config has been set.
     */
    public boolean hasProjectConfig()
    {
        return hasContentConfig(PROJECT);
    }

    /**
     * Returns the project config.
     */
    public ProjectConfig getProjectConfig()
    {
        return hasProjectConfig() ? getSettings(PROJECT).getProjectConfig() : null;
    }

    /**
     * Returns <CODE>true</CODE> if the tool config has been set.
     */
    public boolean hasToolConfig()
    {
        return hasContentConfig(TOOL);
    }

    /**
     * Returns the tool config.
     */
    public ToolConfig getToolConfig()
    {
        return hasToolConfig() ? getSettings(TOOL).getToolConfig() : null;
    }
}