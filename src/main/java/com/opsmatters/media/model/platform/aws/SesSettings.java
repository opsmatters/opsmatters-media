/*
 * Copyright 2021 Gerald Curley
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

package com.opsmatters.media.model.platform.aws;

import java.util.Map;

/**
 * Represents the SES settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SesSettings extends AwsSettings
{
    public static final String FROM = "from";

    private String id = "";
    private String from = "";

    /**
     * Copy constructor.
     */
    public SesSettings(SesSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SesSettings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setFrom(obj.getFrom());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public SesSettings(String id, Map<String, Object> map)
    {
        super(id, map);

        if(map.containsKey(FROM))
            setFrom((String)map.get(FROM));
    }

    /**
     * Returns the from address for the SES settings.
     */
    public String getFrom()
    {
        return from;
    }

    /**
     * Sets the from address for the SES settings.
     */
    public void setFrom(String from)
    {
        this.from = from;
    }
}