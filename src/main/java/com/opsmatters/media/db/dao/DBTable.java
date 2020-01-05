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

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.db.provider.DBProvider;

/**
 * The definition of a database table.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DBTable
{
    /**
     * Default constructor.
     */
    public DBTable()
    {
    }

    /**
     * Returns the name of the table.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the table.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the table.
     */
    public void setName(String s)
    {
        if(s != null)
            name = new String(s);
    }

    /**
     * Returns <CODE>true</CODE> if the table has been initialised.
     */
    public boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Set to <CODE>true</CODE> if the table has been initialised.
     */
    public void setInitialised(boolean b)
    {
        initialised = b;
    }

    /**
     * Adds the given column to the table.
     */
    public void addColumn(String name, int type, boolean required)
    {
        columns.add(new DBColumn(name, type, required));
    }

    /**
     * Adds the given column to the table.
     */
    public void addColumn(String name, int type, int size, boolean required)
    {
        columns.add(new DBColumn(name, type, size, required));
    }

    /**
     * Adds the given column to the table.
     */
    public void addColumn(String name, int type, boolean required, boolean deflt)
    {
        columns.add(new DBColumn(name, type, required, deflt));
    }

    /**
     * Sets the given primary key on the table.
     */
    public void setPrimaryKey(String name, String column)
    {
        primaryKey = new DBIndex(name, DBIndex.PRIMARY_KEY, column);
    }

    /**
     * Sets the given primary key on the table.
     */
    public void setPrimaryKey(String name, String[] columns)
    {
        primaryKey = new DBIndex(name, DBIndex.PRIMARY_KEY, columns);
    }

    /**
     * Adds the given index to the table.
     */
    public void addIndex(String name, String column)
    {
        indices.add(new DBIndex(name, DBIndex.INDEX, this.name, column));
    }

    /**
     * Adds the given index to the table.
     */
    public void addIndex(String name, String[] columns)
    {
        indices.add(new DBIndex(name, DBIndex.INDEX, this.name, columns));
    }

    /**
     * Returns the SQL used to create the table.
     */
    public String getTableSQL(DBProvider provider)
    {
        if(tableSQL == null)
            generateTableSQL(provider);
        return tableSQL;
    }

    /**
     * Generates the SQL used to create the table.
     */
    private void generateTableSQL(DBProvider provider)
    {
        StringBuffer buff = new StringBuffer();
        buff.append("CREATE TABLE ");
        if(!provider.isCaseSensitive())
            buff.append(getName().toLowerCase());
        else
            buff.append(getName());

        buff.append(" ( ");

        // Add the columns
        boolean first = true;
        for(DBColumn column : columns)
        {
            if(!first)
                buff.append(", ");
            buff.append(column.getSQL(provider));
            first = false;
        }

        // Add the primary key
        if(primaryKey != null)
        {
            buff.append(", ");
            buff.append(primaryKey.getSQL());
        }

        buff.append(")");
        tableSQL = buff.toString();
    }

    /**
     * Returns the SQL used to create the indices.
     */
    public String[] getIndicesSQL(DBProvider provider)
    {
        if(indicesSQL == null)
            generateIndicesSQL(provider);
        return indicesSQL;
    }

    /**
     * Generates the SQL used to create the indices.
     */
    private void generateIndicesSQL(DBProvider provider)
    {
        indicesSQL = new String[indices.size()];

        int i = 0;
        for(DBIndex index : indices)
        {
            indicesSQL[i] = index.getSQL();
            ++i;
        }
    }

    private String name = "";
    private List<DBColumn> columns = new ArrayList<DBColumn>();
    private DBIndex primaryKey;
    private List<DBIndex> indices = new ArrayList<DBIndex>();
    private String tableSQL = null;
    private String[] indicesSQL = null;
    private boolean initialised = false;
}