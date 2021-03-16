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
import com.opsmatters.media.config.content.WhitePaperConfiguration;
import com.opsmatters.media.model.site.Site;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a white paper resource.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class WhitePaperResource extends PublicationResource
{
    /**
     * Default constructor.
     */
    public WhitePaperResource()
    {
    }

    /**
     * Constructor that takes a white paper resource.
     */
    public WhitePaperResource(WhitePaperResource obj)
    {
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a publication.
     */
    public WhitePaperResource(Site site, String code, PublicationDetails obj)
    {
        init();
        setSiteId(site.getId());
        setCode(code);
        setPublicationDetails(obj);
    }

    /**
     * Constructor that takes a publication summary.
     */
    public WhitePaperResource(Site site, String code, PublicationSummary obj)
    {
        init();
        setSiteId(site.getId());
        setCode(code);
        setContentSummary(obj);
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public WhitePaperResource(Site site, String code, String[] values) throws DateTimeParseException
    {
        super(site, code, values);
    }

    /**
     * Constructor that takes a JSON object.
     */
    public WhitePaperResource(JSONObject obj)
    {
        fromJson(obj);
    }

    /**
     * Returns a new resource with defaults.
     */
    public static WhitePaperResource getDefault(Site site, WhitePaperConfiguration config) throws DateTimeParseException
    {
        WhitePaperResource resource = new WhitePaperResource();

        resource.init();
        resource.setSiteId(site.getId());
        resource.setTitle("New White Paper");
        resource.setDescription(StringUtils.EMPTY);
        resource.setImagePrefix(config.getImagePrefix());
        resource.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));

        return resource;
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.WHITE_PAPER;
    }
}