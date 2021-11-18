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

package com.opsmatters.media.handler;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.text.StringSubstitutor;
import com.google.common.io.Files;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.opsmatters.media.config.content.ContentConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.config.content.FieldSource;
import com.opsmatters.media.config.content.OrganisationContentConfiguration;
import com.opsmatters.media.model.platform.Environment;
import com.opsmatters.media.model.platform.aws.S3Settings;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.Organisation;
import com.opsmatters.media.client.SshClient;
import com.opsmatters.media.client.aws.S3Client;
import com.opsmatters.media.file.InputFileReader;
import com.opsmatters.media.file.OutputFileWriter;
import com.opsmatters.media.file.FileFormat;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.FileUtils;

/**
 * Creates and writes a formatted set of fields representing content.
 *
 * @author Gerald Curley (opsmatters)
 */
public class ContentHandler implements FieldSource
{
    private static final Logger logger = Logger.getLogger(ContentHandler.class.getName());

    public static final String DEFAULT_SHEET = "Sheet1";

    private static Map<String,SshClient> sshClients = new HashMap<String,SshClient>();
    private static S3Client s3client;

    private String name = "";
    private String filename = "";
    private String sheet = "";
    private List<String[]> lines;
    private Map<String,String> output;
    private String workingDir = "";
    private String dateFormat = Formats.CONTENT_DATE_FORMAT;
    private File file;
    private Fields fields;
    private Map<String,OrganisationContentConfiguration> configurationMap;
    private Map<String,Organisation> organisationMap;
    private S3Settings s3Settings;

    /**
     * Default constructor.
     */
    public ContentHandler()
    {
    }

    /**
     * Returns the name of this content.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this content.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the content configuration for the handler.
     */
    public void setConfiguration(ContentConfiguration config)
    {
        setName(config.getName());
        setFilename(config.getFilename());
        setSheet(config.getSheet());
        setOutput(config.getOutput());

        // Use the default sheet name if none was given
        if(sheet == null || sheet.length() == 0)
            sheet = DEFAULT_SHEET;
    }

    /**
     * Returns the working directory for the handler.
     */
    public String getWorkingDirectory()
    {
        return workingDir;
    }

    /**
     * Sets the working directory for the handler.
     */
    public void setWorkingDirectory(String workingDir)
    {
        this.workingDir = workingDir;
        logger.info("Using working directory: "+workingDir);
    }

    /**
     * Returns the filename of this content.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the filename of this content.
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * Returns the fields for this handler.
     */
    @Override
    public Fields getFields()
    {
        return fields;
    }

    /**
     * Sets the fields for this handler.
     */
    public void setFields(Fields fields)
    {
        this.fields = fields;
    }

    /**
     * Adds the fields for this handler.
     */
    public void addFields(Map<String,String> fields)
    {
        if(this.fields == null)
            this.fields = new Fields();
        this.fields.putAll(fields);
    }

    /**
     * Returns the configuration map for the handler.
     */
    public Map<String,OrganisationContentConfiguration> getConfigurationMap()
    {
        return configurationMap;
    }

    /**
     * Sets the configuration map for the handler.
     */
    public void setConfigurationMap(Map<String,OrganisationContentConfiguration> configurationMap)
    {
        this.configurationMap = configurationMap;
    }

    /**
     * Returns the configuration for the given code.
     */
    public OrganisationContentConfiguration getConfiguration(String name)
    {
        return configurationMap.get(name);
    }

    /**
     * Returns the organisation map for the handler.
     */
    public Map<String,Organisation> getOrganisationMap()
    {
        return organisationMap;
    }

    /**
     * Sets the organisation map for the handler.
     */
    public void setOrganisationMap(Map<String,Organisation> organisationMap)
    {
        this.organisationMap = organisationMap;
    }

    /**
     * Returns the organisation for the given code.
     */
    public Organisation getOrganisation(String code)
    {
        return organisationMap.get(code);
    }

    /**
     * Returns the S3 settings for the handler.
     */
    public S3Settings getS3Settings()
    {
        return s3Settings;
    }

    /**
     * Sets the S3 settings for the handler.
     */
    public void setS3Settings(S3Settings s3Settings)
    {
        this.s3Settings = s3Settings;
    }

    /**
     * Returns the worksheet of this content.
     */
    public String getSheet()
    {
        return sheet;
    }

    /**
     * Sets the worksheet of this content.
     */
    public void setSheet(String sheet)
    {
        this.sheet = sheet;
    }

    /**
     * Returns the output fields.
     */
    public Map<String,String> getOutput()
    {
        return output;
    }

    /**
     * Sets the output fields.
     */
    public void setOutput(Map<String,String> output)
    {
        this.output = output;
    }

    /**
     * Returns a list of output headers.
     */
    public List<String> getHeaders()
    {
        List<String> headers = new ArrayList<String>();

        if(output != null)
        {
            for(Map.Entry<String, String> field : output.entrySet())
                headers.add(field.getKey());
        }
        else
        {
            logger.severe("No output fields found for: "+getName());
        }

        return headers;
    }

    /**
     * Returns the position of the given header.
     */
    public int indexOf(String header)
    {
        int ret = -1;

        if(output != null)
        {
            int i = 0;
            for(Map.Entry<String, String> field : output.entrySet())
            {
                if(field.getKey().equals(header))
                    ret = i;
                ++i;
            }
        }

        return ret;
    }

    /**
     * Returns the header at the given position.
     */
    public String headerAt(int pos)
    {
        String ret = null;

        if(output != null)
        {
            int i = 0;
            for(Map.Entry<String, String> field : output.entrySet())
            {
                if(i == pos)
                    ret = field.getKey();
                ++i;
            }
        }

        return ret;
    }

    /**
     * Returns the list of output lines.
     */
    public List<String[]> getLines()
    {
        return lines;
    }

    /**
     * Returns the number of output lines.
     */
    public int getLineCount()
    {
        return lines != null ? lines.size() : -1;
    }

    /**
     * Trims the output lines to the given size by removing the first lines.
     */
    public void trimLines(int firstRow)
    {
        if(firstRow > 2 && getLineCount() > firstRow)
            lines.subList(1, firstRow).clear();
    }

    /**
     * Process the input fields to add additional required fields.
     */
    private void processInputs(Map<String,String> input)
    {
        // Derive the formatted ID
        int id = Integer.parseInt(input.get(Fields.ID));
        input.put(Fields.ID, String.format("%05d", id));

        // Derive the dates
        String publishedDate = input.get(Fields.PUBLISHED_DATE);
        if(publishedDate != null)
            input.put(Fields.PUBDATE, publishedDate);
        String startDate = input.get(Fields.START_DATE);
        if(startDate != null)
            input.put(Fields.START_DATE, startDate);
        String endDate = input.get(Fields.END_DATE);
        if(endDate != null)
            input.put(Fields.END_DATE, endDate);
    }

    /**
     * Return the set of processed values for the given set of input fields.
     */
    public List<String> getValues(Map<String,String> input)
    {
        List<String> values = new ArrayList<String>();

        if(output != null)
        {
            processInputs(input);
            StringSubstitutor substitutor = new StringSubstitutor(input);
            for(Map.Entry<String, String> field : output.entrySet())
                values.add(substitutor.replace(field.getValue()));
        }
        else
        {
            logger.severe("No output fields found for: "+getName());
        }

        return values;
    }

    /**
     * Returns the last file used for reading or writing.
     */
    public File getLastFile()
    {
        return file;
    }

    /**
     * Returns the last filename used for reading or writing.
     */
    public String getLastFilename()
    {
        return file != null ? file.getName() : null;
    }

    /**
     * Create a new worksheet at the current filename.
     */
    public void initFile() throws IOException
    {
        initFile(filename);
    }

    /**
     * Initialise an empty file at the given filename.
     */
    public void initFile(String filename) throws IOException
    {
        file = new File(workingDir, filename);
        lines = new ArrayList<String[]>();
        lines.add(0, getHeaders().toArray(new String[0]));
    }

    /**
     * Read the worksheet at the current filename.
     */
    public void readFile() throws IOException
    {
        readFile(filename, sheet);
    }

    /**
     * Read the worksheet at the given filename.
     */
    public void readFile(String filename, String sheet) throws IOException
    {
        InputStream is = null;
        try
        {
            // Read the contents of the stream
            logger.info("Reading file: "+filename);
            this.file = new File(workingDir, filename);

            // Check if the file exists
            if(file.exists())
            {
                is = new FileInputStream(this.file);
                InputFileReader reader = InputFileReader.builder()
                    .name(filename)
                    .worksheet(sheet)
                    .withInputStream(is)
                    .dateFormat(dateFormat)
                    .build();
                lines = reader.parse();
                logger.info("Read "+(lines.size()+1)+" lines");
            }
            else // Create new file with headers
            {
                lines = new ArrayList<String[]>();
                logger.info("Created new file: "+filename);
            }
        }
        finally
        {
            try
            {
                // Close the input stream
                if(is != null)
                    is.close();
            }
            catch(IOException e)
            {
            }
        }

        if(lines != null)
        {
            // Add the headers
            lines.add(0, getHeaders().toArray(new String[0]));
        }
    }

    /**
     * Read the worksheet at the current filename from the given S3 bucket.
     */
    public boolean readFileFromBucket(String bucket) throws IOException
    {
        return readFileFromBucket(filename, sheet, bucket);
    }

    /**
     * Read the worksheet at the given filename from the given S3 bucket.
     */
    public boolean readFileFromBucket(String filename, String sheet, String bucket) throws IOException
    {
        S3Client client = null;
        InputStream is = null;
        boolean ret = false;

        if(bucket == null || bucket.length() == 0)
            throw new IllegalArgumentException("bucket empty");

        try
        {
            // Connect to the remote server using S3
            client = s3client;
            if(client == null)
            {
                client = S3Client.newClient(s3Settings);
                s3client = client;
            }

            client.changeBucket(bucket); 
        }
        catch(IOException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        try
        {
            if(client != null)
            {
                if(client.exists(filename))
                {
                    // Read the file from a stream using S3
                    logger.info("Reading file from bucket: "+filename);
                    is = client.get(filename);
                    InputFileReader reader = InputFileReader.builder()
                        .name(filename)
                        .worksheet(sheet)
                        .withInputStream(is)
                        .dateFormat(dateFormat)
                        .build();
                    lines = reader.parse();
                    logger.info("Read "+(lines.size()+1)+" lines");
                }
                else // Create new file with headers
                {
                    lines = new ArrayList<String[]>();
                    logger.info("Created new file: "+filename);
                }

                ret = true;
            }
        }
        catch(IOException e)
        {
            logger.severe(StringUtils.serialize(e));
        }
        finally
        {
            try
            {
                // Close the input stream
                if(is != null)
                    is.close();
            }
            catch(IOException e)
            {
            }
        }

        if(lines != null)
        {
            // Add the headers
            lines.add(0, getHeaders().toArray(new String[0]));
        }

        return ret;
    }

    /**
     * Append the given line to the existing lines.
     */
    public void appendLine(List<String> line)
    {
        lines.add(line.toArray(new String[0]));
    }

    /**
     * Returns the CSV filename for the current filename.
     */
    public String getCsvFilename()
    {
        return getCsvFilename(filename);
    }

    /**
     * Returns the CSV filename for the given filename.
     */
    public String getCsvFilename(String filename)
    {
        return FileUtils.getName(filename)+FileFormat.CSV.extension();
    }

    /**
     * Write the lines to the worksheet at the given filename in the working directory.
     */
    public void writeFile() throws IOException
    {
        writeFile(filename, sheet);
    }

    /**
     * Write the lines to the worksheet at the given filename in the working directory.
     */
    public void writeFile(String filename, String sheet) throws IOException
    {
        checkFileWritable(filename);

        OutputStream os = null;
        try
        {
            // Write the contents of the stream
            logger.info("Writing file to working directory: "+filename);
            this.file = new File(workingDir, filename);
            os = new FileOutputStream(this.file, false);
            OutputFileWriter writer = OutputFileWriter.builder()
                .name(filename)
                .worksheet(sheet)
                .withOutputStream(os)
                .build();
            writer.write(lines);
            writer.close();
            logger.info("Wrote "+lines.size()+" lines to file: "+filename);
        }
        finally
        {
            try
            {
                // Close the output stream
                if(os != null)
                {
                    os.flush();
                    os.close();
                }
            }
            catch(IOException e)
            {
            }
        }
    }

    /**
     * Check that the file at the given filename can be written to the working directory.
     */
    public void checkFileWritable(String filename) throws IOException
    {
        OutputStream os = null;
        try
        {
            this.file = new File(workingDir, filename);
            os = new FileOutputStream(this.file, false);
        }
        finally
        {
            try
            {
                // Close the output stream
                if(os != null)
                    os.close();
            }
            catch(IOException e)
            {
            }
        }
    }

    /**
     * Convert the lines to ASCII.
     */
    public void convertLinesToAscii(String... htmlFields)
    {
        if(lines != null)
        {
            // Create a map for the fields that need HTML escaping
            Map<String,Boolean> htmlMap = new HashMap<String,Boolean>();
            if(htmlFields != null)
            {
                for(String htmlField : htmlFields)
                    htmlMap.put(htmlField, true);
            }

            for(String[] line : lines)
            {
                for(int i = 0; i < line.length; i++)
                {
                    boolean isHtml = htmlMap.containsKey(headerAt(i));
                    line[i] = StringUtils.convertToAscii(line[i], isHtml);
                }
            }
        }
    }

    /**
     * Copy the current file in the working directory to the given local directory.
     */
    public void copyFile(String directory) throws IOException
    {
        copyFile(filename, directory);
    }

    /**
     * Copy the given file in the working directory to the given local directory.
     */
    public boolean copyFile(String filename, String directory) throws IOException
    {
        if(directory == null || directory.length() == 0)
            throw new IllegalArgumentException("target directory null");

        File sourceFile = new File(workingDir, filename);
        File targetFile = new File(directory, filename);
        if(targetFile.exists()) // Remove existing file
            targetFile.delete();
        Files.copy(sourceFile, targetFile);
        logger.info("Copied "+filename+" to local directory: "+directory);
        return true;
    }

    /**
     * Copy the current file in the working directory to the given remote directory using SSH.
     */
    public void copyFileToHost(String directory, Environment environment) throws IOException
    {
        copyFileToHost(filename, directory, environment);
    }

    /**
     * Copy the given file in the working directory to the given remote directory using SSH.
     */
    public boolean copyFileToHost(String filename, String directory, Environment environment) throws IOException
    {
        if(directory == null || directory.length() == 0)
            throw new IllegalArgumentException("target directory null");

        InputStream is = null;
        File sourceFile = new File(workingDir, filename);
        boolean ret = false;

        try
        {
            SshClient client = sshClients.get(environment.getKey());
            if(client == null || !client.isConnected())
            {
                if(client != null)
                    client.close();
                client = SshClient.newClient(environment.getKey(), environment.getSshSettings());
                sshClients.put(environment.getKey(), client);
            }

            client.cd(directory); 

            // Transfer the file to the remote feeds directory using SSH
            if(directory != null)
            {
                is = new FileInputStream(sourceFile);
                ret = client.put(is, filename);
                logger.info("Copied file "+filename+" to remote directory: "+client.pwd());
            }
        }
        catch(JSchException | SftpException e)
        {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
        finally
        {
            try
            {
                // Close the input stream
                if(is != null)
                    is.close();
            }
            catch(IOException e)
            {
            }
        }

        return ret;
    }

    /**
     * Delete the given file from the given remote directory using SSH.
     */
    public void deleteFileFromHost(String filename, String directory, Environment environment) throws IOException
    {
        if(directory == null || directory.length() == 0)
            throw new IllegalArgumentException("directory null");

        try
        {
            SshClient client = sshClients.get(environment.getKey());
            if(client == null || !client.isConnected())
            {
                if(client != null)
                    client.close();
                client = SshClient.newClient(environment.getKey(), environment.getSshSettings());
                sshClients.put(environment.getKey(), client);
            }

            client.cd(directory); 

            // Transfer the file to the remote feeds directory using SSH
            if(directory != null)
            {
                client.rm(filename);
                logger.info("Deleted file "+filename+" from remote directory: "+client.pwd());
            }
        }
        catch(JSchException | SftpException e)
        {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    /**
     * Close the ssh clients and release resources.
     */
    public static void closeClients()
    {
        for(SshClient client : sshClients.values())
            client.close();

        if(s3client != null)
            s3client.close();
    }

    /**
     * Copy the current file in the working directory to the given bucket on S3.
     */
    public void copyFileToBucket(String bucket) throws IOException
    {
        copyFileToBucket(filename, bucket);
    }

    /**
     * Copy the given file in the working directory to the given bucket on S3.
     */
    public boolean copyFileToBucket(String filename, String bucket) throws IOException
    {
        if(bucket == null || bucket.length() == 0)
            throw new IllegalArgumentException("target bucket null");

        S3Client client = null;
        InputStream is = null;
        boolean ret = false;

        try
        {
            // Connect to the remote server using S3
            client = s3client;
            if(client == null)
            {
                client = S3Client.newClient(s3Settings);
                s3client = client;
            }

            client.changeBucket(bucket); 
        }
        catch(IOException e)
        {
            logger.severe(StringUtils.serialize(e));
        }

        try
        {
            // Transfer the file to the bucket
            File file = new File(workingDir, filename);
            is = new FileInputStream(file);
            ret = client.put(is, filename, file.length());
            logger.info("Copied file "+filename+" to bucket: "+bucket);
        }
        catch(IOException e)
        {
            logger.severe(StringUtils.serialize(e));
        }
        finally
        {
            try
            {
                // Close the input stream
                if(is != null)
                    is.close();
            }
            catch(IOException e)
            {
            }
        }

        return ret;
    }

    /**
     * Delete the current file in the working directory.
     */
    public void deleteFile()
    {
        deleteFile(filename);
    }

    /**
     * Delete the given file in the working directory.
     */
    public void deleteFile(String filename)
    {
        // Delete the file
        File file = new File(workingDir, filename);
        file.delete();
        logger.info("Removed file from working directory: "+filename);
    }

    /**
     * Returns a builder for the handler.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make handler construction easier.
     */
    public static class Builder
    {
        private ContentHandler handler = new ContentHandler();

        /**
         * Sets the config file to use with the handler.
         * @param config The config file
         * @return This object
         */
        public Builder useConfiguration(ContentConfiguration config)
        {
            handler.setConfiguration(config);
            return this;
        }

        /**
         * Sets the working directory for the handler.
         * @param workingDir The working directory for the handler
         * @return This object
         */
        public Builder withWorkingDirectory(String workingDir)
        {
            handler.setWorkingDirectory(workingDir);
            return this;
        }

        /**
         * Adds a field source to the handler.
         * @param source The field source to add to the handler
         * @return This object
         */
        public Builder withFieldSource(FieldSource source)
        {
            if(source != null)
                handler.addFields(source.getFields());
            return this;
        }

        /**
         * Adds a configuration map to the handler.
         * @param configurationMap The configuration map to add to the handler
         * @return This object
         */
        public Builder withConfigurations(Map<String,OrganisationContentConfiguration> configurationMap)
        {
            handler.setConfigurationMap(configurationMap);
            return this;
        }

        /**
         * Adds an organisation map to the handler.
         * @param organisationMap The organisation map to add to the handler
         * @return This object
         */
        public Builder withOrganisations(Map<String,Organisation> organisationMap)
        {
            handler.setOrganisationMap(organisationMap);
            return this;
        }

        /**
         * Adds S3 settings to the handler.
         * @param setting The S3 settings to add to the handler
         * @return This object
         */
        public Builder withS3Settings(S3Settings settings)
        {
            handler.setS3Settings(settings);
            return this;
        }

        /**
         * Initialise output file for the handler.
         * @return This object
         */
        public Builder initFile() throws IOException
        {
            handler.initFile();
            return this;
        }

        /**
         * Returns the configured handler instance
         * @return The handler instance
         */
        public ContentHandler build()
        {
            return handler;
        }
    }
}