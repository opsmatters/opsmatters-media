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
package com.opsmatters.media.client.system;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import com.opsmatters.media.model.system.Environment;
import com.opsmatters.media.model.system.DatabaseConfig;
import com.opsmatters.media.model.system.SessionId;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.util.TimeUtils;

/**
 * Client to backup and restore from a mysql database.
 */
public class MySqlClient extends Client
{
    private static final Logger logger = Logger.getLogger(MySqlClient.class.getName());

    public static final String SUFFIX = ".db";

    private String key;
    private DatabaseConfig config;
    private String username = "";
    private String password = "";

    private MySqlClient(String key, DatabaseConfig config)
    {
        this.key = key;
        this.config = config;
    }

    /**
     * Returns a new mysql client.
     */
    static public MySqlClient newClient(Environment environment) throws IOException
    {
        MySqlClient ret = new MySqlClient(environment.getKey(), environment.getDatabaseConfig());

        // Configure and create the mysql client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create mysql client: "+environment.getName());

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring mysql client: "+config.getName());

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, key+SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setUsername(obj.optString("username"));
            setPassword(obj.optString("password"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read mysql auth file: "
                +e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured mysql client successfully: "+config.getName());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws RuntimeException
    {
        if(debug())
            logger.info("Creating mysql client: "+config.getName());

        if(config == null)
            throw new IllegalArgumentException("missing database config");
        if(!config.hasHostname())
            throw new IllegalArgumentException("missing hostname");
        if(!config.hasPort())
            throw new IllegalArgumentException("missing port");
        if(!hasUsername())
            throw new IllegalArgumentException("missing username");
        if(!hasPassword())
            throw new IllegalArgumentException("missing password");

        if(debug())
            logger.info("Created mysql client successfully: "+config.getName());

        return true;
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
    }

    /**
     * Returns the username for the client.
     */
    public String getUsername() 
    {
        return username;
    }

    /**
     * Sets the username for the client.
     */
    public void setUsername(String username) 
    {
        this.username = username;
    }

    /**
     * Returns <CODE>true</CODE> if the username has been set.
     */
    public boolean hasUsername()
    {
        return getUsername() != null && getUsername().length() > 0;
    }

    /**
     * Returns the password for the client.
     */
    public String getPassword() 
    {
        return password;
    }

    /**
     * Sets the password for the client.
     */
    public void setPassword(String password) 
    {
        this.password = password;
    }

    /**
     * Returns <CODE>true</CODE> if the password has been set.
     */
    public boolean hasPassword()
    {
        return getPassword() != null && getPassword().length() > 0;
    }

    /**
     * Returns the backup filename with the given name and id.
     */
    private String getBackupName(String name, int id)
    {
        return String.format("%s-%d.sql", name, id);
    }

    /**
     * Returns the backup filename with the given config for the current session.
     */
    public String getBackupName(DatabaseConfig config)
    {
        return getBackupName(config.getName(), SessionId.get());
    }

    /**
     * Returns the backup filename with the current config for the current session.
     */
    public String getBackupName()
    {
        return getBackupName(config);
    }

    /**
     * Backup the current database to the given filename.
     */
    public boolean backup(String filename) throws IOException, InterruptedException
    {
        boolean ret = false;
        Process process = null;

        try
        {
            String name = config.getName();
            File file = new File(filename);

            logger.info(String.format("Starting database backup: %s", name));

            ProcessBuilder pb = new ProcessBuilder("mysqldump",
                "--column-statistics=0",
                "--host", config.getHostname(),
                "--port", Integer.toString(config.getPort()),
                "--user", getUsername(),
                "--password="+getPassword(),
                name,
                "--result-file", file.getPath());
            pb.redirectErrorStream(true);
            process = pb.start();
            logger.info(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            int exitCode = process.waitFor();
            if(exitCode == 0)
            {
                ret = true;
                logger.info("Backup completed successfully: "+filename);
            }
            else
            {
                logger.severe("Backup failed: "+exitCode);
            }
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
     * Backup the current database to the default filename.
     */
    public boolean backup() throws IOException, InterruptedException
    {
        return backup(getBackupName());
    }

    /**
     * Restore the given database from a file.
     */
    public boolean restore(File file) throws IOException, InterruptedException
    {
        if(file == null)
            throw new IllegalArgumentException("missing backup file");

        boolean ret = false;
        Process process = null;

        try
        {
            String name = config.getName();

            logger.info(String.format("Starting database restore: %s", name));

            ProcessBuilder pb = new ProcessBuilder("mysql",
                "--host", config.getHostname(),
                "--port", Integer.toString(config.getPort()),
                "--user", getUsername(),
                "--password="+getPassword(),
                name);
            pb.redirectErrorStream(true);
            pb.redirectInput(ProcessBuilder.Redirect.PIPE.from(file));
            process = pb.start();

            logger.info(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            int exitCode = process.waitFor();
            if(exitCode == 0)
            {
                logger.info("Restore completed successfully");
                ret = true;
            }
            else
            {
                logger.severe("Restore failed: "+exitCode);
            }
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
}