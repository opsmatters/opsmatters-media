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
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSummary;
import com.opsmatters.media.model.content.RoundupSummary;
import com.opsmatters.media.model.content.VideoSummary;
import com.opsmatters.media.model.content.EventSummary;
import com.opsmatters.media.model.content.PublicationSummary;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a snapshot of content monitor content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentSnapshot extends JSONObject
{
    private static final Logger logger = Logger.getLogger(ContentSnapshot.class.getName());

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
     * Process the given snapshot to prepare it for comparison.
     */
    private void process(int maxResults)
    {
        remove(Fields.COUNT); // Checked separately
        JSONArray array = getJSONArray(getTag());
        for(int i = array.length()-1; i >= 0; i--)
        {
            JSONObject item = array.getJSONObject(i);
            item.remove(Fields.PUBLISHED_DATE);
            item.remove(Fields.START_DATE);

            //  Trim array if max results set
            if(maxResults > 0 && (i+1) > maxResults)
                array.remove(i);
        }
    }

    /**
     * Returns <CODE>true<CODE> if the two snapshots do not match.
     */
    public static boolean compare(ContentSnapshot snapshot1, ContentSnapshot snapshot2, int maxResults)
    {
        int count1 = snapshot1.getCount();
        int count2 = snapshot2.getCount();
        float decrease = 0.0f;
        if(count1 >= 0 && count2 >= 0 && count2 < count1)
        {
            decrease = ((count1 - count2) / (float)count1) * 100.0f;
        }

        if(decrease > 50.0f)
            throw new IllegalStateException(String.format("Detected abnormal decrease in items: %.2f%%", decrease));

        snapshot1.process(maxResults);
        snapshot2.process(maxResults);
        return snapshot1.toString().equals(snapshot2.toString());
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