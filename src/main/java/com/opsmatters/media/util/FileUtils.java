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
package com.opsmatters.media.util;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.zip.Inflater;
import java.util.zip.Deflater;
import java.util.zip.DataFormatException;
import java.util.logging.Logger;
import com.google.common.io.Files;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;

import static com.google.common.net.HttpHeaders.*;

/**
 * A set of utility methods to perform miscellaneous tasks related to files.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FileUtils
{
    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    private static final String SUCCESS_RESPONSE = "HTTP\\/1\\.1 20\\d .*";

    /**
     * The default user agent to use with URLConnections to avoid 403 rejection errors
     */
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0";

    /**
     * The timeout for a HTTP connection
     */
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 5000;

    private static Map<URL,FileResponse> responses = new Hashtable<>();

    static
    {
        System.setProperty("http.maxConnections", "20");
        System.setProperty("sun.net.http.errorstream.enableBuffering", "true");
        System.setProperty("http.keepAlive","false");
    }

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private FileUtils()
    {
    }    

    /**
     * Returns the relative directory path between the two paths (using the default "/" separator).
     */
    public static String getRelativePath(String path1, String path2)
    {
        return getRelativePath(path1, path2, "/");
    }

    /**
     * Returns the relative directory path between the two paths (using the given separator).
     */
    public static String getRelativePath(String path1, String path2, String separator)
    {
        int pos = 0;
        boolean foundPrefix = true;
        String prefix = "";
        while((pos = path1.indexOf(separator, pos+1)) != -1 && foundPrefix)
        {
            String test = new String(path1.substring(0, pos));
            foundPrefix = path2.startsWith(test);
            if(foundPrefix)
                prefix = test;
        }

        String newPath1 = new String(path1.substring(prefix.length()+1));
        String newPath2 = new String(path2.substring(prefix.length()+1));

        StringTokenizer st = new StringTokenizer(newPath1, separator);
        int numSlashes = st.countTokens();

        String ret = newPath2;
        for(int i = 0; i < numSlashes; i++)
            ret = ".."+separator+ret;

        return ret;
    }

    /**
     * Returns the path of the 'jar' directory in the installation.
     */
    public static String getInstalledJarDir()
    {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        URL url = loader.getResource("publisher.properties");
        String path = url.getPath();
        if(path.indexOf("!") != -1)
            path = new String(path.substring(0, path.indexOf("!")));
        if(path.indexOf("/") != -1)
            path = new String(path.substring(0, path.lastIndexOf("/")));
        if(path.startsWith("file:"))
            path = new String(path.substring(5));
        return path.replace('/',File.separator.charAt(0));
    }

    /**
     * Returns the file URL of the 'jar' directory in the installation.
     */
    public static URL getInstalledJarURL(String jarName) 
        throws java.net.MalformedURLException
    {
        URL url = null;
        String jarDir = getInstalledJarDir();
        if(jarDir.startsWith("http"))
            url = new URL("jar:"+jarDir.replace('\\','/')+"/"+jarName+"!/");
        else
            url = new URL("jar:file:"+jarDir+File.separator+jarName+"!/");
        return url;
    }

    /**
     * Downloads the given file from the given URL and headers.
     */
    public static void downloadFile(URL url, File file, Map<String,String> headers) 
        throws IOException
    {
        downloadFile(url, file, headers, null, -1); 
    }

    /**
     * Downloads the given file from the given URL and headers.
     */
    public static void downloadFile(URL url, File file, Map<String,String> headers, String md5, int size) 
        throws IOException
    {
        // Create default headers if none provided
        if(headers == null)
            headers = new HashMap<String,String>();
        if(headers.get(USER_AGENT) == null)
            headers.put(USER_AGENT, DEFAULT_USER_AGENT);

        boolean addingFile = false;
        int fileSize = -1;
        String fileMd5 = null;
        if(file.exists())
        {
            fileSize = (int)file.length();
            fileMd5 = getMD5(file);
        }

        if(md5 != null)
        {
            if(md5.equals(fileMd5))
            {
                logger.info("Skipping "+file.getPath()+"...");
                return;
            }
            else if(fileSize == -1)
            {
                if(size == 0)
                {
                    logger.info("Adding directory "+file.getPath()+"...");
                    file.mkdirs();
                    return;
                }
                else 
                {
                    addingFile = true;
                    logger.info("Adding "+file.getPath()+"...");
                }
            }
            else
            {
                logger.info("Updating "+file.getPath()+"...");
            }
        }
        else 
        {
            addingFile = true;
            logger.info("Adding "+file.getPath()+"...");
        }

        if(file.isFile() || addingFile)
        {
            HttpURLConnection conn = null;

            try
            {
                conn = (HttpURLConnection) url.openConnection();
                for(Map.Entry<String,String> entry : headers.entrySet())
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                TrustAnyTrustManager.setTrustManager(conn);
                org.apache.commons.io.FileUtils.copyToFile(conn.getInputStream(), file);
            }
            catch(IOException e)
            {
                try
                {
                    if(conn != null && conn.getErrorStream() != null)
                        conn.getErrorStream().close();
                }
                catch(IOException ex)
                {
                }

                throw e;
            }
        }
    }

    /**
     * Uploads the given file to the given URL.
     */
    public static void uploadFile(URL url, File file) throws IOException
    {
        URLConnection conn = (URLConnection)url.openConnection(); 
        conn.setDoOutput(true);

        int size = 4096;
        Writer ow = new OutputStreamWriter(conn.getOutputStream());
        Reader r = new FileReader(file);
        int len = 0;
        while(len != -1)
        {
            char[] charray = new char[size];
            len = r.read(charray);
            if(len != -1)
                ow.write(charray, 0, len);
        }

        r.close();
        ow.flush();
        ow.close();
    }

    /**
     * Returns the directory containing the given file name below the given directory.
     */
    public static File findFile(String root, String name)
    {
        File ret = null;

        File dir = new File(root);
        File[] files = dir.listFiles();
        if(files != null && files.length > 0)
        {
            for(int i = 0; i < files.length && ret == null; i++)
            {
                File f = files[i];
                if(f.getName().equals(name))
                    ret = f.getParentFile();
                else if(f.isDirectory())
                    ret = findFile(f.getAbsolutePath(), name);
            }
        }

        return ret;
    }

    /**
     * Compresses the input data.
     * @return null if compression results in larger output.
     */
    public static byte[] compress(byte[] input, int compressionLevel)
    {
        Deflater deflater = new Deflater(compressionLevel);
        deflater.setInput(input, 0, input.length);
        deflater.finish();
        byte[] buff = new byte[input.length + 50];
        int wsize = deflater.deflate(buff);
        int compressedSize = deflater.getTotalOut();

        // Did this data compress well?
        if (deflater.getTotalIn() != input.length)
           return null;
        if (compressedSize >= input.length-4)
           return null;

        byte[] output = new byte[compressedSize + 4];
        System.arraycopy(buff, 0, output, 4, compressedSize);
        output[0] = (byte)(input.length >> 24);
        output[1] = (byte)(input.length >> 16);
        output[2] = (byte)(input.length >> 8);
        output[3] = (byte)(input.length);
        return output;
    }

    /**
     * Un-compresses the input data.
     * @throws IOException if the input is not valid.
     */
    public static byte[] uncompress(byte[] input) throws IOException
    {
        try 
        {
            int uncompressedSize = 
               (((input[0] & 0xff) << 24) +
               ((input[1] & 0xff) << 16) +
               ((input[2] & 0xff) << 8) +
               ((input[3] & 0xff)));         

            Inflater inflater = new Inflater();
            inflater.setInput(input, 4, input.length - 4);
            inflater.finished();

            byte[] out = new byte[uncompressedSize];
            inflater.inflate(out);
            inflater.reset();         
            return out;
        } 
        catch (DataFormatException e )
        {
            throw new IOException("Input Stream is corrupt: "+e);
        }
    }

    /**
     * Returns the checksum for the given filename.
     */
    public static String getChecksum(File file)
    {
        if(file == null)
            return null;
        String name = file.getAbsolutePath();
        String ret = (String)checksums.get(name);
        if(ret == null || file.getName().startsWith("opsmatters"))
        {
            ret = getMD5(file);
            if(ret != null)
                checksums.put(name, ret);
        }
        return ret;
    }

    /**
     * Returns the MD5 checksum for the given file.
     */
    public static String getMD5(File file)
    {
        String ret = null;

        try
        {
            if(file.isFile())
            {
                HashCode md5 = Files.hash(file, Hashing.md5());
                if(md5 != null)
                    ret = md5.toString();
            }
            else if(file.isDirectory())
            {
                ret = "";
            }
        }
        catch(IOException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given URL is a relative path.
     */
    public static boolean isRelativePath(String url)
    {
        return url != null && url.length() > 0
            && !url.startsWith("http") && !url.startsWith("//");
    }

    /**
     * Returns the URL with spaces replaced by %20.
     */
    private static String encodeUrl(String url)
    {
        if(url != null)
            url = url.replaceAll(" ", "%20");
        return url;
    }

    /**
     * Returns the size of the file using the given URL.
     */
    public static long getFileSize(URL url) throws IOException
    {
        return getFileSize(url, null);
    }

    /**
     * Returns the size of the file using the given URL and headers.
     */
    public static long getFileSize(URL url, Map<String,String> headers)
        throws IOException
    {
        HttpURLConnection httpConn = null;
        long ret = -1L;

        // Create default headers if none provided
        if(headers == null)
            headers = new HashMap<String,String>();
        if(headers.get(USER_AGENT) == null)
            headers.put(USER_AGENT, DEFAULT_USER_AGENT);

        try
        {
            URLConnection conn = (URLConnection)url.openConnection();

            if(conn instanceof HttpURLConnection)
            {
                httpConn = (HttpURLConnection)conn;
                for(Map.Entry<String,String> entry : headers.entrySet())
                    httpConn.setRequestProperty(entry.getKey(), entry.getValue());
                httpConn.setRequestMethod("HEAD");
                httpConn.setReadTimeout(READ_TIMEOUT);
                httpConn.setConnectTimeout(CONNECT_TIMEOUT);
                TrustAnyTrustManager.setTrustManager(httpConn);
            }

            String response = conn.getHeaderField(0);
            if(response != null && response.matches(SUCCESS_RESPONSE))
                ret = conn.getContentLengthLong();
        }
        catch(IOException e)
        {
            try
            {
                if(httpConn != null && httpConn.getErrorStream() != null)
                    httpConn.getErrorStream().close();
            }
            catch(IOException ex)
            {
            }

            throw e;
        }

        return ret;
    }

    /**
     * Returns the size of the file using the given URL.
     */
    public static long getFileSize(String url)
    {
        return getFileSize(url, null);
    }

    /**
     * Returns the size of the file using the given URL and headers.
     */
    public static long getFileSize(String url, Map<String,String> headers)
    {
        long ret = -1L;

        try
        {
            if(url != null)
                ret = getFileSize(new URL(encodeUrl(url)), headers);
        }
        catch(IOException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        return ret;
    }

    /**
     * Returns the response to a request to the given HTTP URL and headers.
     */
    public static String getResponse(URL url, String method, String message, Map<String,String> headers)
        throws IOException
    {
        HttpURLConnection conn = null;
        String ret = null;

        // Create default headers if none provided
        if(headers == null)
            headers = new HashMap<String,String>();
        if(headers.get(USER_AGENT) == null)
            headers.put(USER_AGENT, DEFAULT_USER_AGENT);

        try
        {
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod(method);
            for(Map.Entry<String,String> entry : headers.entrySet())
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);

            if(message != null && message.length() > 0) // POST
            {
                byte[] bytes = message.getBytes("UTF-8");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(bytes);
            }
            else
            {
                conn.getInputStream();
            }

            ret = conn.getHeaderField(0);
        }
        catch(IOException e)
        {
            try
            {
                if(conn != null && conn.getErrorStream() != null)
                    conn.getErrorStream().close();
            }
            catch(IOException ex)
            {
            }

            throw e;
        }

        return ret;
    }

    /**
     * Returns the response to a request to the given HTTP URL and headers.
     */
    public static String getResponse(URL url, String method, Map<String,String> headers)
        throws IOException
    {
        return getResponse(url, method, null, headers);
    }

    /**
     * Class to capture cached HTTP responses for files
     */
    static class FileResponse
    {
        FileResponse(URL url, String response)
        {
            this.url = url;
            this.response = response;
            tm = System.currentTimeMillis();
        }

        boolean hasExpired()
        {
            return (System.currentTimeMillis()-tm) > 500L;
        }

        URL url;
        String response;
        long tm = -1;
    }

    /**
     * Returns <CODE>true</CODE> if the file at the given HTTP URL exists.
     */
    public static boolean exists(URL url)
    {
        boolean ret = false;

        try
        {
            if(url != null)
            {
                String response = null;
                FileResponse cached = responses.get(url);
                if(cached != null && !cached.hasExpired())
                {
                    response = cached.response;
                }
                else
                {
                    response = getResponse(url, "HEAD", null);
                    responses.put(url, new FileResponse(url, response));
                }

                if(response != null)
                    ret = response.matches(SUCCESS_RESPONSE);
            }
        }
        catch(FileNotFoundException e)
        {
            responses.put(url, new FileResponse(url, e.getClass().getName()));
        }
        catch(IOException e)
        {
            responses.put(url, new FileResponse(url, e.getClass().getName()));
            logger.severe(StringUtils.serialize(e, 30));
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the file at the given HTTP URL exists.
     */
    public static boolean exists(String url)
    {
        boolean ret = false;

        try
        {
            if(url != null && url.length() > 0)
                ret = exists(new URL(encodeUrl(url)));
        }
        catch(MalformedURLException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        return ret;
    }

    /**
     * Remove the cached entry for given HTTP URL.
     */
    public static void remove(URL url)
    {
        responses.remove(url);
    }

    /**
     * Remove the cached entry for given HTTP URL.
     */
    public static void remove(String url)
    {
        try
        {
            if(url != null && url.length() > 0)
                remove(new URL(url));
        }
        catch(MalformedURLException e)
        {
            logger.severe(StringUtils.serialize(e));
        }
    }

    /**
     * Returns the name part of the given filename.
     */
    public static String getName(String filename)
    {
        String ret = filename;

        if(filename != null)
        {
            String str = filename;

            // Remove any path
            int pos = filename.lastIndexOf("/");
            if(pos != -1)
                str = str.substring(pos+1);

            // Extract the string before the last dot
            pos = str.lastIndexOf(".");
            if(pos != -1)
                ret = str.substring(0, pos);
        }

        return ret;
    }

    /**
     * Returns the extension part of the given filename.
     */
    public static String getExtension(String filename)
    {
        String ret = "";

        if(filename != null)
        {
            String str = filename;

            // Remove any path
            int pos = str.lastIndexOf("/");
            if(pos != -1)
                str = str.substring(pos+1);

            // Remove any query string
            pos = str.indexOf("?");
            if(pos != -1)
                str = str.substring(0,pos);

            // Extract the string after the last dot
            pos = str.lastIndexOf(".");
            if(pos != -1)
                ret = str.substring(pos+1);
        }

        return ret;
    }

    /**
     * Replaces the extension in the given filename with the given extension.
     */
    public static String replaceExtension(String filename, String ext)
    {
        String ret = filename;

        if(filename != null)
        {
            int pos = filename.lastIndexOf(".");
            if(pos != -1)
                ret = filename.substring(0, pos+1)+ext;
        }

        return ret;
    }

    /**
     * Lists the files under the given path.
     */
    public static void listFiles(String path)
    {
        File parent = new File(path);
        File[] files = parent.listFiles();
        System.out.println("parent: "+parent.getAbsolutePath());
        if(files != null)
        {
            for(File file : files)
            {
                String type = " ";
                long size = 0L;
                if(file.isDirectory())
                    type = "d";
                else
                    size = file.length();
                System.out.println(String.format("%s %s (%d)",type, file.getName(), size));
            }
        }
    }

    private static Map checksums = new Hashtable(10);
    private static Map manifests = new Hashtable(10);
}