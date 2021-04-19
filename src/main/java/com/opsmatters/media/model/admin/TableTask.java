/*
 * Copyright 2021 Gerald Curley
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
package com.opsmatters.media.model.admin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a database table task to be executed.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TableTask extends Task
{
    private String tableName = "";
    private int period = -1;
    private ChronoUnit periodUnit = ChronoUnit.MONTHS;

    /**
     * Default constructor.
     */
    public TableTask()
    {
        setType(TaskType.DATA_CLEANING);
    }

    /**
     * Copy constructor.
     */
    public TableTask(TableTask obj)
    {
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a name.
     */
    public TableTask(String name)
    {
        this();
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
        setInterval(1);
        setPeriod(1);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(TableTask obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTableName(obj.getTableName());
            setPeriod(obj.getPeriod());
            setPeriodUnit(obj.getPeriodUnit());
        }
    }

    /**
     * Returns the table name.
     */
    public String getTableName()
    {
        return tableName;
    }

    /**
     * Sets the table name.
     */
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    /**
     * Returns the retention period.
     */
    public int getPeriod()
    {
        return period;
    }

    /**
     * Sets the retention period.
     */
    public void setPeriod(int period)
    {
        this.period = period;
    }

    /**
     * Returns the retention period unit.
     */
    public ChronoUnit getPeriodUnit()
    {
        return periodUnit;
    }

    /**
     * Sets the retention period unit.
     */
    public void setPeriodUnit(ChronoUnit periodUnit)
    {
        this.periodUnit = periodUnit;
    }

    /**
     * Sets the retention period unit.
     */
    public void setPeriodUnit(String periodUnit)
    {
        setPeriodUnit(ChronoUnit.valueOf(periodUnit));
    }
}