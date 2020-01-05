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

import java.util.Date;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import jxl.*;

/**
 * Wrapper class for an Excel XLS worksheet.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class XlsWorksheet extends Worksheet
{
    /**
     * Constructor that takes a sheet.
     * @param s The worksheet to create
     */
    public XlsWorksheet(Sheet s)
    {
        sheet = s;
    }

    /**
     * Returns the name of the worksheet.
     */
    @Override
    public String getName()
    {
        return sheet.getName();
    }

    /**
     * Returns the data in this worksheet.
     * @return The data in this worksheet
     */
    public Sheet getSheet()
    {
        return sheet;
    }

    /**
     * Returns the number of columns in this worksheet.
     * @return The number of columns in this worksheet
     */
    @Override
    public int getColumns()
    {
        if(sheet != null)
            return sheet.getColumns();
        else
            return 0;
    }

    /**
     * Returns the number of rows in this worksheet.
     * @return The number of rows in this worksheet
     */
    @Override
    public int getRows()
    {
        if(sheet != null)
            return sheet.getRows();
        else
            return 0;
    }

    /**
     * Returns the array of columns for the given row in this worksheet.
     * @param i The index of the row in the worksheet
     * @param df The date format to use for date columns
     * @return The array of columns for the given row in this worksheet
     */
    @Override
    public String[] getRow(int i, SimpleDateFormat df)
    {
        String[] ret = null;

        if(sheet != null)
        {
            Cell[] row = sheet.getRow(i);
            if(row != null)
            {
                ret = new String[row.length];
                for(int j = 0; j < row.length; j++)
                {
                    Cell c = row[j];
                    String value = c.getContents();
                    if(c instanceof DateCell)
                    {
                        DateCell dc = (DateCell)c;
                        long dt = dc.getDate().getTime();
                        if(dt > 0L) // avoid hh:mm:ss 
                        {
                            if(value.length() > 8)
                                value = df.format(dc.getDate());
                        }
                        else
                        {
                            // Workaround for a bug in the handling of time fields
                            double d = (dt/86400000d)+25569;
                            dt = (long)(d*86400000d);
                            value = df.format(new Date(dt+1L)); // Add 1ms for rounding errors
                        }
                    }
                    ret[j] = value;
                }
            }
        }

        return ret;
    }

    /**
     * Returns the sheets from the given Excel XLS file.
     * @param file The file with the name of the XLS file
     * @return The sheet names from the XLS file
     * @throws IOException if the file cannot be opened
     * @throws jxl.read.biff.BiffException if there is a format error with the file
     */
    public static String[] getXlsWorksheets(File file) 
        throws IOException, jxl.read.biff.BiffException
    {
        Workbook workbook = Workbook.getWorkbook(file);
        String[] sheets = workbook.getSheetNames();
        workbook.close();
        return sheets;
    }

    /**
     * Returns the sheets from the given Excel XLS file.
     * @param stream The input stream with the XLS file
     * @return The sheet names from the XLS file
     * @throws IOException if the file cannot be opened
     * @throws jxl.read.biff.BiffException if there is a format error with the file
     */
    public static String[] getXlsWorksheets(InputStream stream) 
        throws IOException, jxl.read.biff.BiffException
    {
        XlsWorkbook workbook = XlsWorkbook.getWorkbook(stream);
        String[] sheets = workbook.getSheetNames();
        workbook.close();
        return sheets;
    }

    private Sheet sheet;
}
