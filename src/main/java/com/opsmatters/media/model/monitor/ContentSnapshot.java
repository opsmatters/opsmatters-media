/*
 * Copyright 2021 Gerald Curley
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
package com.opsmatters.media.model.monitor;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import java.sql.SQLException;
import org.json.JSONObject;
import org.json.JSONArray;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSummary;
import com.opsmatters.media.model.content.ContentItem;
import com.opsmatters.media.model.content.RoundupSummary;
import com.opsmatters.media.model.content.VideoSummary;
import com.opsmatters.media.model.content.VideoArticle;
import com.opsmatters.media.model.content.EventSummary;
import com.opsmatters.media.model.content.PublicationSummary;
import com.opsmatters.media.model.content.LinkedContent;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a snapshot of content monitor content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentSnapshot extends JSONObject
{
    private static final Logger logger = Logger.getLogger(ContentSnapshot.class.getName());

    private ContentLookup lookup;

    /**
     * Constructor that takes a content type and list of items.
     */
    public ContentSnapshot(ContentType type, List<? extends ContentSummary> items)
    {
        JSONArray array = new JSONArray();
        if(items != null)
        {
            for(ContentSummary content : items)
                array.put(createObject(type, content));
        }

        put(type.tag(), array);
        put(Fields.COUNT, array.length());
    }

    /**
     * Constructor that takes a content type and list of items.
     */
    public ContentSnapshot(List<JSONObject> items, ContentType type)
    {
        JSONArray array = new JSONArray();
        if(items != null)
        {
            for(JSONObject item : items)
                array.put(item);
        }

        put(type.tag(), array);
        put(Fields.COUNT, array.length());
    }

    /**
     * Constructor that takes a content type.
     */
    public ContentSnapshot(ContentType type)
    {
        this(type, (List<? extends ContentSummary>)null);
    }

    /**
     * Constructor that takes a content type and an error message.
     */
    public ContentSnapshot(ContentType type, String message)
    {
        put(type.tag(), new JSONArray().put(message));
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public ContentSnapshot(JSONObject obj)
    {
        super(obj.toString());
    }

    /**
     * Constructor that takes a JSON string.
     */
    public ContentSnapshot(String str)
    {
        super(str);
    }

    /**
     * Returns the content type.
     */
    public ContentType getContentType()
    {
        ContentType ret = null;
        for(Object keyStr : keySet())
        {
            String key = keyStr.toString();
            Object obj = get(key);
            if(obj instanceof JSONArray)
                ret = ContentType.fromTag(key);
        }

        return ret;
    }

    /**
     * Returns the tag.
     */
    public String getTag()
    {
        return getContentType().tag();
    }

    /**
     * Returns the item count.
     */
    public int getCount()
    {
        return optInt(Fields.COUNT, -1);
    }

    /**
     * Returns <CODE>true</CODE> if the item count is zero.
     */
    public boolean isEmpty()
    {
        return getCount() == 0;
    }

    /**
     * Returns the object used to lookup content items.
     */
    public ContentLookup getLookup()
    {
        return lookup;
    }

    /**
     * Sets the object used to lookup content items.
     */
    public void setLookup(ContentLookup lookup)
    {
        this.lookup = lookup;
    }

    /**
     * Create an object for the content item.
     */
    private JSONObject createObject(ContentType type, ContentSummary content)
    {
        switch(type)
        {
            case ROUNDUP:
                return createObject((RoundupSummary)content);
            case VIDEO:
                return createObject((VideoSummary)content);
            case EVENT:
                return createObject((EventSummary)content);
            case WHITE_PAPER:
            case EBOOK:
                return createObject((PublicationSummary)content);
            default:
                return null;
        }
    }

    /**
     * Create an object for the roundup item.
     */
    private JSONObject createObject(RoundupSummary content)
    {
        JSONObject ret = new JSONObject();
        ret.put(Fields.TITLE, StringUtils.convertToAscii(content.getTitle(), false, false));
        if(content.getPublishedDate() != null)
            ret.put(Fields.PUBLISHED_DATE, content.getPublishedDateAsString());
        ret.put(Fields.URL, content.getUrl());
        return ret;
    }

    /**
     * Create an object for the video item.
     */
    private JSONObject createObject(VideoSummary content)
    {
        JSONObject ret = new JSONObject();
        ret.put(Fields.TITLE, StringUtils.convertToAscii(content.getTitle(), false, false));
        if(content.getPublishedDate() != null)
            ret.put(Fields.PUBLISHED_DATE, content.getPublishedDateAsString());
        ret.put(Fields.VIDEO_ID, content.getVideoId());
        return ret;
    }

    /**
     * Create an object for the event item.
     */
    private JSONObject createObject(EventSummary content)
    {
        JSONObject ret = new JSONObject();
        ret.put(Fields.TITLE, content.getTitle());
        if(content.getStartDate() != null)
            ret.put(Fields.START_DATE, content.getStartDateAsString());
        ret.put(Fields.URL, content.getUrl());
        return ret;
    }

    /**
     * Create an object for the publication item.
     */
    private JSONObject createObject(PublicationSummary content)
    {
        JSONObject ret = new JSONObject();
        ret.put(Fields.TITLE, content.getTitle());
        if(content.getPublishedDate() != null)
            ret.put(Fields.PUBLISHED_DATE, content.getPublishedDateAsString());
        ret.put(Fields.URL, content.getUrl());
        return ret;
    }

    /**
     * Returns the difference between the two snapshots (or null if they are the same).
     */
    public static ContentSnapshot compare(String code, ContentSnapshot current,
        ContentSnapshot latest, boolean checkDecrease)
        throws SQLException
    {
        int currentCount = current.getCount();
        int latestCount = latest.getCount();
        float decrease = 0.0f;
        if(currentCount >= 0 && latestCount >= 0 && latestCount < currentCount)
        {
            decrease = ((currentCount - latestCount) / (float)currentCount) * 100.0f;
        }

        if(checkDecrease && decrease > 50.0f)
            throw new IllegalStateException(String.format("Detected abnormal decrease in items: %.2f%%", decrease));


        ContentLookup lookup = current.getLookup();
        ContentType type = current.getContentType();
        Map<String,JSONObject> titles = new HashMap<String,JSONObject>();
        Map<String,JSONObject> ids = new HashMap<String,JSONObject>();

        JSONArray latestArray = latest.getJSONArray(latest.getTag());
        for(int i = 0; i < latestArray.length(); i++)
        {
            JSONObject item = latestArray.getJSONObject(i);
            String title = item.optString(Fields.TITLE);
            String id = item.optString(Fields.URL);
            if(type == ContentType.VIDEO)
                id = item.optString(Fields.VIDEO_ID);
            titles.put(title, item);
            ids.put(id, item);
        }

        if(lookup != null)
            logger.info(String.format("Before compare snapshot for %s: titles=%d ids=%d",
                code, titles.size(), ids.size()));

        JSONArray currentArray = current.getJSONArray(current.getTag());
        for(int i = 0; i < currentArray.length(); i++)
        {
            JSONObject item = currentArray.getJSONObject(i);
            String title = item.optString(Fields.TITLE);
            String id = item.optString(Fields.URL);
            if(type == ContentType.VIDEO)
                id = item.optString(Fields.VIDEO_ID);
            titles.remove(title);
            ids.remove(id);
        }

        if(lookup != null)
            logger.info(String.format("After compare with current for %s: titles=%d ids=%d",
                code, titles.size(), ids.size()));

        // If there are still unresolved items, try looking for them
        //   in the stored content items to see if we've seen them before
        if(lookup != null && (titles.size() > 0 || ids.size() > 0))
        {
            Iterator<Entry<String,JSONObject>> iterator = titles.entrySet().iterator();
            while(iterator.hasNext())
            {
                Entry<String,JSONObject> entry = iterator.next();
                String title = entry.getKey();
                JSONObject item = entry.getValue();
                String publishedDate = item.optString(Fields.PUBLISHED_DATE);
                if(publishedDate.length() > 0)
                    publishedDate = publishedDate.substring(0, publishedDate.indexOf(" ")); // Remove time part
                String id = item.optString(Fields.URL);
                if(type == ContentType.VIDEO)
                    id = item.optString(Fields.VIDEO_ID);

                ContentItem content = lookup.getByTitle(title);
                if(content != null)
                {
                    // Store the last videoId or URL if it has changed
                    if(type == ContentType.VIDEO)
                    {
                        VideoArticle video = (VideoArticle)content;
                        if(!id.equals(video.getVideoId()))
                        {
                            item.put(Fields.LAST_VIDEO_ID, video.getVideoId());
                            entry.setValue(item);
                        }
                    }
                    else
                    {
                        LinkedContent linked = (LinkedContent)content;
                        if(!id.equals(linked.getUrl()))
                        {
                            item.put(Fields.LAST_URL, linked.getUrl());
                            entry.setValue(item);
                        }
                    }

                    // Store the last published date if it has changed
                    if(publishedDate.length() > 0
                        && !content.getPublishedDateAsString().startsWith(publishedDate))
                    {
                        item.put(Fields.LAST_PUBLISHED_DATE, content.getPublishedDateAsString());
                        entry.setValue(item);
                        continue;
                    }

                    iterator.remove();
                    logger.info(String.format("Found stored title for %s: title='%s' titles=%d",
                        code, title, titles.size()));
                }
                else
                {
                    content = lookup.getById(id);
                    if(content != null)
                    {
                        // Store the last title if it has changed
                        if(!title.equals(content.getTitle()))
                        {
                            item.put(Fields.LAST_TITLE, content.getTitle());
                            entry.setValue(item);
                        }

                        // Store the last published date if it has changed
                        if(publishedDate.length() > 0
                            && !content.getPublishedDateAsString().startsWith(publishedDate))
                        {
                            item.put(Fields.LAST_PUBLISHED_DATE, content.getPublishedDateAsString());
                            entry.setValue(item);
                        }
                    }
                }
            }

            iterator = ids.entrySet().iterator();
            while(iterator.hasNext())
            {
                Entry<String,JSONObject> entry = iterator.next();
                String id = entry.getKey();
                JSONObject item = entry.getValue();
                String publishedDate = item.optString(Fields.PUBLISHED_DATE);
                if(publishedDate.length() > 0)
                    publishedDate = publishedDate.substring(0, publishedDate.indexOf(" ")); // Remove time part
                String title = item.optString(Fields.TITLE);

                ContentItem content = lookup.getById(id);
                if(content != null)
                {

                    // Store the last title if it has changed
                    if(!title.equals(content.getTitle()))
                    {
                        item.put(Fields.LAST_TITLE, content.getTitle());
                        entry.setValue(item);
                    }

                    // Store the last published date if it has changed
                    if(publishedDate.length() > 0
                        && !content.getPublishedDateAsString().startsWith(publishedDate))
                    {
                        item.put(Fields.LAST_PUBLISHED_DATE, content.getPublishedDateAsString());
                        entry.setValue(item);
                        continue;
                    }

                    iterator.remove();
                    logger.info(String.format("Found stored id for %s: id=%s ids=%d",
                        code, id, ids.size()));
                }
                else
                {
                    content = lookup.getByTitle(title);
                    if(content != null)
                    {
                        // Store the last videoId or URL if it has changed
                        if(type == ContentType.VIDEO)
                        {
                            VideoArticle video = (VideoArticle)content;
                            if(!id.equals(video.getVideoId()))
                            {
                                item.put(Fields.LAST_VIDEO_ID, video.getVideoId());
                                entry.setValue(item);
                            }
                        }
                        else
                        {
                            LinkedContent linked = (LinkedContent)content;
                            if(!id.equals(linked.getUrl()))
                            {
                                item.put(Fields.LAST_URL, linked.getUrl());
                                entry.setValue(item);
                            }
                        }

                        // Store the last published date if it has changed
                        if(publishedDate.length() > 0
                            && !content.getPublishedDateAsString().startsWith(publishedDate))
                        {
                            item.put(Fields.LAST_PUBLISHED_DATE, content.getPublishedDateAsString());
                            entry.setValue(item);
                        }
                    }
                }
            }
        }

        if(lookup != null)
            logger.info(String.format("After compare with stored for %s: titles=%d ids=%d",
                code, titles.size(), ids.size()));

        // Create the array of difference items
        List<JSONObject> items = new ArrayList<JSONObject>();
 
        // Go through the leftover titles and add to the differences
        if(titles.size() > 0)
        {
            Iterator<Entry<String,JSONObject>> iterator = titles.entrySet().iterator();
            while(iterator.hasNext())
            {
                Entry<String,JSONObject> entry = iterator.next();
                String title = entry.getKey();
                JSONObject item = entry.getValue();
                String id = item.optString(Fields.URL);
                if(type == ContentType.VIDEO)
                    id = item.optString(Fields.VIDEO_ID);
                String publishedDate = item.optString(Fields.PUBLISHED_DATE);
                logger.info(String.format("Unable to find item with title for %s: id=%s, published=%s, title=%s",
                    code, id, publishedDate, title));
                if(!items.contains(item))
                    items.add(item);
            }
        }

        // Go through the leftover ids and add to the differences
        if(ids.size() > 0)
        {
            Iterator<Entry<String,JSONObject>> iterator = ids.entrySet().iterator();
            while(iterator.hasNext())
            {
                Entry<String,JSONObject> entry = iterator.next();
                String id = entry.getKey();
                JSONObject item = entry.getValue();
                String title = item.optString(Fields.TITLE);
                String publishedDate = item.optString(Fields.PUBLISHED_DATE);
                logger.info(String.format("Unable to find item with id for %s: id=%s, published=%s, title=%s",
                    code, id, publishedDate, title));
                if(!items.contains(item))
                    items.add(item);
            }
        }

        return new ContentSnapshot(items, type);
    }

    /**
     * Returns <CODE>true<CODE> if this snapshot contains a list of items.
     */
    public boolean containsItems()
    {
        return toString().indexOf(String.format("\"%s\"", Fields.COUNT)) != -1;
    }

    /**
     * Returns the snapshot as a list of items.
     */
    public String format()
    {
        StringBuilder builder = new StringBuilder();
        String type = getTag();
        Object value = get(type);
        if(value instanceof JSONArray)
        {
            JSONArray array = getJSONArray(type);
            for(int i = 0; i < array.length(); i++)
            {
                JSONObject item = array.getJSONObject(i);
                if(i > 0)
                    builder.append("\n");

                // Add an empty Date field if one wasn't provided
                //   Otherwise the fields could be out of sync and mess up the comparison.
                String fieldname = Fields.PUBLISHED_DATE;
                if(type.equals(ContentType.EVENT.tag()))
                    fieldname = Fields.START_DATE;
                if(!item.has(fieldname))
                    item.put(fieldname, "");

                for(Object key : item.keySet())
                    builder.append(String.format("%s=%s\n", key, item.optString(key.toString())));
            }
        }
        else
        {
            builder.append(value);
        }

        return builder.toString();
    }
}