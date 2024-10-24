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
package com.opsmatters.media.model.content.event;

import java.util.List;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Resource;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.field.FieldFilter;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing an event.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Event extends Resource<EventDetails>
{
    private String eventType = "";

    /**
     * Default constructor.
     */
    public Event()
    {
        setDetails(new EventDetails());
    }

    /**
     * Constructor that takes an event.
     */
    public Event(Event obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a site and code.
     */
    public Event(Site site, String code)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Event obj)
    {
        super.copyAttributes(obj);
        setContentDetails(obj.getDetails());
        setEventType(new String(obj.getEventType() != null ? obj.getEventType() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public Event(Site site, String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        setSiteId(site.getId());

        String id = values[0];
        String pubdate = values[1];
        String startDate = values[2];
        String endDate = values[3];
        String timezone = values[4];
        String title = values[5];
        String summary = values[6];
        String description = values[7];
        String organisation = values[8];
        String eventType = values[9];
        String location = values[10];
        String url = values[11];
        String linkText = values[12];
        String thumbnail = values[13];
        String thumbnailText = values[14];
        String thumbnailTitle = values[15];
        String createdBy = values[16];
        String published = values[17];

        setCode(code);
        setId(Integer.parseInt(id.substring(id.lastIndexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setStartDateAsString(startDate);
        setEndDateAsString(endDate);
        setTimeZone(timezone);
        setTitle(title);
        setSummary(summary);
        setDescription(description);
        setEventType(eventType);
        setLocation(location);
        setUrl(url, false);
        setLinkText(linkText);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        ret.put(START_DATE.value(), getStartDateMillis());
        ret.put(END_DATE.value(), getEndDateMillis());
        ret.putOpt(TIMEZONE.value(), getTimeZone());
        ret.putOpt(EVENT_TYPE.value(), getEventType());
        ret.putOpt(PLATFORM.value(), getPlatform());
        ret.putOpt(LOCATION.value(), getLocation());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);

        setStartDateMillis(obj.optLong(START_DATE.value()));
        setEndDateMillis(obj.optLong(END_DATE.value()));
        if(getPublishedDateMillis() == 0L) // Default to start date
            setPublishedDate(TimeUtils.truncateTimeUTC(getStartDate()));
        setTimeZone(obj.optString(TIMEZONE.value()));
        setEventType(obj.optString(EVENT_TYPE.value()));
        setPlatform(obj.optString(PLATFORM.value()));
        setLocation(obj.optString(LOCATION.value()));
    }

    /**
     * Returns the set of output fields from the event.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(START_DATE, getStartDateAsString());
        ret.put(END_DATE, getEndDateAsString());
        ret.put(TIMEZONE, getTimeZone());
        ret.put(LOCATION, getLocation());
        ret.put(EVENT_TYPE, getEventType());

        return ret;
    }

    /**
     * Returns a new event with defaults.
     */
    public static Event getDefault(Site site, EventConfig config)
        throws DateTimeParseException
    {
        Event event = new Event();

        event.init();
        event.setSiteId(site.getId());
        event.setTitle("New Event");
        event.setPublishedDateAsString(TimeUtils.toStringUTC(config.getField(PUBLISHED_DATE)));
        event.setStartDateAsString(TimeUtils.toStringUTC(config.getField(PUBLISHED_DATE)));
        event.addStartTime(config);

        return event;
    }

    /**
     * Adds the default start time to the start date for the event.
     */
    public void addStartTime(EventConfig config)
    {
        if(config.getFields().containsKey(START_TIME))
        {
            String start = config.getFields().get(START_TIME);
            addStartTime(TimeUtils.toMillisTime(start, Formats.SHORT_TIME_FORMAT));
        }
    }

    /**
     * Adds the given start time to the start date for the event.
     */
    public void addStartTime(long starttm)
    {
        if(starttm > 0L)
            setStartDateMillis(getStartDateMillis()+starttm);
    }

    /**
     * Use the given configuration to set defaults for the event.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite,
        EventConfig config, CrawlerWebPage page)
    {
        super.init(organisation, organisationSite, config);

        setEventType(config.getField(EVENT_TYPE, ""));
        setLinkText(config.getField(LINK_TEXT, ""));
    }

    /**
     * Prepare the fields in the event using the given configuration.
     */
    public void prepare(EventConfig config, CrawlerWebPage page, boolean debug)
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getField(PUBLISHED_DATE)));

        // Add the organisation prefix to the title
        if(!getTitle().startsWith(config.getName()))
            setTitle(String.format("%s: %s", config.getName(), getTitle()));

        formatSummary(config, page.getArticles().getFilters(), false, debug);

        // Use the default timezone if an event timezone wasn't found
        if(config.hasField(TIMEZONE) && getTimeZone().length() == 0)
            setTimeZone(config.getField(TIMEZONE));

        // Use the default location if an event location wasn't found
        if(config.hasField(LOCATION) && getLocation().length() == 0)
            setLocation(config.getField(LOCATION));
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.EVENT;
    }

    /**
     * Sets the event details from a teaser.
     */
    @Override
    public void setTeaserDetails(EventDetails obj)
    {
        super.setTeaserDetails(obj);

        if(obj != null)
        {
            setStartDate(obj.getStartDate());
            setEndDate(obj.getEndDate());
            setUrl(new String(obj.getUrl()), false);
            setPlatform(obj.getPlatform());
        }
    }

    /**
     * Sets the event details.
     */
    @Override
    public void setContentDetails(EventDetails obj)
    {
        if(obj != null)
        {
            setTeaserDetails(obj);
            setTimeZone(new String(obj.getTimeZone() != null ? obj.getTimeZone() : ""));
            setLocation(new String(obj.getLocation() != null ? obj.getLocation() : ""));
            setConfigured(true);
        }
    }

    /**
     * Format the event body and summary.
     */
    public void formatSummary(EventConfig config, List<FieldFilter> filters, boolean force, boolean debug)
    {
        if(hasDescription())
        {
            BodyParser parser = new BodyParser(getDescription(), filters, debug);
            if(parser.converted())
                setDescription(parser.formatBody());
            if(getSummary().length() == 0 || force)
                setSummary(parser.formatSummary(getType()));
        }
    }

    /**
     * Returns <CODE>true</CODE> if this content should be promoted.
     */
    @Override
    public boolean isPromoted()
    {
        return true;
    }

    /**
     * Returns the event type.
     */
    public String getEventType()
    {
        return eventType;
    }

    /**
     * Sets the event type.
     */
    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    /**
     * Returns the date for the event.
     */
    public Instant getStartDate()
    {
        return getDetails().getStartDate();
    }

    /**
     * Returns the date for the event.
     */
    public long getStartDateMillis()
    {
        return getStartDate() != null ? getStartDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date for the event.
     */
    public LocalDateTime getStartDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getStartDate());
    }

    /**
     * Returns the date of the event.
     */
    public String getStartDateAsString()
    {
        return getStartDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the date of the event.
     */
    public String getStartDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(getStartDate(), pattern);
    }

    /**
     * Sets the date of the event.
     */
    public void setStartDate(Instant startDate)
    {
        getDetails().setStartDate(startDate);
    }

    /**
     * Sets the date of the event.
     */
    public void setStartDateMillis(long millis)
    {
        if(millis > 0L)
            setStartDate(Instant.ofEpochMilli(millis));
    }

    /**
     * Sets the date of the event.
     */
    public void setStartDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setStartDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date of the event.
     */
    public void setStartDateAsString(String str) throws DateTimeParseException
    {
        setStartDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date of the event.
     */
    public void setStartDateUTC(LocalDateTime startDate)
    {
        if(startDate != null)
            setStartDate(TimeUtils.toInstantUTC(startDate));
    }

    /**
     * Returns the end date for the event.
     */
    public Instant getEndDate()
    {
        return getDetails().getEndDate();
    }

    /**
     * Returns the end date for the event.
     */
    public long getEndDateMillis()
    {
        return getEndDate() != null ? getEndDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the end date for the event.
     */
    public LocalDateTime getEndDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getEndDate());
    }

    /**
     * Returns the end date of the event.
     */
    public String getEndDateAsString()
    {
        return getEndDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the end date of the event.
     */
    public String getEndDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(getEndDate(), pattern);
    }

    /**
     * Sets the end date of the event.
     */
    public void setEndDate(Instant endDate)
    {
        getDetails().setEndDate(endDate);
    }

    /**
     * Sets the end date of the event.
     */
    public void setEndDateMillis(long millis)
    {
        if(millis > 0L)
            setEndDate(Instant.ofEpochMilli(millis));
    }

    /**
     * Sets the end date of the event.
     */
    public void setEndDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setEndDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the end date of the event.
     */
    public void setEndDateAsString(String str) throws DateTimeParseException
    {
        setEndDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the end date of the event.
     */
    public void setEndDateUTC(LocalDateTime endDate)
    {
        if(endDate != null)
            setEndDate(TimeUtils.toInstantUTC(endDate));
    }

    /**
     * Returns the event timezone.
     */
    public String getTimeZone()
    {
        return getDetails().getTimeZone();
    }

    /**
     * Sets the event timezone.
     */
    public void setTimeZone(String timezone)
    {
        getDetails().setTimeZone(timezone);
    }

    /**
     * Returns the platform of the event.
     */
    public String getPlatform()
    {
        return getDetails().getPlatform();
    }

    /**
     * Sets the platform of the event.
     */
    public void setPlatform(String platform)
    {
        getDetails().setPlatform(platform);
    }

    /**
     * Returns the event location.
     */
    public String getLocation()
    {
        return getDetails().getLocation();
    }

    /**
     * Sets the event location.
     */
    public void setLocation(String location)
    {
        getDetails().setLocation(location);
    }
}