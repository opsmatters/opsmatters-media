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

package com.opsmatters.media.config.content;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Contains a list of fields for output.
 *
 * @author Gerald Curley (opsmatters)
 */
public class Fields extends LinkedHashMap<String,String>
{
    public static final String ID = "id";
    public static final String PUBDATE = "pubdate";
    public static final String CODE = "code";
    public static final String ORGANISATION = "organisation";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String DESCRIPTION = "description";
    public static final String PUBLISHED_DATE = "published-date";
    public static final String START_DATE = "start-date";
    public static final String START_TIME = "start-time";
    public static final String END_DATE = "end-date";
    public static final String TIMEZONE = "timezone";
    public static final String LOCATION = "location";
    public static final String URL = "url";
    public static final String UUID = "uuid";
    public static final String DURATION = "duration";
    public static final String VIDEO_ID = "video-id";
    public static final String VIDEO_URL = "video-url";
    public static final String PROVIDER = "provider";
    public static final String CHANNEL_ID = "channel-id";
    public static final String CHANNEL_URL = "channel-url";
    public static final String CHANNEL_TITLE = "channel-title";
    public static final String VIDEO_TYPE = "video-type";
    public static final String ACTIVITY_TYPE = "activity-type";
    public static final String IMAGE = "image";
    public static final String IMAGE_SOURCE = "image-source";
    public static final String IMAGE_TEXT = "image-text";
    public static final String THUMBNAIL = "thumbnail";
    public static final String THUMBNAIL_TEXT = "thumbnail-text";
    public static final String TAGS = "tags";
    public static final String LINK = "link";
    public static final String LINK_TEXT = "link-text";
    public static final String CREATOR_LINK = "creator-link";
    public static final String EMAIL = "email";
    public static final String WEBSITE = "website";
    public static final String AUTHOR = "author";
    public static final String AUTHOR_LINK = "author-link";
    public static final String PUBLISHED = "published";
    public static final String PROMOTE = "promote";
    public static final String NEWSLETTER = "newsletter";
    public static final String FEATURED = "featured";
    public static final String SPONSORED = "sponsored";
    public static final String CREATED_BY = "created-by";
    public static final String DEPLOYED = "deployed";
    public static final String SPONSOR = "sponsor";
    public static final String TABS = "tabs";
    public static final String CONTENT = "content";
    public static final String ADVERT = "advert";
    public static final String FOUNDED = "founded";
    public static final String STOCK_SYMBOL = "stock-symbol";
    public static final String FACEBOOK = "facebook";
    public static final String FACEBOOK_USERNAME = "facebook-username";
    public static final String TWITTER = "twitter";
    public static final String TWITTER_USERNAME = "twitter-username";
    public static final String LINKEDIN = "linkedin";
    public static final String INSTAGRAM = "instagram";
    public static final String YOUTUBE = "youtube";
    public static final String VIMEO = "vimeo";
    public static final String PROJECTS = "projects";
    public static final String GITHUB = "github";
    public static final String TOOLS = "tools";
    public static final String ALTERNATIVES = "alternatives";
    public static final String FEATURES = "features";
    public static final String SOCIAL = "social";
    public static final String HASHTAG = "hashtag";
    public static final String HASHTAGS = "hashtags";
    public static final String DOWNLOAD = "download";
    public static final String DOWNLOAD_TEXT = "download-text";
    public static final String PRICING = "pricing";
    public static final String BADGES = "badges";
    public static final String LINKS = "links";
    public static final String LICENSE = "license";
    public static final String CANONICAL_URL = "canonical-url";
    public static final String METATAGS = "metatags";
    public static final String IMAGES_PATH = "images-path";
    public static final String LOGOS_PATH = "logos-path";

    /**
     * Default constructor.
     */
    public Fields()
    {
    }

    /**
     * Copy constructor.
     */
    public Fields(Fields fields)
    {
        putAll(fields);
    }

    /**
     * Add the fields from the given sources without overwriting existing fields.
     */
    public Fields add(FieldSource... sources)
    {
        if(sources != null)
        {
            for(FieldSource source : sources)
            {
                if(source != null)
                {
                    Fields fields = source.getFields();
                    if(fields != null)
                    {
                        for(String key : fields.keySet())
                        {
                            String value = get(key);
                            if(value == null || value.length() == 0)
                                put(key, fields.get(key));
                        }
                    }
                }
            }
        }

        return this;
    }

    /**
     * Returns the mapping with the given key.
     * <p>
     * Returns the fallback if no mapping is found.
     */
    public String get(String key, String fallback)
    {
        String ret = get(key);
        if(ret == null)
            ret = fallback;
        return ret;
    }

    /**
     * Copies all of the mappings from the specified map to this map (optional operation).
     */
    public void putAll(Map<? extends String,? extends String> map)
    {
        if(map != null)
            super.putAll(map);
    }
}