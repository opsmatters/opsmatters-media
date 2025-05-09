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

import java.io.Serializable;

/**
 * Represents the name of a default value for a parameter.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ChartParameterValue implements Serializable
{
    START_OF_TODAY,
    START_OF_TOMORROW,
    START_OF_WEEK,
    START_OF_MONTH,
    LAST_7_DAYS,
    LAST_14_DAYS,
    LAST_30_DAYS,
    LAST_1_MONTH,
    LAST_2_MONTHS,
    LAST_3_MONTHS,
    CURRENT_SITE,
    CURRENT_SESSION,
    CURRENT_YESTERDAY,
    CURRENT_WEEK,
    CURRENT_MONTH,
    CURRENT_YEAR;
}