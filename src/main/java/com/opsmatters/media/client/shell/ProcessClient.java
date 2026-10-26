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

package com.opsmatters.media.client.shell;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;
import com.opsmatters.media.client.Client;

/**
 * Client to create and execute local commands.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProcessClient extends Client
{
    private static final Logger logger = Logger.getLogger(ProcessClient.class.getName());

    private Process process;
    private File directory;
    private boolean windows = false;
    private int exitCode = -1;
    private String output = null;

    /**
     * Constructor that takes the working directory.
     */
    public ProcessClient(String directory)
    {
        setDirectory(directory);
    }

    /**
     * Returns a new process client for the given working directory.
     */
    public static ProcessClient newClient(String directory)
    {
        ProcessClient ret = new ProcessClient(directory);

        // Configure and create the process client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create process client");

        return ret;
    }

    /**
     * Returns a new process client for the current directory.
     */
    public static ProcessClient newClient()
    {
        return newClient(null);
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure()
    {
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create()
    {
        return true;
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
        setDirectory(directory != null ? new File(directory) : null);
    }

    /**
     * Set to <CODE>true</CODE> if the command needs to be executed using cmd.exe.
     */
    public void useWindowsShell(boolean windows)
    {
        this.windows = windows;
    }

    /**
     * Returns the exit code of the last command execution.
     */
    public int getExitCode()
    {
        return exitCode;
    }

    /**
     * Returns the output of the last command execution.
     */
    public String getOutput()
    {
        return output;
    }

    /**
     * Returns <CODE>true</CODE> if the last command execution had output.
     */
    public boolean hasOutput()
    {
        return output != null && output.length() > 0;
    }

    /**
     * Execute the command using the list of arguments.
     * @param file The input file to the command
     * @param arguments The list of arguments that make up the command
     * @return <CODE>true</CODE> if the command completed
     */
    private boolean command(File input, List<String> arguments)
        throws IOException, InterruptedException
    {
        exitCode = -1;
        output = null;

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

        process = builder.start();
        output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        exitCode = process.waitFor();

        return true;
    }

    /**
     * Execute the command.
     * @param command The command to execute
     * @return <CODE>true</CODE> if the command completed
     */
    public boolean command(String command)
        throws IOException, InterruptedException
    {
        return command(null, Arrays.asList(command.split(" ")));
    }

    /**
     * Execute the command using the list of arguments.
     * @param file The input file to the command
     * @param arguments The list of arguments that make up the command
     * @return <CODE>true</CODE> if the command completed
     */
    public boolean command(File input, String... arguments)
        throws IOException, InterruptedException
    {
        return command(input, Arrays.asList(arguments));
    }

    /**
     * Execute the command using the list of arguments.
     * @param arguments The list of arguments that make up the command
     * @return <CODE>true</CODE> if the command completed
     */
    public boolean command(String... arguments)
        throws IOException, InterruptedException
    {
        return command(null, arguments);
    }

    /**
     * Close the process.
     */
    @Override
    public void close() 
    {
        if(process != null)
        {
            try
            {
                process.getOutputStream().close();
                process.getInputStream().close();
                process.getErrorStream().close();
            }
            catch(IOException e)
            {
            }
        }

        process.destroy();
    }
}