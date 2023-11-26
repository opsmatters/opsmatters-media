/*
 * Copyright 2023 Gerald Curley
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

import java.time.Instant;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.model.content.ResourceItem;

/**
 * Class representing an event list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventItem extends ResourceItem<Event>
{
    private Event content = new Event();

    /**
     * Default constructor.
     */
    public EventItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public EventItem(EventItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a event.
     */
    public EventItem(Event obj)
    {
        super.set(content);
        copyAttributes(obj);
        content.setConfigured(false);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(EventItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(Event obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public Event get()
    {
        return content;
    }

    /**
     * Returns the event type.
     */
    public String getEventType()
    {
        return content.getEventType();
    }

    /**
     * Sets the event type.
     */
    public void setEventType(String eventType)
    {
        content.setEventType(eventType);
    }

    /**
     * Sets the event type.
     */
    public void setEventType(EventType eventType)
    {
        setEventType(eventType.value());
    }

    /**
     * Returns the start date of the event.
     */
    public Instant getStartDate()
    {
        return content.getStartDate();
    }

    /**
     * Returns the start date of the event.
     */
    public long getStartDateMillis()
    {
        return content.getStartDateMillis();
    }

    /**
     * Sets the start date of the event.
     */
    public void setStartDate(Instant startDate)
    {
        content.setStartDate(startDate);
    }

    /**
     * Sets the start date of the event.
     */
    public void setStartDateMillis(long millis)
    {
        content.setStartDateMillis(millis);
    }

    /**
     * Returns the URL of the event.
     */
    public String getUrl()
    {
        return content.getUrl();
    }

    /**
     * Sets the URL of the event.
     */
    public void setUrl(String url)
    {
        content.setUrl(url);
    }

    /**
     * Returns <CODE>true</CODE> if the URL has been set.
     */
    public boolean hasUrl()
    {
        return content.hasUrl();
    }

    /**
     * Returns the event timezone.
     */
    public String getTimeZone()
    {
        return content.getTimeZone();
    }

    /**
     * Sets the event timezone.
     */
    public void setTimeZone(String timezone)
    {
        content.setTimeZone(timezone);
    }
}