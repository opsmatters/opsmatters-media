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

/**
 * The definition of a database table index.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DBIndex
{
    public static final short INDEX = 0;
    public static final short PRIMARY_KEY = 1;

    /**
     * Constructor that takes an index name, type and a column.
     */
    public DBIndex(String name, short type, String column)
    {
        setName(name);
        setType(type);
        setColumns(column);
    }

    /**
     * Constructor that takes an index name, type and a column array.
     */
    public DBIndex(String name, short type, String[] columns)
    {
        setName(name);
        setType(type);
        setColumns(columns);
    }

    /**
     * Constructor that takes an index name, type, table name and a column.
     */
    public DBIndex(String name, short type, String tableName, String column)
    {
        setName(name);
        setType(type);
        setTableName(tableName);
        setColumns(column);
    }

    /**
     * Constructor that takes an index name, type, table name and a column array.
     */
    public DBIndex(String name, short type, String tableName, String[] columns)
    {
        setName(name);
        setType(type);
        setTableName(tableName);
        setColumns(columns);
    }

    /**
     * Returns the name of the index.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the index.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the index.
     */
    public void setName(String s)
    {
        if(s != null)
            name = new String(s);
    }

    /**
     * Returns the type of the index.
     */
    public short getType()
    {
        return type;
    }

    /**
     * Sets the type of the index.
     */
    public void setType(short i)
    {
        type = i;
    }

    /**
     * Returns the table name of the index.
     */
    public String getTableName()
    {
        return tableName;
    }

    /**
     * Sets the table name of the index.
     */
    public void setTableName(String s)
    {
        if(s != null)
            tableName = new String(s);
    }

    /**
     * Returns the columns of the index.
     */
    public String[] getColumns()
    {
        return columns;
    }

    /**
     * Sets the columns of the index.
     */
    public void setColumns(String[] array)
    {
        columns = array;
    }

    /**
     * Sets the columns of the index.
     */
    public void setColumns(String s)
    {
        if(s != null)
            columns = new String[] { s };
    }

    /**
     * Returns the list of columns of the index.
     */
    private String getColumnList()
    {
        StringBuffer buff = new StringBuffer();
        if(columns != null)
        {
            for(int i = 0; i < columns.length; i++)
            {
                if(i > 0)
                    buff.append(",");
                buff.append(columns[i]);
            }
        }
        return buff.toString();
    }

    /**
     * Returns the SQL to create the index.
     */
    public String getSQL()
    {
        StringBuffer buff = new StringBuffer();
        if(type == PRIMARY_KEY)
            buff.append("CONSTRAINT");
        else
            buff.append("CREATE INDEX");
        buff.append(" ");
        buff.append(getName());
        if(type == PRIMARY_KEY)
        {
            buff.append(" PRIMARY KEY ");
        }
        else
        {
            buff.append(" ON ");
            buff.append(tableName);
        }
        buff.append("(");
        buff.append(getColumnList());
        buff.append(")");
        return buff.toString();
    }

    private String name = "";
    private short type = INDEX;
    private String tableName = "";
    private String[] columns = null;
}
