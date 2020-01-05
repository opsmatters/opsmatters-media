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
package com.opsmatters.media.db.dao;

import com.opsmatters.media.db.provider.DBProvider;

/**
 * The definition of a database table column.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DBColumn
{
    /**
     * Constructor that takes a column name, type and required flag.
     */
    public DBColumn(String name, int type, boolean required)
    {
        setName(name);
        setType(type);
        setRequired(required);
    }

    /**
     * Constructor that takes a column name, type, required flag and default flag.
     */
    public DBColumn(String name, int type, boolean required, boolean deflt)
    {
        setName(name);
        setType(type);
        setRequired(required);
        setDefault(deflt);
    }

    /**
     * Constructor that takes a column name, type, size and required flag.
     */
    public DBColumn(String name, int type, int size, boolean required)
    {
        setName(name);
        setType(type);
        setSize(size);
        setRequired(required);
    }

    /**
     * Returns the name of the column.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the column.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the column.
     */
    public void setName(String s)
    {
        if(s != null)
            name = new String(s);
    }

    /**
     * Returns the type of the column.
     */
    public int getType()
    {
        return type;
    }

    /**
     * Sets the type of the column.
     */
    public void setType(int i)
    {
        type = i;
    }

    /**
     * Returns the size of the column.
     */
    public int size()
    {
        return size;
    }

    /**
     * Sets the size of the column.
     */
    public void setSize(int i)
    {
        size = i;
    }

    /**
     * Returns <CODE>true</CODE> if the column cannot be null.
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * Set to <CODE>true</CODE> if the column cannot be null.
     */
    public void setRequired(boolean b)
    {
        required = b;
    }

    /**
     * Returns <CODE>true</CODE> if the column should have a default.
     */
    public boolean hasDefault()
    {
        return deflt;
    }

    /**
     * Set to <CODE>true</CODE> if the column should have a default.
     */
    public void setDefault(boolean b)
    {
        deflt = b;
    }

    /**
     * Returns the SQL to create the column.
     */
    public String getSQL(DBProvider provider)
    {
        StringBuffer buff = new StringBuffer();
        boolean reserved = provider.isReservedWord(getName());
        if(reserved)
            buff.append("\"");
        buff.append(getName());
        if(reserved)
            buff.append("\"");
        buff.append(" ");
        buff.append(provider.getTypeName(getType()));
        if(size() > 0)
        {
            buff.append("(");
            buff.append(size());
            buff.append(")");
        }

        if(isRequired())
            buff.append(" NOT NULL");

        if(hasDefault())
        {
            buff.append(" DEFAULT ");
            buff.append(provider.getDefault(getType()));
        }

        return buff.toString();
    }

    private String name = "";
    private int type = 0;
    private int size = 0;
    private boolean required = false;
    private boolean deflt = false;
}
