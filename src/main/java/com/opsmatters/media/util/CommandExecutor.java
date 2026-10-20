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

    private Process process;
    private File directory;
    private boolean windows = false;

    /**
     * Default constructor.
     */
    public CommandExecutor()
    {
    }

    /**
     * Constructor that takes the working directory.
     */
    public CommandExecutor(String directory)
    {
        setDirectory(directory);
    }

    /**
     * Returns the last process used to execute a command.
     */
    public Process getProcess()
    {
        return process;
    }

    /**
     * Returns the working directory for the commands.
     */
    public File getDirectory()
    {
        return directory;
    }

    /**
     * Sets the working directory for the commands.
     */
    public void setDirectory(File directory)
    {
        this.directory = directory;
    }

    /**
     * Sets the working directory for the commands.
     */
    public void setDirectory(String directory)
    {
        setDirectory(new File(directory));
    }

    /**
     * Set to <CODE>true</CODE> if the command needs to be executed using cmd.exe.
     */
    public void useWindowsShell(boolean windows)
    {
        this.windows = windows;
    }

    /**
     * Execute the command using the list of arguments.
     * @param file The input file to the command
     * @param arguments The list of arguments that make up the command
     * @return the exit code
     */
    private int execute(File input, List<String> arguments)
        throws IOException, InterruptedException
    {
        int ret = -1;

        List<String> argumentList = new ArrayList<String>();
        if(windows)
        {
            argumentList.add("cmd.exe");
            argumentList.add("/c");
        }

        argumentList.addAll(arguments);

        ProcessBuilder builder = new ProcessBuilder(argumentList);
        builder.redirectErrorStream(true);
        if(directory != null)
            builder.directory(directory);
        if(input != null)
            builder.redirectInput(ProcessBuilder.Redirect.PIPE.from(input));

        try
        {
            process = builder.start();
            logger.info(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            ret = process.waitFor();
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

        return ret;
    }

    /**
     * Execute the command.
     * @param command The command to execute
     * @return the exit code
     */
    public int execute(String command)
        throws IOException, InterruptedException
    {
        return execute(null, Arrays.asList(command.split(" ")));
    }

    /**
     * Execute the command using the list of arguments.
     * @param file The input file to the command
     * @param arguments The list of arguments that make up the command
     * @return the exit code
     */
    public static int execute(File input, String... arguments)
        throws IOException, InterruptedException
    {
        return new CommandExecutor().execute(input, Arrays.asList(arguments));
    }

    /**
     * Execute the command using the list of arguments.
     * @param arguments The list of arguments that make up the command
     * @return the exit code
     */
    public static int execute(String... arguments)
        throws IOException, InterruptedException
    {
        return new CommandExecutor().execute(null, Arrays.asList(arguments));
    }
}