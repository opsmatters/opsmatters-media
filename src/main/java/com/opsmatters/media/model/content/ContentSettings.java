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
import java.util.List;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.content.crawler.CrawlerTarget;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.CrawlerVideoChannel;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;

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
    private CrawlerTarget crawlerTarget;

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
        setCreatedDate(organisation.getCreatedDate());
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
     * Returns the config for the content.
     */
    public String getConfig()
    {
        return config;
    }

    /**
     * Sets the config for the content.
     */
    public void setConfig(String config)
    {
        this.config = config;

        setCrawlerTarget();
    }

    /**
     * Returns <CODE>true</CODE> if the config for the content has been configured.
     */
    public boolean hasConfig()
    {
        return config != null && config.length() > 0;
    }

    /**
     * Returns the crawler page for the content.
     */
    public CrawlerWebPage getCrawlerPage()
    {
        return hasCrawlerPage() ? (CrawlerWebPage)crawlerTarget : null;
    }

    /**
     * Returns the crawler video channel for the content.
     */
    public CrawlerVideoChannel getCrawlerVideoChannel()
    {
        return hasCrawlerChannel() ? (CrawlerVideoChannel)crawlerTarget : null;
    }

    /**
     * Sets the crawler target for the content.
     */
    private void setCrawlerTarget()
    {
        crawlerTarget = null;

        if(hasConfig())
        {
            if(type == ContentType.VIDEO)
                crawlerTarget = CrawlerVideoChannel.builder(getCode())
                    .parse(new Yaml().load(getConfig()))
                    .build();
            else
                crawlerTarget = CrawlerWebPage.builder(getCode())
                    .parse(new Yaml().load(getConfig()))
                    .build();
        }
    }

    /**
     * Returns <CODE>true</CODE> if the crawler page for the content has been configured.
     */
    public boolean hasCrawlerPage()
    {
        return crawlerTarget instanceof CrawlerWebPage;
    }

    /**
     * Returns <CODE>true</CODE> if the crawler channel for the content has been configured.
     */
    public boolean hasCrawlerChannel()
    {
        return crawlerTarget instanceof CrawlerVideoChannel;
    }
}