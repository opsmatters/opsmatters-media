/*
 * Copyright 2024 Gerald Curley
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

import java.time.Instant;
import java.util.Map;
import java.util.List;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.organisation.Organisation;
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
 * Class representing the settings of a content type for an organisation.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentSettings extends BaseEntity
{
    private String code = "";
    private ContentType type;
    private String imageRejects = "";
    private String summaryRejects = "";
    private String config = "";
    private ContentConfig contentConfig;

    /**
     * Default constructor.
     */
    public ContentSettings()
    {
    }

    /**
     * Copy constructor.
     */
    public ContentSettings(ContentSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Constructor that takes an organisation and content type.
     */
    public ContentSettings(Organisation organisation, ContentType type)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(organisation.getCode());
        setType(type);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentSettings obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setType(obj.getType());
            setImageRejects(obj.getImageRejects());
            setSummaryRejects(obj.getSummaryRejects());
            setConfig(obj.getConfig());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(IMAGE_REJECTS.value(), getImageRejects());
        ret.putOpt(SUMMARY_REJECTS.value(), getSummaryRejects());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setImageRejects(obj.optString(IMAGE_REJECTS.value()));
        setSummaryRejects(obj.optString(SUMMARY_REJECTS.value()));
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
     * Returns the content type.
     */
    public ContentType getType()
    {
        return type;
    }

    /**
     * Sets the content type.
     */
    public void setType(String type)
    {
        setType(ContentType.valueOf(type));
    }

    /**
     * Sets the content type.
     */
    public void setType(ContentType type)
    {
        this.type = type;
    }

    /**
     * Returns the list of image filenames to reject.
     */
    public String getImageRejects()
    {
        return imageRejects;
    }

    /**
     * Sets the list of image filenames to reject.
     */
    public void setImageRejects(String imageRejects)
    {
        this.imageRejects = imageRejects;
    }

    /**
     * Returns <CODE>true</CODE> if the reject image filenames have been set.
     */
    public boolean hasImageRejects()
    {
        return imageRejects != null && imageRejects.length() > 0;
    }

    /**
     * Returns the list of image filenames to reject.
     */
    public List<String> getImageRejectsList()
    {
        return StringUtils.toList(imageRejects, "\\n");
    }

    /**
     * Returns the list of summary phrases to reject.
     */
    public String getSummaryRejects()
    {
        return summaryRejects;
    }

    /**
     * Sets the list of summary phrases to reject.
     */
    public void setSummaryRejects(String summaryRejects)
    {
        this.summaryRejects = summaryRejects;
    }

    /**
     * Returns <CODE>true</CODE> if the reject summary phrases have been set.
     */
    public boolean hasSummaryRejects()
    {
        return summaryRejects != null && summaryRejects.length() > 0;
    }

    /**
     * Returns the list of summary phrases to reject.
     */
    public List<String> getSummaryRejectsList()
    {
        return StringUtils.toList(summaryRejects, "\\n");
    }

    /**
     * Returns the config for the content type.
     */
    public String getConfig()
    {
        return config;
    }

    /**
     * Sets the config for the content type.
     */
    public void setConfig(String config)
    {
        this.config = config;

        setContentConfig();
    }

    /**
     * Returns <CODE>true</CODE> if the config for the content type has been configured.
     */
    public boolean hasConfig()
    {
        return config != null && config.length() > 0;
    }

    /**
     * Returns the content config.
     */
    public ContentConfig getContentConfig()
    {
        return contentConfig;
    }

    /**
     * Sets the content config for the content.
     */
    private void setContentConfig()
    {
        contentConfig = null;

        if(hasConfig())
        {
            Map<String, Object> map = new Yaml().load(getConfig());
            if(type == ROUNDUP)
                contentConfig = RoundupPostConfig.builder(getCode()).parse(map).build();
            else if(type == VIDEO)
                contentConfig = VideoConfig.builder(getCode()).parse(map).build();
            else if(type == EVENT)
                contentConfig = EventConfig.builder(getCode()).parse(map).build();
            else if(type == PUBLICATION)
                contentConfig = PublicationConfig.builder(getCode()).parse(map).build();
            else if(type == POST)
                contentConfig = PostConfig.builder(getCode()).parse(map).build();
            else if(type == PROJECT)
                contentConfig = ProjectConfig.builder(getCode()).parse(map).build();
            else if(type == TOOL)
                contentConfig = ToolConfig.builder(getCode()).parse(map).build();
        }
    }

    /**
     * Returns <CODE>true</CODE> if the content config has been configured.
     */
    public boolean hasContentConfig()
    {
        return contentConfig != null;
    }

    /**
     * Returns <CODE>true</CODE> if the roundup config has been set.
     */
    public boolean hasRoundupPostConfig()
    {
        return contentConfig instanceof RoundupPostConfig;
    }

    /**
     * Returns the roundup content config.
     */
    public RoundupPostConfig getRoundupPostConfig()
    {
        return hasRoundupPostConfig() ? (RoundupPostConfig)contentConfig : null;
    }

    /**
     * Returns <CODE>true</CODE> if the video config has been set.
     */
    public boolean hasVideoConfig()
    {
        return contentConfig instanceof VideoConfig;
    }

    /**
     * Returns the video content config.
     */
    public VideoConfig getVideoConfig()
    {
        return hasVideoConfig() ? (VideoConfig)contentConfig : null;
    }

    /**
     * Returns <CODE>true</CODE> if the event config has been set.
     */
    public boolean hasEventConfig()
    {
        return contentConfig instanceof EventConfig;
    }

    /**
     * Returns the event content config.
     */
    public EventConfig getEventConfig()
    {
        return hasEventConfig() ? (EventConfig)contentConfig : null;
    }

    /**
     * Returns <CODE>true</CODE> if the publication config has been set.
     */
    public boolean hasPublicationConfig()
    {
        return contentConfig instanceof PublicationConfig;
    }

    /**
     * Returns the publication content config.
     */
    public PublicationConfig getPublicationConfig()
    {
        return hasPublicationConfig() ? (PublicationConfig)contentConfig : null;
    }

    /**
     * Returns <CODE>true</CODE> if the post config has been set.
     */
    public boolean hasPostConfig()
    {
        return contentConfig instanceof PostConfig;
    }

    /**
     * Returns the post content config.
     */
    public PostConfig getPostConfig()
    {
        return hasPostConfig() ? (PostConfig)contentConfig : null;
    }

    /**
     * Returns <CODE>true</CODE> if the project config has been set.
     */
    public boolean hasProjectConfig()
    {
        return contentConfig instanceof ProjectConfig;
    }

    /**
     * Returns the project content config.
     */
    public ProjectConfig getProjectConfig()
    {
        return hasProjectConfig() ? (ProjectConfig)contentConfig : null;
    }

    /**
     * Returns <CODE>true</CODE> if the tool config has been set.
     */
    public boolean hasToolConfig()
    {
        return contentConfig instanceof ToolConfig;
    }

    /**
     * Returns the tool content config.
     */
    public ToolConfig getToolConfig()
    {
        return hasToolConfig() ? (ToolConfig)contentConfig : null;
    }
}