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
import com.opsmatters.media.util.TimeUtils;

/**
 * The base class for all H2-specific database attributes.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class H2Provider extends DBProvider
{
    /**
     * Default constructor.
     */
    public H2Provider()
    {
    }

    /**
     * Define the data types for the H2 provider.
     */
    @Override
    public void defineTypes()
    {
        super.defineTypes();
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is a constraint violation.
     */
    @Override
    public boolean isConstraintViolation(Exception ex)
    {
        boolean ret = false;
        if(ex != null && ex.getMessage() != null)
            ret = ex.getMessage().indexOf("violation") != -1;
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
            ret = ex.getMessage().indexOf("too long") != -1
                || ex.getMessage().indexOf("22001") != -1;
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by a tablespace error.
     */
    @Override
    public boolean isTablespaceError(Exception ex)
    {
        return false; // H2 does not have tablespaces
    }

    /**
     * Returns an expression to convert the given date using the default date format (dd-MM-yyyy HH:mm:ss).
     */
    @Override
    public String getDateConversion(long dt)
    {
        String str = TimeUtils.toStringUTC(dt);
        return "PARSEDATETIME('"+str+"','dd-MM-yyyy HH:mm:ss')";
    }

    /**
     * Returns the expression for yesterday's date.
     */
    @Override
    public String getYesterdayDate()
    {
        return "DATEADD('DAY', -1, CURRENT_TIMESTAMP)";
    }

    /**
     * Returns the expression for the zero date (01-01-1970).
     */
    @Override
    public String getZeroDate()
    {
        return "TIMESTAMP '1970-01-01 00:00:00'";
    }
}
