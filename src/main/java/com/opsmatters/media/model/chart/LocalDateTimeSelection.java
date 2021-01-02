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
    public LocalDateTimeSelection(ChartParameterName parameter)
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
     * Reads the object from the given YAML Document.
     */
    public LocalDateTimeSelection(ChartParameterName parameter, Map<String, Object> map)
    {
        super(map);
        setParameter(parameter);
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
            default:
                return getValue();
        }
    }
}