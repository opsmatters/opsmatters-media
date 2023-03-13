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
package com.opsmatters.media.model.content.crawler;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents a YAML configuration for the loading of web content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentLoading implements ConfigElement
{
    private long wait = 0L;
    private long sleep = 0L;
    private String selector = "";
    private long interval = 0L;
    private long maxWait = 0L;
    private String keywords = "";
    private List<String> keywordList;
    private int scrollX = 0;
    private int scrollY = 0;
    private String moveTo = "";
    private MoreLink moreLink;

    /**
     * Default constructor.
     */
    public ContentLoading()
    {
    }

    /**
     * Copy constructor.
     */
    public ContentLoading(ContentLoading obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentLoading obj)
    {
        if(obj != null)
        {
            setSelector(obj.getSelector());
            setInterval(obj.getInterval());
            setSleep(obj.getSleep());
            setWait(obj.getWait());
            setMaxWait(obj.getMaxWait());
            setKeywords(obj.getKeywords());
            setScrollX(obj.getScrollX());
            setScrollY(obj.getScrollY());
            setMoveTo(obj.getMoveTo());
            if(obj.getMoreLink() != null)
                setMoreLink(new MoreLink(obj.getMoreLink()));
        }
    }

    /**
     * Returns the wait time after page loading (in milliseconds).
     */
    public long getWait()
    {
        return wait;
    }

    /**
     * Sets the wait time after page loading (in milliseconds).
     */
    public void setWait(long wait)
    {
        this.wait = wait;
    }

    /**
     * Returns the sleep time after page loading (in milliseconds).
     */
    public long getSleep()
    {
        return sleep;
    }

    /**
     * Sets the sleep time after page loading (in milliseconds).
     */
    public void setSleep(long sleep)
    {
        this.sleep = sleep;
    }

    /**
     * Returns the selector to look for when page loading.
     */
    public String getSelector()
    {
        return selector;
    }

    /**
     * Sets the selector to look for when page loading.
     */
    public void setSelector(String selector)
    {
        this.selector = selector;
    }

    /**
     * Returns the interval between selector queries when page loading.
     */
    public long getInterval()
    {
        return interval;
    }

    /**
     * Sets the interval between selector queries when page loading.
     */
    public void setInterval(long interval)
    {
        this.interval = interval;
    }

    /**
     * Returns the maximum time to wait for selector queries when page loading.
     */
    public long getMaxWait()
    {
        return maxWait;
    }

    /**
     * Sets the maximum time to wait for selector queries when page loading.
     */
    public void setMaxWait(long maxWait)
    {
        this.maxWait = maxWait;
    }

    /**
     * Returns the keywords to filter on when page loading.
     */
    public String getKeywords()
    {
        return keywords;
    }

    /**
     * Returns the list of keywords to filter on when page loading.
     */
    public List<String> getKeywordList()
    {
        return keywordList;
    }

    /**
     * Sets the keywords to filter on when page loading.
     */
    public void setKeywords(String keywords)
    {
        this.keywords = keywords;

        // Parse the keywords
        if(hasKeywords())
        {
            if(keywordList == null)
                keywordList = new ArrayList<String>();
            keywordList.clear();
            String[] array = keywords.split(",");
            for(String keyword : array)
            {
                String str = keyword.toLowerCase().trim();
                if(str.length() > 0)
                    keywordList.add(str);
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if there are keywords to filter on when page loading.
     */
    public boolean hasKeywords()
    {
        return keywords != null && keywords.length() > 0;
    }

    /**
     * Returns how much to scroll the page in the x direction after loading (in pixels).
     */
    public int getScrollX()
    {
        return scrollX;
    }

    /**
     * Sets how much to scroll the page in the x direction after loading (in pixels).
     */
    public void setScrollX(int scrollX)
    {
        this.scrollX = scrollX;
    }

    /**
     * Returns how much to scroll the page in the y direction after loading (in pixels).
     */
    public int getScrollY()
    {
        return scrollY;
    }

    /**
     * Sets how much to scroll the page in the y direction after loading (in pixels).
     */
    public void setScrollY(int scrollY)
    {
        this.scrollY = scrollY;
    }

    /**
     * Returns the css selector of an element to scroll and move to.
     */
    public String getMoveTo()
    {
        return moveTo;
    }

    /**
     * Sets the css selector of an element to scroll and move to.
     */
    public void setMoveTo(String moveTo)
    {
        this.moveTo = moveTo;
    }

    /**
     * Returns the link to get more items.
     */
    public MoreLink getMoreLink()
    {
        return moreLink;
    }

    /**
     * Sets the link to get more items.
     */
    public void setMoreLink(MoreLink moreLink)
    {
        this.moreLink = moreLink;
    }

    /**
     * Returns <CODE>true</CODE> if the link to get more items has been set for this configuration.
     */
    public boolean hasMoreLink()
    {
        return getMoreLink() != null;
    }

    /**
     * Returns a builder for the configuration.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<ContentLoading>
    {
        // The config attribute names
        private static final String WAIT = "wait";
        private static final String SLEEP = "sleep";
        private static final String SELECTOR = "selector";
        private static final String INTERVAL = "interval";
        private static final String MAX_WAIT = "max-wait";
        private static final String KEYWORDS = "keywords";
        private static final String SCROLL_X = "scroll-x";
        private static final String SCROLL_Y = "scroll-y";
        private static final String MOVE_TO = "move-to";
        private static final String MORE_LINK = "more-link";

        private ContentLoading ret = new ContentLoading();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(WAIT))
                ret.setWait((Integer)map.get(WAIT));
            if(map.containsKey(SLEEP))
                ret.setSleep((Integer)map.get(SLEEP));
            if(map.containsKey(SELECTOR))
                ret.setSelector((String)map.get(SELECTOR));
            if(map.containsKey(INTERVAL))
                ret.setInterval((Integer)map.get(INTERVAL));
            if(map.containsKey(MAX_WAIT))
                ret.setMaxWait((Integer)map.get(MAX_WAIT));
            if(map.containsKey(KEYWORDS))
                ret.setKeywords((String)map.get(KEYWORDS));
            if(map.containsKey(SCROLL_X))
                ret.setScrollX((Integer)map.get(SCROLL_X));
            if(map.containsKey(SCROLL_Y))
                ret.setScrollY((Integer)map.get(SCROLL_Y));
            if(map.containsKey(MOVE_TO))
                ret.setMoveTo((String)map.get(MOVE_TO));
            if(map.containsKey(MORE_LINK))
                ret.setMoreLink(createMoreLink(MORE_LINK, map.get(MORE_LINK)));

            return this;
        }

        /**
         * Create a More link object for the given value.
         */
        private MoreLink createMoreLink(String name, Object value)
        {
            MoreLink.Builder builder = MoreLink.builder();
            if(value instanceof String)
                builder = builder.selector((String)value);
            else if(value instanceof Map)
                builder = builder.parse((Map<String,Object>)value);
            return builder.build();
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public ContentLoading build()
        {
            return ret;
        }
    }
}