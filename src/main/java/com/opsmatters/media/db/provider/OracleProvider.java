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
 * The base class for all Oracle-specific database attributes.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OracleProvider extends DBProvider
{
    /**
     * Default constructor.
     */
    public OracleProvider()
    {
    }

    /**
     * Define the data types for the Oracle provider.
     */
    @Override
    public void defineTypes()
    {
        super.defineTypes();
        setTypeName(Types.VARCHAR, "VARCHAR2");
        setTypeName(Types.SMALLINT, "NUMBER(4,0)");
        setTypeName(Types.INTEGER, "NUMBER(8,0)");
        setTypeName(Types.BIGINT, "NUMBER(16,0)");
        setTypeName(Types.TIMESTAMP, "DATE");
        setTypeName(Types.BOOLEAN, "NUMBER(1)");
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
            ret = ex.getMessage() != null && ex.getMessage().indexOf("violated") != -1;
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
        return msg.indexOf("unable to extend") != -1;    // Oracle
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
        return "SYSDATE-1";
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