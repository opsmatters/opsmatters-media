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
package com.opsmatters.media.db.provider;

import java.sql.Types;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * The base class for all SQL Server-specific database attributes.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SQLServerProvider extends DBProvider
{
    /**
     * The name of the default database.
     */
    public static final String DEFAULT = "default";

    /**
     * Default constructor.
     */
    public SQLServerProvider()
    {
        addReservedWord("PERCENT");
    }

    /**
     * Define the data types for the SQLServer provider.
     */
    @Override
    public void defineTypes()
    {
        super.defineTypes();
        setTypeName(Types.BIGINT, "NUMERIC(19)");
        setTypeName(Types.TIMESTAMP, "DATETIME2");
        setTypeName(Types.LONGVARCHAR, "VARCHAR(MAX)");
        setTypeName(Types.VARBINARY, "VARBINARY(MAX)");
        setTypeName(Types.BOOLEAN, "BIT");
        setDefault(Types.BOOLEAN, "0");
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is a constraint violation.
     */
    @Override
    public boolean isConstraintViolation(Exception ex)
    {
        boolean ret = false;
        if(ex != null)
            ret = ex.getMessage() != null && ex.getMessage().indexOf("Violation") != -1;
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by inserting a value too large for a BLOB.
     */
    @Override
    public boolean isDataTooLongException(Exception ex)
    {
        boolean ret = false;
        if(ex != null && ex.getMessage() != null)
            ret = ex.getMessage().indexOf("bigger than max size") != -1
                || ex.getMessage().indexOf("unreasonable conversion") != -1;
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by a tablespace error.
     */
    @Override
    public boolean isTablespaceError(Exception ex)
    {
        String msg = StringUtils.serialize(ex);
        return msg.indexOf("Could not allocate space") != -1
            || msg.indexOf("is full") != -1;             // SQLServer
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by an invalid column error.
     */
    @Override
    public boolean isInvalidColumnError(Exception ex)
    {
        boolean ret = false;
        if(ex != null && ex.getMessage() != null)
            ret = ex.getMessage().indexOf("column") != -1
                || ex.getMessage().indexOf("out of range") != -1;
        return ret;
    }

    /**
     * Returns an expression to convert the given date using the date format (yyyy-MM-dd HH:mm:ss).
     */
    @Override
    public String getDateConversion(long dt)
    {
        String str = TimeUtils.toStringUTC(dt, "yyyy-MM-dd HH:mm:ss");
        return "CAST('"+str+"' AS DATETIME2)";
    }

    /**
     * Returns the expression for yesterday's date.
     */
    @Override
    public String getYesterdayDate()
    {
        return "DATEADD(day, -1, GETDATE())";
    }

    /**
     * Returns the expression for the zero date (01-01-1970).
     */
    @Override
    public String getZeroDate()
    {
        return "CAST('1970-01-01 00:00:00' AS DATETIME2)";
    }
}