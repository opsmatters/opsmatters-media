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

package com.opsmatters.media.client.social;

import java.io.IOException;
import java.util.List;
import com.opsmatters.media.model.admin.SocialProviderId;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.ChannelPost;

/**
 * Methods to interact with a user's social media channel.
 *
 * @author Gerald Curley (opsmatters)
 */
public interface SocialClient
{
    /**
     * Close the client.
     */
    public void close();

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public boolean debug();

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebug(boolean debug);

    /**
     * Returns the provider for this client.
     */
    public SocialProviderId getProviderId();

    /**
     * Returns the social channel.
     */
    public SocialChannel getChannel();

    /**
     * Returns the name of the current account.
     */
    public String getName() throws Exception;

    /**
     * Sends the given post.
     *
     * @param text The text of the post to be sent.
     */
    public ChannelPost sendPost(String text) throws Exception;

    /**
     * Deletes the given post.
     *
     * @param id The id of the post to be deleted.
     */
    public ChannelPost deletePost(String id) throws Exception;

    /**
     * Returns the posts for the current user.
     */
    public List<ChannelPost> getPosts() throws Exception;

    /**
     * Returns <CODE>true</CODE> if the given error is recoverable.
     */
    public boolean isRecoverable(Exception e);

    /**
     * Returns the error code from the given exception.
     */
    public int getErrorCode(Exception e);

    /**
     * Returns the error message from the given exception.
     */
    public String getErrorMessage(Exception e);
}