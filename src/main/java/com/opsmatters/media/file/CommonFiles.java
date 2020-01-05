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
 * Common file extensions and descriptions.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CommonFiles
{
    /**
     * The extension used for XML files.
     */
    public final static String XML_EXT               = "xml";

    /**
     * The description of the extension used for XML files.
     */
    public final static String XML_DESCRIPTION       = "XML Documents";

    /**
     * The extension used for jar files.
     */
    public final static String JAR_EXT               = "jar";

    /**
     * The description of the extension used for jar files.
     */
    public final static String JAR_DESCRIPTION       = "Java JAR Files";

    /**
     * The extension used for zip files.
     */
    public final static String ZIP_EXT               = "zip";

    /**
     * The description of the extension used for zip files.
     */
    public final static String ZIP_DESCRIPTION       = "Compressed Files";

    /**
     * The extension used for class files.
     */
    public final static String CLASS_EXT             = "class";

    /**
     * The description of the extension used for class files.
     */
    public final static String CLASS_DESCRIPTION     = "Java Class Files";

    /**
     * The extension used for HTML files.
     */
    public final static String HTML_EXT              = "html";

    /**
     * The description of the extension used for HTML files.
     */
    public final static String HTML_DESCRIPTION      = "Web Pages";

    /**
     * The extension used for text files.
     */
    public final static String TXT_EXT               = "txt";

    /**
     * The description of the extension used for text files.
     */
    public final static String TXT_DESCRIPTION       = "Text Files";

    /**
     * The extension used for properties files.
     */
    public final static String PROPERTIES_EXT        = "properties";

    /**
     * The description of the extension used for properties files.
     */
    public final static String PROPERTIES_DESCRIPTION= "Properties Files";

    /**
     * The 2nd extension used for properties files.
     */
    public final static String PROPS_EXT        = "props";

    /**
     * The extension used for CSV files.
     */
    public final static String CSV_EXT               = "csv";

    /**
     * The description of the extension used for CSV files.
     */
    public final static String CSV_DESCRIPTION       = "Comma Separated Files";

    /**
     * The extension used for Excel files.
     */
    public final static String XLS_EXT               = "xls";

    /**
     * The description of the extension used for XLS files.
     */
    public final static String XLS_DESCRIPTION       = "Excel Files";

    /**
     * The extension used for Excel files.
     */
    public final static String XLSX_EXT               = "xlsx";

    /**
     * The description of the extension used for XLSX files.
     */
    public final static String XLSX_DESCRIPTION       = "Excel Files";

    /**
     * The extension used for temporary files.
     */
    public final static String TMP_EXT               = "tmp";

    /**
     * The description of the extension used for temporary files.
     */
    public final static String TMP_DESCRIPTION       = "Temporary Files";

    /**
     * The extension used for log files.
     */
    public final static String LOG_EXT               = "log";

    /**
     * The description of the extension used for temporary files.
     */
    public final static String LOG_DESCRIPTION       = "Log Files";

    /**
     * The extension used for java keystore files.
     */
    public final static String JKS_EXT               = "jks";

    /**
     * The description of the extension used for java keystore files.
     */
    public final static String JKS_DESCRIPTION       = "Keystore Files";

    /**
     * The extension used for windows executable files.
     */
    public final static String EXE_EXT               = "exe";

    /**
     * The description of the extension used for windows executable files.
     */
    public final static String EXE_DESCRIPTION       = "Executable Files";

    /**
     * The extension used for windows batch files.
     */
    public final static String BAT_EXT               = "bat";

    /**
     * The description of the extension used for windows batch files.
     */
    public final static String BAT_DESCRIPTION       = "Batch Files";

    /**
     * The extension used for shell script files.
     */
    public final static String SH_EXT               = "sh";

    /**
     * The description of the extension used for shell script files.
     */
    public final static String SH_DESCRIPTION       = "Shell Files";

    /**
     * The extension used for pgp files.
     */
    public final static String PGP_EXT               = "pgp";

    /**
     * The description of the extension used for pgp files.
     */
    public final static String PGP_DESCRIPTION       = "Encrypted Files";

    /**
     * The extension used for gzip files.
     */
    public final static String GZIP_EXT               = "gz";

    /**
     * The extension used for tar files.
     */
    public final static String TAR_EXT               = "tar";

    /**
     * The extension used for bzip2 files.
     */
    public final static String BZIP2_EXT               = "bz2";

    /**
     * The extension used for cpio files.
     */
    public final static String CPIO_EXT               = "cpio";

    /**
     * The extension used for ar files.
     */
    public final static String AR_EXT               = "ar";

    /**
     * The extension used for xz files.
     */
    public final static String XZ_EXT               = "xz";

    /**
     * The extension used for jpg files.
     */
    public final static String JPG_EXT               = "jpg";

    /**
     * The extension used for jpeg files.
     */
    public final static String JPEG_EXT               = "jpeg";

    /**
     * The extension used for png files.
     */
    public final static String PNG_EXT               = "png";

    /**
     * The extension used for gif files.
     */
    public final static String GIF_EXT               = "gif";

    /**
     * The extension used for webp files.
     */
    public final static String WEBP_EXT               = "webp";

    /**
     * The extension used for svg files.
     */
    public final static String SVG_EXT               = "svg";

    /**
     * Returns <CODE>true</CODE> if the given filename is an XLS or XLSX file.
     * @param filename The filename to be checked
     * @return <CODE>true</CODE> if the given filename is an XLS or XLSX file
     */
    public static boolean isExcelFile(String filename)
    {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith("."+CommonFiles.XLS_EXT)
           || lowerFilename.endsWith("."+CommonFiles.XLSX_EXT);
    }

    /**
     * Returns <CODE>true</CODE> if the given filename is a CSV file.
     * @param filename The filename to be checked
     * @return <CODE>true</CODE> if the given filename is a CSV file
     */
    public static boolean isCsvFile(String filename)
    {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith("."+CommonFiles.CSV_EXT);
    }

    /**
     * Returns <CODE>true</CODE> if the given filename is a supported image file.
     * @param filename The filename to be checked
     * @return <CODE>true</CODE> if the given filename is a PNG, JPG, JPEG or GIF file
     */
    public static boolean isSupportedImageFile(String filename)
    {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith("."+CommonFiles.PNG_EXT)
           || lowerFilename.endsWith("."+CommonFiles.JPG_EXT)
           || lowerFilename.endsWith("."+CommonFiles.JPEG_EXT)
           || lowerFilename.endsWith("."+CommonFiles.GIF_EXT)
           || lowerFilename.endsWith("."+CommonFiles.WEBP_EXT)
           || lowerFilename.endsWith("."+CommonFiles.SVG_EXT);
    }

    /**
     * Returns <CODE>true</CODE> if the given filename is a supported web image file.
     * @param filename The filename to be checked
     * @return <CODE>true</CODE> if the given filename is a PNG, JPG, JPEG or GIF file
     */
    public static boolean isSupportedWebImageFile(String filename)
    {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith("."+CommonFiles.PNG_EXT)
           || lowerFilename.endsWith("."+CommonFiles.JPG_EXT)
           || lowerFilename.endsWith("."+CommonFiles.JPEG_EXT);
    }
}
