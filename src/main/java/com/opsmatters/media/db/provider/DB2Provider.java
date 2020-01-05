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
import com.ibm.db2.jcc.am.*;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * The base class for all DB2-specific database attributes.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DB2Provider extends DBProvider
{
    /**
     * Default constructor.
     */
    public DB2Provider()
    {
        addReservedWord("ATTRIBUTES");
    }

    /**
     * Define the data types for the DB2 provider.
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
            ret = ex instanceof SqlIntegrityConstraintViolationException;
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
            ret = ex.getMessage().indexOf("22001") != -1;
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by a tablespace error.
     */
    @Override
    public boolean isTablespaceError(Exception ex)
    {
        String msg = StringUtils.serialize(ex);
        return msg.indexOf("Unable to allocate") != -1 || msg.indexOf("SQLCODE=-289") != -1;  // DB2
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by an invalid column error.
     */
    @Override
    public boolean isInvalidColumnError(Exception ex)
    {
        boolean ret = false;
        if(ex != null && ex.getMessage() != null)
            ret = ex.getMessage().toLowerCase().indexOf("parameter index") != -1;
        return ret;
    }

    /**
     * Returns an expression to convert the given date using the default date format (dd-MM-yyyy HH:mm:ss).
     */
    @Override
    public String getDateConversion(long dt)
    {
        String str = TimeUtils.toStringUTC(dt);
        return "TO_DATE('"+str+"','DD-MM-YYYY HH24:MI:SS')";
    }

    /**
     * Returns the expression for yesterday's date.
     */
    @Override
    public String getYesterdayDate()
    {
        return "(CURRENT DATE - 1 DAY)";
    }

    /**
     * Returns the expression for the zero date (01-01-1970).
     */
    @Override
    public String getZeroDate()
    {
        return "TO_TIMESTAMP('01-01-1970','DD-MM-YYYY')";
    }
}
