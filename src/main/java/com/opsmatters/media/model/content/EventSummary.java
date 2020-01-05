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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing an event summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventSummary extends ResourceSummary
{
    private Instant startDate;
    private Instant endDate;

    /**
     * Default constructor.
     */
    public EventSummary()
    {
    }

    /**
     * Constructor that takes a url.
     */
    public EventSummary(String url, boolean removeParameters)
    {
        setUrl(url, removeParameters);
    }

    /**
     * Copy constructor.
     */
    public EventSummary(EventSummary obj)
    {
        super(obj);

        if(obj != null)
        {
            setStartDate(obj.getStartDate());
            setEndDate(obj.getEndDate());
        }
    }

    /**
     * Returns the event url.
     */
    @Override
    public String getUniqueId()
    {
        return getUrl();
    }

    /**
     * Returns the date for the event.
     */
    public Instant getStartDate()
    {
        return startDate;
    }

    /**
     * Returns the date for the event.
     */
    public long getStartDateMillis()
    {
        return startDate != null ? startDate.toEpochMilli() : 0L;
    }

    /**
     * Returns the date of the event.
     */
    public String getStartDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(startDate, pattern);
    }

    /**
     * Returns the date of the event.
     */
    public String getStartDateAsString()
    {
        return getStartDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date of the event.
     */
    public void setStartDate(Instant startDate)
    {
        this.startDate = startDate;
    }

    /**
     * Sets the date of the event.
     */
    public void setStartDateMillis(long millis)
    {
        if(millis > 0L)
            this.startDate = Instant.ofEpochMilli(millis);
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
     * Returns the end date for the event.
     */
    public Instant getEndDate()
    {
        return endDate;
    }

    /**
     * Returns the end date for the event.
     */
    public long getEndDateMillis()
    {
        return endDate != null ? endDate.toEpochMilli() : 0L;
    }

    /**
     * Returns the end date of the event.
     */
    public String getEndDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(endDate, pattern);
    }

    /**
     * Returns the end date of the event.
     */
    public String getEndDateAsString()
    {
        return getEndDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the end date of the event.
     */
    public void setEndDate(Instant endDate)
    {
        this.endDate = endDate;
    }

    /**
     * Sets the end date of the event.
     */
    public void setEndDateMillis(long millis)
    {
        if(millis > 0L)
            this.endDate = Instant.ofEpochMilli(millis);
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
}