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

package com.opsmatters.media.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;

/**
 * Utility to create and execute local commands.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CommandExecutor
{
    private static final Logger logger = Logger.getLogger(CommandExecutor.class.getName());

    private List<String> arguments = new ArrayList<String>();
    private File directory;
    private File input;

    private ProcessBuilder builder;
    private Process process;
    private int exitCode = -1;

    /**
     * Default constructor.
     */
    private CommandExecutor()
    {
    }

    /**
     * Returns the working directory for the command.
     */
    public File getDirectory()
    {
        return directory;
    }

    /**
     * Sets the working directory for the command.
     */
    public void setDirectory(File directory)
    {
        this.directory = directory;
    }

    /**
     * Sets the working directory for the command.
     */
    public void setDirectory(String directory)
    {
        setDirectory(new File(directory));
    }

    /**
     * Returns the input file for the command.
     */
    public File getInput()
    {
        return input;
    }

    /**
     * Sets the input file for the command.
     */
    public void setInput(File input)
    {
        this.input = input;
    }

    /**
     * Returns the arguments for the command.
     */
    private List<String> getArguments()
    {
        return arguments;
    }

    /**
     * Returns the process used to execute the command.
     */
    public Process getProcess()
    {
        return process;
    }

    /**
     * Returns the exit code from the execution of the command.
     */
    public int getExitCode()
    {
        return exitCode;
    }

    /**
     * Creates the process builder using the list of arguments.
     */
    private CommandExecutor create()
    {
        builder = new ProcessBuilder(arguments);
        builder.redirectErrorStream(true);
        if(directory != null)
            builder.directory(directory);
        if(input != null)
            builder.redirectInput(ProcessBuilder.Redirect.PIPE.from(input));
        return this;
    }

    /**
     * Execute the process, returning the exit code.
     */
    public int execute() throws IOException, InterruptedException
    {
        if(builder == null)
            throw new IllegalArgumentException("builder null");

        try
        {
            process = builder.start();
            logger.info(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            exitCode = process.waitFor();
        }
        finally
        {
            if(process != null)
            {
                process.getOutputStream().close();
                process.getInputStream().close();
                process.getErrorStream().close();
                process.destroy();
            }
        }

        return exitCode;
    }

    /**
     * Returns a builder for the command executor.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make command executor construction easier.
     */
    public static class Builder
    {
        private CommandExecutor executor = new CommandExecutor();

        /**
         * Sets the working directory for the command.
         * @param directory The directory to set
         * @return This object
         */
        public Builder directory(String directory)
        {
            executor.setDirectory(directory);
            return this;
        }

        /**
         * Adds the given argument to the list of arguments.
         * @param argument The argument to add
         * @return This object
         */
        public Builder argument(String argument)
        {
            executor.getArguments().add(argument);
            return this;
        }

        /**
         * Adds the given arguments to the list of arguments.
         * @param arguments The arguments to add
         * @return This object
         */
        public Builder arguments(String... arguments)
        {
            Collections.addAll(executor.getArguments(), arguments);
            return this;
        }

        /**
         * Splits the given command into its constituent arguments.
         * @param command The command to split
         * @return The array of arguments
         */
        private String[] split(String command)
        {
            return command.split(" ");
        }

        /**
         * Adds the given command to the list of arguments.
         * @param command The command to add
         * @return This object
         */
        public Builder command(String command)
        {
            arguments(split(command));
            return this;
        }

        /**
         * Adds cmd.exe to the start of the command.
         * @return This object
         */
        public Builder windows()
        {
            executor.getArguments().addAll(0,
                Arrays.asList(split("cmd.exe /c")));
            return this;
        }

        /**
         * Sets the input file for the command.
         * @param file The input file
         * @return This object
         */
        public Builder withInput(File file)
        {
            executor.setInput(file);
            return this;
        }

        /**
         * Returns the configured command executor instance
         * @return The command executor instance
         */
        public CommandExecutor build()
        {
            return executor.create();
        }
    }
}