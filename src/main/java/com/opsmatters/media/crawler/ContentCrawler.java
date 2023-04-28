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

import java.io.IOException;
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
import com.opsmatters.media.model.content.ContentTeaser;
import com.opsmatters.media.model.content.crawler.ContentLoading;
import com.opsmatters.media.model.content.crawler.CrawlerTarget;
import com.opsmatters.media.model.content.crawler.CrawlerContent;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;
import com.opsmatters.media.model.content.crawler.field.FieldMatch;
import com.opsmatters.media.model.content.crawler.field.FieldCase;
import com.opsmatters.media.model.content.crawler.field.FieldExtractor;
import com.opsmatters.media.model.logging.Log;
import com.opsmatters.media.model.logging.LogEntry;

/**
 * Class representing a crawler for content items with fields.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentCrawler<T extends ContentTeaser, D extends ContentTeaser>
{
    private static final Logger logger = Logger.getLogger(ContentCrawler.class.getName());

    public static final String CURRENT_DAY = "current-day";
    public static final String CURRENT_MONTH = "current-month";
    public static final String CURRENT_MONTH_NAME = "current-month-name";
    public static final String CURRENT_YEAR = "current-year";

    private String name = "";
    private boolean debug = false;
    private int maxResults = 0;
    private CrawlerTarget target;
    private List<T> teasers = new ArrayList<T>();
    private boolean rootError = false;

    private Map<String, Object> properties = new HashMap<String, Object>();
    protected Log log = new Log();

    /**
     * Constructor that takes a target.
     */
    public ContentCrawler(CrawlerTarget target)
    {
        setName(target.getName());
        this.target = target;

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
     * Returns the title of the crawled page.
     */
    public abstract String getTitle();

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
     * Returns the list of log entries.
     */
    public List<LogEntry> getLogEntries()
    {
        return log.list();
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
     * Process the configured teasers.
     */
    public abstract int processTeasers(boolean cache) throws IOException;

    /**
     * Returns the teasers found by the crawler.
     */
    public List<T> getTeasers()
    {
        return teasers;
    }

    /**
     * Returns number of teasers found by the crawler.
     */
    public int numTeasers()
    {
        return teasers.size();
    }

    /**
     * Adds a selected found by the crawler.
     */
    public void addTeaser(T teaser)
    {
        this.teasers.add(teaser);
    }

    /**
     * Returns the processed content details from the given teaser.
     */
    public abstract D getDetails(T teaser) throws IOException;

    /**
     * Returns <CODE>true</CODE> if the root needs to be replaced following an error.
     */
    public boolean hasRootError()
    {
        return rootError;
    }

    /**
     * Set to <CODE>true</CODE> if the root needs to be replaced following an error.
     */
    public void setRootError(boolean rootError)
    {
        this.rootError = rootError;
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
     * Returns the articles for the crawler.
     */
    public CrawlerContent getArticles()
    {
        return target.getArticles();
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
        setProperty(CURRENT_DAY, calendar.get(Calendar.DAY_OF_MONTH));
        setProperty(CURRENT_MONTH, calendar.get(Calendar.MONTH));
        setProperty(CURRENT_MONTH_NAME, month.getDisplayName(TextStyle.FULL, Locale.getDefault()));
        setProperty(CURRENT_YEAR, calendar.get(Calendar.YEAR));
    }

    /**
     * Returns the given field property.
     */
    protected Object getProperty(String name)
    {
        return properties.get(name);
    }

    /**
     * Adds the given property to be used in field formats.
     */
    protected void setProperty(String name, Object value)
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
    public String getValue(Field field, String value)
    {
        return getValue(field, value, value);
    }

    /**
     * Apply the configured regular expression to the given field value.
     */
    public String getValue(Field field, String value, String dflt)
    {
        // Remove special characters before processing
        value = processValue(value);
        dflt = processValue(dflt);

        String ret = value;

        // Try each extractor in turn
        if(field.hasExtractors())
        {
            for(FieldExtractor extractor : field.getExtractors())
            {
                ret = extract(extractor, value);
                if(ret != null && ret.length() > 0)
                    break;
            }

            if(ret == null)
            {
                logger.info(String.format("No match found for field %s: value=[%s]", 
                    field.getName(), value));
                log.info(String.format("No match found for field %s: value=[%s]", 
                    field.getName(), value));

                // Use the default if none of the extractors matched
                ret = dflt;
            }
        }

        if(ret != null && field.getTextCase() != FieldCase.NONE)
        {
            if(debug())
                logger.info(String.format("Changing case for field %s: ret=[%s], case=[%s]", 
                        field.getName(), ret, field.getTextCase()));

            if(field.getTextCase() == FieldCase.LOWER)
                ret = ret.toLowerCase();
            else if(field.getTextCase() == FieldCase.UPPER)
                ret = ret.toUpperCase();
            else if(field.getTextCase() == FieldCase.CAPITALIZE)
                ret = WordUtils.capitalizeFully(ret);

            if(debug())
                logger.info(String.format("Changed case for field %s: ret=[%s], case=[%s]", 
                        field.getName(), ret, field.getTextCase()));
        }

        return ret;
    }

    /**
     * Extracts the value using the given extractor.
     */
    private String extract(FieldExtractor extractor, String value)
    {
        String ret = null;

        Pattern pattern = extractor.getExprPattern();
        if(pattern != null)
        {
            if(debug())
                logger.info(String.format("Evaluating field %s: pattern=[%s] value=[%s] match=[%s]", 
                    extractor.getName(), pattern.pattern(), value, extractor.getMatch()));

            Matcher m = pattern.matcher(value);

            if(m.find())
            {
                // Get the field format (can including properties)
                String format = getFormatSubstitutor().replace(extractor.getFormat());

                // Make the necessary field replacements
                if(extractor.getMatch() == FieldMatch.ALL)
                    ret = m.replaceAll(format);
                else
                    ret = m.replaceFirst(format);

                if(debug())
                    logger.info(String.format("Match found for field %s: ret=[%s] match=[%s]", 
                        extractor.getName(), ret, extractor.getMatch()));
            }
        }

        return ret;
    }

    /**
     * Process the given value.
     */
    protected String processValue(String value)
    {
        String ret = value;
        if(ret != null)
        {
            ret = ret.replaceAll("&amp;", "&"); // Replace &amp; with &
            ret = ret.replaceAll("&nbsp;", " "); // Replace &nbsp; with space
            ret = ret.replaceAll("[\\u2000-\\u200b\\u202F]", " "); // Replace "thin" spaces with normal space
            ret = ret.replaceAll("\\ufffc", ""); // Remove object replacement character
        }

        return ret;
    }
}