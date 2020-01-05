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

import java.util.List;
import java.util.TimeZone;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.opsmatters.media.util.TimeUtils;

/**
 * Base class for an Excel XLS or XLSX workbook.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Workbook
{
    private boolean headers = true;

    /**
     * Returns an existing workbook object.
     * @param file The file with the name of the workbook
     * @return The existing workbook object
     * @throws IOException if the file cannot be opened
     */
    public static Workbook getWorkbook(File file) throws IOException
    {
        Workbook ret = null;
        String lowerFilename = file.getName().toLowerCase();
        if(lowerFilename.endsWith("."+CommonFiles.XLS_EXT))
        {
            ret = XlsWorkbook.getWorkbook(file);
        }
        else if(lowerFilename.endsWith("."+CommonFiles.XLSX_EXT))
        {
            XlsxWorkbook.initJaxbContexts();
            ret = XlsxWorkbook.getWorkbook(file);
        }
        return ret;
    }

    /**
     * Returns an existing workbook object.
     * @param filename The filename of the workbook
     * @param stream The input stream for the workbook
     * @return The existing workbook object
     * @throws IOException if the file cannot be read
     */
    public static Workbook getWorkbook(String filename, InputStream stream) 
        throws IOException
    {
        Workbook ret = null;
        String lowerFilename = filename.toLowerCase();
        if(lowerFilename.endsWith("."+CommonFiles.XLS_EXT))
        {
            ret = XlsWorkbook.getWorkbook(stream);
        }
        else if(lowerFilename.endsWith("."+CommonFiles.XLSX_EXT))
        {
            XlsxWorkbook.initJaxbContexts();
            ret = XlsxWorkbook.getWorkbook(stream);
        }
        return ret;
    }

    /**
     * Creates a new workbook object.
     * @param format The format of the workbook (XLS or XLSX)
     * @param os The output stream to write the workbook to
     * @param existing An existing workbook to add to
     * @return The new workbook created
     * @throws IOException if the file cannot be written
     */
    public static Workbook createWorkbook(FileFormat format, OutputStream os, Workbook existing)
        throws IOException
    {
        Workbook ret = null;
        if(format == FileFormat.XLS)
        {
            ret = XlsWorkbook.createWorkbook(os, existing);
        }
        else if(format == FileFormat.XLSX)
        {
            XlsxWorkbook.initJaxbContexts();
            ret = XlsxWorkbook.createWorkbook(os, existing);
        }
        return ret;
    }

    /**
     * Creates a new workbook object.
     * @param format The format of the workbook (XLS or XLSX)
     * @param os The output stream to write the workbook to
     * @return The new workbook created
     * @throws IOException if the file cannot be written
     */
    public static Workbook createWorkbook(FileFormat format, OutputStream os)
        throws IOException
    {
        return createWorkbook(format, os, null);
    }

    /**
     * Returns the internal workbook.
     * @return The internal workbook object
     */
    public abstract Object getWorkbook();

    /**
     * Returns the number of worksheets in the given workbook.
     * @return The number of worksheets in the given workbook
     */
    public abstract int numSheets();

    /**
     * Returns the list of worksheet names from the given workbook.
     * @return The list of worksheet names from the given workbook
     */
    public abstract String[] getSheetNames();

    /**
     * Returns the worksheet at the given index in the workbook.
     * @param i The index of the worksheet
     * @return The worksheet at the given index in the workbook
     */
    public abstract Worksheet getSheet(int i);

    /**
     * Returns the worksheet with the given name in the workbook.
     * @param name The name of the worksheet
     * @return The worksheet with the given name in the workbook
     */
    public abstract Worksheet getSheet(String name);

    /**
     * Creates a sheet in the workbook with the given name and lines of data.
     * @param columns The column definitions for the worksheet
     * @param lines The list of lines to be added to the worksheet
     * @param sheetName The name of the worksheet to be added
     * @return The worksheet created
     * @throws IOException if the sheet cannot be created
     */
    public abstract Worksheet createSheet(FileColumn[] columns, List<String[]> lines, String sheetName)
        throws IOException;

    /**
     * Adds the given lines of data to an existing sheet in the workbook.
     * @param columns The column definitions for the worksheet
     * @param lines The list of lines to be added to the worksheet
     * @param sheetName The name of the worksheet to be added
     * @throws IOException if the data cannot be appended
     */
    public abstract void appendToSheet(FileColumn[] columns, List<String[]> lines, String sheetName)
        throws IOException;

    /**
     * Write the workbook.
     * @throws IOException if the workbook cannot be written
     */
    public abstract void write() throws IOException;

    /**
     * Close the workbook.
     */
    public abstract void close();

    /**
     * Returns the current offset for the given date allowing for daylight savings.
     * @param dt The date to be checked
     * @return The current offset for the given date allowing for daylight savings
     */
    protected int getOffset(long dt)
    {
        int ret = 0;
        TimeZone tz = TimeUtils.getCurrentTimeZone();
        if(tz != null)
            ret = tz.getOffset(dt);
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the workbook has a header row.
     * @return <CODE>true</CODE> if the workbook has a header row
     */
    public boolean hasHeaders()
    {
        return headers;
    }

    /**
     * Set to <CODE>true</CODE> if the workbook has a header row.
     * @param headers <CODE>true</CODE> if the workbook has a header row
     */
    public void setHeaders(boolean headers)
    {
        this.headers = headers;
    }
}