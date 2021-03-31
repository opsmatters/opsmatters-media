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
 * Represents the EC2 settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EC2Settings extends AwsSettings
{
    public static final String INSTANCE_ID = "instance-id";

    private String id = "";
    private String instanceId = "";

    /**
     * Copy constructor.
     */
    public EC2Settings(EC2Settings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(EC2Settings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setInstanceId(obj.getInstanceId());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public EC2Settings(String id, Map<String, Object> map)
    {
        super(id, map);

        if(map.containsKey(INSTANCE_ID))
            setInstanceId((String)map.get(INSTANCE_ID));
    }

    /**
     * Returns the instance id for the EC2 settings.
     */
    public String getInstanceId()
    {
        return instanceId;
    }

    /**
     * Sets the instance id for the EC2 settings.
     */
    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }
}