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

package com.opsmatters.media.model.content.project;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentConfig;

/**
 * Class that represents the configuration for project content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProjectConfig extends ContentConfig<Project>
{
    private static final Logger logger = Logger.getLogger(ProjectConfig.class.getName());

    private String branch = "";

    /**
     * Constructor that takes a code.
     */
    public ProjectConfig(String code)
    {
        super(code);
    }

    /**
     * Copy constructor.
     */
    public ProjectConfig(ProjectConfig obj)
    {
        super(obj);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ProjectConfig obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setBranch(obj.getBranch());
        }
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.PROJECT;
    }

    /**
     * Returns the configuration default branch.
     */
    public String getBranch()
    {
        return branch;
    }

    /**
     * Sets the configuration default branch.
     */
    public void setBranch(String branch)
    {
        this.branch = branch;
    }

    /**
     * Returns a builder for the configuration.
     * @param code The code for the configuration
     * @return The builder instance.
     */
    public static Builder builder(String code)
    {
        return new Builder(code);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder extends ContentConfig.Builder<ProjectConfig, Builder>
    {
        private ProjectConfig ret = null;

        // The config attribute names
        private static final String BRANCH = "branch";

        /**
         * Constructor that takes a code.
         * @param code The code for the configuration
         */
        public Builder(String code)
        {
            ret = new ProjectConfig(code);
            super.set(ret);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            super.parse(map);

            if(map.containsKey(BRANCH))
            {
                ret.setBranch((String)map.get(BRANCH));
            }

            return this;
        }

        /**
         * Returns this object.
         * @return This object
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        @Override
        public ProjectConfig build()
        {
            return ret;
        }
    }
}