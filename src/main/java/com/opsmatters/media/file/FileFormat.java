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
    CSV("csv"),
    XLS("xls"),
    XLSX("xlsx"),
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif"),
    WEBP("webp"),
    SVG("svg");

    private String value;
    private String ext;

    /**
     * Constructor that takes the format value.
     * @param value The value for the format
     */
    FileFormat(String value)
    {
        this.value = value;
        this.ext = "."+value;
    }

    /**
     * Returns the value of the format.
     * @return The value of the format.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the format value.
     * @return The format value.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the format extension.
     * @return The format extension.
     */
    public String ext()
    {
        return ext;
    }

    /**
     * Returns <CODE>true</CODE> if the file format is an Excel spreadsheet.
     * @return <CODE>true</CODE> if the file format is an Excel spreadsheet
     */
    public boolean isExcel()
    {
        return this == XLS || this == XLSX;
    }

    /**
     * Returns <CODE>true</CODE> if the file format is a JPEG image.
     * @return <CODE>true</CODE> if the file format is a JPEG image
     */
    public boolean isJPEG()
    {
        return this == JPG || this == JPEG;
    }

    /**
     * Returns <CODE>true</CODE> if the file format is a supported image format.
     * @return <CODE>true</CODE> if the file format is a supported image format
     */
    public boolean isSupportedImage()
    {
        return this == PNG || this.isJPEG() || this == GIF || this == WEBP || this == SVG;
    }

    /**
     * Returns <CODE>true</CODE> if the file format is a supported web image format.
     * @return <CODE>true</CODE> if the file format is a supported web image format
     */
    public boolean isSupportedWebImage()
    {
        return this == PNG || this.isJPEG();
    }

    /**
     * Returns the file format for the given filename.
     * @param filename The filename to check
     * @param dflt The default format to return if no match found
     * @return The file format from the filename
     */
    public static FileFormat fromFilename(String filename, FileFormat dflt)
    {
        FileFormat ret = dflt;
        if(filename != null)
        {
            // Remove any query string from the given filename
            int pos = filename.indexOf("?");
            if(pos != -1)
                filename = filename.substring(0,pos);

            filename = filename.toLowerCase();
            FileFormat[] formats = values();
            for(FileFormat format : formats)
            {
                if(filename.endsWith(format.ext()))
                {
                    ret = format;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Returns the file format for the given filename.
     * @param filename The filename to check
     * @return The file format from the filename
     */
    public static FileFormat fromFilename(String filename)
    {
        return fromFilename(filename, null);
    }

    /**
     * Returns <CODE>true</CODE> if the given filename is a supported image file.
     * @param filename The filename to be checked
     * @return <CODE>true</CODE> if the given filename is a PNG, JPG, JPEG, GIF, WEBP or SVG file
     */
    public static boolean isSupportedImage(String filename)
    {
        FileFormat format = fromFilename(filename);
        return format != null && format.isSupportedImage();
    }

    /**
     * Returns <CODE>true</CODE> if the given filename is a supported web image file.
     * @param filename The filename to be checked
     * @return <CODE>true</CODE> if the given filename is a PNG, JPG or JPEG file
     */
    public static boolean isSupportedWebImage(String filename)
    {
        FileFormat format = fromFilename(filename);
        return format != null && format.isSupportedWebImage();
    }
}