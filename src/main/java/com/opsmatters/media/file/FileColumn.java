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
import java.util.Map;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.DecimalFormat;
import java.text.ParseException;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import java.util.logging.Logger;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class to encapsulate the text and attributes of a file column.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FileColumn
{
    private static final Logger logger = Logger.getLogger(FileColumn.class.getName());

    /**
     * Indicates cell format not set.
     */
    public static final short NO_TYPE = -1;

    /**
     * Indicates the "string" data format.
     */
    public static final short STRING_TYPE = 0;

    /**
     * Indicates the "number" data format.
     */
    public static final short NUMBER_TYPE = 1;

    /**
     * Indicates the "boolean" data format.
     */
    public static final short BOOLEAN_TYPE = 2;

    /**
     * Indicates the "datetime" data format.
     */
    public static final short DATETIME_TYPE = 3;

    /**
     * Indicates the "integer" data format.
     */
    public static final short INTEGER_TYPE = 4;

    /**
     * Indicates the "decimal" data format.
     */
    public static final short DECIMAL_TYPE = 5;

    /**
     * Indicates the "seconds" data format.
     */
    public static final short SECONDS_TYPE = 6;

    /**
     * Indicates the "type" attribute.
     */
    public static final String TYPE_ATTR = "type";

    /**
     * Indicates the "format" attribute.
     */
    public static final String FORMAT_ATTR = "format";

    /**
     * Indicates the "inputType" attribute.
     */
    public static final String INPUT_TYPE_ATTR = "inputType";

    /**
     * Indicates the "inputFormat" attribute.
     */
    public static final String INPUT_FORMAT_ATTR = "inputFormat";

    /**
     * Indicates the "outputType" attribute.
     */
    public static final String OUTPUT_TYPE_ATTR = "outputType";

    /**
     * Indicates the "outputFormat" attribute.
     */
    public static final String OUTPUT_FORMAT_ATTR = "outputFormat";

    /**
     * Indicates the "regex" attribute.
     */
    public static final String REGEX_ATTR = "regex";

    /**
     * Indicates the "nullText" attribute.
     */
    public static final String NULL_TEXT_ATTR = "nulltext";

    /**
     * Indicates the "nullValue" attribute.
     */
    public static final String NULL_VALUE_ATTR = "nullvalue";

    /**
     * Indicates the "align" attribute.
     */
    public static final String ALIGN_ATTR = "align";

    /**
     * Indicates the "wrap" attribute.
     */
    public static final String WRAP_ATTR = "wrap";

    /**
     * Indicates the "display" attribute.
     */
    public static final String DISPLAY_ATTR = "display";

    /**
     * The attribute value for "general" alignment.
     */
    public static final short ALIGN_GENERAL = 0;

    /**
     * The attribute value for "centre" alignment.
     */
    public static final short ALIGN_CENTRE = 1;

    /**
     * The attribute value for "left" alignment.
     */
    public static final short ALIGN_LEFT = 2;

    /**
     * The attribute value for "right" alignment.
     */
    public static final short ALIGN_RIGHT = 3;

    /**
     * The attribute value for "justify" alignment.
     */
    public static final short ALIGN_JUSTIFY = 4;

    /**
     * The attribute value for "fill" alignment.
     */
    public static final short ALIGN_FILL = 5;

    /**
     * Constructor that takes a column name
     * @param s The name of the column
     */
    public FileColumn(String s)
    {
        name = s.trim();
    }

    /**
     * Set the column attributes from the given string in the format name=value[:name=value...]
     * @param str The string containing the formatted attributes
     */
    public void setAttributes(String str)
    {
        str = str.trim();
        String delimiter = ":";    // default is colon delimiter,
        if(str.indexOf(";") != -1) // but use semi-colon with date format parameters (eg. HH:mm)
            delimiter = ";";
        StringTokenizer st = new StringTokenizer(str, delimiter);
        while(st.hasMoreTokens())
        {
            String token = st.nextToken().trim();
            int pos = token.indexOf("=");
            if(pos != -1)
            {
                String name = token.substring(0, pos).trim();
                String value = token.substring(pos+1).trim();

                if(name.equals(TYPE_ATTR))
                {
                    type = getCellType(value);
                }
                else if(name.equals(FORMAT_ATTR))
                {
                    format = value;
                }
                else if(name.equals(INPUT_TYPE_ATTR))
                {
                    inputType = getDataType(value);
                    if(outputType == NO_TYPE)
                        outputType = inputType;
                }
                else if(name.equals(INPUT_FORMAT_ATTR))
                {
                    inputFormat = value;
                }
                else if(name.equals(OUTPUT_TYPE_ATTR))
                {
                    outputType = getDataType(value);
                    if(inputType == NO_TYPE)
                        inputType = outputType;
                }
                else if(name.equals(OUTPUT_FORMAT_ATTR))
                {
                    outputFormat = value;
                }
                else if(name.equals(REGEX_ATTR))
                {
                    regex = value;
                }
                else if(name.equals(NULL_VALUE_ATTR))
                {
                    nullValue = value;
                }
                else if(name.equals(ALIGN_ATTR))
                {
                    align = getAlignment(value);
                }
                else if(name.equals(WRAP_ATTR))
                {
                    wrap = Boolean.parseBoolean(value);
                }
                else if(name.equals(DISPLAY_ATTR))
                {
                    display = value;
                }
            }
        }
    }

    /**
     * Returns the cell type for the given value.
     * @param value The cell type value
     * @return The code for the cell type
     */
    private short getCellType(String value)
    {
        short ret = STRING_TYPE;

        if(value.equals("number"))
            ret = NUMBER_TYPE;
        else if(value.equals("datetime"))
            ret = DATETIME_TYPE;
        else if(value.equals("boolean"))
            ret = BOOLEAN_TYPE;

        return ret;
    }

    /**
     * Returns the data type for the given value.
     * @param value The data type value
     * @return The code for the data type
     */
    private short getDataType(String value)
    {
        short ret = STRING_TYPE;

        if(value.equals("number"))
            ret = NUMBER_TYPE;
        else if(value.equals("integer"))
            ret = INTEGER_TYPE;
        else if(value.equals("decimal"))
            ret = DECIMAL_TYPE;
        else if(value.equals("seconds"))
            ret = SECONDS_TYPE;
        else if(value.equals("datetime"))
            ret = DATETIME_TYPE;
        else if(value.equals("boolean"))
            ret = BOOLEAN_TYPE;

        return ret;
    }

    /**
     * Returns the alignment for the given value.
     * @param value The alignment value
     * @return The code for the alignment
     */
    private short getAlignment(String value)
    {
        short ret = ALIGN_LEFT;

        if(value.equals("centre"))
            ret = ALIGN_CENTRE;
        else if(value.equals("left"))
            ret = ALIGN_LEFT;
        else if(value.equals("right"))
            ret = ALIGN_RIGHT;
        else if(value.equals("justify"))
            ret = ALIGN_JUSTIFY;
        else if(value.equals("fill"))
            ret = ALIGN_FILL;

        return ret;
    }

    /**
     * Returns the name of the column with its attributes.
     */
    public String toString()
    {
        StringBuffer buff = new StringBuffer();
        buff.append("name=");
        buff.append(name);
        buff.append(" type=");
        buff.append(type);
        buff.append(" format=");
        buff.append(format);
        buff.append(" inputType=");
        buff.append(inputType);
        buff.append(" inputFormat=");
        buff.append(inputFormat);
        buff.append(" outputType=");
        buff.append(outputType);
        buff.append(" outputFormat=");
        buff.append(outputFormat);
        buff.append(" regex=");
        buff.append(regex);
        buff.append(" align=");
        buff.append(align);
        buff.append(" wrap=");
        buff.append(wrap);
        buff.append(" nullValue=");
        buff.append(nullValue);
        buff.append(" display=");
        buff.append(display);
        return buff.toString();
    }

    /**
     * Returns the name of the column.
     * @return The name of the column
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the displayed name of the column.
     * @return The displayed name of the column
     */
    public String getDisplayName()
    {
        return display.length() > 0 ? display : name;
    }

    /**
     * Returns the type of the column.
     * @return The type of the column
     */
    public short getType()
    {
        return type;
    }

    /**
     * Sets the type of the column.
     * @param type The type of the column
     */
    public void setType(short type)
    {
        this.type = type;
    }

    /**
     * Returns the text that should be displayed for the column if the value is null.
     * @return The text that should be displayed for the column if the value is null
     */
    public String getNullValue()
    {
        return nullValue;
    }

    /**
     * Returns the display format of the column.
     * @return The display format of the column
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * Sets the display format of the column.
     * @param format The display format of the column
     */
    public void setFormat(String format)
    {
        if(format != null)
            this.format = new String(format);
    }

    /**
     * Returns the input type of the column for conversion.
     * @return The input type of the column for conversion
     */
    public short getInputType()
    {
        return inputType;
    }

    /**
     * Sets the input type of the column for conversion.
     * @param inputType The input type of the column for conversion
     */
    public void setInputType(short inputType)
    {
        this.inputType = inputType;
    }

    /**
     * Returns the input format of the column for conversion.
     * @return The input format of the column for conversion
     */
    public String getInputFormat()
    {
        return inputFormat;
    }

    /**
     * Sets the input format of the column for conversion.
     * @param inputFormat The input format of the column for conversion
     */
    public void setInputFormat(String inputFormat)
    {
        if(inputFormat != null)
            this.inputFormat = new String(inputFormat);
    }

    /**
     * Returns the output type of the column for conversion.
     * @return The output type of the column for conversion
     */
    public short getOutputType()
    {
        return outputType;
    }

    /**
     * Sets the output type of the column for conversion.
     * @param outputType The output type of the column for conversion
     */
    public void setOutputType(short outputType)
    {
        this.outputType = outputType;
    }

    /**
     * Returns the output format of the column.
     * @return The output format of the column for conversion
     */
    public String getOutputFormat()
    {
        return outputFormat;
    }

    /**
     * Sets the output format of the column.
     * @param outputFormat The output format of the column for conversion
     */
    public void setOutputFormat(String outputFormat)
    {
        if(outputFormat != null)
            this.outputFormat = new String(outputFormat);
    }

    /**
     * Returns the regular expression for the column.
     * @return The regular expression for the column
     */
    public String getRegex()
    {
        return regex;
    }

    /**
     * Sets the regular expression for the column.
     * @param regex The regular expression for the column
     */
    public void setRegex(String regex)
    {
        if(regex != null)
            this.regex = new String(regex);
    }

    /**
     * Returns the alignment of the column.
     * @return The alignment of the column
     */
    public short getAlign()
    {
        return align;
    }

    /**
     * Returns <CODE>true</CODE> if the column should wrap lines.
     * @return <CODE>true</CODE> if the column should wrap lines
     */
    public boolean getWrap()
    {
        return wrap;
    }

    /**
     * Returns the alternative (displayed) name for the column.
     * @return The alternative (displayed) name for the column
     */
    public String getDisplay()
    {
        return display;
    }

    /**
     * Sets the alternative (displayed) name for the column.
     * @param display The alternative (displayed) name for the column
     */
    public void setDisplay(String display)
    {
        if(display != null)
            this.display = new String(display);
    }

    /**
     * Returns the format object for this column.
     * @return The format object for this column
     */
    public WritableCellFormat getCellFormat()
    {
        return getCellFormat(true);
    }

    /**
     * Returns the format object for this column.
     * @param create <CODE>true</CODE> if the format object should be created if it doesn't exist
     * @return The format object for this column
     */
    public WritableCellFormat getCellFormat(boolean create)
    {
        WritableCellFormat ret = null;
        if(cellFormat != null)
            ret = cellFormat;
        else if(create)
            ret = new WritableCellFormat(NumberFormats.TEXT);
        return ret;
    }

    /**
     * Sets the format object for this column.
     * @param cf The format object to use for this column
     */
    public void setCellFormat(WritableCellFormat cf)
    {
        cellFormat = cf;
    }

    /**
     * Returns the formatted object for the given cell.
     * @param o The value of the cell
     * @param dateFormat The date format to use if the cell contains a date
     * @return The formatted object for the given cell
     */
    public String getValue(Object o, String dateFormat) throws ParseException
    {
        String ret = "null";
        if(o != null)
        {
            if(o instanceof java.lang.Number)
            {
                ret = o.toString();
                if(type == NO_TYPE)
                    type = NUMBER_TYPE;
                if(inputType != NO_TYPE)
                    ret = convert(ret, dateFormat);
            }
            else if(o instanceof java.lang.Boolean)
            {
                ret = o.toString();
                if(type == NO_TYPE)
                    type = BOOLEAN_TYPE;
            }
            else if(o instanceof Date)
            {
                Date dt = (Date)o;
                ret = Long.toString(dt.getTime());
                if(type == NO_TYPE)
                    type = DATETIME_TYPE;
                if(format.length() == 0)
                    format = new String(dateFormat);
            }
            else
            {
                ret = o.toString().trim();
                if(type == NO_TYPE)
                    type = STRING_TYPE;
                if(!(o instanceof String))
                    logger.severe("invalid file column type="+o.getClass().getName());
                if(inputType != NO_TYPE)
                    ret = convert(ret, dateFormat);
            }
        }
        else // Cell contains null
        {
            // Replace null item with default string
            if(nullValue != null)
                ret = new String(nullValue);
        }

        return ret;
    }

    /**
     * Convert the given string using the defined input and output formats and types.
     * @param str The value to be converted
     * @param dateFormat The date format to use if the value is a date
     * @return The converted value
     */
    private String convert(String str, String dateFormat) throws ParseException
    {
        String ret = str;

        // Carry out the required string conversion
        long longValue = 0L;
        double doubleValue = 0.0d;

        // Convert the input value to a number
        if(str.length() > 0)
        {
            if(inputType == INTEGER_TYPE || inputType == NUMBER_TYPE)
                longValue = Long.parseLong(str);
            else if(inputType == DECIMAL_TYPE || inputType == SECONDS_TYPE)
                doubleValue = Double.parseDouble(str);
            else if(inputType == DATETIME_TYPE && inputFormat.length() > 0)
                longValue = parseDateTime(str, inputFormat);
        }

        // Convert seconds to milliseconds
        if(inputType == SECONDS_TYPE)
            doubleValue *= 1000.0d;

        // Allow for cross type conversions
        //   eg. decimal->datetime, seconds->datetime
        if(inputType == DECIMAL_TYPE || inputType == SECONDS_TYPE)
            longValue = (long)doubleValue;
        else
            doubleValue = (double)longValue;

        // Convert the number to the output format
        if(outputType == INTEGER_TYPE)
            ret = Long.toString(longValue);
        else if(outputType == DECIMAL_TYPE || outputType == SECONDS_TYPE)
            ret = convertDecimal(doubleValue, outputFormat);
        else if(outputType == DATETIME_TYPE)
            ret = convertDateTime(longValue, 
                outputFormat.length() > 0 ? outputFormat : dateFormat);
        else if(outputType == STRING_TYPE)
            ret = convertString(str, outputFormat, regex);

        return ret;
    }

    /**
     * Parses the given date string in the given format to a milliseconds vale.
     * @param str The formatted date to be parsed
     * @param format The format to use when parsing the date
     * @return The date in milliseconds
     */
    private long parseDateTime(String str, String pattern) throws ParseException
    {
        return TimeUtils.toMillisUTC(str, pattern);
    }

    /**
     * Converts the given milliseconds date value to the output date format.
     * @param dt The date to be formatted
     * @param format The format to use for the date
     * @return The formatted date
     */
    private String convertDateTime(long dt, String pattern)
    {
        if(pattern.length() == 0)
            pattern = Formats.DATETIME_FORMAT;
        return TimeUtils.toStringUTC(dt, pattern);
    }

    /**
     * Converts the given numeric value to the output date format.
     * @param d The value to be converted
     * @param format The format to use for the value
     * @return The formatted value
     */
    private String convertDecimal(double d, String format)
    {
        String ret = "";

        if(format.length() > 0)
        {
            DecimalFormat f = new DecimalFormat(format);
            ret = f.format(d);
        }
        else
        {
            ret = Double.toString(d);
        }

        return ret;
    }

    /**
     * Converts the given string value to the output string format.
     * @param str The value to be converted
     * @param format The format to use for the value
     * @param expr A regex to use for formatting the value
     * @return The formatted value
     */
    private String convertString(String str, String format, String expr)
    {
        String ret = str;

        String[] params = null;
        if(format.length() > 0)
        {
            ret = "";
            if(expr.length() > 0) // Indicates a format using regex
            {

                Pattern pattern = getPattern(expr);
                Matcher m = pattern.matcher(str);
                if(m.find())
                {
                    params = new String[m.groupCount()];
                    for(int i = 0; i < m.groupCount(); i++)
                        params[i] = m.group(i+1);
                }
            }
            else if(str != null)
            {
                params = new String[]{str};
            }

            if(params != null)
                ret = String.format(format, params);
        }

        return ret;
    }

    /**
     * Returns the pattern for the given regular expression.
     * @param expr A regex to use for formatting
     * @return The pattern for the expression
     */
    private Pattern getPattern(String expr)
    {
        Pattern pattern = patterns.get(expr);
        if(pattern == null)
        {
            pattern = Pattern.compile(expr);
            patterns.put(expr, pattern);
        }
        return pattern;
    }

    private String name = "";
    private String nullValue = null;
    private short type = NO_TYPE;
    private String format = "";
    private short inputType = NO_TYPE;
    private String inputFormat = "";
    private short outputType = NO_TYPE;
    private String outputFormat = "";
    private String regex = "";
    private WritableCellFormat cellFormat = null;
    private short align = ALIGN_GENERAL;
    private boolean wrap = false;
    private String display = "";

    private static Map<String,Pattern> patterns = new Hashtable<String,Pattern>();
}