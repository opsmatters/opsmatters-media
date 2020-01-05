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

package com.opsmatters.media.file;

import java.text.SimpleDateFormat;

/**
 * Base class for an Excel XLS or XLSX worksheet.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Worksheet
{
    /**
     * Returns the name of the worksheet.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the worksheet.
     */
    public abstract String getName();

    /**
     * Returns the number of columns in this worksheet.
     * @return The number of columns in this worksheet
     */
    public abstract int getColumns();

    /**
     * Returns the number of rows in this worksheet.
     * @return The number of rows in this worksheet
     */
    public abstract int getRows();

    /**
     * Returns the array of columns for the given row in this worksheet.
     * @param i The index of the row in the worksheet
     * @param df The date format to use for date columns
     * @return The array of columns for the given row in this worksheet
     */
    public abstract String[] getRow(int i, SimpleDateFormat df);

    protected SimpleDateFormat df;
}
