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

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.logging.Logger;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import com.opsmatters.media.file.CommonFiles;

/**
 * A set of utility methods to perform miscellaneous tasks related to images.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImageUtils
{
    private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());

    static
    {
        // Prevents a ClassCastException from using the wrong XML parser with SVG images
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }

    /**
     * The user agent to use with URLConnections to avoid 403 rejection errors
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36";

    /**
     * The timeout for a HTTP connection
     */
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 5000;

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private ImageUtils()
    {
    }    

    /**
     * Returns the image from the given URL.
     */
    static public BufferedImage getImage(URL url) throws IOException, FileNotFoundException
    {
        HttpURLConnection conn = null;
        BufferedImage ret = null;
        InputStream stream = null;

        try
        {
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            TrustAnyTrustManager.setTrustManager(conn);

            stream = conn.getInputStream();
            ret = ImageIO.read(stream);
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
        finally
        {
            if(stream != null)           
                stream.close();
        }

        return ret;
    }

    /**
     * Returns the image from the given file.
     */
    static public BufferedImage getImage(File file) throws IOException, FileNotFoundException
    {
        BufferedImage ret = null;
        ImageInputStream stream = ImageIO.createImageInputStream(file);
        if(stream == null)
            throw new IllegalArgumentException("stream null!");
        Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
        if(!readers.hasNext())
            throw new IllegalArgumentException("image reader null!");

        // Try all the readers for the format
        IOException exception = null;
        while(readers.hasNext())
        {
            ImageReader reader = readers.next();
            try
            {
                reader.setInput(stream, true, true);
                ImageReadParam param = reader.getDefaultReadParam();
                if(reader.getFormatName().equals(CommonFiles.SVG_EXT))
                    param.setSourceRegion(getSvgBounds(file));
                ret = reader.read(0, param);
                exception = null;
                break;
            }
            catch(IOException e)
            {
                exception = e;
            }
            finally
            {
                reader.dispose();
            }
        }

        stream.close();
        if(exception != null)
            throw exception;
        return ret;
    }

    /**
     * Returns the size of the SVG image from the given file.
     */
    static private Rectangle getSvgBounds(File file) throws IOException
    {
        Rectangle ret = null;
        FileInputStream stream = null;
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());

        try
        {
            stream = new FileInputStream(file);
            SVGDocument doc = factory.createSVGDocument(file.toURI().toString(), stream);
            SVGSVGElement root = doc.getRootElement();
            if(root != null)
            {
                if(root.hasAttribute("width") && root.hasAttribute("height"))
                {
                    ret = new Rectangle(Integer.parseInt(getNumber(root.getAttribute("width"))), 
                        Integer.parseInt(getNumber(root.getAttribute("height"))));
                }
                else // Otherwise use the viewbox
                {
                    String viewBoxStr = root.getAttributeNS(null, SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);
                    if(viewBoxStr.length() != 0)
                    {
                        float[] rect = ViewBox.parseViewBoxAttribute(root, viewBoxStr, null);
                        ret = new Rectangle((int)rect[0], (int)rect[1], (int)rect[2], (int)rect[3]);
                    }
                }
            }
        }
        finally
        {
            if(stream != null)           
                stream.close();
        }

        return ret;
    }

    /**
     * Returns the pixels number from the given attribute value.
     */
    static private String getNumber(String str)
    {
        String ret = str;
        if(str != null && str.endsWith("px"))
            ret = str.substring(0, str.length()-2);
        return ret;
    }

    /**
     * Returns the dimensions of the image at the given URL.
     */
    static public Dimension getImageDimension(URL url) throws IOException
    {
        Dimension ret = null;
        ImageInputStream in = null;

        try
        {
            in = ImageIO.createImageInputStream(url.openStream());
            Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if(readers.hasNext())
            {
                ImageReader reader = readers.next();
                try
                {
                    reader.setInput(in);
                    ret = new Dimension(reader.getWidth(0), reader.getHeight(0));
                }
                finally
                {
                    reader.dispose();
                }
            }
        }
        finally
        {
            try
            {
                if(in != null)
                    in.close();
            }
            catch(IOException e)
            {
            }
        }

        return ret;
    }

    /**
     * Returns the image type for the given image, correcting for image type = 0.
     * @param image The image bytes
     * @throws IOException
     */
    private static int getImageType(BufferedImage image)
    {
        return image.getType() == 0 ? BufferedImage.TYPE_3BYTE_BGR : image.getType();
    }

    /**
     * Returns an image resized to a absolute width and height.
     * @param file The input image file
     * @param scaledWidth absolute width in pixels
     * @param scaledHeight absolute height in pixels
     * @return the scaled image
     * @throws IOException
     */
    static public BufferedImage getResizedImage(File file, int scaledWidth, int scaledHeight)
        throws IOException
    {
        return getResizedImage(ImageIO.read(file), scaledWidth, scaledHeight);
    }

    /**
     * Returns an image resized by a percentage of the original size (proportional).
     * @param file The input image file
     * @param percent the percentage of the output image over the input image.
     * @return the scaled image
     * @throws IOException
     */
    public static BufferedImage getResizedImage(File file, float percent)
        throws IOException
    {
        BufferedImage inputImage = ImageIO.read(file);
        return getResizedImage(inputImage,
            (int)(inputImage.getWidth()*percent)/100,
            (int)(inputImage.getHeight()*percent)/100);
    }

    /**
     * Returns an image resized to a absolute width and height.
     * @param inputImage The input image
     * @param scaledWidth absolute width in pixels
     * @param scaledHeight absolute height in pixels
     * @return the scaled image
     * @throws IOException
     */
    static public BufferedImage getResizedImage(BufferedImage inputImage, int scaledWidth, int scaledHeight)
        throws IOException
    {
        // Create the output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
            scaledHeight, getImageType(inputImage));
 
        // Scale the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        inputImage.flush();
 
        return outputImage;
    }

    /**
     * Returns <CODE>true</CODE> if the given suffix is JPEG format.
     * @param suffix The suffix of the image file (JPG, PNG, GIF)
     * @return <CODE>true</CODE> if the given suffix is JPEG format
     */
    public static boolean isJPEG(String suffix)
    {
        return suffix != null 
            && (suffix.equalsIgnoreCase(CommonFiles.JPG_EXT)
                || suffix.equalsIgnoreCase(CommonFiles.JPEG_EXT));
    }

    /**
     * Write the given image file to a file in the current suffix.
     * @param image The image bytes
     * @param file The output image file
     * @throws IOException
     */
    public static void writeImage(BufferedImage image, File file)
        throws IOException
    {
        writeImage(image, file, FileUtils.getExtension(file.getName()));
    }

    /**
     * Write the given image file to a file in the specified suffix.
     * @param image The image bytes
     * @param file The output image file
     * @param suffix The suffix of the image file (JPG, PNG, GIF)
     * @throws IOException
     */
    public static void writeImage(BufferedImage image, File file, String suffix)
        throws IOException
    {
        // Remove the alpha channel when converting JPEGs otherwise
        //   this screws up the colours because JPEG doesn't have transparency
        if(isJPEG(suffix) && image != null && image.getColorModel().hasAlpha())
        {
            logger.info(String.format("Removing alpha from image of type %d: file=%s", 
                image.getType(), file.getName()));
            image = removeImageAlpha(image);
        }
        else if(image.getType() == 0)
        {
            logger.info(String.format("Fixing image with zero type: file=%s", 
                file.getName()));
            image = fixImageType(image);
        }

        ImageIO.write(image, suffix, file);
        image.flush();
    }

    /**
     * Convert the given image file removing the alpha (transparency) channel.
     * @param image The image bytes
     * @throws IOException
     */
    private static BufferedImage removeImageAlpha(BufferedImage image)
    {
        BufferedImage ret = image;
        if(image != null && image.getColorModel().hasAlpha())
        {
            BufferedImage convertedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

            // Rewritee the input image to the output image, changing the type
            Graphics2D g2d = convertedImage.createGraphics();
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            g2d.dispose();
            image.flush();
            ret = convertedImage;
        }

        return ret;
    }

    /**
     * Fix the given PNG image file with image type zero by setting the correct type.
     * @param image The image bytes
     * @throws IOException
     */
    private static BufferedImage fixImageType(BufferedImage image)
    {
        BufferedImage ret = image;
        if(image != null)
        {
            ret = new BufferedImage(image.getWidth(),
                image.getHeight(), getImageType(image));
            image.flush();
        }

        return ret;
    }
}