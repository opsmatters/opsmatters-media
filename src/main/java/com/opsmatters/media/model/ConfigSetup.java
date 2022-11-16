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
package com.opsmatters.media.model;

import java.io.File;
import java.io.IOException;
import com.opsmatters.media.file.YamlFileReader;

/**
 * Base class for a configuration setup file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ConfigSetup implements ConfigElement
{
    /**
     * Returns the name of the setup file.
     */
    public abstract String getFilename();

    protected abstract static class Builder<T extends ConfigSetup, B extends Builder<T,B>>
    {
        private String directory = "";
        private String filename = "";

        /**
         * Sets the configuration file directory.
         * @param key The configuration file directory
         * @return This object
         */
        public B directory(String directory)
        {
            this.directory = directory;
            return self();
        }

        /**
         * Sets the configuration filename.
         * @param key The configuration filename
         * @return This object
         */
        public B filename(String filename)
        {
            this.filename = filename;
            return self();
        }

        /**
         * Read the configuration setup file
         * @return The social setup instance
         */
        protected void read(ConfigParser parser) throws IOException
        {
            YamlFileReader reader = new YamlFileReader(new File(directory, filename));
            parser.parse(reader.read());
            reader.close();
        }

        /**
         * Returns this object.
         * @return This object
         */
        protected abstract B self();

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public abstract T build() throws IOException;
    }
}