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
package com.opsmatters.media.client.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
import com.opsmatters.media.model.provider.RepositoryProviderId;
import com.opsmatters.media.model.content.project.ProjectConfig;
import com.opsmatters.media.model.content.project.ProjectDetails;
import com.opsmatters.media.model.content.project.OpenSourceLicense;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents a connection to GitHub for repositories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class GitHubClient extends Client implements RepositoryClient
{
    private static final Logger logger = Logger.getLogger(GitHubClient.class.getName());

    public static final String SUFFIX = ".github";

    private GitHub client;
    private String branch = "";
    private String accessToken = "";

    private static final String LINKS = "<p>Download source code as "
        +"<a href=\"%1$s/zipball/%2$s\">[.zip file]</a> "
        +"<a href=\"%1$s/tarball/%2$s\">[.tar.gz file]</a>"
        +"<br>Documentation: "
        +"<a href=\"%1$s/blob/%2$s/README.md\">[README]</a>"
        +"</p>";

    /**
     * Returns a new github client using an access token.
     */
    static public GitHubClient newClient(ProjectConfig config) throws IOException
    {
        GitHubClient ret = new GitHubClient().builder()
            .branch(config.getBranch())
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
    public RepositoryProviderId getProviderId()
    {
        return RepositoryProviderId.GITHUB;
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
        RepositoryProviderId providerId = RepositoryProviderId.fromUrl(url);
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
         * Returns the configured client instance
         * @return The client instance
         */
        public GitHubClient build()
        {
            return client;
        }
    }
}