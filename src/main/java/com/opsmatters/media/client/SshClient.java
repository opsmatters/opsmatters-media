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
package com.opsmatters.media.client;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.opsmatters.media.model.platform.SshConfig;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents a connection to a host over SSH.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SshClient extends Client
{
    private static final Logger logger = Logger.getLogger(SshClient.class.getName());

    static
    {
        JSch.setConfig("StrictHostKeyChecking", "no");
    }

    public static final String SUFFIX = ".ssh";
    public static final String KEY = ".pk";
    public static final int DEFAULT_SSH_PORT = 22;

    private static JSch jsch = new JSch();
    private String env = "";
    private String hostname = "";
    private int port = 0;
    private String username = "";
    private String password = "";
    private String keyfile = "";
    private Session session;
    private ChannelSftp channel;

    /**
     * Returns a new SSH connection using the given configuration.
     */
    static public SshClient newClient(String key, SshConfig config) 
        throws JSchException, SftpException
    {
        SshClient ret = SshClient.builder()
            .env(key)
            .hostname(config.getHostname())
            .port(config.getPort())
            .keyfile(key+KEY)
            .build();

        // Configure and create the SSH client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create SSH client: "+ret.getHostname());

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws JSchException, SftpException
    {
        if(debug())
            logger.info("Configuring SSH client: "+getHostname());

        String directory = System.getProperty("app.auth", ".");

        if(port == 0)
            port = DEFAULT_SSH_PORT;

        File file = new File(directory, env+SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setUsername(obj.optString("username"));
            setPassword(obj.optString("password"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read SSH auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        // Use the auth directory for the keyfile if no path was given
        if(keyfile != null && keyfile.indexOf("/") == -1)
            keyfile = new File(directory, keyfile).getAbsolutePath();

        // Authenticate the session using password or key
        Session session = jsch.getSession(username, hostname, port);
        if(keyfile != null && keyfile.length() > 0)
        {
            if(password != null && password.length() > 0)
                jsch.addIdentity(keyfile, password);
            else
                jsch.addIdentity(keyfile);
        }
        else if(password != null)
        {
            session.setPassword(password);
        }

        this.session = session;

        if(debug())
            logger.info("Configured SSH client successfully: "+getHostname());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() 
        throws JSchException, SftpException
    {
        if(debug())
            logger.info("Creating SSH channel: "+getHostname());

        session.connect();
        channel = (ChannelSftp)session.openChannel("sftp");
        channel.connect();

        if(debug())
            logger.info("Created SSH channel successfully: "+getHostname());

        return channel.isConnected();
    }

    /**
     * Returns the environment for the client.
     */
    public String getEnv() 
    {
        return env;
    }

    /**
     * Sets the environment for the client.
     */
    public void setEnv(String env) 
    {
        this.env = env;
    }

    /**
     * Returns the hostname for the client.
     */
    public String getHostname() 
    {
        return hostname;
    }

    /**
     * Sets the hostname for the client.
     */
    public void setHostname(String hostname) 
    {
        this.hostname = hostname;
    }

    /**
     * Returns the port for the client.
     */
    public int getPort() 
    {
        return port;
    }

    /**
     * Sets the port for the client.
     */
    public void setPort(int port) 
    {
        this.port = port;
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
     * Returns the keyfile for the client.
     */
    public String getKeyfile() 
    {
        return keyfile;
    }

    /**
     * Sets the keyfile for the client.
     */
    public void setKeyfile(String keyfile) 
    {
        this.keyfile = keyfile;
    }

    /**
     * Returns <CODE>true</CODE> if the channel is connected.
     */
    public boolean isConnected() 
    {
        return channel != null && channel.isConnected();
    }

    /**
     * Change to the given working directory.
     */
    public boolean cd(String workingDir) 
        throws JSchException, SftpException
    {
        boolean ret = false;
        if(channel != null && channel.isConnected() && workingDir.length() > 0)
        {
            channel.cd(workingDir);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the current working directory.
     */
    public String pwd() throws SftpException
    {
        return channel != null ? channel.pwd() : null;
    }

    /**
     * Returns <CODE>true</CODE> if the given file exists in the current directory.
     */
    public boolean exists(String filename)
    {
        boolean ret = false;

        try
        {
            if(channel != null && channel.isConnected())
                ret = channel.lstat(filename) != null;
        }
        catch(SftpException e)
        {
        }

        return ret;
    }

    /**
     * Write the given file to the server.
     */
    public boolean put(InputStream stream, String filename) throws SftpException
    {
        boolean ret = false;

        if(channel != null && channel.isConnected())
        {
            channel.put(stream, filename);
            ret = true;
        }

        return ret;
    }

    /**
     * Delete the given file from the server.
     */
    public void rm(String filename) throws SftpException
    {
        if(channel != null && channel.isConnected())
            channel.rm(filename);
    }

    /**
     * Returns the size (in bytes) of the given file.
     */
    public long getSize(String filename)
    {
        long ret = -1L;

        try
        {
            if(channel != null && channel.isConnected())
            {
                SftpATTRS attrs = channel.lstat(filename);
                if(attrs != null && !attrs.isDir())
                    ret = attrs.getSize();
            }
        }
        catch(SftpException e)
        {
        }

        return ret;
    }

    /**
     * Close the session.
     */
    @Override
    public void close() 
    {
        if(channel != null && channel.isConnected())
            channel.disconnect();

        if(session != null && session.isConnected())
            session.disconnect();
    }

    /**
     * Returns a builder for the client.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make client construction easier.
     */
    public static class Builder
    {
        private SshClient client = new SshClient();

        /**
         * Sets the env for the client.
         * @param env The env for the client
         * @return This object
         */
        public Builder env(String env)
        {
            client.setEnv(env);
            return this;
        }

        /**
         * Sets the hostname for the client.
         * @param hostname The hostname for the client
         * @return This object
         */
        public Builder hostname(String hostname)
        {
            client.setHostname(hostname);
            return this;
        }

        /**
         * Sets the port for the client.
         * @param port The port for the client
         * @return This object
         */
        public Builder port(int port)
        {
            client.setPort(port);
            return this;
        }

        /**
         * Sets the keyfile for the client.
         * @param keyfile The keyfile for the client
         * @return This object
         */
        public Builder keyfile(String keyfile)
        {
            client.setKeyfile(keyfile);
            return this;
        }

        /**
         * Returns the configured client instance
         * @return The client instance
         */
        public SshClient build()
        {
            return client;
        }
    }
}