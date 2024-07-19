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
package com.opsmatters.media.model.content.tool;

import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Resource;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing a tool.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Tool extends Resource<ToolDetails>
{
    private String downloadText = "";

    /**
     * Default constructor.
     */
    public Tool()
    {
        setDetails(new ToolDetails());
    }

    /**
     * Constructor that takes a tool.
     */
    public Tool(Tool obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a site and code.
     */
    public Tool(Site site, String code)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Tool obj)
    {
        super.copyAttributes(obj);
        setContentDetails(obj.getDetails());
        setDownloadText(new String(obj.getDownloadText() != null ? obj.getDownloadText() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public Tool(Site site, String code, String[] values) throws DateTimeParseException
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
        String website = values[6];
        String url = values[7];
        String linkText = values[8];
        String download = values[9];
        String downloadText = values[10];
        String features = values[11];
        String pricing = values[12];
        String thumbnail = values[13];
        String thumbnailText = values[14];
        String thumbnailTitle = values[15];
        String createdBy = values[16];
        String published = values[17];
        String promote = values[18];

        setCode(code);
        setId(Integer.parseInt(id.substring(id.lastIndexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setTitle(title);
        setSummary(summary);
        setDescription(description);
        setWebsite(website);
        setUrl(url);
        setLinkText(linkText);
        setDownload(download);
        setDownloadText(downloadText);
        setFeatures(features);
        setPricing(pricing);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
        setPromoted(promote != null && promote.equals("1"));
    }

    /**
     * Constructor that takes a JSON object.
     */
    public Tool(JSONObject obj)
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

        setWebsite(obj.optString(WEBSITE.value()));
        setDownload(obj.optString(DOWNLOAD.value()));
        setDownloadText(obj.optString(DOWNLOAD_TEXT.value()));
        setPricing(obj.optString(PRICING.value()));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(WEBSITE.value(), getWebsite());
        ret.putOpt(DOWNLOAD.value(), getDownload());
        ret.putOpt(DOWNLOAD_TEXT.value(), getDownloadText());
        ret.putOpt(PRICING.value(), getPricing());

        return ret;
    }

    /**
     * Returns the set of output fields from the tool.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(WEBSITE, getWebsite());
        ret.put(DOWNLOAD, getDownload());
        ret.put(DOWNLOAD_TEXT, getDownloadText());
        ret.put(PRICING, getPricing());

        return ret;
    }

    /**
     * Returns a new tool with defaults.
     */
    public static Tool getDefault(Organisation organisation, OrganisationSite organisationSite, ToolConfig config)
        throws DateTimeParseException
    {
        Tool tool = new Tool();

        tool.init();
        tool.setSiteId(organisationSite.getSiteId());
        tool.setTitle("New Tool");
        tool.setDescription(StringUtils.EMPTY);
        tool.setPublishedDateAsString(TimeUtils.toStringUTC(config.getField(PUBLISHED_DATE)));

        return tool;
    }

    /**
     * Use the given configuration to set defaults for the tool.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite, ToolConfig config)
    {
        super.init(organisation, organisationSite, config);

        setWebsite(organisation.getWebsite());

        setLinkText(config.getField(LINK_TEXT, ""));
        setDownloadText(config.getField(DOWNLOAD_TEXT, ""));
        setPricing(config.getField(PRICING, ""));

        String promote = config.getField(PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the tool using the given configuration.
     */
    public void prepare(ToolConfig config, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getField(PUBLISHED_DATE)));

        formatSummary(config, false, debug);
    }

    /**
     * Format the tool body and summary.
     */
    public void formatSummary(ToolConfig config, boolean force, boolean debug)
    {
        if(hasDescription())
        {
            BodyParser parser = new BodyParser(getDescription(), debug);
            if(parser.converted())
                setDescription(parser.formatBody());
            if(getSummary().length() == 0 || force)
                setSummary(parser.formatSummary(config.getSummary()));
        }
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.TOOL;
    }

    /**
     * Sets the tool details from a teaser.
     */
    @Override
    public void setTeaserDetails(ToolDetails obj)
    {
        super.setTeaserDetails(obj);

        if(obj != null)
        {
            setUrl(new String(obj.getUrl()));
            setPricing(new String(obj.getPricing() != null ? obj.getPricing() : ""));
        }
    }

    /**
     * Sets the tool details.
     */
    @Override
    public void setContentDetails(ToolDetails obj)
    {
        if(obj != null)
        {
            setTeaserDetails(obj);
            setWebsite(new String(obj.getWebsite() != null ? obj.getWebsite() : ""));
            setDownload(new String(obj.getDownload() != null ? obj.getDownload() : ""));
            setConfigured(true);
        }
    }

    /**
     * Returns the tool website.
     */
    public String getWebsite()
    {
        return getDetails().getWebsite();
    }

    /**
     * Sets the tool website.
     */
    public void setWebsite(String website)
    {
        getDetails().setWebsite(website);
    }

    /**
     * Returns <CODE>true</CODE> if the tool website has been set.
     */
    public boolean hasWebsite()
    {
        return getDetails().hasWebsite();
    }

    /**
     * Sets the URL of the tool.
     */
    @Override
    public void setUrl(String url)
    {
        setUrl(url, true);
    }

    /**
     * Returns the tool download URL.
     */
    public String getDownload()
    {
        return getDetails().getDownload();
    }

    /**
     * Sets the tool download URL.
     */
    public void setDownload(String download)
    {
        getDetails().setDownload(download);
    }

    /**
     * Returns <CODE>true</CODE> if the tool download URL has been set.
     */
    public boolean hasDownload()
    {
        return getDetails().hasDownload();
    }

    /**
     * Returns the download text.
     */
    public String getDownloadText()
    {
        return downloadText;
    }

    /**
     * Sets the download text.
     */
    public void setDownloadText(String downloadText)
    {
        this.downloadText = downloadText;
    }

    /**
     * Returns the pricing.
     */
    public String getPricing()
    {
        return getDetails().getPricing();
    }

    /**
     * Sets the pricing.
     */
    public void setPricing(String pricing)
    {
        getDetails().setPricing(pricing);
    }
}