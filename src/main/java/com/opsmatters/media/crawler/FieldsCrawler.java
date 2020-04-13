/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.crawler;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.Month;
import java.time.format.TextStyle;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.WordUtils;
import com.opsmatters.media.config.content.FieldsConfiguration;
import com.opsmatters.media.config.content.LoadingConfiguration;
import com.opsmatters.media.config.content.ContentField;
import com.opsmatters.media.config.content.ContentFields;
import com.opsmatters.media.config.content.ContentFieldMatch;
import com.opsmatters.media.config.content.ContentFieldCase;
import com.opsmatters.media.model.content.ContentSummary;

/**
 * Class representing a crawler for content items with fields.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class FieldsCrawler<T extends ContentSummary>
{
    private static final Logger logger = Logger.getLogger(FieldsCrawler.class.getName());

    public static final String CURRENT_DAY = "current-day";
    public static final String CURRENT_MONTH = "current-month";
    public static final String CURRENT_MONTH_NAME = "current-month-name";
    public static final String CURRENT_YEAR = "current-year";

    private String name = "";
    protected boolean initialised = false;
    private boolean debug = false;
    private int maxResults = 0;
    private FieldsConfiguration config;
    private List<T> content = new ArrayList<T>();

    protected Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * Constructor that takes a name.
     */
    public FieldsCrawler(FieldsConfiguration config)
    {
        setName(config.getName());
        this.config = config;

        // Initialise the format properties
        initFormatProperties();
    }

    /**
     * Close the crawler and release resources.
     */
    public abstract void close();

    /**
     * Returns the name of the crawler.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the crawler.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the crawler.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public boolean debug()
    {
        return debug;
    }

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
     * Returns the teaser page loading configuration.
     */
    public LoadingConfiguration getTeaserLoading()
    {
        return config.getTeaserLoading();
    }

    /**
     * Returns the teaser selections of the crawler.
     */
    public List<ContentFields> getTeaserFields()
    {
        return config.getTeaserFields();
    }

    /**
     * Returns the content page loading configuration.
     */
    public LoadingConfiguration getContentLoading()
    {
        return config.getContentLoading();
    }

    /**
     * Returns the content selection of the crawler.
     */
    public ContentFields getContentFields()
    {
        return config.getContentFields();
    }

    /**
     * Returns the maximum results of the crawler.
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * Sets the maximum results of the crawler.
     */
    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
    }

    /**
     * Returns the content items selected by the crawler.
     */
    public List<T> getContent()
    {
        return content;
    }

    /**
     * Returns number of content items selected by the crawler.
     */
    public int numContentItems()
    {
        return content.size();
    }

    /**
     * Adds a selected content item to the crawler.
     */
    public void addContent(T content)
    {
        this.content.add(content);
    }

    /**
     * Returns <CODE>true</CODE> if parameters should be removed from the URL.
     */
    public boolean removeParameters()
    {
        return getContentLoading() != null ? getContentLoading().removeParameters() : true;
    }

    /**
     * Sets the default properties to be used in field formats.
     * <P>
     * Properties supported:
     * <ul>
     * <li> <strong>current-day</strong>: the current day of the month.
     * <li> <strong>current-month</strong>: the current month of the year.
     * <li> <strong>current-month-name</strong>: the name of the current month of the year.
     * <li> <strong>current-year</strong>: the current 4-digit year.
     * </ul>
     */
    private void initFormatProperties()
    {
        properties.clear();

        Calendar calendar = Calendar.getInstance();
        Month month = Month.of(calendar.get(Calendar.MONTH)+1); 
        properties.put(CURRENT_DAY, calendar.get(Calendar.DAY_OF_MONTH));
        properties.put(CURRENT_MONTH, calendar.get(Calendar.MONTH));
        properties.put(CURRENT_MONTH_NAME, month.getDisplayName(TextStyle.FULL, Locale.getDefault()));
        properties.put(CURRENT_YEAR, calendar.get(Calendar.YEAR));
    }

    /**
     * Adds the given property to be used in field formats.
     */
    protected void setFormatProperty(String name, String value)
    {
        properties.put(name, value);
    }

    /**
     * Returns the propert substitutor to be used in field formats.
     */
    protected StringSubstitutor getFormatSubstitutor()
    {
        return new StringSubstitutor(properties);
    }

    /**
     * Apply the configured regular expression to the given field value.
     */
    public String getValue(ContentField field, String value)
    {
        String ret = value;

        if(field.getTextCase() != ContentFieldCase.NONE)
        {
            if(debug())
                logger.info(String.format("Changing case for field %s: ret=[%s], case=[%s]", 
                        field.getName(), ret, field.getTextCase()));

            if(field.getTextCase() == ContentFieldCase.LOWER)
                ret = ret.toLowerCase();
            else if(field.getTextCase() == ContentFieldCase.UPPER)
                ret = ret.toUpperCase();
            else if(field.getTextCase() == ContentFieldCase.CAPITALIZE)
                ret = WordUtils.capitalizeFully(ret);

            if(debug())
                logger.info(String.format("Changed case for field %s: ret=[%s], case=[%s]", 
                        field.getName(), ret, field.getTextCase()));
        }

        Pattern pattern = field.getExprPattern();
        if(pattern != null)
        {
            if(debug())
                logger.info(String.format("Evaluating field %s: pattern=[%s] value=[%s] match=[%s]", 
                    field.getName(), pattern.pattern(), ret, field.getMatch()));

            Matcher m = pattern.matcher(ret);

            if(m.find())
            {
                // Get the field format (can including properties)
                String format = "$1";
                if(field.hasFormat())
                {
                    format = getFormatSubstitutor().replace(field.getFormat());
                }

                // Make the necessary field replacements
                if(field.getMatch() == ContentFieldMatch.ALL)
                    ret = m.replaceAll(format);
                else
                    ret = m.replaceFirst(format);

                if(debug())
                    logger.info(String.format("Match found for field %s: ret=[%s] match=[%s]", 
                        field.getName(), ret, field.getMatch()));
            }
            else
            {
                logger.warning(String.format("No match found for field %s: value=[%s]", 
                    field.getName(), ret));
            }
        }

        return ret;
    }
}