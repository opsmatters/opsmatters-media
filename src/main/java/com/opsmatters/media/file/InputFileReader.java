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

import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import com.opencsv.CSVReader;
import com.opsmatters.media.util.Formats;

/**
 * Defines the methods to parse and reference an input file in a CSV, XLS or XLSX format.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class InputFileReader
{
    private static final Logger logger = Logger.getLogger(InputFileReader.class.getName());

    private String name = "";
    private FileDelimiter delimiter = FileDelimiter.COMMA;
    private String worksheet = "";
    private boolean trim = true;
    private boolean removeQuotes = false;
    private InputStream stream;
    private String[] headers;
    private List<String[]> rows = new ArrayList<String[]>();
    private String dateFormat = "dd/MM/yyyy HH:mm:ss";

    /**
     * Default constructor.
     */
    public InputFileReader() 
    {
    }

    /**
     * Constructor that takes a name.
     * @param name The name of the input file
     */
    public InputFileReader(String name) 
    {
        this.name = name;
    }

    /**
     * Sets the name of the input file.
     * @param name The name of the input file
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the input file.
     * @return The name of the input file
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the delimiter used in the input file (CSV only).
     * @param delimiter The delimiter used in the input file (CSV only)
     */
    public void setDelimiter(FileDelimiter delimiter)
    {
        this.delimiter = delimiter;
    }

    /**
     * Returns the delimiter used in the input file (CSV only).
     * @return The delimiter used in the input file (CSV only)
     */
    public FileDelimiter getDelimiter()
    {
        return delimiter;
    }

    /**
     * Set to <CODE>true</CODE> if the input file cells should be trimmed for leading and trailing whitespace.
     * @param trim <CODE>true</CODE> if the input file cells should be trimmed for leading and trailing whitespace
     */
    public void setTrim(boolean trim)
    {
        this.trim = trim;
    }

    /**
     * Returns <CODE>true</CODE> if the input file cells should be trimmed for leading and trailing whitespace.
     * @return <CODE>true</CODE> if the input file cells should be trimmed for leading and trailing whitespace
     */
    public boolean getTrim()
    {
        return trim;
    }

    /**
     * Returns the worksheet to be opened in the input file (XLS, XLSX only).
     * @return The worksheet to be opened in the input file (XLS, XLSX only)
     */
    public String getWorksheet()
    {
        return worksheet;
    }

    /**
     * Sets the worksheet to be opened in the input file (XLS, XLSX only).
     * @param worksheet The worksheet to be opened in the input file (XLS, XLSX only)
     */
    public void setWorksheet(String worksheet)
    {
        this.worksheet = worksheet;
    }

    /**
     * Sets to <CODE>true</CODE> if quotes should be removed when parsing the file.
     * @param removeQuotes <CODE>true</CODE> if quotes should be removed when parsing the file
     */
    public void setRemoveQuotes(boolean removeQuotes)
    {
        this.removeQuotes = removeQuotes;
    }

    /**
     * Returns <CODE>true</CODE> if quotes should be removed when parsing the file.
     * @return <CODE>true</CODE> if quotes should be removed when parsing the file
     */
    public boolean getRemoveQuotes()
    {
        return removeQuotes;
    }

    /**
     * Sets the input stream for the input file.
     * @param stream The input stream for the input file
     */
    public void setInputStream(InputStream stream)
    {
        this.stream = stream;
    }

    /**
     * Returns the input stream for the input file.
     * @return The input stream for the input file
     */
    public InputStream getInputStream()
    {
        return stream;
    }

    /**
     * Returns the format to use for dates.
     */
    public String getDateFormat()
    {
        return dateFormat;
    }

    /**
     * Sets the format to use for dates.
     */
    public void setDateFormat(String dateFormat)
    {
        this.dateFormat = dateFormat;
    }

    /**
     * Returns the number of rows in the input file.
     * @return The number of rows in the input file
     */
    public int numRows()
    {
        return rows.size();
    }

    /**
     * Returns the number of columns in the input file.
     * @return The number of columns in the input file
     */
    public int numColumns()
    {
        return headers != null ? headers.length : -1;
    }

    /**
     * Returns the column headers in the input file.
     * @return The column headers in the input file
     */
    public String[] getHeaders()
    {
        return headers;
    }

    /**
     * Returns the column header in the input file at the given index.
     * @param col The column in the input file
     * @return The column header in the input file at the given index
     */
    public String getHeader(int col)
    {
        return headers != null ? headers[col] : null;
    }

    /**
     * Returns the lines in the input file.
     * @return The lines in the input file
     */
    public List<String[]> getRows()
    {
        return rows;
    }

    /**
     * Returns the line in the input file for the given row.
     * @param row The row in the input file
     * @return The line in the input file for the given row
     */
    public String[] getRow(int row)
    {
        return rows.get(row);
    }

    /**
     * Returns the value from the input file at the given co-ordinates.
     * @param row The row in the input file
     * @param col The column in the input file
     * @return The value at the given co-ordinates
     */
    public String getValue(int row, int col)
    {
        return rows.get(row)[col];
    }

    /**
     * Parses the input file into an array in memory.
     * @return The rows of data from the input file
     * @throws IOException if there is a problem reading the input file or it does not exist
     */
    public List<String[]> parse() throws IOException
    {
        if(stream == null)
            throw new IllegalArgumentException("input stream null");
        return parse(stream);
    }

    /**
     * Parses the input file into an array in memory.
     * @param stream The input stream with the file contents
     * @return The rows of data from the input file
     * @throws IOException if there is a problem reading the input file or it does not exist
     */
    public List<String[]> parse(InputStream stream) throws IOException
    {
        int columnCount = -1;
        List<String[]> lines = new ArrayList<String[]>();
        rows.clear();

        // Excel spreadsheet
        if(CommonFiles.isExcelFile(name))
        {
            SimpleDateFormat df = new SimpleDateFormat(dateFormat);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));

            Workbook workbook = Workbook.getWorkbook(name, stream);
            Worksheet sheet = null;
            if(worksheet != null && worksheet.length() > 0)
                sheet = workbook.getSheet(worksheet);
            else if(workbook.numSheets() > 0) // Read the first sheet
                sheet = workbook.getSheet(0);

            if(sheet != null)
            {
                columnCount = sheet.getColumns();
                for(int i = 0; i < sheet.getRows(); i++)
                {
                    String[] values = sheet.getRow(i, df);
                    if(values != null)
                        lines.add(values);
                }
            }
            else if(worksheet != null && worksheet.length() > 0)
            {
                throw new IllegalStateException("Worksheet '"+worksheet+"' not found");
            }
            else
            {
                throw new IllegalStateException("Worksheet not found");
            }

            workbook.close();
        }
        else if(CommonFiles.isCsvFile(name))
        {
            Reader reader = new InputStreamReader(stream);
            CSVReader csv = new CSVReader(reader, delimiter.separator().charAt(0));
            String [] line;
            while ((line = csv.readNext()) != null) 
            {
                if(columnCount == -1)
                    columnCount = line.length;
                lines.add(line);
            }
            reader.close();
        }

        int lineCount = lines.size();

        logger.fine("InputFileReader.parse: lines="+lineCount+" columns="+columnCount);

        // Error if the file is empty
        if(lineCount <= 0 || columnCount == 0)
            throw new IllegalStateException("input file does not contain any data");

        // Parse the file headers and rows
        int count = 0;
        for(String[] line : lines)
        {
            // Ignore the line if there is no data
            if(isBlankRow(line))
                continue;

            String[] row = new String[columnCount];  // the row of data

            for(int column = 0; column < columnCount; column++)
            {

                // Check that the row column count matches the header column count
                if(column >= line.length)
                {
                    if(line.length > 0 && isCompleteLine(line))
                        break;
                    else
                        throw new IllegalStateException("Row "+count+" has more columns than the header row");
                }

                // The first row gives the column headers
                if(count == 0)
                {
                    try
                    {
                        row[column] = line[column].trim();
                    }
                    catch(NoSuchElementException e)
                    {
                        row[column] = "";
                    }
                }
                else // The subsequent rows give the actual data
                {
                    try
                    {
                        String token = line[column];

                        // Remove quotes
                        if(removeQuotes && token.startsWith("\""))
                        {
                            token = token.substring(1);
                            if(token.endsWith("\""))
                                token = token.substring(0,token.length()-1);

                            // Replace all other double quotes with a single quote
                            token = token.replaceAll("\"\"", "\"");
                        }

                        // Trim the value (if required)
                        if(trim)
                            token.trim();

                        // Put the processed token in the array
                        row[column] = token;
                        logger.fine("InputFileReader.parse: row["+count+","+headers[column]+"]="+token);
                    }
                    catch(NoSuchElementException e)
                    {
                        row[column] = "";
                    }
                }
            }

            if(count == 0)
                headers = row;
            else
                rows.add(row);
            ++count;
        }

        return rows;
    }

    /**
     * Returns <CODE>true</CODE> if the given array of strings represents a complete line.
     * <P>
     * Returns <CODE>false</CODE> if end of the last item in the given array of strings has a line break in the middle.
     */
    private boolean isCompleteLine(String[] line)
    {
        // If the last string starts with a quote, but doesnt't end with one
        //   then it must be a linefeed in the middle of a data field
        String lastStr = line[line.length-1];
        boolean isPartial = lastStr != null 
            && (lastStr.equals("\"") || line.length == 1
                || (lastStr.startsWith("\"") && !lastStr.endsWith("\"")));
        return !isPartial;
    }

    /**
     * Returns <CODE>true</CODE> if the given array of strings represents a blank line.
     */
    private boolean isBlankRow(String[] line)
    {
        boolean ret = true;
        for(int i = 0; i < line.length && ret; i++)
        {
            if(line[i] != null && line[i].trim().length() > 0)
                ret = false;
        }
        return ret;
    }

    /**
     * Returns a builder for the reader.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make reader construction easier.
     */
    public static class Builder
    {
        private InputFileReader reader = new InputFileReader();

        /**
         * Sets the name of the input file.
         * @param name The name of the input file
         * @return This object
         */
        public Builder name(String name)
        {
            reader.setName(name);
            return this;
        }

        /**
         * Set to <CODE>true</CODE> if the input file cells should be trimmed for leading and trailing whitespace.
         * @param trim <CODE>true</CODE> if the input file cells should be trimmed for leading and trailing whitespace
         * @return This object
         */
        public Builder trim(boolean trim)
        {
            reader.setTrim(trim);
            return this;
        }

        /**
         * Sets the worksheet to be opened in the input file (XLS, XLSX only).
         * @param worksheet The worksheet to be opened in the input file (XLS, XLSX only)
         * @return This object
         */
        public Builder worksheet(String worksheet)
        {
            reader.setWorksheet(worksheet);
            return this;
        }

        /**
         * Sets the delimiter used in the input file (CSV only).
         * @param delimiter The delimiter used in the input file (CSV only)
         * @return This object
         */
        public Builder delimiter(FileDelimiter delimiter)
        {
            reader.setDelimiter(delimiter);
            return this;
        }

        /**
         * Sets the input stream for the input file.
         * @param stream The input stream for the input file
         * @return This object
         */
        public Builder withInputStream(InputStream stream)
        {
            reader.setInputStream(stream);
            return this;
        }

        /**
         * Sets the format to use for dates.
         * @param dateFormat The format to use for dates
         * @return This object
         */
        public Builder dateFormat(String dateFormat)
        {
            reader.setDateFormat(dateFormat);
            return this;
        }

        /**
         * Returns the configured reader instance
         * @return The reader instance
         */
        public InputFileReader build()
        {
            return reader;
        }
    }
}