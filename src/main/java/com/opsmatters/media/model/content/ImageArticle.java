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
package com.opsmatters.media.model.content;

import com.opsmatters.media.file.CommonFiles;

/**
 * Class representing an article with an image.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ImageArticle extends Article
{
    /**
     * Default constructor.
     */
    public ImageArticle()
    {
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ImageArticle obj)
    {
        super.copyAttributes(obj);
    }

    /**
     * Returns the article image name.
     */
    public abstract String getImage();

    /**
     * Sets the article image name.
     */
    public abstract void setImage(String image);

    /**
     * Returns <CODE>true</CODE> if the article image has been set.
     */
    public abstract boolean hasImage();

    /**
     * Returns <CODE>true</CODE> if the article image name has a JPG extension.
     */
    public boolean isJpgImage()
    {
        String image = getImage() != null ? getImage().toLowerCase() : "";
        return image.endsWith("."+CommonFiles.JPG_EXT) || image.endsWith("."+CommonFiles.JPEG_EXT);
    }

    /**
     * Returns <CODE>true</CODE> if the articleimage name has a PNG extension.
     */
    public boolean isPngImage()
    {
        String image = getImage() != null ? getImage().toLowerCase() : "";
        return image.endsWith("."+CommonFiles.PNG_EXT);
    }

    /**
     * Returns <CODE>true</CODE> if the article image name has a GIF extension.
     */
    public boolean isGifImage()
    {
        String image = getImage() != null ? getImage().toLowerCase() : "";
        return image.endsWith("."+CommonFiles.GIF_EXT);
    }

    /**
     * Returns the article image source.
     */
    public abstract String getImageSource();

    /**
     * Returns <CODE>true</CODE> if the article image source has been set.
     */
    public abstract boolean hasImageSource();
}