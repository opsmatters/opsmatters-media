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

/**
 * Represents the set of file formats.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum FileFormat
{
    CSV(".csv"),
    XLS(".xls"),
    XLSX(".xlsx");

    FileFormat(String extension)
    {
        this.extension = extension;
    }

    public String extension()
    {
        return extension;
    }

    /**
     * Returns the file format for the given filename.
     * @param filename The filename to check
     * @return The file format from the filename
     */
    public static FileFormat getFileFormat(String filename)
    {
        FileFormat ret = CSV;
        filename = filename.toLowerCase();
        FileFormat[] formats = values();
        for(FileFormat format : formats)
        {
            if(filename.endsWith(XLS.extension()))
                ret = XLS;
            else if(filename.endsWith(XLSX.extension()))
                ret = XLSX;
            else if(filename.endsWith(CSV.extension()))
                ret = CSV;
        }
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the file format is an Excel spreadsheet.
     * @return <CODE>true</CODE> if the file format is an Excel spreadsheet
     */
    public boolean isExcel()
    {
        return this == XLS || this == XLSX;
    }

    private String extension;
}