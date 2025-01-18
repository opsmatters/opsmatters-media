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
import com.opsmatters.media.model.ConfigParser;

import static com.opsmatters.media.model.platform.aws.InstanceStatus.*;

/**
 * Represents the EC2 configuration.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Ec2Config extends AwsConfig
{
    private String instanceId = "";
    private InstanceStatus status = UNKNOWN;

    /**
     * Constructor that takes an id.
     */
    protected Ec2Config(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public Ec2Config(Ec2Config obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Ec2Config obj)
    {
        if(obj != null)
        {
            setInstanceId(obj.getInstanceId());
        }
    }

    /**
     * Returns the instance id for the EC2 configuration.
     */
    public String getInstanceId()
    {
        return instanceId;
    }

    /**
     * Sets the instance id for the EC2 configuration.
     */
    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    /**
     * Returns the status of the instance.
     */
    public InstanceStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the status of the instance.
     */
    public void setStatus(InstanceStatus status)
    {
        this.status = status;
    }

    /**
     * Returns a builder for the configuration.
     * @param id The id of the configuration
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<Ec2Config>
    {
        // The config attribute names
        private static final String REGION = "region";
        private static final String INSTANCE_ID = "instance-id";

        private Ec2Config ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new Ec2Config(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(REGION))
                ret.setRegion((String)map.get(REGION));
            if(map.containsKey(INSTANCE_ID))
                ret.setInstanceId((String)map.get(INSTANCE_ID));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public Ec2Config build()
        {
            return ret;
        }
    }
}