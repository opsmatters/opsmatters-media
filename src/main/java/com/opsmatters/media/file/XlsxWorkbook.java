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
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.SharedStrings;
import org.docx4j.openpackaging.parts.SpreadsheetML.Styles;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.Sheets;
import org.xlsx4j.sml.Sheet;
import org.xlsx4j.sml.SheetData;
import org.xlsx4j.sml.SheetView;
import org.xlsx4j.sml.SheetViews;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTSst;
import org.xlsx4j.sml.CTStylesheet;
import org.xlsx4j.sml.CTNumFmts;
import org.xlsx4j.sml.CTNumFmt;
import org.xlsx4j.sml.CTFonts;
import org.xlsx4j.sml.CTFont;
import org.xlsx4j.sml.CTFontName;
import org.xlsx4j.sml.CTFontSize;
import org.xlsx4j.sml.CTIntProperty;
import org.xlsx4j.sml.CTBooleanProperty;
import org.xlsx4j.sml.CTXf;
import org.xlsx4j.sml.CTXstringWhitespace;
import org.xlsx4j.sml.CTCellXfs;
import org.xlsx4j.sml.CTCellStyle;
import org.xlsx4j.sml.CTCellStyles;
import org.xlsx4j.sml.CTCellStyleXfs;
import org.xlsx4j.sml.CTCellAlignment;
import org.xlsx4j.sml.CTCellFormula;
import org.xlsx4j.sml.STHorizontalAlignment;
import org.xlsx4j.sml.CTBorders;
import org.xlsx4j.sml.CTBorder;
import org.xlsx4j.sml.CTBorderPr;
import org.xlsx4j.sml.CTFills;
import org.xlsx4j.sml.CTFill;
import org.xlsx4j.sml.CTPane;
import org.xlsx4j.sml.CTPatternFill;
import org.xlsx4j.sml.CTRElt;
import org.xlsx4j.sml.CTSelection;
import org.xlsx4j.sml.Cols;
import org.xlsx4j.sml.Col;
import org.xlsx4j.sml.STPane;
import org.xlsx4j.sml.STPaneState;
import org.xlsx4j.sml.STPatternType;
import org.xlsx4j.sml.STCellFormulaType;
import java.util.logging.Logger;
import com.opsmatters.media.util.StringUtils;

/**
 * Wrapper class for an Excel XLSX workbook.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class XlsxWorkbook extends Workbook
{
    private static final Logger logger = Logger.getLogger(XlsxWorkbook.class.getName());

    public static final String URI = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";

    /**
     * The maximum width for a cell.
     */
    public static final int MAX_WIDTH = 100;

    /**
     * The maximum length for a cell.
     */
    public static final int MAX_LENGTH = 32767;

    /**
     * The maximum rows for an XLSX output.
     */
    public static final int MAX_ROWS = 200000;

    /**
     * Private constructor.
     * @param file The file with the workbook
     */
    private XlsxWorkbook(File file) throws Docx4JException
    {
        this.file = file;
        pkg = SpreadsheetMLPackage.load(file);
        initWorkbook();
    }

    /**
     * Private constructor.
     * @param stream The input stream with the workbook
     */
    private XlsxWorkbook(InputStream stream) 
        throws Docx4JException
    {
        pkg = (SpreadsheetMLPackage)SpreadsheetMLPackage.load(stream);
        initWorkbook();
    }

    /**
     * Private constructor.
     * @param pkg The package to use to create the workbook
     * @param stream The output stream to write the workbook to
     */
    private XlsxWorkbook(SpreadsheetMLPackage pkg, OutputStream stream) 
        throws Docx4JException
    {
        this.pkg = pkg;
        outputStream = stream;
        initWorkbook();
    }

    /**
     * Returns an existing workbook object.
     * @param file The file with the workbook
     * @return The existing workbook object
     * @throws IOException if the workbook cannot be opened
     */
    public static XlsxWorkbook getWorkbook(File file) throws IOException
    {
        try
        {
            return new XlsxWorkbook(file);
        }
        catch(Docx4JException e)
        {
            if(e.getCause() != null
                && e.getCause() instanceof IOException)
                throw (IOException)e.getCause();
            else
                throw new IOException(e);
        }
    }

    /**
     * Returns an existing workbook object.
     * @param stream The stream with the workbook
     * @return The existing workbook object
     * @throws IOException if the stream cannot be read
     */
    public static XlsxWorkbook getWorkbook(InputStream stream) throws IOException
    {
        try
        {
            return new XlsxWorkbook(stream);
        }
        catch(Docx4JException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Initialises the internal workbook.
     */
    private void initWorkbook() throws Docx4JException
    {
        if(pkg == null)
            return;

        wbp = pkg.getWorkbookPart();
        sheets = ((org.xlsx4j.sml.Workbook)wbp.getJaxbElement()).getSheets().getSheet();
        wbrp = wbp.getRelationshipsPart();
        if(wbrp == null)
        {
            wbrp = new RelationshipsPart();
            wbrp.setPartName(new PartName("/xl/_rels/workbook.xml.rels"));

            //wbrp = new RelationshipsPart(new PartName("/xl/_rels/workbook.xml.rels"));
            wbp.addTargetPart(wbrp);
        }

        prp = pkg.getRelationshipsPart();
        if(prp == null)
        {
            prp = new RelationshipsPart();
            pkg.addTargetPart(prp);
        }

        PartName ssPart = new PartName("/xl/sharedStrings.xml");
        sharedStrings = (SharedStrings)pkg.getParts().get(ssPart);
        if(sharedStrings == null)
        {
            sharedStrings = new SharedStrings(ssPart);
            sharedStrings.setJaxbElement(new CTSst());
            wbp.addTargetPart(sharedStrings);
        }
        strings = sharedStrings.getJaxbElement();

        PartName stylesPart = new PartName("/xl/styles.xml");
        styles = (Styles)pkg.getParts().get(stylesPart);
        if(styles == null)
        {
            styles = new Styles(stylesPart);
            CTStylesheet styleSheet = new CTStylesheet();
            styles.setJaxbElement(styleSheet);
            wbp.addTargetPart(styles);
        }
        stylesheet = styles.getJaxbElement();

        fonts = stylesheet.getFonts();
        if(fonts == null)
        {
            fonts = new CTFonts();
            stylesheet.setFonts(fonts);
        }

        cellXfs = stylesheet.getCellXfs();
        if(cellXfs == null)
        {
            cellXfs = new CTCellXfs();
            stylesheet.setCellXfs(cellXfs);
        }

        cellStyles = stylesheet.getCellStyles();
        if(cellStyles == null)
        {
            cellStyles = new CTCellStyles();
            stylesheet.setCellStyles(cellStyles);
        }

        cellStyleXfs = stylesheet.getCellStyleXfs();
        if(cellStyleXfs == null)
        {
            cellStyleXfs = new CTCellStyleXfs();
            stylesheet.setCellStyleXfs(cellStyleXfs);
        }

        borders = stylesheet.getBorders();
        if(borders == null)
        {
            borders = new CTBorders();
            stylesheet.setBorders(borders);
        }

        fills = stylesheet.getFills();
        if(fills == null)
        {
            fills = new CTFills();
            stylesheet.setFills(fills);
        }

        if(sheets != null) 
        {
            for(int i = 0; i < sheets.size(); i++)
            {
                Sheet sheet = (Sheet)sheets.get(i);
                WorksheetPart wsp = (WorksheetPart)wbrp.getPart(sheet.getId());
                if(wsp != null)
                    worksheets.put(sheet.getName(), new XlsxWorksheet(this, wsp));
            }
        }

        //printFontInfo();
    }

    /**
     * Returns the internal workbook.
     * @return The internal workbook object
     */
    @Override
    public Object getWorkbook() 
    {
        return wbp;
    }

    /**
     * Returns the number of worksheets in the given workbook.
     * @return The number of worksheets in the given workbook
     */
    @Override
    public int numSheets()
    {
        return sheets != null ? sheets.size() : -1;
    }

    /**
     * Returns the list of worksheet names from the given Excel XLSX file.
     * @return The list of worksheet names from the given workbook
     */
    @Override
    public String[] getSheetNames()
    {
        String[] ret = null;
        if(sheets != null)
        {
            ret = new String[sheets.size()];
            for(int i = 0; i < sheets.size(); i++)
            {
                Sheet sheet = (Sheet)sheets.get(i);
                ret[i] = sheet.getName();
            }
        }
        return ret;
    }

    /**
     * Returns the worksheet at the given index in the workbook.
     * @param i The index of the worksheet
     * @return The worksheet at the given index in the workbook
     */
    @Override
    public XlsxWorksheet getSheet(int i)
    {
        XlsxWorksheet ret = null;
        if(sheets != null)
        {
            Sheet sheet = (Sheet)sheets.get(i);
            if(sheet != null)
                ret = getSheet(sheet.getName());
        }
        return ret;
    }

    /**
     * Returns the worksheet with the given name in the workbook.
     * @param name The name of the worksheet
     * @return The worksheet with the given name in the workbook
     */
    @Override
    public XlsxWorksheet getSheet(String name)
    {
        return (XlsxWorksheet)worksheets.get(name);
    }

    /**
     * Returns the name corresponding to the given worksheet part.
     * @param part The worksheet part
     * @return The name of the worksheet
     */
    public String getSheetName(WorksheetPart part)
    {
        String ret = null;

        if(sheets != null) 
        {
            for(int i = 0; i < sheets.size(); i++)
            {
                Sheet sheet = (Sheet)sheets.get(i);
                WorksheetPart wsp = (WorksheetPart)wbrp.getPart(sheet.getId());
                if(wsp == part)
                    ret = sheet.getName();
            }
        }

        return ret;
    }

    /**
     * Returns the string at the given index in SharedStrings.xml.
     * @param i The index of the string
     * @return The string at the given index in SharedStrings.xml
     */
    public String getSharedString(int i)
    {
        String ret = null;
        CTRst string = strings.getSi().get(i);
        if(string != null && string.getT() != null)
            ret = string.getT().getValue();
        if(ret == null) // cell has multiple formats or fonts
        {
            List<CTRElt> list = string.getR();
            if(list.size() > 0)
            {
                for(CTRElt lt : list)
                {
                    String str = lt.getT().getValue();
                    if(str != null)
                    {
                        if(ret == null)
                            ret = "";
                        ret += str;
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Returns a reference to the stylesheet in styles.xml.
     * @return A reference to the stylesheet in styles.xml
     */
    public CTStylesheet getStylesheet()
    {
        return stylesheet;
    }

    /**
     * Returns the number format code for given id in styles.xml.
     * @param id The number format id
     * @return The number format code for given id in styles.xml
     */
    public String getFormatCode(long id)
    {
        if(numFmts == null)
            cacheFormatCodes();
        return (String)numFmts.get(Long.valueOf(id));
    }

    /**
     * Adds the given number format to the cache.
     * @param fmt The number format to be added
     */
    private void addFormatCode(CTNumFmt fmt) 
    {
        if(numFmts == null)
            numFmts = new HashMap();
        numFmts.put(fmt.getNumFmtId(), 
            fmt.getFormatCode());
    }

    /**
     * Returns the id for the given number format from the cache.
     * @param formatCode The number format code to be checked
     * @return The id for the given number format from the cache
     */
    private long getFormatId(String formatCode) 
    {
        long ret = 0L;

        if(formatCode != null && formatCode.length() > 0)
        {
            if(numFmts != null)
            {
                Iterator it = numFmts.entrySet().iterator();
                while(it.hasNext() && ret == 0L)
                {
                    java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
                    Long id = (Long)entry.getKey();
                    String code = (String)entry.getValue();
                    if(code != null && code.equals(formatCode))
                        ret = id.longValue();
                }
            }

            // If not found, also search
            //   the built-in formats
            if(ret == 0L)
            {
                Long l = (Long)builtinNumFmts.get(formatCode);
                if(l != null)
                    ret = l.longValue();
            }

            // If still not found, 
            //   create a new format
            if(ret == 0L)
            {
                CTNumFmts numFmts = stylesheet.getNumFmts();
                if(numFmts == null)
                {
                    numFmts = new CTNumFmts();
                    stylesheet.setNumFmts(numFmts);
                }

                List list = numFmts.getNumFmt();
                CTNumFmt numFmt = new CTNumFmt();
                numFmt.setNumFmtId(getMaxNumFmtId()+1);
                numFmt.setFormatCode(formatCode);
                list.add(numFmt);
                numFmts.setCount((long)list.size());
                addFormatCode(numFmt);
                ret = numFmt.getNumFmtId();
            }
        }

        return ret;
    }

    /**
     * Returns the maximum numFmtId in styles.xml.
     * @return The maximum numFmtId in styles.xml
     */
    private long getMaxNumFmtId()
    {
        long ret = 163;
        List list = stylesheet.getNumFmts().getNumFmt();
        for(int i = 0; i < list.size(); i++)
        {
            CTNumFmt numFmt = (CTNumFmt)list.get(i);
            if(numFmt.getNumFmtId() > ret)
                ret = numFmt.getNumFmtId();
        }
        return ret;
    }

    /**
     * Caches the format codes in styles.xml.
     */
    private void cacheFormatCodes()
    {
        CTNumFmts numFmts = stylesheet.getNumFmts();
        if(numFmts != null)
        {
            List list = numFmts.getNumFmt();
            for(int i = 0; i < list.size(); i++)
                addFormatCode((CTNumFmt)list.get(i));
        }
    }

    /**
     * Creates a new workbook object.
     * @param os The output stream for the workbook
     * @param existing An existing workbook to add to
     * @return The new workbook object
     * @throws IOException if the workbook cannot be written
     */
    public static XlsxWorkbook createWorkbook(OutputStream os, Workbook existing) 
        throws IOException
    {
        try
        {
            SpreadsheetMLPackage pkg = null;
            if(existing != null)
                pkg = (SpreadsheetMLPackage)((XlsxWorkbook)existing).pkg;
            else
                pkg = SpreadsheetMLPackage.createPackage();
            return new XlsxWorkbook(pkg, os);
        }
        catch(Docx4JException e)
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
    public XlsxWorksheet createSheet(FileColumn[] columns, List<String[]> lines, String sheetName)
        throws IOException
    {
        if(pkg == null)
            return null;

        WorksheetPart wsp = null;

        try
        {
            int sheetId = worksheets.size()+1;
            PartName sheetPart = new PartName("/xl/worksheets/sheet"+sheetId+".xml");
            wsp = pkg.createWorksheetPart(sheetPart, sheetName, (long)sheetId);
        }
        catch(InvalidFormatException e)
        {
            throw new IOException(e);
        }
        catch(javax.xml.bind.JAXBException e)
        {
            throw new IOException(e);
        }

        org.xlsx4j.sml.Worksheet sheet = (org.xlsx4j.sml.Worksheet)wsp.getJaxbElement();
        SheetData sheetData = sheet.getSheetData();

        // Create a default font for body and a bold font for header
        if(fonts.getFont().size() == 0)
        {
            fonts.getFont().add(getFont("Calibri", 11, 2, false)); // fontId = 0
            fonts.getFont().add(getFont("Calibri", 11, 2, true));  // fontId = 1
        }

        // Create default styles
        getXfPos(getXf(0L, 0L, 0, false));
        if(cellStyles.getCellStyle().size() == 0)
            cellStyles.getCellStyle().add(new CTCellStyle());
        if(cellStyleXfs.getXf().size() == 0)
            cellStyleXfs.getXf().add(new CTXf());
        cacheFormatCodes();

        // Default border
        if(borders.getBorder().size() == 0)
        {
            CTBorder border = new CTBorder();
            border.setLeft(new CTBorderPr());
            border.setRight(new CTBorderPr());
            border.setTop(new CTBorderPr());
            border.setBottom(new CTBorderPr());
            border.setDiagonal(new CTBorderPr());
            borders.getBorder().add(border);
        }

        // Default fill
        if(fills.getFill().size() == 0)
        {
            CTFill fill = new CTFill();
            CTPatternFill patternFill = new CTPatternFill();
            patternFill.setPatternType(STPatternType.NONE);
            fill.setPatternFill(patternFill);
            fills.getFill().add(fill);
        }

        // Freeze panes at first row
        SheetViews sheetViews = new SheetViews();
        SheetView sheetView = new SheetView();
        sheetView.setTabSelected(true);
        CTPane pane = new CTPane();
        pane.setActivePane(STPane.BOTTOM_LEFT);
        pane.setYSplit(1d);
        pane.setState(STPaneState.FROZEN);
        int rowcount = lines.size()+1;
        pane.setTopLeftCell("A"+(rowcount > 3 ? rowcount-2 : 2));
        CTSelection selection = new CTSelection();
        selection.setPane(STPane.BOTTOM_LEFT);
        selection.setActiveCell("A"+(rowcount > 3 ? rowcount-1 : 2));
        selection.getSqref().add(selection.getActiveCell());
        sheetView.setPane(pane);
        sheetView.getSelection().add(selection);
        sheetViews.getSheetView().add(sheetView);
        sheet.setSheetViews(sheetViews);

        int[] colWidths = appendRows(sheetData, columns, lines, sheetName);

        // Get the column count from the 1st row
        int numColumns = 0;
        List rows = sheetData.getRow();
        if(rows != null && rows.size() > 0)
        {
            Row r = (Row)rows.get(0);
            numColumns = r.getC().size();
        }

        // Set the column to autosize
        Cols cols = new Cols();
        sheet.getCols().add(cols);
        for(int i = 0; i < numColumns; i++)
        {
            Col col = new Col();
            col.setMin(i+1);
            col.setMax(i+1);
            if(colWidths != null && i < colWidths.length)
                col.setWidth(colWidths[i]+1.0d);
            cols.getCol().add(col);
        }

        XlsxWorksheet ret = new XlsxWorksheet(this, wsp);
        worksheets.put(sheetName, ret);
        return ret;
    }

    /**
     * Adds the given lines of data to an existing sheet in the workbook.
     * @param columns The column definitions for the worksheet
     * @param lines The list of lines to be added to the worksheet
     * @param sheetName The name of the worksheet to be added
     */
    @Override
    public void appendToSheet(FileColumn[] columns, List<String[]> lines, String sheetName)
    {
        XlsxWorksheet sheet = getSheet(sheetName);
        if(sheet != null)
            appendRows(sheet.getSheetData(), columns, lines, sheetName);
    }

    /**
     * Appends the given lines to the bottom of the given sheet.
     * @param sheetData The sheet to add the lines to
     * @param columns The column definitions for the worksheet
     * @param lines The list of lines to be added to the worksheet
     * @param sheetName The name of the worksheet to be added
     * @return The array of column widths following the addition
     */
    private int[] appendRows(SheetData sheetData, FileColumn[] columns, 
        List<String[]> lines, String sheetName)
    {
        int[] colWidths = null;
        int size = sheetData.getRow().size();
        for(int i = 0; i < lines.size(); i++)
        {
            String[] line = lines.get(i);

            if((i+size) > MAX_ROWS)
            {
                logger.severe("the worksheet '"+sheetName
                    +"' has exceeded the maximum rows and will be truncated");
                break;
            }

            Row row = Context.getsmlObjectFactory().createRow();

            if(i == 0)
                colWidths = new int[line.length];
            row.setR(i+size+1L);

            for(int j = 0; j < line.length; j++)
            {
                Cell cell = Context.getsmlObjectFactory().createCell();

                String data = line[j];
                if(data == null)
                    data = "";

                boolean formula = data.startsWith("=");

                // Get a cell with the correct formatting
                short type = FileColumn.STRING_TYPE;
                short align = FileColumn.ALIGN_GENERAL;
                boolean wrap = false;
                long numFmtId = 0L;
                String format = null;

                // Calculate the max column width for auto-fit
                if(colWidths != null && j < colWidths.length)
                {
                    int len = data.length();
                    int pos = data.indexOf("\n");
                    if(pos != -1)
                        len = pos;
                    if(len > MAX_WIDTH)
                    {
                        len = MAX_WIDTH;
                        wrap = true;
                    }

                    if(formula)
                        len = 15;

                    // Add extra column to give right margin
                    len += 1;

                    if(len > colWidths[j])
                        colWidths[j] = len;
                }

                if(columns != null && j < columns.length)
                {
                    FileColumn column = columns[j];
                    setCellAttributes(cell, column, j, i+size, data, sheetName);
                    type = column.getType();
                    align = column.getAlign();
                    wrap = column.getWrap();
                    format = column.getFormat();
                }
                else // Try to figure out the type of the column
                { 
                    try
                    {
                        setNumberCell(cell, data);
                    }
                    catch(NumberFormatException e)
                    {
                        if(formula)
                            setFormulaCell(cell, data);
                        else
                            setSharedStringCell(cell, data, sheetName);
                    }
                }

                if(cell != null)
                {
                    STCellType t = cell.getT();
                    String v = cell.getV();
                    if(t == STCellType.S)
                    {
                        numFmtId = 49L;
                        if(i > 0 && type == FileColumn.DATETIME_TYPE)
                        { 
                            if(format != null && format.length() > 0)
                            {
                                numFmtId = getFormatId(format);
                            }
                            else
                            {
                                Double d = 0.0d;
                                if(v != null && v.length() > 0)
                                    d = Double.parseDouble(v);
                                if(d > 1.0d)
                                    numFmtId = 22L;
                                else
                                    numFmtId = 20L;
                            }
                        }
                    }
                    else if(t == STCellType.N)
                    {
                        if(format != null && format.length() > 0)
                            numFmtId = getFormatId(format);
                        else
                            numFmtId = 1L;
                    }

                    String ref = getColumn(j)+Long.toString(row.getR());

                    if(!formula)
                    {
                        CTXf xf = getXf((i+size) == 0 && hasHeaders() ? 1L : 0L, 
                            numFmtId, align, wrap);
                        int pos = getXfPos(xf);
                        if(pos > 0)
                            cell.setS((long)pos);
                    }

                    cell.setR(ref);
                    row.getC().add(cell);
                }
            }

            sheetData.getRow().add(row);
        }
        return colWidths;
    }

    /**
     * Returns the column reference from the column number.
     * <P>
     * eg. 0=A, 1=B, etc
     * @param i The column number
     * @return The column reference from the column number
     */
    private String getColumn(int i)
    {
        int mult = i/26;
        int rem = i%26;
        StringBuffer buff = new StringBuffer();
        if(mult > 0)
            buff.append((char)('@'+mult));
        buff.append((char)('A'+rem));
        return buff.toString();
    }

    /**
     * Returns the position of the given Xf in the list.
     * <P>
     * If the item is not present in the list, 
     * the new item is added and its position returned.
     * @param xf The Xf to be checked
     * @return The position of the given Xf in the list
     */
    private int getXfPos(CTXf xf)
    {
        int ret = -1;
        List list = cellXfs.getXf();

        long xfFontId = 0L;
        if(xf.getFontId() != null)
            xfFontId = xf.getFontId().longValue();
        boolean xfApplyFont = false;
        if(xf.isApplyFont() != null)
            xfApplyFont = xf.isApplyFont().booleanValue();
        long xfNumFmtId = 0L;
        if(xf.getNumFmtId() != null)
            xfNumFmtId = xf.getNumFmtId().longValue();
        boolean xfApplyNumberFormat = false;
        if(xf.isApplyNumberFormat() != null)
            xfApplyNumberFormat = xf.isApplyNumberFormat().booleanValue();
        boolean xfApplyAlignment = false;
        if(xf.isApplyAlignment() != null)
            xfApplyAlignment = xf.isApplyAlignment().booleanValue();

        boolean xfWrap = false;
        STHorizontalAlignment xfHoriz = STHorizontalAlignment.GENERAL;
        CTCellAlignment xfAlign = xf.getAlignment();
        if(xfAlign != null)
        {
            Boolean b = xfAlign.isWrapText();
            if(b != null)
                xfWrap = b.booleanValue();
            if(xfAlign.getHorizontal() != null)
                xfHoriz = xfAlign.getHorizontal();
        }

        for(int i = 0; i < list.size() && ret == -1; i++)
        {
            CTXf item = (CTXf)list.get(i);

            long itemFontId = 0L;
            if(item.getFontId() != null)
                itemFontId = item.getFontId().longValue();
            boolean itemApplyFont = false;
            if(item.isApplyFont() != null)
                itemApplyFont = item.isApplyFont().booleanValue();
            long itemNumFmtId = 0L;
            if(item.getNumFmtId() != null)
                itemNumFmtId = item.getNumFmtId().longValue();
            boolean itemApplyNumberFormat = false;
            if(item.isApplyNumberFormat() != null)
                itemApplyNumberFormat = item.isApplyNumberFormat().booleanValue();
            boolean itemApplyAlignment = false;
            if(item.isApplyAlignment() != null)
                itemApplyAlignment = item.isApplyAlignment().booleanValue();
            boolean itemWrap = false;
            STHorizontalAlignment itemHoriz = STHorizontalAlignment.GENERAL;
            CTCellAlignment itemAlign = item.getAlignment();
            if(itemAlign != null)
            {
                Boolean b = itemAlign.isWrapText();
                if(b != null)
                    itemWrap = b.booleanValue();
                if(itemAlign.getHorizontal() != null)
                    itemHoriz = itemAlign.getHorizontal();
            }

            if(xfFontId == itemFontId
                && xfApplyFont == itemApplyFont
                && xfNumFmtId == itemNumFmtId
                && xfApplyNumberFormat == itemApplyNumberFormat
                && xfWrap == itemWrap
                && xfHoriz == itemHoriz
                && xfApplyAlignment == itemApplyAlignment)
            {
                ret = i;
            }
        }

        if(ret == -1)
        {
            ret = list.size();
            list.add(xf);
        }

        return ret;
    }

    /**
     * Returns a new CTXf object.
     * @param fontId The id of the font
     * @param numFmtId The number format of the cell
     * @param align The alignment of the cell
     * @param wrap <CODE>true</CODE> if line wrapping should be enabled for the cell
     * @return The new CTXf object
     */
    private CTXf getXf(long fontId, long numFmtId, int align, boolean wrap)
    {
        CTXf ret = new CTXf();

        ret.setXfId(0L);
        ret.setBorderId(0L);
        ret.setFillId(0L);

        if(numFmtId > 0L)
        {
            ret.setApplyNumberFormat(true);
            ret.setNumFmtId(numFmtId);
        }

        if(fontId > 0L)
        {
            ret.setApplyFont(true);
            ret.setFontId(fontId);
        }

        if(align != FileColumn.ALIGN_GENERAL || wrap)
        {
            ret.setApplyAlignment(true);
            CTCellAlignment ca = new CTCellAlignment();

            if(align != FileColumn.ALIGN_GENERAL)
            {
                STHorizontalAlignment a = STHorizontalAlignment.GENERAL;
                if(align == FileColumn.ALIGN_CENTRE)
                    a = STHorizontalAlignment.CENTER;
                else if(align == FileColumn.ALIGN_LEFT)
                    a = STHorizontalAlignment.LEFT;
                else if(align == FileColumn.ALIGN_RIGHT)
                    a = STHorizontalAlignment.RIGHT;
                else if(align == FileColumn.ALIGN_JUSTIFY)
                    a = STHorizontalAlignment.JUSTIFY;
                else if(align == FileColumn.ALIGN_FILL)
                    a = STHorizontalAlignment.FILL;
                ca.setHorizontal(a);
            }

            if(wrap)
                ca.setWrapText(true);

            ret.setAlignment(ca);
        }

        return ret;
    }

    /**
     * Returns the formatted object for the given cell.
     * @param cell The cell to set attributes for
     * @param column The column definition to take the attributes from
     * @param col The column number of the cell
     * @param row The row number of the cell
     * @param data The data in the cell
     * @param sheetName The name of the worksheet containing the cell
     * @return The formatted object for the given cell
     */
    private void setCellAttributes(Cell cell, FileColumn column, int col, int row, 
        String data, String sheetName)
    {
        short type = column.getType();
        boolean formula = data.startsWith("=");

        try
        {
            if((row == 0 && hasHeaders()) || data.length() == 0 || data.equals("null"))
            {
                setSharedStringCell(cell, data, sheetName);
            }
            else if(type == FileColumn.STRING_TYPE || type == FileColumn.NO_TYPE)
            {
                if(formula)
                    setFormulaCell(cell, data);
                else
                    setSharedStringCell(cell, data, sheetName);
            }
            else if(type == FileColumn.NUMBER_TYPE)
            {
                setNumberCell(cell, data);
            }
            else if(type == FileColumn.BOOLEAN_TYPE)
            {
                setBooleanCell(cell, data);
            }
            else if(type == FileColumn.DATETIME_TYPE)
            {
                setDateCell(cell, data);
            }
        }
        catch(NumberFormatException e)
        {
            logger.severe("column has illegal type or format (data="+data
                +", column=["+column.toString()+"]): "+e.getMessage());
            setSharedStringCell(cell, data, sheetName);
        }
    }

    /**
     * Sets the given cell to the "S" (shared string) type.
     * @param cell The cell to set
     * @param data The data in the cell
     * @param sheetName The name of the worksheet containing the cell
     */
    private void setSharedStringCell(Cell cell, String data, String sheetName)
    {
        if(stringMap == null)
        {
            stringMap = new HashMap();
            List list = strings.getSi();
            for(int i = 0; i < list.size(); i++)
            {
                CTRst ctrst = (CTRst)list.get(i);
                stringMap.put(ctrst.getT(), Integer.valueOf(i));
            }
        }

        // Truncate the cell text if > 32767
        if(data.length() > MAX_LENGTH)
        {
            logger.severe("Truncating cell in sheet '"+sheetName
                +"' as maximum length exceeded: "+data.length());
            data = data.substring(0, MAX_LENGTH-1);
        }

        // Remove any illegal characters from the value
        String converted = StringUtils.stripNonValidXMLCharacters(data);
        if(!data.equals(converted))
        {
            logger.severe("Removed illegal characters from cell in sheet '"
                +sheetName+"': "+data);
            data = converted;
        }

        cell.setT(STCellType.S);
        int pos = -1;
        Integer str = (Integer)stringMap.get(data);
        if(str == null)
        {
            CTRst crt = new CTRst();
            CTXstringWhitespace csw = new CTXstringWhitespace();
            csw.setValue(data);
            crt.setT(csw);
            strings.getSi().add(crt);
            pos = stringMap.size();
            stringMap.put(data, Integer.valueOf(pos));
        }
        else
        {
            pos = str.intValue();
        }
        cell.setV(Integer.toString(pos));
    }

    /**
     * Sets the given cell to contain a formula.
     * @param cell The cell to set
     * @param data The data in the cell
     */
    private void setFormulaCell(Cell cell, String data)
    {
        CTCellFormula f = new CTCellFormula();
        String value = data.substring(1); // Remove "="
        if(value.startsWith("[") && value.endsWith("]")) // SHARED formula
        {
            value = value.substring(1, value.length()-1);
            if(value.indexOf("|") != -1)
            {
                String[] tokens = value.split("\\|"); // Split by | char
                long si = Long.parseLong(tokens[0]);
                String ref = tokens[1];
                String v = tokens[2];
                setSharedFormula(si, new SharedFormula(ref, v));

                f.setT(STCellFormulaType.SHARED);
                f.setRef(ref);
                f.setSi(si);
                f.setValue(v);
            }
            else // ref
            {
                f.setT(STCellFormulaType.SHARED);
                f.setSi(Long.parseLong(value));
            }
        }
        else
        {
            f.setValue(value);
        }

        cell.setF(f);
    }

    /**
     * Sets the given cell to the "N" (number) type.
     * @param cell The cell to set
     * @param data The data in the cell
     */
    private void setNumberCell(Cell cell, String data)
    {
        double num = Double.parseDouble(data);
        cell.setT(STCellType.N);
        cell.setV(data);
    }

    /**
     * Sets the given cell to the "S" (string) type with a date format.
     * @param cell The cell to set
     * @param data The data in the cell
     */
    private void setDateCell(Cell cell, String data)
    {
        long dt = Long.parseLong(data);
        dt += getOffset(dt);
        double d = dt/86400000d;  // Convert millis to days
        if(d > 1.0d)     // Times stored as fraction of a day
            d += 25569d; // Add offset because excel dates are from 1900
        cell.setV(Double.toString(d));
    }

    /**
     * Sets the given cell to the "B" (boolean) type.
     * @param cell The cell to set
     * @param data The data in the cell
     */
    private void setBooleanCell(Cell cell, String data)
    {
        cell.setT(STCellType.B);
        cell.setV(data.equals("TRUE") ? "1" : "0");
    }

    /**
     * Returns a new CTFont object with the given attributes.
     * @param name The name of the font
     * @param sz The size of the font
     * @param family The family of the font
     * @param bold <CODE>true</CODE> if the font should be bold
     * @return The new CTFont object with the given attributes
     */
    private CTFont getFont(String name, int sz, int family, boolean bold)
    {
        CTFont ret = new CTFont();
        List list = ret.getNameOrCharsetOrFamily();
        list.add(new JAXBElement(new QName(URI, "name"), 
            CTFontName.class, getFontName(name)));
        list.add(new JAXBElement(new QName(URI, "sz"), 
            CTFontSize.class, getFontSize(sz)));
        list.add(new JAXBElement(new QName(URI, "family"), 
            CTIntProperty.class, getIntProperty(family)));
        if(bold)
            list.add(new JAXBElement(new QName(URI, "b"), 
                CTBooleanProperty.class, getBooleanProperty(bold)));
        return ret;
    }

    /**
     * Returns a new CTFontName object.
     * @param name The name of the font
     * @return The new CTFontName object
     */
    private CTFontName getFontName(String name)
    {
        CTFontName ret = new CTFontName();
        ret.setVal(name);
        return ret;
    }

    /**
     * Returns a new CTFontSize object.
     * @param sz The size of the font
     * @return The new CTFontSize object
     */
    private CTFontSize getFontSize(int sz)
    {
        CTFontSize ret = new CTFontSize();
        ret.setVal(sz);
        return ret;
    }

    /**
     * Returns a new CTIntProperty object.
     * @param i The value of the property
     * @return The new CTIntProperty object
     */
    private CTIntProperty getIntProperty(int i)
    {
        CTIntProperty ret = new CTIntProperty();
        ret.setVal(i);
        return ret;
    }

    /**
     * Returns a new CTBooleanProperty object.
     * @param b The value of the property
     * @return The new CTBooleanProperty object
     */
    private CTBooleanProperty getBooleanProperty(boolean b)
    {
        CTBooleanProperty ret = new CTBooleanProperty();
        ret.setVal(b);
        return ret;
    }

    /**
     * Returns the shared formula at the given index.
     * @param i The index of the formula
     * @return The formula at the given index
     */
    public SharedFormula getSharedFormula(long i)
    {
        return sharedFormulas.get(i);
    }

    /**
     * Sets the shared formula at the given index.
     * @param i The index of the formula
     * @param f The formula at the given index
     */
    public void setSharedFormula(long i, SharedFormula f)
    {
        if(sharedFormulas == null)
            sharedFormulas = new HashMap<Long,SharedFormula>();
        sharedFormulas.put(i, f);
    }

    /**
     * Write the workbook.
     * @throws IOException if the workbook cannot be written
     */
    @Override
    public void write() throws IOException
    {
        if(pkg != null)
        {
            try
            {
                if(outputStream != null)
                {
                    Save saver = new Save(pkg);
                    saver.save(outputStream);
                }
                else if(file != null)
                {
                    pkg.save(file);
                }
            }
            catch(Docx4JException e)
            {
                throw new IOException(e);
            }
        }
    }

    /**
     * Close the workbook.
     */
    @Override
    public void close()
    {
        pkg = null;
        file = null;
        outputStream = null;
        wbp = null;
        if(sheets != null)
            sheets.clear();
        prp = null;
        wbrp = null;
        sharedStrings = null;
        strings = null;
        styles = null;
        numFmts = null;
        if(stringMap != null)
            stringMap.clear();
        worksheets.clear();
    }

    /**
     * Initialises the JAXB contexts
     */
    public static void initJaxbContexts()
    {
        if(!jaxbInitialised)
        {
            logger.fine("Initialising xlsx subsystem...");
            new org.docx4j.jaxb.Context();
            new org.xlsx4j.jaxb.Context();
            logger.fine("Initialisation of xlsx subsystem complete");
            jaxbInitialised = true;
        }
    }

    /**
     * Prints information on the fonts for this workbook.
     */
    public void printFontInfo()
    {
        if(fonts != null)
        {
            List font = fonts.getFont();
            for(int i = 0; i < font.size(); i++)
            {
                CTFont f = (CTFont)font.get(i);
                List names = f.getNameOrCharsetOrFamily();
                for(int j = 0; j < names.size(); j++)
                {
                    JAXBElement elem = (JAXBElement)names.get(j);
                    logger.info("fonts: i="+i+" name="+elem.getName()
                        +" value="+elem.getValue()
                        +" obj="+elem.getValue().getClass().getName());
                }
            }
        }
    }

    private static Map builtinNumFmts = new HashMap();

    static
    {
        // Add the built in formats to the cache
        builtinNumFmts.put("0", Long.valueOf(1L));
        builtinNumFmts.put("0.00", Long.valueOf(2L));
        builtinNumFmts.put("#,##0", Long.valueOf(3L));
        builtinNumFmts.put("#,##0.00", Long.valueOf(4L));
        builtinNumFmts.put("0%", Long.valueOf(9L));
        builtinNumFmts.put("0.00%", Long.valueOf(10L));
        builtinNumFmts.put("0.00E+00", Long.valueOf(11L));
        builtinNumFmts.put("# ?/?", Long.valueOf(12L));
        builtinNumFmts.put("# ??/??", Long.valueOf(13L));
        builtinNumFmts.put("mm-dd-yy", Long.valueOf(14L));
        builtinNumFmts.put("d-mmm-yy", Long.valueOf(15L));
        builtinNumFmts.put("d-mmm", Long.valueOf(16L));
        builtinNumFmts.put("mmm-yy", Long.valueOf(17L));
        builtinNumFmts.put("h:mm AM/PM", Long.valueOf(18L));
        builtinNumFmts.put("h:mm:ss AM/PM", Long.valueOf(19L));
        builtinNumFmts.put("h:mm", Long.valueOf(20L));
        builtinNumFmts.put("h:mm:ss", Long.valueOf(21L));
        builtinNumFmts.put("m/d/yy h:mm", Long.valueOf(22L));
        builtinNumFmts.put("[$-404]e/m/d", Long.valueOf(27L));
        builtinNumFmts.put("m/d/yy", Long.valueOf(30L));
        builtinNumFmts.put("[$-404]e/m/d", Long.valueOf(36L));
        builtinNumFmts.put("#,##0 ;(#,##0)", Long.valueOf(37L));
        builtinNumFmts.put("#,##0 ;[Red](#,##0)", Long.valueOf(38L));
        builtinNumFmts.put("#,##0.00;(#,##0.00)", Long.valueOf(39L));
        builtinNumFmts.put("#,##0.00;[Red](#,##0.00)", Long.valueOf(40L));
        builtinNumFmts.put("mm:ss", Long.valueOf(45L));
        builtinNumFmts.put("[h]:mm:ss", Long.valueOf(46L));
        builtinNumFmts.put("mmss.0", Long.valueOf(47L));
        builtinNumFmts.put("##0.0E+0", Long.valueOf(48L));
        builtinNumFmts.put("@", Long.valueOf(49L));
        builtinNumFmts.put("[$-404]e/m/d", Long.valueOf(50L));
        builtinNumFmts.put("[$-404]e/m/d", Long.valueOf(57L));
    }

    protected SpreadsheetMLPackage pkg;
    private File file;
    private OutputStream outputStream;
    private WorkbookPart wbp;
    private List sheets;
    private RelationshipsPart prp, wbrp;
    private SharedStrings sharedStrings;
    private CTSst strings;
    private Map<String,XlsxWorksheet> worksheets = new HashMap<String,XlsxWorksheet>();
    private Styles styles;
    private Map numFmts = null;
    private CTStylesheet stylesheet;
    private Map stringMap = null;
    private CTFonts fonts;
    private CTCellXfs cellXfs;
    private CTCellStyles cellStyles;
    private CTCellStyleXfs cellStyleXfs;
    private CTBorders borders;
    private CTFills fills;
    private Map<Long,SharedFormula> sharedFormulas;

    private static boolean jaxbInitialised = false;
}
