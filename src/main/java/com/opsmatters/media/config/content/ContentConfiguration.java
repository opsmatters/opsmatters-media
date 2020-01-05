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

package com.opsmatters.media.config.content;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import com.opsmatters.media.config.YamlConfiguration;
import com.opsmatters.media.model.content.Fields;
import com.opsmatters.media.model.content.FieldSource;
import com.opsmatters.media.util.FileUtils;

/**
 * Class that represents a YAML configuration for content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentConfiguration extends YamlConfiguration implements FieldSource
{
    private static final Logger logger = Logger.getLogger(ContentConfiguration.class.getName());

    public static final String FILENAME = "filename";
    public static final String SHEET = "sheet";
    public static final String SOURCE = "source";
    public static final String DEFAULT_DATE_PATTERN = "default-date-pattern";
    public static final String FIELDS = "fields";
    public static final String OUTPUT = "output";

    private String filename = "";
    private String sheet = "";
    private ContentSource source = ContentSource.STORE;
    private String defaultDatePattern = "";
    private Fields fields;
    private Map<String,String> output;

    /**
     * Default constructor.
     */
    public ContentConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setFilename(obj.getFilename());
            setSheet(obj.getSheet());
            setSource(obj.getSource());
            setDefaultDatePattern(obj.getDefaultDatePattern());
            setFields(new Fields(obj.getFields()));
            setOutput(new LinkedHashMap<String,String>(obj.getOutput()));
        }
    }

    /**
     * Returns the type for this configuration.
     */
    public abstract String getType();

    /**
     * Returns the title for this configuration.
     */
    public abstract String getTitle();

    /**
     * Returns the organisation code for this configuration.
     */
    public String getCode()
    {
        return fields.get(Fields.CODE);
    }

    /**
     * Returns the organisation for this configuration.
     */
    public String getOrganisation()
    {
        return fields.get(Fields.ORGANISATION);
    }

    /**
     * Returns the import filename for this configuration.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the import filename for this configuration.
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
        setName(FileUtils.getName(filename));
    }

    /**
     * Returns the worksheet for this configuration.
     */
    public String getSheet()
    {
        return sheet;
    }

    /**
     * Sets the worksheet for this configuration.
     */
    public void setSheet(String sheet)
    {
        this.sheet = sheet;
    }

    /**
     * Returns the default date pattern for this configuration.
     */
    public String getDefaultDatePattern()
    {
        return defaultDatePattern;
    }

    /**
     * Sets the default date pattern for this configuration.
     */
    public void setDefaultDatePattern(String defaultDatePattern)
    {
        this.defaultDatePattern = defaultDatePattern;
    }

    /**
     * Returns the content source for this configuration.
     */
    public ContentSource getSource()
    {
        return source;
    }

    /**
     * Sets the content source for this configuration.
     */
    public void setSource(ContentSource source)
    {
        this.source = source;
    }

    /**
     * Returns the fields for this configuration.
     */
    @Override
    public Fields getFields()
    {
        return fields;
    }

    /**
     * Sets the fields for this configuration.
     */
    public void setFields(Fields fields)
    {
        this.fields = fields;
    }

    /**
     * Adds the fields for this configuration.
     */
    public void addFields(Map<String,String> fields)
    {
        if(this.fields == null)
            this.fields = new Fields();
        this.fields.putAll(fields);
    }

    /**
     * Returns <CODE>true</CODE> if given field has been set.
     */
    public boolean hasField(String name)
    {
        return fields.containsKey(name);
    }

    /**
     * Returns the value of the given field.
     */
    public String getField(String name)
    {
        return fields.get(name);
    }

    /**
     * Returns the value of the given field.
     * <p>
     * Returns the fallback if the field is not found.
     */
    public String getField(String name, String fallback)
    {
        return fields.get(name, fallback);
    }

    /**
     * Returns the output fields for this configuration.
     */
    public Map<String,String> getOutput()
    {
        return output;
    }

    /**
     * Sets the output fields for this configuration.
     */
    public void setOutput(Map<String,String> output)
    {
        this.output = output;
    }

    /**
     * Adds the output fields for this configuration.
     */
    public void addOutput(Map<String,String> output)
    {
        if(this.output == null)
            this.output = new LinkedHashMap<String,String>();
        this.output.putAll(output);
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Object doc)
    {
        if(doc instanceof Map)
        {
            Map map = (Map)doc;

            if(map.containsKey(FILENAME))
                setFilename((String)map.get(FILENAME));
            if(map.containsKey(SHEET))
                setSheet((String)map.get(SHEET));
            if(map.containsKey(DEFAULT_DATE_PATTERN))
                setDefaultDatePattern((String)map.get(DEFAULT_DATE_PATTERN));
            if(map.containsKey(SOURCE))
                setSource(ContentSource.fromCode((String)map.get(SOURCE)));
            if(map.containsKey(FIELDS))
                addFields((Map<String,String>)map.get(FIELDS));
            if(map.containsKey(OUTPUT))
                addOutput((Map<String,String>)map.get(OUTPUT));
        }
    }
}