/*
 * Copyright 2022 Gerald Curley
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
package com.opsmatters.media.model.content.util;

import java.time.Instant;
import com.opsmatters.media.model.BaseItem;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content image.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentImage extends BaseItem
{
    private String code = "";
    private String filename = "";
    private String text = "";
    private ImageType type;
    private ImageStatus status = ImageStatus.DISABLED;

    /**
     * Default constructor.
     */
    public ContentImage()
    {
    }

    /**
     * Constructor that takes an organisation code and type.
     */
    public ContentImage(String code, ImageType type)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(code);
        setType(type);
        setStatus(ImageStatus.ACTIVE);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentImage obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setType(obj.getType());
            setFilename(obj.getFilename());
            setText(obj.getText());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the filename.
     */
    public String toString()
    {
        return getFilename();
    }

    /**
     * Returns the organisation code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the organisation code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the type.
     */
    public ImageType getType()
    {
        return type;
    }

    /**
     * Sets the type.
     */
    public void setType(ImageType type)
    {
        this.type = type;
    }

    /**
     * Returns <CODE>true</CODE> if the type has been set.
     */
    public boolean hasType()
    {
        return type != null;
    }

    /**
     * Returns the filename.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Returns the file URI.
     */
    public String getFileUri()
    {
        return String.format("%s/%s", type.path(), filename);
    }

    /**
     * Sets the filename.
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * Returns <CODE>true</CODE> if the filename has been set.
     */
    public boolean hasFilename()
    {
        return filename != null && filename.length() > 0;
    }

    /**
     * Returns the image text.
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text.
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Returns <CODE>true</CODE> if the text has been set.
     */
    public boolean hasText()
    {
        return text != null && text.length() > 0;
    }

    /**
     * Returns the image's status.
     */
    public ImageStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the image is active.
     */
    public boolean isActive()
    {
        return status == ImageStatus.ACTIVE;
    }

    /**
     * Sets the image's status.
     */
    public void setStatus(ImageStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the image's status.
     */
    public void setStatus(String status)
    {
        setStatus(ImageStatus.valueOf(status));
    }
}