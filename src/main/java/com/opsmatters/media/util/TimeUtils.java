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

package com.opsmatters.media.util;

import java.util.List;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.Locale;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.DateTimeException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import org.apache.commons.lang3.LocaleUtils;

/**
 * A set of utility methods to perform miscellaneous tasks related to dates and calendars.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TimeUtils
{
    /**
     * The default timezone.
     */
    public static final String DEFAULT_TIMEZONE = "Europe/London";

    /**
     * The default country.
     */
    public static final String DEFAULT_COUNTRY = "GB";

    /**
     * The default timezone.
     */
    public static final TimeZone defaultTZ = new SimpleTimeZone(0, "");

    /**
     * The current timezone.
     */
    public static TimeZone currentTZ = null;

    /**
     * The current locale.
     */
    public static Locale currentLocale = null;

    /**
     * The cache of locale data.
     */
    private static final ConcurrentMap<Locale, int[]> localeData = new ConcurrentHashMap(3);

    /**
     * The name of the BST time zone.
     */
    public static final String BST_TZ       = "GMT0BST";

    public static SimpleTimeZone tzBST;

    static
    {
        // Set up the default GMT0BST time zone
        tzBST = new SimpleTimeZone(0,     // no offset from GMT
            BST_TZ,                       // individualized tz id
            Calendar.MARCH,-1,Calendar.SUNDAY,1*60*60*1000,    // last Sun Mar 1AM
            Calendar.OCTOBER,-1,Calendar.SUNDAY,2*60*60*1000); // last Sun Oct 2AM
    }

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private TimeUtils()
    {
    }    

    /**
     * Returns <CODE>true</CODE> if the given date is inside the start and end hours.
     * @param dt The date to be checked
     * @param tz The timezone associated with the date
     * @param from The start hour to check that the date is after
     * @param to The end hour to check that the date is before
     * @param tolerance The tolerance that by the which the date can be outside the range but still be considered inside
     * @return <CODE>true</CODE> if the given date is inside the start and end hours
     */
    public static boolean isBetweenHours(long dt, TimeZone tz, int from, int to, int tolerance) 
    {
        long start = 0L;
        long end = 0L;

        // If from > to then the "to" date may be in the next day
        //   or the "from" date may be in the previous day, depending
        //   on whether or not it's before or after midnight
        if(from > to)
        {
            // Assume that start is in today and end is in tomorrow
            start = getDateForHour(dt, tz, from, 0);
            end = getDateForHour(dt, tz, to, 1)+(tolerance*1000L);

            // If we end up before the start date,
            //   try moving the dates back by a day
            if(dt < start) // move start to yesterday
            {
                start = getDateForHour(dt, tz, from, -1);
                end = getDateForHour(dt, tz, to, 0)+(tolerance*1000L);
            }
        }
        else // to > from
        {
            start = getDateForHour(dt, tz, from, 0);
            end = getDateForHour(dt, tz, to, 0)+(tolerance*1000L);
        }

        return dt >= start && dt <= end;
    }

    /**
     * Returns <CODE>true</CODE> if the current date is inside the start and end hours.
     * @param from The start hour to check that the date is after
     * @param to The end hour to check that the date is before
     * @return <CODE>true</CODE> if the current date is inside the start and end hours
     */
    public static boolean isBetweenHours(int from, int to) 
    {
        return isBetweenHours(System.currentTimeMillis(), currentTZ, from, to, 0);
    }

    /**
     * Returns <CODE>true</CODE> if the current date is inside the start and end hours.
     * @param from The start hour to check that the date is after
     * @param to The end hour to check that the date is before
     * @param tz The timezone associated with the date
     * @return <CODE>true</CODE> if the current date is inside the start and end hours
     */
    public static boolean isBetweenHours(int from, int to, TimeZone tz) 
    {
        return isBetweenHours(System.currentTimeMillis(), tz, from, to, 0);
    }

    /**
     * Returns <CODE>true</CODE> if the given date is inside the start and end hours.
     * @param dt The date to be checked
     * @param from The start hour to check that the date is after
     * @param to The end hour to check that the date is before
     * @param tz The timezone associated with the date
     * @return <CODE>true</CODE> if the given date is inside the start and end hours
     */
    public static boolean isBetweenHours(long dt, TimeZone tz, int from, int to) 
    {
        return isBetweenHours(dt, tz, from, to, 0);
    }

    /**
     * Returns the date for the given hour.
     * @param dt The date to be checked
     * @param tz The timezone associated with the date
     * @param hour The hour to be checked
     * @param dayoffset The day of the month to offset
     * @return The date for the given hour
     */
    private static long getDateForHour(long dt, TimeZone tz, int hour, int dayoffset)
    {
        Calendar c = getCalendar(tz);
        c.setTimeInMillis(dt);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        int mm = c.get(Calendar.MONTH);
        int yy = c.get(Calendar.YEAR);
        c.set(yy, mm, dd, hour, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        if(dayoffset != 0)
            c.add(Calendar.DAY_OF_MONTH, dayoffset);
        return c.getTimeInMillis();
    }

    /**
     * Returns a new calendar object using the given timezone and locale.
     * @param tz The timezone associated with the new calendar
     * @param locale The locale associated with the new calendar
     * @return A new calendar object for the given timezone and locale
     */
    public static Calendar getCalendar(TimeZone tz, Locale locale)
    {
        if(tz == null)
            tz = getCurrentTimeZone();
        if(locale == null)
            locale = getCurrentLocale();
        return Calendar.getInstance(tz, locale);
    }

    /**
     * Returns a new calendar object using the given timezone and default locale.
     * @param tz The timezone associated with the new calendar
     * @return A new calendar object for the given timezone
     */
    public static Calendar getCalendar(TimeZone tz)
    {
        return getCalendar(tz, null);
    }

    /**
     * Returns a new calendar object in the current timezone and default locale.
     * @return A new calendar object for the current timezone
     */
    public static Calendar getCalendar()
    {
        return getCalendar(null, null);
    }

    /**
     * Returns the current time zone.
     * @return The current time zone
     */
    public static TimeZone getCurrentTimeZone()
    {
        if(currentTZ == null)
            setCurrentTimeZone(getUserTimeZone());
        return currentTZ;
    }

    /**
     * Sets the current time zone.
     * @param tz The current time zone to set
     */
    public static void setCurrentTimeZone(TimeZone tz)
    {
        currentTZ = tz;
    }

    /**
     * Returns the current configured time zone.
     * <P>
     * Converts GMT to British Summer Time (BST) so that it uses Daylight Savings.
     * @return The current configured time zone
     */
    public static TimeZone getUserTimeZone()
    {
        TimeZone ret = null;

        // First try the configured timezone
        String tz = DEFAULT_TIMEZONE;
        ret = AppTimeZone.getTimeZoneById(tz);
        if(ret == null)
        {
            // Next try the locale timezone
            tz = System.getProperty("user.timezone");

            // Next try the default timezone
            if(tz == null || tz.length() == 0)
                ret = TimeZone.getDefault();
            else if(tz.equals("GMT"))
                ret = tzBST;                     // Use BST instead of GMT
            else
                ret = TimeZone.getTimeZone(tz);  // Otherwise use the standard TZ
        }

        return ret;
    }

    /**
     * Returns the current locale.
     * @return The current configured locale
     */
    public static Locale getCurrentLocale()
    {
        if(currentLocale == null)
            setCurrentLocale(getUserLocale());
        return currentLocale;
    }

    /**
     * Sets the current locale.
     * @param locale The current configured locale to set
     */
    public static void setCurrentLocale(Locale locale)
    {
        currentLocale = locale;
    }

    /**
     * Returns the locale for the current country.
     * @return The locale for the current country
     */
    public static Locale getUserLocale()
    {
        String country = System.getProperty("user.country");
        return getLocale(country);
    }

    /**
     * Returns the country from the default locale.
     * @return The current country from the default locale
     */
    public static String getCurrentCountry()
    {
        return getCurrentLocale().getCountry();
    }

    /**
     * Returns the locale for the given country.
     * @param country The country for the locale
     * @return The locale for the given country
     */
    public static Locale getLocale(String country)
    {
        if(country == null || country.length() == 0)
            country = Locale.getDefault().getCountry();
        List<Locale> locales = LocaleUtils.languagesByCountry(country);
        Locale locale = Locale.getDefault();
        if(locales.size() > 0)
            locale = locales.get(0); // Use the first locale that matches the country
        return locale;
    }

    /**
     * Set the attributes of the given calendar from the given locale.
     * @param calendar The calendar to set the date settings on
     * @param locale The locale to use for the date settings
     */
    public static void setCalendarData(Calendar calendar, Locale locale)
    {
        int[] array = (int[])localeData.get(locale);
        if(array == null)
        {
            Calendar c = Calendar.getInstance(locale);
            array = new int[2];
            array[0] = c.getFirstDayOfWeek();
            array[1] = c.getMinimalDaysInFirstWeek();
            localeData.putIfAbsent(locale, array);
        }
        calendar.setFirstDayOfWeek(array[0]);
        calendar.setMinimalDaysInFirstWeek(array[1]);
    }

    /**
     * Returns the given date adding the given number of days.
     * @param dt The date to add the days to
     * @param days The number of days to add. To subtract days, use a negative value.
     * @return The date with the given days added
     */
    public static Date addDays(long dt, int days)
    {
        Calendar c = getCalendar();
        if(dt > 0L)
            c.setTimeInMillis(dt);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /**
     * Returns the system date adding the given number of days.
     * @param days The number of days to add. To subtract days, use a negative value.
     * @return The date with the given days added
     */
    public static Date addDays(int days)
    {
        return addDays(0L, days);
    }

    /**
     * Returns the time portion from the given date.
     * @param dt The date from which to extract the time
     * @return the time portion
     */
    public static long getTime(long millis)
    {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return ((c.get(Calendar.HOUR_OF_DAY)*3600L)+(c.get(Calendar.MINUTE)*60L)+c.get(Calendar.SECOND))*1000L;
    }

    /**
     * Returns <CODE>true</CODE> if the given date has the time portion set.
     * @param dt The date to test
     * @return <CODE>true</CODE> if the given date has the time portion set
     */
    public static boolean hasTime(long millis)
    {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return (c.get(Calendar.HOUR_OF_DAY)+c.get(Calendar.MINUTE)+c.get(Calendar.SECOND)) > 0;
    }

    /**
     * Returns the given time formatted as a number of days, hours and minutes.
     * @param dt The time to be parsed
     * @return The given time formatted as a number of days, hours and minutes
     */
    static public String getLongFormattedDays(long dt)
    {
        StringBuffer ret = new StringBuffer();

        long days = dt/86400000L;
        long millis = dt-(days*86400000L);
        if(days > 0)
        {
            ret.append(Long.toString(days));
            ret.append(" day");
            if(days > 1)
                ret.append("s");
        }

        long hours = millis/3600000L;
        millis = millis-(hours*3600000L);

        if(hours > 0)
        {
            if(ret.length() > 0)
                ret.append(" ");
            ret.append(Long.toString(hours));
            ret.append(" hour");
            if(hours > 1)
                ret.append("s");
        }

        long minutes = millis/60000L;
        millis = millis-(minutes*60000L);

        if(minutes > 0)
        {
            if(ret.length() > 0)
                ret.append(" ");
            ret.append(Long.toString(minutes));
            ret.append(" minute");
            if(minutes > 1)
                ret.append("s");
        }

        long seconds = millis/1000L;
        if(seconds > 0)
        {
            if(ret.length() > 0)
                ret.append(" ");
            ret.append(Long.toString(seconds));
            ret.append(" second");
            if(seconds > 1)
                ret.append("s");
        }

        return ret.toString();
    }

    /**
     * Returns the given fractional number of seconds formatted as "0.0##".
     * @param t The seconds to be formatted
     * @return The given fractional number of seconds formatted as "0.0##"
     */
    static public String getFormattedSeconds(double t)
    {
        DecimalFormat f = new DecimalFormat("0.0##");
        return f.format(t)+"s";
    }

    /**
     * Returns the given LocalDateTime in UTC as an instant.
     */
    public static Instant toInstantUTC(LocalDateTime dt)
    {
        Instant ret = null;

        if(dt != null)
        {
            Instant instant = dt.atZone(ZoneOffset.UTC).toInstant();
            if(instant.toEpochMilli() > 0L)
                ret = instant;
        }

        return ret;
    }

    /**
     * Returns the given instant as a LocalDateTime in UTC.
     */
    public static LocalDateTime toDateTimeUTC(Instant dt)
    {
        return dt != null ? LocalDateTime.ofInstant(dt, ZoneOffset.UTC) : null;
    }

    /**
     * Returns the given instant in UTC with the time part set to zero.
     */
    static public Instant truncateTimeUTC(Instant dt)
    {
        return dt != null ? toDateTimeUTC(dt).truncatedTo(ChronoUnit.DAYS).toInstant(ZoneOffset.UTC) : null;
    }

    /**
     * Returns the current time in UTC with the time part set to zero.
     */
    static public Instant truncateTimeUTC()
    {
        return truncateTimeUTC(Instant.now());
    }

    /**
     * Returns the given instant as a string in UTC.
     */
    public static String toStringUTC(Instant dt, String pattern)
    {
        String ret = "";

        if(dt != null && dt.toEpochMilli() > 0L)
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            ret = LocalDateTime.ofInstant(dt, ZoneOffset.UTC).format(formatter);
        }

        return ret;
    }

    /**
     * Returns the given instant as a string in the given timezone.
     */
    public static String toString(Instant dt, String pattern, String timezone)
    {
        String ret = "";

        if(dt != null && dt.toEpochMilli() > 0L)
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            ret = dt.atZone(ZoneId.of(timezone)).format(formatter);
        }

        return ret;
    }

    /**
     * Returns the given millis as a string in UTC.
     */
    public static String toStringUTC(long millis, String pattern)
    {
        return toStringUTC(Instant.ofEpochMilli(millis), pattern);
    }

    /**
     * Returns the given millis as a string in UTC using the default pattern (dd-MM-yyyy HH:mm:ss).
     */
    public static String toStringUTC(long millis)
    {
        return toStringUTC(Instant.ofEpochMilli(millis), Formats.DATETIME_FORMAT);
    }

    /**
     * Returns the current date as a string in UTC.
     */
    public static String toStringUTC(String pattern)
    {
        return toStringUTC(System.currentTimeMillis(), pattern);
    }

    /**
     * Returns the given LocaDateTime as millis in UTC.
     */
    public static long toMillisUTC(LocalDateTime dt)
    {
        return dt != null ? dt.toInstant(ZoneOffset.UTC).toEpochMilli() : 0L;
    }

    /**
     * Returns the given date string as millis in UTC.
     */
    public static long toMillisUTC(String str, String pattern) throws DateTimeParseException
    {
        Instant instant = toInstantUTC(str, pattern);
        return instant != null ? instant.toEpochMilli() : 0L;
    }

    /**
     * Returns the given LocalDateTime as an instant in the given timezone.
     */
    public static Instant toInstant(LocalDateTime dt, String timezone)
    {
        Instant ret = null;

        if(dt != null)
        {
            Instant instant = dt.atZone(ZoneId.of(timezone)).toInstant();
            if(instant.toEpochMilli() > 0L)
                ret = instant;
        }

        return ret;
    }

    /**
     * Returns the given instant as a LocalDateTime in the given timezone.
     */
    public static LocalDateTime toDateTime(Instant dt, String timezone)
    {
        return dt != null ? LocalDateTime.ofInstant(dt, ZoneId.of(timezone)) : null;
    }

    /**
     * Returns the formatter for the given date pattern.
     */
    private static DateTimeFormatter getFormatter(String pattern)
    {
        DateTimeFormatter formatter = null;

        if(pattern.startsWith(Formats.ISO8601_FORMAT))
        {
            formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        }
        else
        {
            formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter();
        }

        return formatter;
    }

    /**
     * Returns the given time string in ISO format as millis.
     */
    public static long toMillisTime(String str, String pattern) throws DateTimeParseException
    {
        DateTimeFormatter formatter = getFormatter(pattern);
        TemporalAccessor dt = formatter.parse(preprocessTimeString(str), new ParsePosition(0));
        return LocalTime.from(dt).toSecondOfDay()*1000L;
    }

    /**
     * Returns the given date string in UTC as an instant.
     */
    public static Instant toInstantUTC(String str, String pattern) throws DateTimeParseException
    {
        Instant ret = null;

        if(str != null && str.length() > 0)
        {
            DateTimeFormatter formatter = getFormatter(pattern);
            TemporalAccessor dt = formatter.parse(preprocessDateString(str), new ParsePosition(0));

            try
            {
                // Try to parse as date time
                ret = LocalDateTime.from(dt).toInstant(ZoneOffset.UTC);
            }
            catch(DateTimeException e)
            {
                // Next try to parse as date only
                ret = LocalDate.from(dt).atStartOfDay().toInstant(ZoneOffset.UTC);
            }
        }

        return ret;
    }

    /**
     * Preprocess date strings that won't parse.
     * @param s The date to be parsed
     * @return The given date string prepared for parsing
     */
    static private String preprocessDateString(String s)
    {
        String ret = s;

        if(ret != null)
        {
            ret = ret.trim();

            // Remove ordinal numbers as they can't be parsed
            ret = ret.replaceAll("(?<=\\d)( ?st| ?nd| ?rd| ?th)", "");

            // Replace "Sept" as it doesnt parse
            ret = ret.replaceAll("Sept( |\\.)", "Sep$1");
        }

        return ret;
    }

    /**
     * Preprocess time strings that won't parse.
     * @param s The time to be parsed
     * @return The given time string prepared for parsing
     */
    static private String preprocessTimeString(String s)
    {
        String ret = s;

        if(ret != null)
        {
            ret = ret.trim();

            // Replace "a.m" as it doesnt parse
            ret = ret.replaceAll("([AaPp])\\.([Mm])\\.?", "$1$2");
        }

        return ret;
    }
}