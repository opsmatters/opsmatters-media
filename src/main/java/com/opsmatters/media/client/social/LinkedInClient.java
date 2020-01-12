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
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.security.GeneralSecurityException;
import org.apache.commons.io.FileUtils;
import com.google.common.collect.Lists;
import org.json.JSONObject;
import com.echobox.api.linkedin.client.DefaultLinkedInClient;
import com.echobox.api.linkedin.version.Version;
import com.echobox.api.linkedin.types.urn.URN;
import com.echobox.api.linkedin.types.urn.URNEntityType;
import com.echobox.api.linkedin.types.organization.Organization;
import com.echobox.api.linkedin.types.Share;
import com.echobox.api.linkedin.types.ShareText;
import com.echobox.api.linkedin.types.request.ShareRequestBody;
import com.echobox.api.linkedin.connection.v2.OrganizationConnection;
import com.echobox.api.linkedin.connection.v2.ShareConnection;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.social.SocialProvider;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.SocialPost;

/**
 * Class that represents a connection to LinkedIn for social media posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LinkedInClient extends Client implements SocialClient
{
    private static final Logger logger = Logger.getLogger(LinkedInClient.class.getName());

    public static final String AUTH = ".linkedIn";

    private Organization organization;
    private OrganizationConnection organizationConnection;
    private String organizationId;
    private URN organizationURN;
    private ShareConnection shareConnection;
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
            logger.severe("Unable to create linkedin client: "+channel.getName());

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
            logger.info("Configuring linkedin client: "+channel.getName());

        String directory = System.getProperty("om-config.auth", ".");

        File auth = new File(directory, channel.getName()+AUTH);
        JSONObject obj = null;
        try
        {
            // Read file from auth directory
            obj = new JSONObject(FileUtils.readFileToString(auth, "UTF-8"));
            setOrganizationId(obj.optString("organizationId"));
            setAppId(obj.optString("appId"));
            setAppSecret(obj.optString("appSecret"));
            setRedirectUri(obj.optString("redirectUri"));
            setVerificationCode(obj.optString("verificationCode"));
            setAccessToken(obj.optString("accessToken"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read linkedin auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        // Get the access token using the credentials
        if(getAppId() != null && getAppId().length() > 0
            && (getAccessToken() == null || getAccessToken().length() == 0))
        {
            DefaultLinkedInClient client = new DefaultLinkedInClient(Version.DEFAULT_VERSION);
            com.echobox.api.linkedin.client.LinkedInClient.AccessToken token = client.obtainUserAccessToken(getAppId(),
                getAppSecret(), getRedirectUri(), getVerificationCode());
            setAccessToken(token.getAccessToken());
            obj.putOpt("accessToken", getAccessToken());
            obj.remove("verificationCode");
            FileUtils.writeStringToFile(auth, obj.toString(), "UTF-8");
        }

        if(debug())
            logger.info("Configured linkedin client successfully: "+channel.getName());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException, GeneralSecurityException
    {
        if(debug())
            logger.info("Creating linkedin client: "+channel.getName());

        // Create the client
        DefaultLinkedInClient linkedin = new DefaultLinkedInClient(getAccessToken(), Version.DEFAULT_VERSION);
        shareConnection = new ShareConnection(linkedin);
        organizationConnection = new OrganizationConnection(linkedin);

        // Issue command to test connectivity
        organizationURN = new URN(URNEntityType.ORGANIZATION, organizationId);
        organization = organizationConnection.retrieveOrganization(organizationURN, null);

        if(debug())
            logger.info("Created linkedin client successfully: "+channel.getName());

        return organization != null && organization.getId() > 0L;
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
     * Sends the given post.
     *
     * @param text The text of the post to be sent.
     */
    public SocialPost sendPost(String text) throws IOException
    {
        ShareRequestBody shareRequestBody = new ShareRequestBody(organizationURN);
        ShareText shareText = new ShareText();
        shareText.setText(text);
        shareRequestBody.setText(shareText);
        Share share = shareConnection.postShare(shareRequestBody);
        return share != null ? new SocialPost(share, channel) : null;
    }

    /**
     * Deletes the given post.
     *
     * @param id The id of the post to be deleted.
     */
    public SocialPost deletePost(String id) throws IOException
    {
        Share share = shareConnection.getShare(Long.parseLong(id));
        shareConnection.deleteShare(share.getId());
        return share != null ? new SocialPost(share, channel) : null;
    }

    /**
     * Returns the posts for the current user.
     */
    public List<SocialPost> getPosts() throws IOException
    {
        List<SocialPost> ret = new ArrayList<SocialPost>();
        List<Share> shares = shareConnection.getShares(Lists.newArrayList(organizationURN), 30, null);
        for(Share share : shares)
            ret.add(new SocialPost(share, channel));
        return ret;
    }
}