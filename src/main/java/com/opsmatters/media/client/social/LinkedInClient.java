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
package com.opsmatters.media.client.social;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.security.GeneralSecurityException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import com.echobox.api.linkedin.client.VersionedLinkedInClient;
import com.echobox.api.linkedin.client.DefaultVersionedLinkedInClient;
import com.echobox.api.linkedin.version.Version;
import com.echobox.api.linkedin.types.urn.URN;
import com.echobox.api.linkedin.types.urn.URNEntityType;
import com.echobox.api.linkedin.types.organization.Organization;
import com.echobox.api.linkedin.types.posts.Post;
import com.echobox.api.linkedin.types.posts.Distribution;
import com.echobox.api.linkedin.types.posts.ViewContext;
import com.echobox.api.linkedin.types.images.InitializeUploadRequestBody;
import com.echobox.api.linkedin.connection.versioned.VersionedOrganizationConnection;
import com.echobox.api.linkedin.connection.versioned.VersionedPostConnection;
import com.echobox.api.linkedin.connection.versioned.VersionedImageConnection;
import com.echobox.api.linkedin.exception.LinkedInAPIException;
import com.echobox.api.linkedin.exception.LinkedInQueryParseException;
import com.echobox.api.linkedin.exception.LinkedInResourceNotFoundException;
import com.echobox.api.linkedin.util.PostUtils;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.social.SocialProvider;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.ChannelPost;
import com.opsmatters.media.util.StringUtils;

import static com.echobox.api.linkedin.types.images.InitializeUploadRequestBody.*;

/**
 * Class that represents a connection to LinkedIn for social media posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LinkedInClient extends Client implements SocialClient
{
    private static final Logger logger = Logger.getLogger(LinkedInClient.class.getName());

    public static final String SUFFIX = ".social";
    public static final String OAUTH_SUFFIX = ".linkedin";

    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECT_TIMEOUT = 5000;

    private Organization organization;
    private VersionedOrganizationConnection organizationConnection;
    private String organizationId;
    private URN organizationURN;
    private VersionedPostConnection postConnection;
    private VersionedImageConnection imageConnection;
    private String appId = "";
    private String appSecret = "";
    private String redirectUri = "";
    private String verificationCode;
    private String accessToken;
    private SocialChannel channel;

    /**
     * Private constructor.
     */
    private LinkedInClient(SocialChannel channel)
    {
        setChannel(channel);
    }

    /**
     * Returns a new linkedin client using the given channel.
     */
    static public LinkedInClient newClient(SocialChannel channel) throws IOException, GeneralSecurityException
    {
        LinkedInClient ret = new LinkedInClient(channel);

        // Configure and create the linkedin client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create linkedin client: "+channel.getCode());

        return ret;
    }

    /**
     * Returns the provider for this client.
     */
    public SocialProvider getProvider()
    {
        return SocialProvider.LINKEDIN;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring linkedin client: "+channel.getCode());

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, OAUTH_SUFFIX);
        JSONObject obj = null;
        try
        {
            // Read file from auth directory
            obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setAppId(obj.optString("appId"));
            setAppSecret(obj.optString("appSecret"));
            setRedirectUri(obj.optString("redirectUri"));
            setAccessToken(obj.optString("accessToken"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read default linkedin auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        file = new File(directory, channel.getCode().toLowerCase()+SUFFIX);
        try
        {
            // Read file from auth directory
            obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setOrganizationId(obj.optString("organizationId"));
            setAppId(obj.optString("appId", getAppId()));
            setAppSecret(obj.optString("appSecret", getAppSecret()));
            setRedirectUri(obj.optString("redirectUri", getRedirectUri()));
            setVerificationCode(obj.optString("verificationCode"));
            setAccessToken(obj.optString("accessToken", getAccessToken()));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read channel linkedin auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        // Get the access token using the credentials
        if(getAppId() != null && getAppId().length() > 0
            && (getAccessToken() == null || getAccessToken().length() == 0))
        {
            VersionedLinkedInClient client = new DefaultVersionedLinkedInClient(Version.DEFAULT_VERSION);
            com.echobox.api.linkedin.client.LinkedInClient.AccessToken token = client.obtainUserAccessToken(getAppId(),
                getAppSecret(), getRedirectUri(), getVerificationCode());
            setAccessToken(token.getAccessToken());
            obj.putOpt("accessToken", getAccessToken());
            obj.remove("verificationCode");
            FileUtils.writeStringToFile(file, obj.toString(), "UTF-8");
        }

        if(debug())
            logger.info("Configured linkedin client successfully: "+channel.getCode());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException, GeneralSecurityException
    {
        if(debug())
            logger.info("Creating linkedin client: "+channel.getCode());

        // Create the client and connections
        VersionedLinkedInClient linkedin = new DefaultVersionedLinkedInClient(getAccessToken(), Version.DEFAULT_VERSION);
        organizationConnection = new VersionedOrganizationConnection(linkedin);
        postConnection = new VersionedPostConnection(linkedin);
        imageConnection = new VersionedImageConnection(linkedin);

        // Issue command to test connectivity
        organizationURN = new URN(URNEntityType.ORGANIZATION, organizationId);
        organization = organizationConnection.retrieveOrganization(organizationURN, null);

        if(debug())
            logger.info("Created linkedin client successfully: "+channel.getCode());

        return organization != null && organization.getId() > 0L;
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
        organizationConnection = null;
        postConnection = null;
        imageConnection = null;
    }

    /**
     * Returns the organization id for the client.
     */
    public String getOrganizationId() 
    {
        return organizationId;
    }

    /**
     * Sets the organization id for the client.
     */
    public void setOrganizationId(String organizationId) 
    {
        this.organizationId = organizationId;
    }

    /**
     * Returns the app id for the client.
     */
    public String getAppId() 
    {
        return appId;
    }

    /**
     * Sets the app id for the client.
     */
    public void setAppId(String appId) 
    {
        this.appId = appId;
    }

    /**
     * Returns the app secret for the client.
     */
    public String getAppSecret() 
    {
        return appSecret;
    }

    /**
     * Sets the app secret for the client.
     */
    public void setAppSecret(String appSecret) 
    {
        this.appSecret = appSecret;
    }

    /**
     * Returns the redirect uri for the client.
     */
    public String getRedirectUri() 
    {
        return redirectUri;
    }

    /**
     * Sets the redirect uri for the client.
     */
    public void setRedirectUri(String redirectUri) 
    {
        this.redirectUri = redirectUri;
    }

    /**
     * Returns the verification code for the client.
     */
    public String getVerificationCode() 
    {
        return verificationCode;
    }

    /**
     * Sets the verification code for the client.
     */
    public void setVerificationCode(String verificationCode) 
    {
        this.verificationCode = verificationCode;
    }

    /**
     * Returns the access token for the client.
     */
    public String getAccessToken() 
    {
        return accessToken;
    }

    /**
     * Sets the access token for the client.
     */
    public void setAccessToken(String accessToken) 
    {
        this.accessToken = accessToken;
    }

    /**
     * Returns the social channel.
     */
    public SocialChannel getChannel()
    {
        return channel;
    }

    /**
     * Sets the social channel.
     */
    public void setChannel(SocialChannel channel)
    {
        this.channel = channel;
    }

    /**
     * Returns the screen name of the current account.
     */
    public String getName() throws IOException
    {
        return organization.getLocalizedName();
    }

    /**
     * Returns a channel post for the given post.
     */
    private ChannelPost newChannelPost(Post post, SocialChannel channel)
    {
        return new ChannelPost(post.getId().getId(), channel, post.getCommentary());
    }

    /**
     * Sends the given post.
     *
     * @param text The text of the post to be sent.
     */
    public ChannelPost sendPost(String text) throws IOException
    {
        String link = StringUtils.extractUrl(text);
        if(link == null)
            throw new IllegalArgumentException("missing url");

        URL url = new URL(link);

        String title = null;
        String description = null;
        String imageUrl = null;

        try
        {
            // Crawl the link's page to get the metatags
            Document doc = Jsoup.parse(url, READ_TIMEOUT);
            List<Element> tags = doc.getElementsByTag("meta");
            title = getMetatag(tags, "og:title");
            description = getMetatag(tags, "og:description");
            imageUrl = getMetatag(tags, "og:image");
        }
        catch(SocketTimeoutException e)
        {
            logger.severe("Error crawling page for linkedin send post: "+url);
            throw new SocialTimeoutException("Error crawling page for url: "+url, e);
        }

        String filename = null;
        File file = null;

        try
        {
            // Download the image file
            filename = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
            file = File.createTempFile("image-", null);
            FileUtils.copyURLToFile(new URL(imageUrl), file, CONNECT_TIMEOUT, READ_TIMEOUT);
        }
        catch(SocketTimeoutException e)
        {
            logger.severe("Error downloading image for linkedin send post: "+imageUrl);
            throw new SocialTimeoutException("Error downloading image: "+imageUrl, e);
        }

        // Upload the image file and delete the downloaded file
        InitializeUploadRequest request = new InitializeUploadRequest(organizationURN);
        URN imageURN = imageConnection.uploadImage(new InitializeUploadRequestBody(request), filename, file);
        file.delete();

        Distribution distribution = new Distribution(Distribution.FeedDistribution.MAIN_FEED);
        Post post = new Post(organizationURN, text, distribution,
            Post.LifecycleState.PUBLISHED, Post.Visibility.PUBLIC);
        PostUtils.fillArticleContent(post, link, imageURN, title, description);
        URN postURN = postConnection.createPost(post);
        post.setId(postURN);
        return newChannelPost(post, channel);
    }

    /**
     * Get the content of the given metatag.
     *
     * @param tags The list of metatags.
     * @param name The name of the metatag.
     * @return The content of the metatag.
     */
    private String getMetatag(List<Element> tags, String name)
    {
        String ret = null;
        for(Element tag : tags)
        {
            String attr = tag.attr("property");
            if(attr != null && attr.equals(name))
            {
                ret = tag.attr("content");
                break;
            }
        }

        return ret;
    }

    /**
     * Deletes the given post.
     *
     * @param id The id of the post to be deleted.
     */
    public ChannelPost deletePost(String id) throws IOException
    {
        ChannelPost ret = null;
        URN postURN = new URN(URNEntityType.SHARE, id);

        try
        {
            Post post = postConnection.retrievePost(postURN, ViewContext.AUTHOR);
            postConnection.deletePost(postURN);
            ret = newChannelPost(post, channel);
        }
        catch(LinkedInResourceNotFoundException e)
        {
            // Post already deleted
            ret = new ChannelPost(id, channel);
        }

        return ret;
    }

    /**
     * Returns the posts for the current user.
     */
    public List<ChannelPost> getPosts() throws IOException
    {
        List<ChannelPost> ret = new ArrayList<ChannelPost>();
        List<Post> posts = postConnection.retrievePostsByAuthor(organizationURN, 30).getData();
        for(Post post : posts)
            ret.add(newChannelPost(post, channel));
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given error is recoverable.
     */
    public boolean isRecoverable(Exception e)
    {
        boolean ret = true;
        if(e instanceof LinkedInQueryParseException)
        {
            ret = false;
        }
        else
        {
            int errorCode = getErrorCode(e);
            return errorCode != 409; // Content is a duplicate
        }

        return ret;
    }

    /**
     * Returns the error code from the given exception.
     */
    public int getErrorCode(Exception e)
    {
        int ret = -1;

        if(e instanceof LinkedInAPIException)
        {
            LinkedInAPIException ex = (LinkedInAPIException)e;
            Integer code = ex.getErrorCode();
            if(code != null)
                ret = code.intValue();
        }

        return ret;
    }

    /**
     * Returns the error message from the given exception.
     */
    public String getErrorMessage(Exception e)
    {
        String ret = "";

        if(e instanceof LinkedInAPIException)
        {
            LinkedInAPIException ex = (LinkedInAPIException)e;
            ret = ex.getErrorMessage();
        }

        return ret;
    }
}