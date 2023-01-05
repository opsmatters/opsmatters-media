/*
 * Copyright 2022 Gerald Curley
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

/**
 * Contains a list of fields names.
 *
 * @author Gerald Curley (opsmatters)
 */
public enum FieldName
{
    ID("id"),
    CODE("code"),
    ORGANISATION("organisation"),
    PUBDATE("pubdate"),
    TITLE("title"),
    LAST_TITLE("last-title"),
    SUMMARY("summary"),
    DESCRIPTION("description"),
    PUBLISHED_DATE("published-date"),
    LAST_PUBLISHED_DATE("last-published-date"),
    START_DATE("start-date"),
    START_TIME("start-time"),
    END_DATE("end-date"),
    TIMEZONE("timezone"),
    LOCATION("location"),
    URL("url"),
    LAST_URL("last-url"),
    UUID("uuid"),
    DURATION("duration"),
    VIDEO_ID("video-id"),
    LAST_VIDEO_ID("last-video-id"),
    VIDEO_URL("video-url"),
    PROVIDER("provider"),
    CHANNEL_ID("channel-id"),
    CHANNEL_URL("channel-url"),
    CHANNEL_TITLE("channel-title"),
    VIDEO_TYPE("video-type"),
    EVENT_TYPE("event-type"),
    PUBLICATION_TYPE("publication-type"),
    FILE_PREFIX("file-prefix"),
    IMAGE("image"),
    IMAGE_SOURCE("image-source"),
    IMAGE_PREFIX("image-prefix"),
    IMAGE_TEXT("image-text"),
    THUMBNAIL("thumbnail"),
    THUMBNAIL_TEXT("thumbnail-text"),
    TAGS("tags"),
    LINK("link"),
    LINK_TEXT("link-text"),
    CREATOR_LINK("creator-link"),
    EMAIL("email"),
    WEBSITE("website"),
    AUTHOR("author"),
    AUTHOR_LINK("author-link"),
    PUBLISHED("published"),
    PROMOTE("promote"),
    NEWSLETTER("newsletter"),
    FEATURED("featured"),
    SPONSORED("sponsored"),
    CREATED_BY("created-by"),
    DEPLOYED("deployed"),
    SPONSOR("sponsor"),
    TABS("tabs"),
    CONTENT("content"),
    FOUNDED("founded"),
    STOCK_SYMBOL("stock-symbol"),
    FEED_PROVIDER("feed-provider"),
    FACEBOOK("facebook"),
    TWITTER("twitter"),
    LINKEDIN("linkedin"),
    INSTAGRAM("instagram"),
    YOUTUBE("youtube"),
    VIMEO("vimeo"),
    PROJECTS("projects"),
    GITHUB("github"),
    TOOLS("tools"),
    ALTERNATIVES("alternatives"),
    FEATURES("features"),
    SOCIAL("social"),
    HANDLE("handle"),
    HASHTAG("hashtag"),
    HASHTAGS("hashtags"),
    DOWNLOAD("download"),
    DOWNLOAD_TEXT("download-text"),
    PRICING("pricing"),
    BADGES("badges"),
    LINKS("links"),
    LICENSE("license"),
    JOBS("jobs"),
    PACKAGE("package"),
    TECHNOLOGIES("technologies"),
    CONTACT("contact"),
    CANONICAL_URL("canonical-url"),
    METATAGS("metatags"),
    STATUS("status"),
    COUNT("count"),
    TRACKING("tracking"),
    TEMPLATE("template"),
    LISTING("listing"),
    FOOTER("footer");

    private String value;

    /**
     * Constructor that takes the value.
     * @param value The value of the field
     */
    FieldName(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the field.
     * @return The value of the field.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the field.
     * @return The value of the field.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the type for the given value.
     * @param valu The type value
     * @return The type for the given value
     */
    public static FieldName fromValue(String value)
    {
        FieldName[] types = values();
        for(FieldName type : types)
        {
            if(type.value().equals(value))
                return type;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param code The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}