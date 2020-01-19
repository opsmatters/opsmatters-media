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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.config.content.EventConfiguration;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing an event resource.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventResource extends Resource
{
    private EventDetails details = new EventDetails();
    private String activityType = "";

    /**
     * Default constructor.
     */
    public EventResource()
    {
        setContentDetails(details);
    }

    /**
     * Constructor that takes an event resource.
     */
    public EventResource(EventResource obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes an event.
     */
    public EventResource(String code, EventDetails obj)
    {
        this();
        init();
        setCode(code);
        setEventDetails(obj);
    }

    /**
     * Constructor that takes an event summary.
     */
    public EventResource(String code, EventSummary obj)
    {
        this();
        init();
        setCode(code);
        setContentSummary(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(EventResource obj)
    {
        super.copyAttributes(obj);

        setEventDetails(obj.getEventDetails());
        setActivityType(new String(obj.getActivityType() != null ? obj.getActivityType() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public EventResource(String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        String id = values[0];
        String pubdate = values[1];
        String startDate = values[2];
        String endDate = values[3];
        String timezone = values[4];
        String title = values[5];
        String summary = values[6];
        String description = values[7];
        String organisation = values[8];
        String activityType = values[9];
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
        setActivityType(activityType);
        setLocation(location);
        setUrl(url, false);
        setLinkText(linkText);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
    }

    /**
     * Constructor that takes a JSON object.
     */
    public EventResource(JSONObject obj)
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

        setStartDateMillis(obj.optLong(Fields.START_DATE));
        setEndDateMillis(obj.optLong(Fields.END_DATE));
        if(getPublishedDateMillis() == 0L) // Default to start date
            setPublishedDate(TimeUtils.truncateTimeUTC(getStartDate()));
        setTimeZone(obj.optString(Fields.TIMEZONE));
        setActivityType(obj.optString(Fields.ACTIVITY_TYPE));
        setLocation(obj.optString(Fields.LOCATION));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.put(Fields.START_DATE, getStartDateMillis());
        ret.put(Fields.END_DATE, getEndDateMillis());
        ret.putOpt(Fields.TIMEZONE, getTimeZone());
        ret.putOpt(Fields.ACTIVITY_TYPE, getActivityType());
        ret.putOpt(Fields.LOCATION, getLocation());

        return ret;
    }

    /**
     * Returns the set of output fields from the resource.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.START_DATE, getStartDateAsString());
        ret.put(Fields.END_DATE, getEndDateAsString());
        ret.put(Fields.TIMEZONE, getTimeZone());
        ret.put(Fields.LOCATION, getLocation());
        ret.put(Fields.ACTIVITY_TYPE, getActivityType());

        return ret;
    }

    /**
     * Returns a new resource with defaults.
     */
    public static EventResource getDefault(EventConfiguration config) throws DateTimeParseException
    {
        EventResource resource = new EventResource();

        resource.init();
        resource.setTitle(config.getOrganisation()+": New Event");
        resource.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));
        resource.setStartDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));
        resource.addStartTime(config);

        return resource;
    }

    /**
     * Adds the default start time to the start date for the event.
     */
    public void addStartTime(EventConfiguration config)
    {
        if(config.getFields().containsKey(Fields.START_TIME))
        {
            String start = config.getFields().get(Fields.START_TIME);
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
     * Use the given organisation to set defaults for the resource.
     */
    public void init(Organisation organisation)
    {
    }

    /**
     * Use the given configuration to set defaults for the resource.
     */
    public void init(EventConfiguration config)
    {
        super.init(config);

        setActivityType(config.getField(Fields.ACTIVITY_TYPE, ""));
        setLinkText(config.getField(Fields.LINK_TEXT, ""));
    }

    /**
     * Prepare the fields in the resource using the given configuration.
     */
    public void prepare(EventConfiguration config)
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));
        setDescription(FormatUtils.getFormattedDescription(getDescription()));
        setSummary(FormatUtils.getFormattedSummary(getDescription()));

        // Use the default timezone if an resource timezone wasn't found
        if(config.hasField(Fields.TIMEZONE) && getTimeZone().length() == 0)
            setTimeZone(config.getField(Fields.TIMEZONE));

        // Use the default location if an resource location wasn't found
        if(config.hasField(Fields.LOCATION) && getLocation().length() == 0)
            setLocation(config.getField(Fields.LOCATION));
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
     * Returns the event details.
     */
    public EventDetails getEventDetails()
    {
        return details;
    }

    /**
     * Sets the event details.
     */
    public void setEventDetails(EventDetails obj)
    {
        setContentSummary(obj);
        setTimeZone(new String(obj.getTimeZone() != null ? obj.getTimeZone() : ""));
        setLocation(new String(obj.getLocation() != null ? obj.getLocation() : ""));
        setContentDetails(true);
    }

    /**
     * Sets the event details from a summary.
     */
    public void setContentSummary(EventSummary obj)
    {
        super.setContentSummary(obj);
        setStartDate(obj.getStartDate());
        setEndDate(obj.getEndDate());
        setUrl(new String(obj.getUrl()), false);
    }

    /**
     * Returns the activity type.
     */
    public String getActivityType()
    {
        return activityType;
    }

    /**
     * Sets the activity type.
     */
    public void setActivityType(String activityType)
    {
        this.activityType = activityType;
    }

    /**
     * Returns the date for the event.
     */
    public Instant getStartDate()
    {
        return details.getStartDate();
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
        details.setStartDate(startDate);
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
        return details.getEndDate();
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
        details.setEndDate(endDate);
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
        return details.getTimeZone();
    }

    /**
     * Sets the event timezone.
     */
    public void setTimeZone(String timezone)
    {
        details.setTimeZone(timezone);
    }

    /**
     * Returns the event location.
     */
    public String getLocation()
    {
        return details.getLocation();
    }

    /**
     * Sets the event location.
     */
    public void setLocation(String location)
    {
        details.setLocation(location);
    }
}