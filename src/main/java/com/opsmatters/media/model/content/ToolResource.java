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
import com.opsmatters.media.config.content.ToolConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a tool resource.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ToolResource extends Resource
{
    private ToolDetails details = new ToolDetails();
    private String downloadText = "";

    /**
     * Default constructor.
     */
    public ToolResource()
    {
        setContentDetails(details);
    }

    /**
     * Constructor that takes a tool resource.
     */
    public ToolResource(ToolResource obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a tool.
     */
    public ToolResource(Site site, String code, ToolDetails obj)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
        setToolDetails(obj);
    }

    /**
     * Constructor that takes a tool summary.
     */
    public ToolResource(Site site, String code, ToolSummary obj)
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
    public void copyAttributes(ToolResource obj)
    {
        super.copyAttributes(obj);

        setToolDetails(obj.getToolDetails());
        setDownloadText(new String(obj.getDownloadText() != null ? obj.getDownloadText() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public ToolResource(Site site, String code, String[] values) throws DateTimeParseException
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
    public ToolResource(JSONObject obj)
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

        setWebsite(obj.optString(Fields.WEBSITE));
        setDownload(obj.optString(Fields.DOWNLOAD));
        setDownloadText(obj.optString(Fields.DOWNLOAD_TEXT));
        setPricing(obj.optString(Fields.PRICING));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(Fields.WEBSITE, getWebsite());
        ret.putOpt(Fields.DOWNLOAD, getDownload());
        ret.putOpt(Fields.DOWNLOAD_TEXT, getDownloadText());
        ret.putOpt(Fields.PRICING, getPricing());

        return ret;
    }

    /**
     * Returns the set of output fields from the resource.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.WEBSITE, getWebsite());
        ret.put(Fields.DOWNLOAD, getDownload());
        ret.put(Fields.DOWNLOAD_TEXT, getDownloadText());
        ret.put(Fields.PRICING, getPricing());

        return ret;
    }

    /**
     * Returns a new resource with defaults.
     */
    public static ToolResource getDefault(Site site, ToolConfiguration config) throws DateTimeParseException
    {
        ToolResource resource = new ToolResource();

        resource.init();
        resource.setSiteId(site.getId());
        resource.setTitle("New Tool");
        resource.setDescription(StringUtils.EMPTY);
        resource.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));

        return resource;
    }

    /**
     * Use the given organisation to set defaults for the resource.
     */
    public void init(Organisation organisation)
    {
        setWebsite(organisation.getWebsite());
    }

    /**
     * Use the given configuration to set defaults for the resource.
     */
    public void init(ToolConfiguration config)
    {
        super.init(config);

        setFeatures(config.getField(Fields.FEATURES, ""));
        setLinkText(config.getField(Fields.LINK_TEXT, ""));
        setDownloadText(config.getField(Fields.DOWNLOAD_TEXT, ""));
        setPricing(config.getField(Fields.PRICING, ""));

        String promote = config.getField(Fields.PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the resource using the given configuration.
     */
    public void prepare(ToolConfiguration config, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));

        BodyParser parser = new BodyParser(getDescription(), debug);
        if(parser.converted())
            setDescription(parser.formatBody());
        setSummary(parser.formatSummary(config.getSummary()));
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
     * Returns the tool details.
     */
    public ToolDetails getToolDetails()
    {
        return details;
    }

    /**
     * Sets the tool details.
     */
    public void setToolDetails(ToolDetails obj)
    {
        setContentSummary(obj);
        setWebsite(new String(obj.getWebsite() != null ? obj.getWebsite() : ""));
        setDownload(new String(obj.getDownload() != null ? obj.getDownload() : ""));
        setContentDetails(true);
    }

    /**
     * Sets the tool details from a summary.
     */
    public void setContentSummary(ToolSummary obj)
    {
        super.setContentSummary(obj);
        setUrl(new String(obj.getUrl()));
        setPricing(new String(obj.getPricing() != null ? obj.getPricing() : ""));
    }

    /**
     * Returns the tool website.
     */
    public String getWebsite()
    {
        return details.getWebsite();
    }

    /**
     * Sets the tool website.
     */
    public void setWebsite(String website)
    {
        details.setWebsite(website);
    }

    /**
     * Returns <CODE>true</CODE> if the tool website has been set.
     */
    public boolean hasWebsite()
    {
        return details.hasWebsite();
    }

    /**
     * Sets the URL of the resource.
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
        return details.getDownload();
    }

    /**
     * Sets the tool download URL.
     */
    public void setDownload(String download)
    {
        details.setDownload(download);
    }

    /**
     * Returns <CODE>true</CODE> if the tool download URL has been set.
     */
    public boolean hasDownload()
    {
        return details.hasDownload();
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
        return details.getPricing();
    }

    /**
     * Sets the pricing.
     */
    public void setPricing(String pricing)
    {
        details.setPricing(pricing);
    }
}