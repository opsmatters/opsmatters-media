/*
 * Copyright 2026 Gerald Curley
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
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing an image file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImageFile extends BaseEntity
{
    private String code = "";
    private ImageType type;
    private String filename = "";
    private String oldFilename = "";
    private FileStatus status = FileStatus.NEW;
    private int sessionId = 0;

    /**
     * Default constructor.
     */
    public ImageFile()
    {
    }

    /**
     * Constructor that takes a content item.
     */
    public ImageFile(Content content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(content.getCode());
        setType(ImageType.POST);
        setFilename(content.getImage());
    }

    /**
     * Constructor that takes a code and filename.
     */
    public ImageFile(String code, String filename)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(code);
        setType(ImageType.POST);
        setFilename(filename);
    }

    /**
     * Constructor that takes a content image.
     */
    public ImageFile(ContentImage image)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(image.getCode());
        setType(image.getType());
        setFilename(image.getFilename());
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ImageFile obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setType(obj.getType());
            setFilename(obj.getFilename());
            setOldFilename(obj.getOldFilename());
            setStatus(obj.getStatus());
            setSessionId(obj.getSessionId());
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
     * Returns <CODE>true</CODE> if the code has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
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
     * Returns the original filename.
     */
    public String getOldFilename()
    {
        return oldFilename;
    }

    /**
     * Sets the original filename.
     */
    public void setOldFilename(String oldFilename)
    {
        this.oldFilename = oldFilename;
    }

    /**
     * Returns <CODE>true</CODE> if the original filename has been set.
     */
    public boolean hasOldFilename()
    {
        return oldFilename != null && oldFilename.length() > 0;
    }

    /**
     * Returns the file's status.
     */
    public FileStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the file's status.
     */
    public void setStatus(FileStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the file's status.
     */
    public void setStatus(String status)
    {
        setStatus(FileStatus.valueOf(status));
    }

    /**
     * Returns the file's session id.
     */
    public int getSessionId()
    {
        return sessionId;
    }

    /**
     * Sets the file's session id.
     */
    public void setSessionId(int sessionId)
    {
        this.sessionId = sessionId;
    }
}