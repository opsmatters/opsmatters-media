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

package com.opsmatters.media.model.chart;

import java.util.Map;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import com.opsmatters.media.model.ConfigParser;

import static com.opsmatters.media.model.chart.ChartParameterValue.*;

/**
 * Represents a LocalDateTime selection for a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LocalDateTimeSelection extends ChartSelection<LocalDateTime>
{
    /**
     * Constructor that takes a parameter.
     */
    public LocalDateTimeSelection(ChartParameter parameter)
    {
        setParameter(parameter);
    }

    /**
     * Copy constructor.
     */
    public LocalDateTimeSelection(LocalDateTimeSelection obj)
    {
        copyAttributes(obj);
    }

    /**
     * Returns the parameter type for the selection.
     */
    public ChartParameterType getType()
    {
        return ChartParameterType.LOCAL_DATE_TIME;
    }

    /**
     * Returns the parameter value for the selection.
     */
    @Override
    public LocalDateTime value()
    {
        switch(getDefaultValue())
        {
            case START_OF_TODAY:
                return LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            case START_OF_TOMORROW:
                return LocalDateTime.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
            case START_OF_WEEK:
                return LocalDateTime.now().with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
            case START_OF_MONTH:
                return LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
            case LAST_7_DAYS:
                return LocalDateTime.now().minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
            case LAST_14_DAYS:
                return LocalDateTime.now().minus(14, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
            case LAST_30_DAYS:
                return LocalDateTime.now().minus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
            case LAST_1_MONTH:
                return LocalDateTime.now().minus(1, ChronoUnit.MONTHS).truncatedTo(ChronoUnit.DAYS);
            case LAST_2_MONTHS:
                return LocalDateTime.now().minus(2, ChronoUnit.MONTHS).truncatedTo(ChronoUnit.DAYS);
            case LAST_3_MONTHS:
                return LocalDateTime.now().minus(3, ChronoUnit.MONTHS).truncatedTo(ChronoUnit.DAYS);
            default:
                return getValue();
        }
    }

    /**
     * Returns a builder for the configuration.
     * @param parameter The chart parameter
     * @return The builder instance.
     */
    public static Builder builder(ChartParameter parameter)
    {
        return new Builder(parameter);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder extends ChartSelection.Builder<LocalDateTime, LocalDateTimeSelection, Builder>
    {
        private LocalDateTimeSelection ret = null;

        /**
         * Constructor that take a parameter.
         */
        public Builder(ChartParameter parameter)
        {
            ret = new LocalDateTimeSelection(parameter);
            super.set(ret);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            super.parse(map);
            return this;
        }

        /**
         * Returns this object.
         * @return This object
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        @Override
        public LocalDateTimeSelection build()
        {
            return ret;
        }
    }
}