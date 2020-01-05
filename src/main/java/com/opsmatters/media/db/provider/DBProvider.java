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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.sql.Types;

/**
 * The base class for all provider-specific database attributes.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class DBProvider
{
    /**
     * Default constructor.
     */
    public DBProvider()
    {
        defineTypes();
    }

    /**
     * Returns <CODE>true</CODE> if the names for the provider are case-sensitive.
     */
    public boolean isCaseSensitive()
    {
        return caseSensitive;
    }

    /**
     * Set to <CODE>true</CODE> if the names for the provider are case-sensitive.
     */
    public void setCaseSensitive(boolean b)
    {
        caseSensitive = b;
    }

    /**
     * Returns <CODE>true</CODE> if the provider can use integers (0,1) for booleans.
     */
    public boolean useIntegerForBoolean()
    {
        return useIntegerForBoolean;
    }

    /**
     * Set to <CODE>true</CODE> if the provider can use integers (0,1) for booleans.
     */
    public void setUseIntegerForBoolean(boolean b)
    {
        useIntegerForBoolean = b;
    }

    /**
     * Returns <CODE>true</CODE> if the provider uses String instead of CLOB.
     */
    public boolean useStringForCLOB()
    {
        return useStringForCLOB;
    }

    /**
     * Set to <CODE>true</CODE> if the provider uses String instead of CLOB.
     */
    public void setUseStringForCLOB(boolean b)
    {
        useStringForCLOB = b;
    }

    /**
     * Returns <CODE>true</CODE> if the provider uses Bytes instead of BLOB.
     */
    public boolean useBytesForBLOB()
    {
        return useBytesForBLOB;
    }

    /**
     * Set to <CODE>true</CODE> if the provider uses Bytes instead of BLOB.
     */
    public void setUseBytesForBLOB(boolean b)
    {
        useBytesForBLOB = b;
    }

    /**
     * Defines the default data types for the provider.
     */
    public void defineTypes()
    {
        setTypeName(Types.VARCHAR, "VARCHAR");
        setTypeName(Types.SMALLINT, "SMALLINT");
        setTypeName(Types.INTEGER, "INTEGER");
        setTypeName(Types.BIGINT, "BIGINT");
        setTypeName(Types.CHAR, "CHAR(1)");
        setTypeName(Types.TIMESTAMP, "TIMESTAMP");
        setTypeName(Types.BOOLEAN, "BOOLEAN");
        setTypeName(Types.LONGVARCHAR, "CLOB");
        setTypeName(Types.VARBINARY, "BLOB");

        setDefault(Types.SMALLINT, "0");
        setDefault(Types.INTEGER, "0");
        setDefault(Types.BIGINT, "0");
        setDefault(Types.BOOLEAN, "FALSE");
    }

    /**
     * Returns the data type for the given type.
     */
    public String getTypeName(int type)
    {
        return typeNames.get(type);
    }

    /**
     * Sets the data type for the given type.
     */
    public void setTypeName(int type, String value)
    {
        typeNames.put(type, value);
    }

    /**
     * Returns the default value for the given type.
     */
    public String getDefault(int type)
    {
        return defaults.get(type);
    }

    /**
     * Sets the default value for the given type.
     */
    public void setDefault(int type, String value)
    {
        defaults.put(type, value);
    }

    /**
     * Adds the given reserved word for the provider.
     */
    public void addReservedWord(String value)
    {
        reserved.add(value);
    }

    /**
     * Returns <CODE>true</CODE> if the given value is a reserved word for the provider.
     */
    public boolean isReservedWord(String value)
    {
        return reserved.contains(value);
    }

    /**
     * Returns the given string with quotes around all reserved words.
     */
    public String quoteReservedWords(String value)
    {
        String ret = value;
        if(reserved.size() == 0)
            return ret;

        for(String word : reserved)
            ret = ret.replaceAll(word, "\""+word+"\"");

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is a constraint violation.
     */
    public abstract boolean isConstraintViolation(Exception ex);

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by inserting a value too large for a BLOB.
     */
    public abstract boolean isDataTooLongException(Exception ex);

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by a tablespace error.
     */
    public abstract boolean isTablespaceError(Exception e);

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by an invalid column error.
     */
    public boolean isInvalidColumnError(Exception ex)
    {
        return isDefaultInvalidColumnError(ex);
    }

    /**
     * Returns <CODE>true</CODE> if the given exception is caused by an invalid column error.
     */
    public static boolean isDefaultInvalidColumnError(Exception ex)
    {
        boolean ret = false;
        if(ex != null && ex.getMessage() != null)
            ret = ex.getMessage().toLowerCase().indexOf("column") != -1;
        return ret;
    }

    /**
     * Returns an expression to convert the given date using the default date format (dd-MM-yyyy HH:mm:ss).
     */
    public abstract String getDateConversion(long dt);

    /**
     * Returns the expression for yesterday's date.
     */
    public abstract String getYesterdayDate();

    /**
     * Returns the expression for the zero date (01-01-1970 00:00:00).
     */
    public abstract String getZeroDate();

    private boolean caseSensitive = true;
    private boolean useIntegerForBoolean = true;
    private boolean useStringForCLOB = false;
    private boolean useBytesForBLOB = false;
    private Map<Integer,String> typeNames = new HashMap<Integer,String>();
    private Map<Integer,String> defaults = new HashMap<Integer,String>();
    private List<String> reserved = new ArrayList<String>();
}
