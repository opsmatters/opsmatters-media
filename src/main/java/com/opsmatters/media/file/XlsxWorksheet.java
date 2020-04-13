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
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.xlsx4j.sml.SheetData;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.CTXf;
import org.xlsx4j.sml.CTCellFormula;
import org.xlsx4j.sml.STCellFormulaType;

/**
 * Wrapper class for an Excel XLSX worksheet.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class XlsxWorksheet extends Worksheet
{
    /**
     * Constructor that takes a worksheet and a workbook parent.
     * @param book The workbook that will be the parent of the sheet
     * @param s The worksheet to create
     */
    public XlsxWorksheet(XlsxWorkbook book, WorksheetPart s)
    {
        workbook = book;
        sheet = s;
    }

    /**
     * Returns the name of the worksheet.
     */
    @Override
    public String getName()
    {
        return workbook.getSheetName(sheet);
    }

    /**
     * Unmarshall the data in this worksheet.
     */
    private void unmarshall()
    {
        sheetData = sheet.getJaxbElement().getSheetData();
        rows = sheetData.getRow();
        if(rows != null && rows.size() > 0)
        {
            Row r = (Row)rows.get(0);
            numColumns = r.getC().size();
        }
    }

    /**
     * Returns the data in this worksheet.
     * @return The data in this worksheet
     */
    public SheetData getSheetData()
    {
        if(sheetData == null)
            unmarshall();
        return sheetData;
    }

    /**
     * Returns the number of columns in this worksheet.
     * @return The number of columns in this worksheet
     */
    @Override
    public int getColumns()
    {
        if(sheetData == null)
            unmarshall();
        return numColumns;
    }

    /**
     * Returns the number of rows in this worksheet.
     * @return The number of rows in this worksheet
     */
    @Override
    public int getRows()
    {
        if(sheetData == null)
            unmarshall();
        int ret = 0;
        if(rows != null)
            ret = rows.size();
        return ret;
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
        if(sheetData == null)
            unmarshall();
        String[] ret = null;
        Row r = (Row)rows.get(i);
        List row = (List)r.getC();
        if(row != null)
        {
            ret = new String[numColumns];
            for(int j = 0; j < row.size(); j++)
            {
                Cell c = (Cell)row.get(j);

                int col = getColumn(c.getR())-1;
                if(col >= numColumns)
                    continue;

                STCellType type = c.getT();
                long style = c.getS();
                String value = c.getV();
                CTCellFormula formula = c.getF();

                if(type.equals(STCellType.S))
                { 
                    ret[col] = workbook.getSharedString(Integer.parseInt(value));
                }
                else if(type.equals(STCellType.B))
                { 
                    ret[col] = value.equals("1") ? "TRUE" : "FALSE";
                }
                else if(type.equals(STCellType.N) && value != null && isDateTime(style))
                {
                    double d = Double.parseDouble(value);

                    // Convert excel date (based on 1/1/1900)
                    //   to java date (based on 1/1/1970)
                    // Ignore times, which are stored as fraction of a day
                    if(d > 1.0d) 
                        d -= 25569d; 

                    long dt = (long)(d*86400000d);
                    ret[col] = df.format(new Date(dt+1L)); // Add 1ms to allow for rounding errors
                }
                else if(formula != null)
                {
                    if(formula.getT() == STCellFormulaType.SHARED)
                    {
                        if(formula.getRef() != null)
                        {
                            ret[col] = String.format("=[%d|%s|%s]", formula.getSi(), 
                                formula.getRef(), formula.getValue());
                        }
                        else // ref to shared formula
                        {
                            ret[col] = String.format("=[%d]", formula.getSi());
                        }
                    }
                    else // type = NORMAL
                    {
                        ret[col] = "="+formula.getValue();
                    }
                } 
                else
                {
                    ret[col] = value;
                } 
            }

            // Change any null strings to the empty string
            if(ret != null)
            {
                for(int j = 0; j < ret.length; j++)
                {
                    if(ret[j] == null)
                        ret[j] = "";
//GERALD: test
ret[j] = ret[j].replaceAll("_x000D_", "");
                }
            }
        }
        return ret;
    }

    /**
     * Returns the column number from the given cell reference (eg. A1).
     * <P>
     * eg. A=1, B=2, etc
     * @param str The cell reference
     * @return The column number for the cell
     */
    private int getColumn(String str)
    {
        int ret = 0;

        // Find the end of the column characters
        int len = 0;
        for(int i = 0; 
            i < str.length() && str.charAt(i) >= 'A' && str.charAt(i) <= 'Z'; 
            i++, len++);

        // Calculate the column number
        int pos = 0;
        for(int i = len-1; i >= 0; i--, pos++)
        {
            char c = str.charAt(i);
            int col = (int)(c - '@');
            if(pos > 0)
                col *= 26*pos;
            ret += col;
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given style ref is a date or time.
     * <P>
     * The following formats are supported:
     * 14 = 'mm-dd-yy'
     * 15 = 'd-mmm-yy'
     * 16 = 'd-mmm'
     * 17 = 'mmm-yy'
     * 18 = 'h:mm AM/PM'
     * 19 = 'h:mm:ss AM/PM'
     * 20 = 'h:mm'
     * 21 = 'h:mm:ss'
     * 22 = 'm/d/yy h:mm'
     * 30 = 'm/d/yy'
     * 45 = 'mm:ss'
     * 46 = '[h]:mm:ss'
     * 47 = 'mmss.0'
     * @param style The style reference
     * @return <CODE>true</CODE> if the given style ref is a date or time
     */
    private boolean isDateTime(long style)
    {
        boolean ret = false;

        if(style > 0L)
        {
            CTXf xf = workbook.getStylesheet().getCellXfs().getXf().get((int)style);
            long numFmtId = 0L;
            if(xf.getNumFmtId() != null)
                numFmtId = xf.getNumFmtId().longValue();
            if(numFmtId < 164)
            {
                ret = (numFmtId >= 14 && numFmtId <= 22) 
                    || numFmtId == 30 
                    || (numFmtId >= 45 && numFmtId <= 47);
            }
            else
            {
                String formatCode = workbook.getFormatCode(numFmtId);
                if(formatCode != null)
                {
                    ret = formatCode.indexOf("h:mm") != -1;
                }
            }
        }

        return ret;
    }

    /**
     * Returns the sheets from the given Excel XLSX file.
     * @param file The file with the name of the XLSX file
     * @return The sheet names from the XLSX file
     * @throws IOException if the file cannot be opened
     */
    public static String[] getXlsxWorksheets(File file) 
        throws IOException
    {
        XlsxWorkbook workbook = XlsxWorkbook.getWorkbook(file);
        String[] sheets = workbook.getSheetNames();
        workbook.close();
        return sheets;
    }

    /**
     * Returns the sheets from the given Excel XLSX file.
     * @param stream The input stream with the XLSX file
     * @return The sheet names from the XLSX file
     * @throws IOException if the file cannot be opened
     */
    public static String[] getXlsxWorksheets(InputStream stream) 
        throws IOException
    {
        XlsxWorkbook workbook = XlsxWorkbook.getWorkbook(stream);
        String[] sheets = workbook.getSheetNames();
        workbook.close();
        return sheets;
    }

    private XlsxWorkbook workbook;
    private WorksheetPart sheet;
    private SheetData sheetData;
    private List rows;
    private int numColumns = 0;
}