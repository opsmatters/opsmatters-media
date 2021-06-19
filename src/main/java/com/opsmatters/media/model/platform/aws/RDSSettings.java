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
 * Represents the RDS settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RDSSettings extends AwsSettings
{
    public static final String CLUSTER_ID = "cluster-id";

    private String id = "";
    private String clusterId = "";

    /**
     * Copy constructor.
     */
    public RDSSettings(RDSSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(RDSSettings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setClusterId(obj.getClusterId());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public RDSSettings(String id, Map<String, Object> map)
    {
        super(id, map);

        if(map.containsKey(CLUSTER_ID))
            setClusterId((String)map.get(CLUSTER_ID));
    }

    /**
     * Returns the cluster id for the RDS settings.
     */
    public String getClusterId()
    {
        return clusterId;
    }

    /**
     * Sets the cluster id for the RDS settings.
     */
    public void setClusterId(String clusterId)
    {
        this.clusterId = clusterId;
    }
}