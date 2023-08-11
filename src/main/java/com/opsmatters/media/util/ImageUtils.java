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
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import com.opsmatters.media.file.FileFormat;

import static com.opsmatters.media.util.ImageAlignment.*;

/**
 * A set of utility methods to perform miscellaneous tasks related to images.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImageUtils
{
    private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());

    /**
     * Number of mm in a pixel.
     */
    private static final double MM_TO_PX = 3.7795275591d;

    /**
     * The user agent to use with URLConnections to avoid 403 rejection errors
     */
    private static final String USER_AGENT = "Mozilla/5.0";

    /**
     * The timeout for a HTTP connection
     */
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 5000;

    static
    {
        // Prevents a ClassCastException from using the wrong XML parser with SVG images
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }

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
        catch(ArrayIndexOutOfBoundsException e)
        {
            // Deal with JDK bug that causes some GIF files to throw ArrayIndexOutOfBoundsException
            ImageIcon icon = new ImageIcon(url);
            Image image = icon.getImage();

            // Create empty BufferedImage, sized to Image
            BufferedImage buffImage = new BufferedImage(
              image.getWidth(null), 
              image.getHeight(null), 
              BufferedImage.TYPE_INT_ARGB);

            // Draw Image into BufferedImage
            Graphics g = buffImage.getGraphics();
            g.drawImage(image, 0, 0, null);
            ret = buffImage;
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
            throw new IllegalArgumentException("Image stream null");
        Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
        if(!readers.hasNext())
            throw new IllegalArgumentException("No readers found for image format");

        // Try all the readers for the format
        IOException exception = null;
        while(readers.hasNext())
        {
            ImageReader reader = readers.next();
            try
            {
                reader.setInput(stream, true, true);
                ImageReadParam param = reader.getDefaultReadParam();
                if(reader.getFormatName().equals(FileFormat.SVG.value()))
                    param.setSourceRegion(getSvgBounds(file));
                ret = reader.read(0, param);
                exception = null;
                break;
            }
            catch(IOException e)
            {
                exception = e;
            }
            catch(NullPointerException e)
            {
                exception = new IOException(e);
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                // Deal with JDK bug that causes some GIF files to throw ArrayIndexOutOfBoundsException
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image image = icon.getImage();

                // Create empty BufferedImage, sized to Image
                BufferedImage buffImage = new BufferedImage(
                  image.getWidth(null), 
                  image.getHeight(null), 
                  BufferedImage.TYPE_INT_ARGB);

                // Draw Image into BufferedImage
                Graphics g = buffImage.getGraphics();
                g.drawImage(image, 0, 0, null);
                ret = buffImage;
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
                try
                {
                    if(root.hasAttribute("width") && root.hasAttribute("height"))
                    {
                        ret = new Rectangle(Integer.parseInt(getNumber(root.getAttribute("width"))), 
                            Integer.parseInt(getNumber(root.getAttribute("height"))));
                    }
                }
                catch(NumberFormatException e)
                {
                    // How to handle width/height with units too? eg. "5.33in"
                }

                if(ret == null) // Otherwise try to use the viewbox
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
        if(str != null)
        {
            if(str.endsWith("px"))
            {
                ret = str.substring(0, str.length()-2);
            }
            else if(str.endsWith("mm")) // Convert to px
            {
                ret = str.substring(0, str.length()-2);
                double mm = Double.parseDouble(ret);
                ret = Integer.toString((int)(mm * MM_TO_PX));
            }
        }

        return ret;
    }

    /**
     * Returns the dimensions of the image at the given URL.
     */
    static public Dimension getImageDimension(URL url) throws IOException
    {
        Dimension ret = null;
        BufferedImage image = getImage(url);
        if(image != null)
            ret = new Dimension(image.getWidth(), image.getHeight());
        return ret;
    }

    /**
     * Returns the image type for the given image, correcting for image type = 0.
     * @param image The image bytes
     * @throws IOException
     */
    private static int getImageType(BufferedImage image)
    {
        int ret = image.getType();
        if(ret == 0 || ret == BufferedImage.TYPE_BYTE_INDEXED)
        {
            if(image.getColorModel().hasAlpha())
                ret = BufferedImage.TYPE_4BYTE_ABGR;
            else
                ret = BufferedImage.TYPE_3BYTE_BGR;
        }

        return ret;
    }

    /**
     * Returns an image scaled by a factor of the original size (proportional).
     * @param file The input image file
     * @param factor the proportion of the output image to the input image [0..1].
     * @return the scaled image
     * @throws IOException
     */
    public static BufferedImage getScaledFactorImage(File file, double factor)
        throws IOException
    {
        BufferedImage inputImage = ImageIO.read(file);

        if(inputImage == null)
            return inputImage;

        return Thumbnails.of(inputImage)
            .scale(factor)
            .imageType(getImageType(inputImage))
            .asBufferedImage();
    }

    /**
     * Returns an image scaled to the given width.
     * @param file The input image file
     * @param width the width of the output image
     * @return the scaled image
     * @throws IOException
     */
    public static BufferedImage getScaledWidthImage(File file, int width)
        throws IOException
    {
        BufferedImage inputImage = ImageIO.read(file);

        if(inputImage == null)
            return inputImage;

        return Thumbnails.of(inputImage)
            .width(width)
            .imageType(getImageType(inputImage))
            .asBufferedImage();
    }

    /**
     * Returns an image cropped using a ratio.
     * @param file The input image file
     * @param ratio the ratio of the width to the height
     * @return the cropped image
     * @throws IOException
     */
    public static BufferedImage getCroppedImage(File file, float ratio, ImageAlignment alignment)
        throws IOException
    {
        BufferedImage inputImage = ImageIO.read(file);

        if(inputImage == null)
            return inputImage;

        int inputWidth = inputImage.getWidth();
        int inputHeight = inputImage.getHeight();
        int max = Math.max(inputWidth, inputHeight);

        int width = inputWidth;
        int height = inputHeight;

        // Portrait image
        if(inputHeight == max)
        {
            height = (int)(inputWidth/ratio);
        }
        else // Landscape image
        {
            width = (int)(inputHeight*ratio);
        }

        // Image dimensions unchanged
        if(width == inputWidth && height == inputHeight)
            return inputImage;

        // Crop from the selected position
        Positions position = Positions.CENTER;
        if(alignment == LEFT)
            position = Positions.CENTER_LEFT;
        else if(alignment == RIGHT)
            position = Positions.CENTER_RIGHT;
        else if(alignment == TOP)
            position = Positions.TOP_CENTER;
        else if(alignment == BOTTOM)
            position = Positions.BOTTOM_CENTER;

        return Thumbnails.of(inputImage)
            .size(width, height)
            .crop(position)
            .imageType(getImageType(inputImage))
            .asBufferedImage();
    }

    /**
     * Write the given image file to a file in the specified suffix.
     * @param image The image bytes
     * @param file The output image file
     * @throws IOException
     */
    public static void writeImage(BufferedImage image, File file)
        throws IOException
    {
        FileFormat format = FileFormat.fromFilename(file.getName());

        // Remove the alpha channel when converting JPEGs otherwise
        //   this screws up the colours because JPEG doesn't have transparency
        if(format != null && format.isJPEG()
            && image != null && image.getColorModel().hasAlpha())
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

        ImageIO.write(image, format.value(), file);
        image.flush();
    }

    /**
     * Convert the given image file removing the alpha (transparency) channel.
     * @param image The image bytes
     * @throws IOException
     */
    private static BufferedImage removeImageAlpha(BufferedImage image) throws IOException
    {
        BufferedImage ret = image;
        if(image != null && image.getColorModel().hasAlpha())
        {
            BufferedImage convertedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

            // Rewritee the input image to the output image, changing the type
            Graphics2D g2d = convertedImage.createGraphics();
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), Color.WHITE, null); // Convert transparent to white
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