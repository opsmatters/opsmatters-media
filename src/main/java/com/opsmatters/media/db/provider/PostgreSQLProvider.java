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
 * The base class for all PostgreSQL-specific database attributes.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PostgreSQLProvider extends DBProvider
{
    /**
     * Default constructor.
     */
    public PostgreSQLProvider()
    {
        setCaseSensitive(false); // Postgres changes all table names to lower case
        setUseIntegerForBoolean(false); //Postgres doesn't support (0,1) for boolean
        setUseStringForCLOB(true); // Postgres uses TEXT instead of CLOB
        setUseBytesForBLOB(true); // Postgres uses BYTEA instead of BLOB
    }

    /**
     * Define the data types for the PostgreSQL provider.
     */
    @Override
    public void defineTypes()
    {
        super.defineTypes();
        setTypeName(Types.LONGVARCHAR, "TEXT");
        setTypeName(Types.VARBINARY, "BYTEA");
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is a constraint violation.
     */
    @Override
    public boolean isConstraintViolation(Exception ex)
    {
        boolean ret = false;
        if(ex != null)
            ret = ex.getMessage() != null && ex.getMessage().indexOf("violates") != -1;
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
        String msg = StringUtils.serialize(ex);
        return msg.indexOf("could not extend") != -1;    // PostgreSQL
    }

    /**
     * Returns an expression to convert the given date using the default date format (dd-MM-yyyy HH:mm:ss).
     */
    @Override
    public String getDateConversion(long dt)
    {
        String str = TimeUtils.toStringUTC(dt);
        return "TO_TIMESTAMP('"+str+"','DD-MM-YYYY HH24:MI:SS')";
    }

    /**
     * Returns the expression for yesterday's date.
     */
    @Override
    public String getYesterdayDate()
    {
        return "(CURRENT_TIMESTAMP - INTERVAL '1 day')";
    }

    /**
     * Returns the expression for the zero date (01-01-1970).
     */
    @Override
    public String getZeroDate()
    {
        return "TO_TIMESTAMP('01-01-1970 01:00:00','DD-MM-YYYY HH24:MI:SS')";
    }
}
