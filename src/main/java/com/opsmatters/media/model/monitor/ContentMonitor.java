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
package com.opsmatters.media.model.monitor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.sql.SQLException;
import org.json.JSONObject;
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.cache.organisation.OrganisationSites;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.admin.Email;
import com.opsmatters.media.model.admin.EmailBody;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentTeaser;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.ContentLookup;
import com.opsmatters.media.model.content.crawler.CrawlerTarget;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content monitor.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentMonitor<T extends ContentTeaser> extends BaseEntity
{
    private static final Logger logger = Logger.getLogger(ContentMonitor.class.getName());

    private static final int DEFAULT_INTERVAL = 60;

    public static final String CONTENT_TYPE = "content-type";
    public static final String URL = "url";
    public static final String INTERVAL = "interval";
    public static final String SUCCESS_DATE = "success-date";
    public static final String EXECUTION_TIME = "execution-time";
    public static final String ERROR_MESSAGE = "error-message";
    public static final String RETRY = "retry";
    public static final String SUBSCRIBED_DATE = "subscribed-date";
    public static final String SITES = "sites";
    public static final String TITLE = "title";
    public static final String KEYWORDS = "keywords";
    public static final String ALERTS = "alerts";

    private String code = "";
    private String organisation = "";
    private String name = "";
    private ContentType contentType;
    private Instant executedDate;
    private Instant successDate;
    private long executionTime = -1L;
    private MonitorStatus status;
    private String url = "";
    private String snapshot = "";
    private EventType eventType;
    private String eventId = "";
    private int interval = -1;
    private String errorMessage = "";
    private int retry = 0;
    private Instant subscribedDate;
    private String sites = "";
    private String title = "";
    private String keywords = "";
    private boolean alerts = false;

    private Map<String,String> siteMap = new HashMap<String,String>();

    /**
     * Default constructor.
     */
    public ContentMonitor()
    {
    }

    /**
     * Copy constructor.
     */
    public ContentMonitor(ContentMonitor obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentMonitor obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setOrganisation(obj.getOrganisation());
            setName(obj.getName());
            setContentType(obj.getContentType());
            setStatus(obj.getStatus());
            setExecutedDate(obj.getExecutedDate());
            setSuccessDate(obj.getSuccessDate());
            setExecutionTime(obj.getExecutionTime());
            setUrl(obj.getUrl());
            setSnapshot(obj.getSnapshot());
            setEventType(obj.getEventType());
            setEventId(obj.getEventId());
            setInterval(obj.getInterval());
            setErrorMessage(obj.getErrorMessage());
            setRetry(obj.getRetry());
            setSubscribedDate(obj.getSubscribedDate());
            setSites(obj.getSites());
            setTitle(obj.getTitle());
            setKeywords(obj.getKeywords());
            setAlerts(obj.hasAlerts());
        }
    }

    /**
     * Initialise the monitor using a config and target.
     */
    public void init(ContentConfig config, CrawlerTarget target)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(config.getCode());
        setContentType(config.getType());
        setName(target.getName());
        setStatus(MonitorStatus.NEW);
        setSnapshot(new ContentSnapshot(config.getType()));
        setInterval(DEFAULT_INTERVAL);
        setSites(target.getSites());
        setAlerts(true);
    }

    /**
     * Executes a check using this monitor.
     */
    public ContentSnapshot check(int maxResults, boolean debug)
        throws IOException
    {
        return null;
    }

    /**
     * Update the last snapshot for the given change.
     */
    public boolean updateChange(ContentChange change, ContentLookup lookup, int maxResults)
        throws SQLException, IOException
    {
        boolean ret = false;
        Instant now = Instant.now();
        ContentSnapshot snapshot = check(maxResults, false);
        if(snapshot != null && !change.getSnapshotAfter().equals(snapshot.toString()))
        {
            Instant then = Instant.now();
            ContentSnapshot diff = compareSnapshot(snapshot, lookup);
            change.setSnapshotDiff(diff);
            change.setSnapshotAfter(snapshot);
            change.setUpdatedDate(now);

            setExecutedDate(then);
            setSuccessDate(then);
            setExecutionTime(Duration.between(now, then).toMillis());

            ret = true;
        }

        return ret;
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(CONTENT_TYPE, getContentType().name());
        ret.putOpt(URL, getUrl());
        ret.putOpt(INTERVAL, getInterval());
        ret.putOpt(SUCCESS_DATE, getSuccessDateMillis());
        ret.putOpt(EXECUTION_TIME, getExecutionTime());
        ret.putOpt(ERROR_MESSAGE, getErrorMessage());
        ret.putOpt(RETRY, getRetry());
        ret.putOpt(SUBSCRIBED_DATE, getSubscribedDateMillis());
        ret.putOpt(SITES, getSites());
        ret.putOpt(TITLE, getTitle());
        ret.putOpt(ALERTS, hasAlerts());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setContentType(obj.optString(CONTENT_TYPE));
        setUrl(obj.optString(URL));
        setInterval(obj.optInt(INTERVAL));
        setSuccessDateMillis(obj.optLong(SUCCESS_DATE));
        setExecutionTime(obj.optLong(EXECUTION_TIME));
        setErrorMessage(obj.optString(ERROR_MESSAGE));
        setRetry(obj.optInt(RETRY));
        setSubscribedDateMillis(obj.optLong(SUBSCRIBED_DATE));
        setSites(obj.optString(SITES));
        setTitle(obj.optString(TITLE));
        setAlerts(obj.optBoolean(ALERTS, false));
    }

    /**
     * Returns the monitor GUID.
     */
    public String toString()
    {
        return getGuid();
    }

    /**
     * Returns the monitor GUID.
     */
    public String getGuid()
    {
        return getGuid(getContentType(), code, name);
    }

    /**
     * Returns the monitor GUID.
     */
    public static String getGuid(ContentType type, String code, String name)
    {
        return String.format("%s-%s-%s", type.code(), code, name);
    }

    /**
     * Returns the monitor organisation.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the monitor organisation.
     */
    public void setCode(String code)
    {
        this.code = code;

        Organisation organisation = Organisations.get(code);
        setOrganisation(organisation != null ? organisation.getName() : "");
    }

    /**
     * Returns <CODE>true</CODE> if the monitor organisation has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
    }

    /**
     * Returns the monitor organisation name.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the monitor organisation name.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor organisation name has been set.
     */
    public boolean hasOrganisation()
    {
        return organisation != null && organisation.length() > 0;
    }

    /**
     * Returns the monitor name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the monitor name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the monitor content type.
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Sets the monitor content type.
     */
    public void setContentType(String contentType)
    {
        setContentType(ContentType.valueOf(contentType));
    }

    /**
     * Sets the monitor content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Returns the monitor status.
     */
    public MonitorStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the monitor status.
     */
    public void setStatus(String status)
    {
        setStatus(MonitorStatus.valueOf(status));
    }

    /**
     * Sets the monitor status.
     */
    public void setStatus(MonitorStatus status)
    {
        this.status = status;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor is enabled.
     */
    public boolean isActive()
    {
        return getStatus() != MonitorStatus.DISABLED;
    }

    /**
     * Set the monitor status to CHANGE.
     */
    public void setChange(ContentChange change)
    {
        if(getStatus() != MonitorStatus.CHANGE)
        {
            setStatus(MonitorStatus.CHANGE);
            setUpdatedDate(Instant.now());
            setEvent(change);
        }
    }

    /**
     * Clear the monitor status after CHANGE.
     */
    public void clearChange(ContentChange change)
    {
        if(change == null || getEventId().equals(change.getId()))
        {
            if(getStatus() == MonitorStatus.CHANGE)
            {
                setStatus(MonitorStatus.RESUMING);
                setSnapshot(change.getSnapshotAfter());
                setUpdatedDate(Instant.now());
                clearEvent();
            }
        }
    }

    /**
     * Set the monitor status to ALERT.
     */
    public void setAlert(ContentAlert alert)
    {
        if(getStatus() != MonitorStatus.ALERT)
        {
            setStatus(MonitorStatus.ALERT);
            setUpdatedDate(Instant.now());
            setEvent(alert);
        }
    }

    /**
     * Clear the monitor status after ALERT.
     */
    public void clearAlert(ContentAlert alert)
    {
        if(alert == null || getEventId().equals(alert.getId()))
        {
            if(getStatus() == MonitorStatus.ALERT)
            {
                setStatus(MonitorStatus.RESUMING);
                setUpdatedDate(Instant.now());
                clearEvent();
            }
        }
    }

    /**
     * Restarts the monitor eg. after a suspension.
     */
    public void restart()
    {
        setStatus(MonitorStatus.RESUMING);
        setUpdatedDate(Instant.now());
        setExecutedDate(null);
        clearEvent();
        setErrorMessage("");
    }

    /**
     * Returns <CODE>true</CODE> if the monitor status is EXECUTING.
     */
    public boolean isExecuting()
    {
        return status == MonitorStatus.EXECUTING;
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public Instant getExecutedDate()
    {
        return executedDate;
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public long getExecutedDateMillis()
    {
        return getExecutedDate() != null ? getExecutedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public LocalDateTime getExecutedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getExecutedDate());
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public String getExecutedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(executedDate, pattern);
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public String getExecutedDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(executedDate, pattern, timezone);
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public String getExecutedDateAsString()
    {
        return getExecutedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDate(Instant executedDate)
    {
        this.executedDate = executedDate;
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDateMillis(long millis)
    {
        if(millis > 0L)
            this.executedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the monitor item was last executed.
     */
    public void setExecutedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setExecutedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDateAsString(String str) throws DateTimeParseException
    {
        setExecutedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDateUTC(LocalDateTime executedDate)
    {
        if(executedDate != null)
            setExecutedDate(TimeUtils.toInstantUTC(executedDate));
    }

    /**
     * Returns the date the monitor was last executed successfully.
     */
    public Instant getSuccessDate()
    {
        return successDate;
    }

    /**
     * Returns the date the monitor was last executed successfully.
     */
    public long getSuccessDateMillis()
    {
        return getSuccessDate() != null ? getSuccessDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the monitor was last executed successfully.
     */
    public LocalDateTime getSuccessDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getSuccessDate());
    }

    /**
     * Returns the date the monitor was last executed successfully.
     */
    public String getSuccessDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(successDate, pattern);
    }

    /**
     * Returns the date the monitor was last executed successfully.
     */
    public String getSuccessDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(successDate, pattern, timezone);
    }

    /**
     * Returns the date the monitor was last executed successfully.
     */
    public String getSuccessDateAsString()
    {
        return getSuccessDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the monitor was last executed successfully.
     */
    public void setSuccessDate(Instant successDate)
    {
        this.successDate = successDate;
    }

    /**
     * Sets the date the monitor was last executed successfully.
     */
    public void setSuccessDateMillis(long millis)
    {
        if(millis > 0L)
            this.successDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the monitor item was last executed successfully.
     */
    public void setSuccessDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setSuccessDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the monitor was last executed successfully.
     */
    public void setSuccessDateAsString(String str) throws DateTimeParseException
    {
        setSuccessDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the monitor was last executed successfully.
     */
    public void setSuccessDateUTC(LocalDateTime successDate)
    {
        if(successDate != null)
            setSuccessDate(TimeUtils.toInstantUTC(successDate));
    }

    /**
     * Returns the time taken for the last monitor execution.
     */
    public long getExecutionTime()
    {
        return executionTime;
    }

    /**
     * Sets the time taken for the last monitor execution.
     */
    public void setExecutionTime(long executionTime)
    {
        this.executionTime = executionTime;
    }

    /**
     * Returns the monitor url.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the monitor url.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor url has been set.
     */
    public boolean hasUrl()
    {
        return url != null && url.length() > 0;
    }

    /**
     * Returns the last monitor snapshot.
     */
    public String getSnapshot()
    {
        return snapshot;
    }

    /**
     * Returns the last monitor snapshot with pretty print.
     */
    public String getPrettySnapshot()
    {
        return new ContentSnapshot(snapshot).toString(2);
    }

    /**
     * Sets the last monitor snapshot.
     */
    public void setSnapshot(String snapshot)
    {
        this.snapshot = snapshot;
    }

    /**
     * Sets the last monitor snapshot.
     */
    public void setSnapshot(ContentSnapshot snapshot)
    {
        setSnapshot(snapshot.toString());
    }

    /**
     * Sets the last monitor snapshot.
     */
    public void setPrettySnapshot(String snapshot)
    {
        setSnapshot(new ContentSnapshot(snapshot));
    }

    /**
     * Returns the event type.
     */
    public EventType getEventType()
    {
        return eventType;
    }

    /**
     * Sets the event type.
     */
    public void setEventType(EventType eventType)
    {
        this.eventType = eventType;
    }

    /**
     * Sets the event type.
     */
    public void setEventType(String eventType)
    {
        if(eventType != null && eventType.length() > 0)
            setEventType(EventType.valueOf(eventType));
    }

    /**
     * Returns the event id.
     */
    public String getEventId()
    {
        return eventId;
    }

    /**
     * Sets the event id.
     */
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    /**
     * Returns <CODE>true</CODE> if the event id has been set.
     */
    public boolean hasEventId()
    {
        return eventId != null && eventId.length() > 0;
    }

    /**
     * Sets the given event.
     */
    public void setEvent(ContentEvent event)
    {
        setEventType(event.getType());
        setEventId(event.getId());
    }

    /**
     * Clears the event id and type.
     */
    public void clearEvent()
    {
        this.eventType = null;
        this.eventId = "";
    }

    /**
     * Returns the interval between monitor checks (in minutes).
     */
    public int getInterval()
    {
        return interval;
    }

    /**
     * Sets the interval between monitor checks (in minutes).
     */
    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    /**
     * Returns the monitor error message.
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Sets the monitor error message.
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor error message has been set.
     */
    public boolean hasErrorMessage()
    {
        return errorMessage != null && errorMessage.length() > 0;
    }

    /**
     * Returns the number of retries since the last error.
     */
    public int getRetry()
    {
        return retry;
    }

    /**
     * Sets the number of retries since the last error.
     */
    public void setRetry(int retry)
    {
        this.retry = retry;
    }

    /**
     * Returns the date the monitor was last subscribed.
     */
    public Instant getSubscribedDate()
    {
        return subscribedDate;
    }

    /**
     * Returns the date the monitor was last subscribed.
     */
    public long getSubscribedDateMillis()
    {
        return getSubscribedDate() != null ? getSubscribedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the monitor was last subscribed.
     */
    public String getSubscribedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(getSubscribedDate(), pattern);
    }

    /**
     * Returns the date the monitor was last subscribed.
     */
    public String getSubscribedDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(getSubscribedDate(), pattern, timezone);
    }

    /**
     * Returns the date the monitor was last subscribed.
     */
    public String getSubscribedDateAsString()
    {
        return getSubscribedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the monitor was last subscribed.
     */
    public void setSubscribedDate(Instant subscribedDate)
    {
        this.subscribedDate = subscribedDate;
    }

    /**
     * Sets the date the monitor was last subscribed.
     */
    public void setSubscribedDateMillis(long millis)
    {
        if(millis > 0L)
            setSubscribedDate(Instant.ofEpochMilli(millis));
    }

    /**
     * Sets the date the monitor item was last subscribed.
     */
    public void setSubscribedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setSubscribedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the monitor was last subscribed.
     */
    public void setSubscribedDateAsString(String str) throws DateTimeParseException
    {
        setSubscribedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the monitor sites.
     */
    public String getSites()
    {
        return sites;
    }

    /**
     * Sets the monitor sites.
     */
    public void setSites(String sites)
    {
        this.sites = sites;

        siteMap.clear();
        if(sites != null && sites.length() > 0)
        {
            List<String> siteList = StringUtils.toList(sites);
            for(String site : siteList)
                siteMap.put(site, site);
        }
    }

    /**
     * Returns <CODE>true</CODE> if this monitor is configured for the given site.
     */
    public boolean hasSite(Site site)
    {
        return hasSite(site.getId());
    }

    /**
     * Returns <CODE>true</CODE> if this monitor is configured for the given site.
     */
    public boolean hasSite(String siteId)
    {
        return siteMap.size() == 0 || siteMap.get(siteId) != null;
    }

    /**
     * Returns the monitor crawled page title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the monitor crawled page title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the keywords to filter on when page loading.
     */
    public String getKeywords()
    {
        return keywords;
    }

    /**
     * Sets the keywords to filter on when page loading.
     */
    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    /**
     * Returns <CODE>true</CODE> if there are keywords to filter on when page loading.
     */
    public boolean hasKeywords()
    {
        return keywords != null && keywords.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if this monitor has content alerts.
     */
    public boolean hasAlerts()
    {
        return alerts;
    }

    /**
     * Returns <CODE>true</CODE> if this monitor has content alerts.
     */
    public Boolean getAlertsObject()
    {
        return Boolean.valueOf(hasAlerts());
    }

    /**
     * Set to <CODE>true</CODE> if this monitor has content alerts.
     */
    public void setAlerts(boolean alerts)
    {
        this.alerts = alerts;
    }

    /**
     * Set to <CODE>true</CODE> if this monitor has content alerts.
     */
    public void setAlertsObject(Boolean alerts)
    {
        setAlerts(alerts != null && alerts.booleanValue());
    }

    /**
     * Compare the given snapshot with the current one.
     */
    public ContentSnapshot compareSnapshot(ContentSnapshot snapshot, ContentLookup lookup)
        throws SQLException
    {
        ContentSnapshot current = new ContentSnapshot(getSnapshot());
        ContentSnapshot latest = new ContentSnapshot(snapshot);

        if(lookup != null)
            lookup.setOrganisations(OrganisationSites.list(getCode()));

        return ContentSnapshot.compare(getCode(),
            current, latest, lookup, getContentType() != ContentType.VIDEO);
    }

    /**
     * Returns the email for a monitor with an alert.
     */
    public Email getAlertEmail(AlertReason reason)
    {
        String subject = String.format("Monitor %s: %s", reason.name(), getGuid());
        EmailBody body = new EmailBody()
            .addParagraph("The following monitor has changed:")
            .addTable(new String[][]
            {
                {"ID", getGuid()},
                {"Organisation", getOrganisation()},
                {"Status", getStatus().name()},
                {"Reason", reason.value()},
                {"Updated", getUpdatedDateAsString(Formats.CONTENT_DATE_FORMAT)},
            });
        return new Email(subject, body);
    }
}