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
import org.apache.derby.client.am.*;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * The base class for all Derby-specific database attributes.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DerbyProvider extends DBProvider
{
    /**
     * Default constructor.
     */
    public DerbyProvider()
    {
    }

    /**
     * Define the data types for the Derby provider.
     */
    @Override
    public void defineTypes()
    {
        super.defineTypes();
        setTypeName(Types.BOOLEAN, "SMALLINT");
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
            ret = (ex instanceof SqlException && ex.getMessage().indexOf("violation") != -1)
               || ex instanceof java.sql.SQLIntegrityConstraintViolationException;
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
            ret = ex.getMessage().indexOf("too long") != -1;
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by a tablespace error.
     */
    @Override
    public boolean isTablespaceError(Exception ex)
    {
        String msg = StringUtils.serialize(ex);
        return msg.indexOf("space") != -1;              // Derby
    }

    /**
     * Returns an expression to convert the given date using the date format ("yyyyMMddHHmmss").
     */
    @Override
    public String getDateConversion(long dt)
    {
        String str = TimeUtils.toStringUTC(dt, "yyyyMMddHHmmss");
        return "TIMESTAMP('"+str+"')";
    }

    /**
     * Returns the expression for yesterday's date.
     */
    @Override
    public String getYesterdayDate()
    {
        return "{fn TIMESTAMPADD(SQL_TSI_DAY, -1, CURRENT_TIMESTAMP)}";
    }

    /**
     * Returns the expression for the zero date (01-01-1970).
     */
    @Override
    public String getZeroDate()
    {
        return "TIMESTAMP('19700101000000')";
    }
}
