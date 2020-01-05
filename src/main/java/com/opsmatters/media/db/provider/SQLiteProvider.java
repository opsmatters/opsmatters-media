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
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.FormatUtils;

/**
 * The base class for all SQLite-specific database attributes.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SQLiteProvider extends DBProvider
{
    /**
     * Default constructor.
     */
    public SQLiteProvider()
    {
        setUseStringForCLOB(true); // SQLite uses TEXT instead of CLOB
        setUseBytesForBLOB(true); // SQLite uses byte[] instead of BLOB
    }

    /**
     * Define the data types for the SQLite provider.
     */
    @Override
    public void defineTypes()
    {
        super.defineTypes();
        setTypeName(Types.BOOLEAN, "TINYINT");
        setTypeName(Types.LONGVARCHAR, "TEXT");
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
            ret = ex.getMessage() != null 
                && (ex.getMessage().indexOf("constraint failed") != -1
                    || ex.getMessage().indexOf("statement is not executing") != -1);
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
            ret = ex.getMessage().indexOf("value too long") != -1;
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by a tablespace error.
     */
    @Override
    public boolean isTablespaceError(Exception ex)
    {
        return false; // SQLite does not have tablespaces
    }

    /**
     * Returns an expression to convert the given date using the ISO8601 date format.
     */
    @Override
    public String getDateConversion(long dt)
    {
        return Long.toString(dt); // Uses millis directly instead of date function
    }

    /**
     * Returns the expression for yesterday's date.
     */
    @Override
    public String getYesterdayDate()
    {
        return "datetime('now', '-1 day')";
    }

    /**
     * Returns the expression for the zero date (01-01-1970).
     */
    @Override
    public String getZeroDate()
    {
        return "datetime(0,'unixepoch')";
    }
}
