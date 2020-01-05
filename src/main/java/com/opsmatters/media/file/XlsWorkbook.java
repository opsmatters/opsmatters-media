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
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jxl.*;
import jxl.write.WritableWorkbook;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableCellFeatures;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.Boolean;
import jxl.write.DateTime;
import jxl.write.DateFormat;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WriteException;
import jxl.format.Alignment;
import jxl.format.CellFormat;
import java.util.logging.Logger;
import com.opsmatters.media.util.StringUtils;

/**
 * Wrapper class for an Excel XLS workbook.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class XlsWorkbook extends Workbook
{
    private static final Logger logger = Logger.getLogger(XlsWorkbook.class.getName());

    /**
     * The maximum rows for an XLS output.
     */
    public static final int MAX_ROWS = 65535;

    /**
     * Private constructor.
     * @param file The file with the workbook
     */
    private XlsWorkbook(File file) 
        throws jxl.read.biff.BiffException, IOException
    {
        this.file = file;
        workbook = jxl.Workbook.getWorkbook(file, settings);
    }

    /**
     * Private constructor.
     * @param stream The input stream with the workbook
     */
    private XlsWorkbook(InputStream stream) 
        throws jxl.read.biff.BiffException, IOException
    {
        workbook = jxl.Workbook.getWorkbook(stream, settings);
    }

    /**
     * Private constructor.
     * @param wb The workbook object to use to create the workbook
     */
    private XlsWorkbook(WritableWorkbook wb) 
        throws jxl.read.biff.BiffException, IOException
    {
        writableWorkbook = wb;
    }

    /**
     * Returns an existing workbook object.
     * @param file The file with the workbook
     * @return The existing workbook object
     * @throws IOException if the workbook cannot be opened
     */
    public static XlsWorkbook getWorkbook(File file) throws IOException
    {
        try
        {
            return new XlsWorkbook(file);
        }
        catch(jxl.read.biff.BiffException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Returns an existing workbook object.
     * @param stream The stream with the workbook
     * @return The existing workbook object
     * @throws IOException if the stream cannot be read
     */
    public static XlsWorkbook getWorkbook(InputStream stream) throws IOException
    {
        try
        {
            return new XlsWorkbook(stream);
        }
        catch(jxl.read.biff.BiffException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Returns the internal workbook.
     * @return The internal workbook object
     */
    @Override
    public Object getWorkbook() 
    {
        return writableWorkbook != null ? writableWorkbook : workbook;
    }

    /**
     * Returns the number of worksheets in the given workbook.
     * @return The number of worksheets in the given workbook
     */
    @Override
    public int numSheets()
    {
        int ret = -1;
        if(workbook != null)
            ret = workbook.getNumberOfSheets();
        else if(writableWorkbook != null)
            ret = writableWorkbook.getNumberOfSheets();
        return ret;
    }

    /**
     * Returns the list of worksheet names from the given Excel XLS file.
     * @return The list of worksheet names from the given workbook
     */
    @Override
    public String[] getSheetNames()
    {
        String[] ret = null;
        if(workbook != null)
            ret = workbook.getSheetNames();
        else if(writableWorkbook != null)
            ret = writableWorkbook.getSheetNames();
        return ret;
    }

    /**
     * Returns the worksheet at the given index in the workbook.
     * @param i The index of the worksheet
     * @return The worksheet at the given index in the workbook
     */
    @Override
    public XlsWorksheet getSheet(int i)
    {
        XlsWorksheet ret = null;
        if(workbook != null)
            ret = new XlsWorksheet(workbook.getSheet(i));
        else if(writableWorkbook != null)
            ret = new XlsWorksheet(writableWorkbook.getSheet(i));
        return ret;
    }

    /**
     * Returns the worksheet with the given name in the workbook.
     * @param name The name of the worksheet
     * @return The worksheet with the given name in the workbook
     */
    @Override
    public XlsWorksheet getSheet(String name)
    {
        XlsWorksheet ret = null;
        if(workbook != null)
        {
            Sheet sheet = workbook.getSheet(name);
            if(sheet != null)
                ret = new XlsWorksheet(sheet);
        }
        else if(writableWorkbook != null)
        {
            Sheet sheet = writableWorkbook.getSheet(name);
            if(sheet != null)
                ret = new XlsWorksheet(sheet);
        }
        return ret;
    }

    /**
     * Creates a new workbook object.
     * @param os The output stream for the workbook
     * @param existing An existing workbook to add to
     * @return The new workbook object
     * @throws IOException if the workbook cannot be written
     */
    public static XlsWorkbook createWorkbook(OutputStream os, Workbook existing) 
        throws IOException
    {
        try
        {
            if(existing != null)
                return new XlsWorkbook(jxl.Workbook.createWorkbook(os, 
                    (jxl.Workbook)existing.getWorkbook(), settings));
            else
                return new XlsWorkbook(jxl.Workbook.createWorkbook(os, settings));
        }
        catch(jxl.read.biff.BiffException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Creates a sheet in the workbook with the given name and lines of data.
     * @param columns The column definitions for the worksheet
     * @param lines The list of lines to be added to the worksheet
     * @param sheetName The name of the worksheet to be added
     * @return The worksheet created
     * @throws IOException if the sheet cannot be created
     */
    @Override
    public XlsWorksheet createSheet(FileColumn[] columns, List<String[]> lines, String sheetName)
        throws IOException
    {
        // Create the worksheet and add the cells
        WritableSheet sheet = writableWorkbook.createSheet(sheetName, 9999); // Append sheet

        try
        {
            appendRows(sheet, columns, lines, sheetName);
        }
        catch(WriteException e)
        {
            throw new IOException(e);
        }

        // Set the column to autosize
        int numColumns = sheet.getColumns();
        for(int i = 0; i < numColumns; i++)
        {
            CellView column = sheet.getColumnView(i);
            column.setAutosize(true);
            if(columns != null && i < columns.length)
            {
                CellFormat format = columns[i].getCellFormat();
                if(format != null)
                    column.setFormat(format);
            }
            sheet.setColumnView(i, column);
        }

        return new XlsWorksheet(sheet);
    }

    /**
     * Adds the given lines of data to an existing sheet in the workbook.
     * @param columns The column definitions for the worksheet
     * @param lines The list of lines to be added to the worksheet
     * @param sheetName The name of the worksheet to be added
     * @throws IOException if the data cannot be appended
     */
    @Override
    public void appendToSheet(FileColumn[] columns, List<String[]> lines, String sheetName)
        throws IOException
    {
        try
        {
            XlsWorksheet sheet = getSheet(sheetName);
            if(sheet != null)
                appendRows((WritableSheet)sheet.getSheet(), columns, lines, sheetName);
        }
        catch(WriteException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Appends the given lines to the bottom of the given sheet.
     * @param sheet The sheet to add the lines to
     * @param columns The column definitions for the worksheet
     * @param lines The list of lines to be added to the worksheet
     * @param sheetName The name of the worksheet to be added
     * @return The array of column widths following the addition
     */
    private void appendRows(WritableSheet sheet, FileColumn[] columns, 
        List<String[]> lines, String sheetName)
        throws WriteException
    {
        WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD); 
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        WritableCellFeatures features = new WritableCellFeatures();
        features.removeDataValidation();

        int size = sheet.getRows();
        for(int i = 0; i < lines.size(); i++)
        {
            String[] line = lines.get(i);

            if((i+size) > MAX_ROWS)
            {
                logger.severe("the worksheet '"+sheetName
                    +"' has exceeded the maximum rows and will be truncated");
                break;
            }

            for(int j = 0; j < line.length; j++)
            {
                WritableCell cell = null;
                String data = line[j];
                if(data == null)
                    data = "";

                // Get a cell with the correct formatting
                if(columns != null && j < columns.length)
                {
                    cell = getCell(columns[j], j, i+size, data);
                }
                else // Try to figure out the type of the column
                { 
                    try
                    {
                        double num = Double.parseDouble(data);
                        cell = new Number(j, i+size, num);
                    }
                    catch(NumberFormatException e)
                    {
                        cell = new Label(j, i+size, data);
                    }
                }

                if(cell != null)
                {
                    if((i+size) == 0 && hasHeaders())
                        cell.setCellFormat(headerFormat);
                    else
                        cell.setCellFeatures(features);
                    sheet.addCell(cell);
                }
            }
        }
    }

    /**
     * Returns the formatted object for the given cell.
     * @param column The column definition to take the attributes from
     * @param col The column number of the cell
     * @param row The row number of the cell
     * @param data The data in the cell
     * @return The formatted object for the given cell
     */
    private WritableCell getCell(FileColumn column, int col, int row, String data)
    {
        WritableCell ret = null;
        short type = column.getType();
        String format = column.getFormat();
        WritableCellFormat cellFormat = column.getCellFormat(false);

        try
        {
            if((row == 0 && hasHeaders()) || data.length() == 0 || data.equals("null"))
            {
                ret = new Label(col, row, data);
            }
            else if(type == FileColumn.STRING_TYPE || type == FileColumn.NO_TYPE)
            {
                if(cellFormat == null)
                {
                    cellFormat = new WritableCellFormat(NumberFormats.TEXT);
                    column.setCellFormat(cellFormat);
                    setCellFormatAttributes(cellFormat, column);
                }
                ret = new Label(col, row, data);
            }
            else if(type == FileColumn.NUMBER_TYPE)
            {
                double num = Double.parseDouble(data);
                if(cellFormat == null && format.length() > 0)
                {
                    cellFormat = new WritableCellFormat(new NumberFormat(format));
                    column.setCellFormat(cellFormat);
                    setCellFormatAttributes(cellFormat, column);
                }
                ret = new Number(col, row, num);
            }
            else if(type == FileColumn.BOOLEAN_TYPE)
            {
                boolean b = java.lang.Boolean.parseBoolean(data);
                ret = new Boolean(col, row, b);
            }
            else if(type == FileColumn.DATETIME_TYPE)
            {
                long dt = Long.parseLong(data);
                dt += getOffset(dt);
                if(cellFormat == null && format.length() > 0)
                {
                    cellFormat = new WritableCellFormat(new DateFormat(format));
                    column.setCellFormat(cellFormat);
                    setCellFormatAttributes(cellFormat, column);
                }
                ret = new DateTime(col, row, new Date(dt));
            }
        }
        catch(NumberFormatException e)
        {
            logger.severe("column has illegal type or format (data="+data
                +", column=["+column.toString()+"]): "+e.getMessage());
            ret = new Label(col, row, data);
        }

        return ret;
    }

    /**
     * Sets the cell attributes from the given column.
     * @param cellFormat The dell to set the attributes on
     * @param column The column definition to take the attributes from
     */
    public void setCellFormatAttributes(WritableCellFormat cellFormat, FileColumn column)
    {
        try
        {
            if(cellFormat != null && column != null)
            {
                Alignment a = Alignment.GENERAL;
                short align = column.getAlign();
                if(align == FileColumn.ALIGN_CENTRE)
                    a = Alignment.CENTRE;
                else if(align == FileColumn.ALIGN_LEFT)
                    a = Alignment.LEFT;
                else if(align == FileColumn.ALIGN_RIGHT)
                    a = Alignment.RIGHT;
                else if(align == FileColumn.ALIGN_JUSTIFY)
                    a = Alignment.JUSTIFY;
                else if(align == FileColumn.ALIGN_FILL)
                    a = Alignment.FILL;
                cellFormat.setAlignment(a);
                cellFormat.setWrap(column.getWrap());
            }
        }
        catch(WriteException e)
        {
            logger.severe(StringUtils.serialize(e));
        }
    }

    /**
     * Write the workbook.
     * @throws IOException if the workbook cannot be written
     */
    @Override
    public void write() throws IOException
    {
        if(writableWorkbook != null)
            writableWorkbook.write();
    }

    /**
     * Close the workbook.
     */
    @Override
    public void close()
    {
        if(workbook != null)
            workbook.close();

        try
        {
            if(writableWorkbook != null)
                writableWorkbook.close();
        }
        catch(IOException e)
        {
        }
        catch(WriteException e)
        {
        }
    }

    private jxl.Workbook workbook;
    private WritableWorkbook writableWorkbook;
    private File file;
    private static WorkbookSettings settings = null;

    static
    {
        settings = new WorkbookSettings();
        settings.setCellValidationDisabled(true);
        settings.setSuppressWarnings(true);
        settings.setRationalization(false);
        settings.setGCDisabled(true);
    }
}
