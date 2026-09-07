/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.client.repo;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.logging.Logger;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHLicense;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GHCommitBuilder;
import com.opsmatters.media.model.provider.RepoProviderId;
import com.opsmatters.media.model.content.project.ProjectDetails;
import com.opsmatters.media.model.content.project.OpenSourceLicense;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents a connection to GitHub for repositories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class GitHubClient extends Client implements RepoClient
{
    private static final Logger logger = Logger.getLogger(GitHubClient.class.getName());

    private static final String LINKS = "<p>Download source code as "
        +"<a href=\"%1$s/zipball/%2$s\">[.zip file]</a> "
        +"<a href=\"%1$s/tarball/%2$s\">[.tar.gz file]</a>"
        +"<br>Documentation: "
        +"<a href=\"%1$s/blob/%2$s/README.md\">[README]</a>"
        +"</p>";

    public static final String SUFFIX = ".github";

    private GitHub client;
    private String accessToken = "";
    private String branch = "";
    private String directory = "";

    private Map<String,GHContent> contentMap = new HashMap<String,GHContent>();

    /**
     * Returns a new github client using an access token.
     */
    public static GitHubClient newClient(String branch) throws IOException
    {
        GitHubClient ret = new GitHubClient().builder()
//            .branch(config.getBranch())
            .branch(branch)
            .build();

        // Configure and create the github client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create github client");

        return ret;
    }

    /**
     * Returns the provider for this client.
     */
    public RepoProviderId getProviderId()
    {
        return RepoProviderId.GITHUB;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring github client");

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, SUFFIX);
        try
        {
            // Read access token from auth directory
            accessToken = FileUtils.readFileToString(file, "UTF-8");
        }
        catch(IOException e)
        {
            logger.severe("Unable to read github access token: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured github client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating github client");

        // Authenticate using OAuth access token
        if(accessToken != null && accessToken.length() > 0)
            client = new GitHubBuilder().withOAuthToken(accessToken).build();

        logger.info("Using branch "+branch);

        if(debug())
            logger.info("Created github client successfully");

        return true;
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
        client = null;
    }

    /**
     * Returns the default branch for the client.
     */
    public String getBranch()
    {
        return branch;
    }

    /**
     * Sets the default branch for the client.
     */
    public void setBranch(String branch)
    {
        this.branch = branch;
    }

    /**
     * Returns the current directory for the client.
     */
    public String getDirectory()
    {
        return directory;
    }

    /**
     * Sets the current directory for the client.
     */
    public void setDirectory(String directory)
    {
        this.directory = directory;
    }

    /**
     * Returns <CODE>true</CODE> if the current directory has been set.
     */
    public boolean hasDirectory() 
    {
        return directory != null && directory.length() > 0;
    }

    /**
     * Returns the name of the current account.
     */
    public String getName() throws IOException
    {
        return client.getMyself().getName();
    }

    /**
     * Returns the organization with the given name.
     */
    public GHOrganization getOrganization(String name) throws IOException
    {
        GHOrganization ret = client.getOrganization(name);
        if(ret == null)
            logger.severe("Unable to find github organization: "+name);
        return ret;
    }

    /**
     * Returns the repository with the given repo url.
     */
    public GHRepository getRepository(String url) throws IOException
    {
        if(url == null || url.length() == 0)
            throw new IllegalArgumentException("missing repo URL");
        RepoProviderId providerId = RepoProviderId.fromUrl(url);
        return getRepository(providerId.getRepoUser(url), providerId.getRepoName(url));
    }

    /**
     * Returns the repository with the given name for the given user.
     */
    public GHRepository getRepository(String username, String name) throws IOException
    {
        GHRepository ret = null;
        GHUser user = client.getUser(username);
        if(user != null)
        {
            GHRepository repository = user.getRepository(name);
            if(repository != null)
            {
                if(debug())
                    logger.info("Found github repository: "+repository.getName());
                ret = repository;
            }
            else
            {
                logger.severe("Unable to find github repository: "+name);
            }
        }
        else
        {
            logger.severe("Unable to find github user: "+username);
        }

        return ret;
    }

    /**
     * Returns the contents of the README for the given repository.
     */
    public String getReadme(GHRepository repository) throws IOException
    {
        if(repository == null)
            throw new IllegalArgumentException("repository null");
        String ret = "";
        GHContent readme = repository.getReadme();
        if(readme != null)
            ret = IOUtils.toString(readme.read(), StandardCharsets.UTF_8.name());
        else
            logger.severe("Unable to find README for repository: "+repository.getName());
        return ret;
    }

    /**
     * Returns the founded year for the given repository.
     */
    public String getFounded(GHRepository repository) throws IOException
    {
        Calendar calendar = Calendar.getInstance();
        GHContent readme = repository.getReadme();
        List<GHCommit> commits = repository.queryCommits().path(readme.getPath()).list().toList();
        if(commits.size() > 0)
        {
            GHCommit commit = commits.get(commits.size()-1);
            calendar.setTime(commit.getCommitDate());
        }

        return Integer.toString(calendar.get(Calendar.YEAR));
    }

    /**
     * Returns the project for the given repo url.
     */
    public ProjectDetails getProject(String url) throws IOException
    {
        ProjectDetails project = null;

        GHRepository repository = getRepository(url);
        if(repository != null)
        {
            project = new ProjectDetails();
            project.setUrl(url, true);
            project.setPublishedDate(Instant.now());
            project.setTitle(repository.getName());
            project.setSummary(repository.getDescription());
            //GC: 29/09/2020 removed because it causes errors
            //project.setDescription(StringUtils.markdownToHtml(getReadme(repository)));
            project.setWebsite(repository.getHomepage());
            project.setFounded(getFounded(repository));

            String repoUrl = String.format("%s/%s", getProviderId().url(), repository.getFullName());
            project.setLinks(String.format(LINKS, repoUrl, branch));

            GHLicense license = repository.getLicense();
            if(license != null)
            {
                OpenSourceLicense l = OpenSourceLicense.fromCode(license.getKey());
                if(l != null)
                    project.setLicense(l.value());
                else
                    logger.warning("Repository license not found for key: "+license.getKey());
            }
        }

        return project;
    }

    /**
     * Commit and push the files in the current directory for the given repository.
     */
    public GHCommit commit(GHRepository repository, String path, String message)
        throws IOException
    {
        if(repository == null)
            throw new IllegalArgumentException("repository null");

        GHCommit ret = null;
        String branch = getBranch();

        if(hasDirectory())
        {
            if(branch == null)
                branch = repository.getDefaultBranch();
            GHRef ref = repository.getRef(String.format("heads/%s", branch));
            String sha = repository.getTreeRecursive(branch, 1).getSha();
            GHTreeBuilder treeBuilder = repository.createTree().baseTree(sha);
            File baseDirectory = new File(directory, path);
            List<File> files = new ArrayList<File>();
            addContent(repository, path);
            addFilesToTree(repository, path, treeBuilder, baseDirectory, baseDirectory, files);

            if(files.size() > 0)
            {
                GHTree tree = treeBuilder.create();
                GHCommit commit = repository.createCommit()
                    .message(message)
                    .tree(tree.getSha())
                    .parent(ref.getObject().getSha())
                    .create();
                ref.updateTo(commit.getSHA1());
                ret = commit;

                if(debug())
                    logger.info(String.format("Created commit for directory %s: SHA=%s URL=%s",
                        baseDirectory, commit.getSHA1(), commit.getHtmlUrl()));

                logger.info(String.format("Committed %d files for %s directory",
                    files.size(), path));
            }
        }
        else
        {
            logger.severe("No current directory");
        }

        contentMap.clear();

        return ret;
    }

    /**
     * Commit and push the files in the current directory for the given repository.
     */
    public GHCommit commit(GHRepository repository, String path) throws IOException
    {
        return commit(repository, path, "Updated "+path);
    }

    /**
     * Recurse through the files in the given directory to build the file tree for the commit.
     */
    private void addFilesToTree(GHRepository repository, String path, GHTreeBuilder treeBuilder,
        File baseDirectory, File currentDirectory, List<File> files)
        throws IOException
    {
        for(File file : currentDirectory.listFiles())
        {
            String relativePath = String.format("%s/%s", path, 
                baseDirectory.toURI().relativize(file.toURI()).getPath());
            if(file.isFile())
            {
                if(!file.getName().startsWith("~$")) // Ignore temporary xlsx files
                {
                    long size = -1L;
                    try
                    {
                        GHContent content = contentMap.get(relativePath);
                        if(content == null)
                            content = repository.getFileContent(relativePath);
                        if(content != null)
                            size = content.getSize();
                    }
                    catch(FileNotFoundException e)
                    {
                        logger.severe(String.format("File not found in repository: %s",
                            relativePath));
                    }

                    if(file.length() != size)
                    {
                        treeBuilder.add(relativePath, FileUtils.readFileToByteArray(file), false);
                        files.add(file);
                    }
                }
            }
            else
            {
                addContent(repository, relativePath);
                addFilesToTree(repository, path, treeBuilder, baseDirectory, file, files);
            }
        }
    }

    /**
     * Add the content entries from the repository for the given path to the map.
     */
    private void addContent(GHRepository repository, String path)
        throws IOException
    {
        List<GHContent> contentList = repository.getDirectoryContent(path);
        for(GHContent content : contentList)
            contentMap.put(content.getPath(), content);
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
        private GitHubClient client = new GitHubClient();

        /**
         * Sets the default branch for the client.
         * @param branch The default branch for the client
         * @return This object
         */
        public Builder branch(String branch)
        {
            client.setBranch(branch);
            return this;
        }

        /**
         * Sets the default directory for the client.
         * @param branch The default directory for the client
         * @return This object
         */
        public Builder directory(String directory)
        {
            client.setDirectory(directory);
            return this;
        }

        /**
         * Returns the configured client instance
         * @return The client instance
         */
        public GitHubClient build()
        {
            return client;
        }
    }
}