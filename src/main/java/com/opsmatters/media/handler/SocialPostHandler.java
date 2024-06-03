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

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.text.StringSubstitutor;
import com.vdurmont.emoji.EmojiParser;
import com.twitter.twittertext.TwitterTextParser;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.SocialProvider;
import com.opsmatters.media.model.social.SocialPostProperties;
import com.opsmatters.media.model.social.Hashtag;

import static com.opsmatters.media.model.social.SocialPostProperty.*;

/**
 * Class representing a handler used to prepare social media posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialPostHandler
{
    private List<Token> tokens = new ArrayList<Token>();
    private String hashtag = null;
    private String hashtags = null;
    private List<Hashtag> hashtagList = null;
    private Map<String,HashtagItem> hashtagMap = new LinkedHashMap<String,HashtagItem>();
    private SocialPostProperties properties = new SocialPostProperties();
    private String message = null;
    private String markupMessage = null;
    private int messageLength = -1;
    private int hashtagCount = -1;

    /**
     * Private constructor.
     */
    private SocialPostHandler()
    {
    }

    /**
     * Sets the main organisation hashtag.
     */
    private void setHashtag(String hashtag)
    {
        this.hashtag = hashtag;
    }

    /**
     * Sets the list of hashtags.
     */
    private void setHashtags(String hashtags)
    {
        this.hashtags = hashtags;
    }

    /**
     * Adds a list of hashtags.
     */
    private void addHashtags(List<Hashtag> hashtagList)
    {
        if(this.hashtagList == null)
            this.hashtagList = new ArrayList<Hashtag>();
        this.hashtagList.addAll(hashtagList);
    }

    /**
     * Returns the properties for the handler.
     */
    public SocialPostProperties getProperties()
    {
        return properties;
    }

    /**
     * Sets the properties for the handler.
     */
    public void setProperties(SocialPostProperties properties)
    {
        getProperties().set(properties);
    }

    /**
     * Represents a hashtag in the hashtag list.
     */
    class HashtagItem
    {
        String key;
        String value;
        boolean optional = false;
        boolean ignored = false;

        /**
         * Constructor that takes a hashtag with format: #value[?]
         */
        HashtagItem(String str)
        {
            if(str.endsWith("?"))
            {
                optional = true;
                str = str.substring(0, str.length()-1); // Remove trailing "?"
            }
            else if(str.endsWith("!"))
            {
                ignored = true;
                optional = true;
                str = str.substring(0, str.length()-1); // Remove trailing "!"
            }

            value = str.substring(1); // remove leading "#"
            key = value.toLowerCase();
        }

        /**
         * Returns the value of the hashtag prefixed by "#".
         */
        public String toString()
        {
            return "#"+value;
        }

        /**
         * Returns the key for the hashtag in the map.
         */
        String getKey()
        {
            return key;
        }

        /**
         * Returns the value of the hashtag.
         */
        String getValue()
        {
            return value;
        }

        /**
         * Returns <CODE>true</CODE> if the hashtag is optional.
         */
        boolean isOptional()
        {
            return optional;
        }

        /**
         * Set to <CODE>true</CODE> if the hashtag is optional.
         */
        void setOptional(boolean optional)
        {
            this.optional = optional;
        }

        /**
         * Returns <CODE>true</CODE> if the hashtag should be ignored.
         */
        boolean isIgnored()
        {
            return ignored;
        }

        /**
         * Set to <CODE>true</CODE> if the hashtag should be ignored.
         */
        void setIgnored(boolean ignored)
        {
            this.ignored = ignored;
        }
    }

    /**
     * Populates the hashtag map from the hashtag list.
     */
    private void parseHashtags()
    {
        hashtagMap.clear();

        // Add the main organisation hashtag
        if(hashtag != null && hashtag.length() > 0)
        {
            HashtagItem item = new HashtagItem(hashtag);
            item.setOptional(true);
            hashtagMap.put(item.getKey(), item);
        }

        // Add the site hashtags
        if(hashtagList != null)
        {
            for(Hashtag hashtag : hashtagList)
            {
                HashtagItem item = new HashtagItem(hashtag.getValue());
                item.setOptional(true);
                hashtagMap.put(item.getKey(), item);
            }
        }

        // Add the organisation hashtags
        if(hashtags != null && hashtags.length() > 0)
        {
            for(String str : hashtags.split(" "))
            {
                if(str.startsWith("#") && str.length() > 2)
                {
                    HashtagItem item = new HashtagItem(str);
                    if(hashtagMap.containsKey(item.getKey()))
                        hashtagMap.remove(item.getKey());
                    hashtagMap.put(item.getKey(), item);
                }
            }
        }
    }

    /**
     * Creates a hashtag list from the hashtag map.
     */
    private String getHashtags()
    {
        StringBuilder builder = new StringBuilder();
        Collection<HashtagItem> items = hashtagMap.values();
        for(HashtagItem item : items)
        {
            if(!item.isOptional())
            {
                if(builder.length() > 0)
                    builder.append(" ");
                builder.append(item);
            }
        }

        return builder.toString();
    }

    /**
     * Integrate the hashtags in the update properties with the message.
     */
    public void parse(String message)
    {
        if(message != null && message.length() > 0)
        {
            // Parse the hashtags into a map
            parseHashtags();

            // Process the message to find any hashtags in the message
            //   that can be removed from the hashtag list
            getProperties().remove(HASHTAGS);
            parseTokens(new StringSubstitutor(getProperties()).replace(message));

            // Put back the amended hashtag list and process the message again
            //   to resolve the HASHTAGS property with the new value
            if(hashtags != null)
                getProperties().put(HASHTAGS, getHashtags());
            parseTokens(new StringSubstitutor(getProperties()).replace(getMessage()));
        }
    }

    /**
     * Parses the given message into tokens.
     */
    private void parseTokens(String message)
    {
        // Parse the message into a list of tokens
        tokens.clear();
        boolean inWord = false;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < message.length(); i++)
        {
            char c = message.charAt(i);
            String str = null;

            if(c == '#' || c == '@') // Start of a hashtag or handle
            {
                addToken(builder.toString());
                builder.setLength(0);
                inWord = true;
            }
            else if((str = message.substring(i)).startsWith("${")) // Start of property
            {
                String property = StringUtils.extractProperty(str);
                if(!inWord && property != null)
                {
                    addToken(builder.toString());
                    addToken(property);
                    i += property.length()-1;
                    c = 0;
                    builder.setLength(0);
                    inWord = false;
                }
            }
            else if(str.startsWith(":")) // Start of emoji
            {
                String emoji = StringUtils.extractEmoji(str);
                if(!inWord && emoji != null)
                {
                    addToken(builder.toString());
                    addToken(emoji);
                    i += emoji.length()-1;
                    c = 0;
                    builder.setLength(0);
                    inWord = false;
                }
                else if(inWord) // End of hashtag, handle or word
                {
                    addToken(builder.toString());
                    builder.setLength(0);
                    inWord = false;
                }
            }
            else if(str.startsWith("http:") || str.startsWith("https:")) // Start of URL
            {
                String url = StringUtils.extractUrl(str);
                if(!inWord && url != null)
                {
                    addToken(builder.toString());
                    addToken(url);
                    i += url.length()-1;
                    c = 0;
                    builder.setLength(0);
                    inWord = false;
                }
            }
            else if(Character.isLetterOrDigit(c) || c == '_') // In a hashtag, handle or word
            {
                if(!inWord)
                {
                    addToken(builder.toString());
                    builder.setLength(0);
                    inWord = true;
                }
            }
            else // End of hashtag, handle or word
            {
                addToken(builder.toString());
                builder.setLength(0);
                inWord = false;
            }

            if(c > 0)
                builder.append(c);

            // Add the last token
            if(i == message.length()-1)
            {
                addToken(builder.toString());
            }
        }

        // If a string token matches a hashtag in the list,
        //    remove it from the list and replace the token with a hashtag token
        Token lastToken = null;
        for(Token token : tokens)
        {
            if(hashtagMap.containsKey(token.getKey()))
            {
                HashtagItem item = hashtagMap.get(token.getKey());
                if(!item.isIgnored())
                {
                    hashtagMap.remove(token.getKey());
                    if(token.getType() == TokenType.STRING)
                        tokens.set(tokens.indexOf(token), new HashtagToken(token.getValue()));
                }
            }

            lastToken = token;
        }
    }

    /**
     * Add the given token to the list.
     */
    private void addToken(String token)
    {
        if(token.length() > 0)
        {
            if(token.startsWith("#") && token.length() > 2)
                tokens.add(new HashtagToken(token.substring(1)));
            else if(token.startsWith("@") && token.length() > 2)
                tokens.add(new HandleToken(token.substring(1)));
            else if(token.startsWith("http:") && token.length() > 10)
                tokens.add(new UrlToken(token));
            else if(token.startsWith("https:") && token.length() > 11)
                tokens.add(new UrlToken(token));
            else if(token.startsWith("${") && token.endsWith("}") && token.length() > 3)
                tokens.add(new PropertyToken(token.substring(2, token.length()-1)));
            else if(token.startsWith(":") && token.endsWith(":") && token.length() > 2)
                tokens.add(new EmojiToken(token.substring(1, token.length()-1)));
            else if(!token.equals("\r")) // Throw away CRs
                tokens.add(new StringToken(token));
        }
    }

    /**
     * The types of tokens that can be parsed.
     */
    enum TokenType
    {
        STRING,
        PROPERTY,
        EMOJI,
        HASHTAG,
        HANDLE,
        URL
    }

    /**
     * The base class for all tokens.
     */
    abstract class Token
    {
        String key;
        String value;

        /**
         * Constructor that takes a value.
         */
        Token(String value)
        {
            setValue(value);
        }

        /**
         * Returns the type of the token.
         */
        public abstract TokenType getType();

        /**
         * Sets the token value.
         */
        public void setValue(String value)
        {
            this.value = value;
            this.key = value.toLowerCase();
        }

        /**
         * Returns the token value.
         */
        public String getValue()
        {
            return value;
        }

        /**
         * Returns the length of the token value.
         */
        public int length(SocialChannel channel)
        {
            return value.length();
        }

        /**
         * Returns the token key.
         */
        public String getKey()
        {
            return key;
        }

        /**
         * Returns the token value.
         */
        public String toString()
        {
            return getValue();
        }

        /**
         * Returns the token markup.
         */
        String getMarkup(SocialChannel channel) 
        {
            return toString();
        }
    }

    /**
     * Represents a token based on a string literal.
     */
    class StringToken extends Token
    {
        /**
         * Constructor that takes a value.
         */
        StringToken(String value)
        {
            super(value);
        }

        /**
         * Returns the type of the token.
         */
        @Override
        public TokenType getType()
        {
            return TokenType.STRING;
        }
    }

    /**
     * Represents a token based on an emoji.
     */
    class EmojiToken extends Token
    {
        /**
         * Constructor that takes a value.
         */
        EmojiToken(String value)
        {
            super(value);
        }

        /**
         * Returns the type of the token.
         */
        @Override
        public TokenType getType()
        {
            return TokenType.EMOJI;
        }

        /**
         * Returns the length of the token value.
         */
        @Override
        public int length(SocialChannel channel)
        {
            return 2;
        }

        /**
         * Returns the token value as an emoji.
         */
        @Override
        public String toString()
        {
            return ":"+getValue()+":";
        }
    }

    /**
     * Represents a token based on a property.
     */
    class PropertyToken extends Token
    {
        /**
         * Constructor that takes a value.
         */
        PropertyToken(String value)
        {
            super(value);
        }

        /**
         * Returns the type of the token.
         */
        @Override
        public TokenType getType()
        {
            return TokenType.PROPERTY;
        }

        /**
         * Returns the token value as an emoji.
         */
        @Override
        public String toString()
        {
            return "${"+getValue()+"}";
        }
    }

    /**
     * Represents a token based on a social hashtag.
     */
    class HashtagToken extends Token
    {
        /**
         * Constructor that takes a value.
         */
        HashtagToken(String value)
        {
            super(value);
        }

        /**
         * Returns the type of the token.
         */
        @Override
        public TokenType getType()
        {
            return TokenType.HASHTAG;
        }

        /**
         * Returns the length of the token value.
         */
        @Override
        public int length(SocialChannel channel)
        {
            return value.length()+1;
        }

        /**
         * Returns the token value as a hashtag.
         */
        @Override
        public String toString()
        {
            return "#"+getValue();
        }

        /**
         * Returns the token markup.
         */
        @Override
        String getMarkup(SocialChannel channel)
        {
            String url = String.format(channel.getProvider().hashtagUrl(), getValue());
            return String.format("<a class=\"link\" target=\"_blank\" href=\"%s\">%s</a>", url, toString());
        }
    }

    /**
     * Represents a token based on a social handle.
     */
    class HandleToken extends Token
    {
        /**
         * Constructor that takes a value.
         */
        HandleToken(String value)
        {
            super(value);
        }

        /**
         * Returns the type of the token.
         */
        @Override
        public TokenType getType()
        {
            return TokenType.HANDLE;
        }

        /**
         * Returns the length of the token value.
         */
        @Override
        public int length(SocialChannel channel)
        {
            return value.length()+1;
        }

        /**
         * Returns the token value as a handle.
         */
        @Override
        public String toString()
        {
            return "@"+getValue();
        }

        /**
         * Returns the token markup.
         */
        @Override
        String getMarkup(SocialChannel channel)
        {
            String url = String.format(channel.getProvider().handleUrl(), getValue());
            return String.format("<a class=\"link\" target=\"_blank\" href=\"%s\">%s</a>", url, toString());
        }
    }

    /**
     * Represents a token based on a URL.
     */
    class UrlToken extends Token
    {
        /**
         * Constructor that takes a value.
         */
        UrlToken(String value)
        {
            super(value);
        }

        /**
         * Returns the type of the token.
         */
        @Override
        public TokenType getType()
        {
            return TokenType.URL;
        }

        /**
         * Returns the length of the token value.
         */
        @Override
        public int length(SocialChannel channel)
        {
            if(channel != null && channel.getProvider().urlLength() != -1)
                return channel.getProvider().urlLength();
            else
                return super.length(channel);
        }

        /**
         * Returns the token markup.
         */
        @Override
        String getMarkup(SocialChannel channel) 
        {
            return String.format("<a class=\"link\" target=\"_blank\" href=\"%s\">%s</a>", toString(), toString());
        }
    }

    /**
     * Creates the messages from the tokens.
     */
    public void createMessages(SocialChannel channel)
    {
        StringBuilder message = new StringBuilder();
        StringBuilder markup = new StringBuilder();

        int length = 0;
        int hashtagCount = 0;

        for(Token token : tokens)
        {
            if(token instanceof StringToken)
            {
                message.append(token.toString());
                length += token.length(channel);
                if(channel != null)
                    markup.append(token.toString());
            }
            else if(token instanceof PropertyToken)
            {
                String str = getProperties().getOrDefault(token.getValue(), token.toString());
                if(str == null)
                    str = token.toString();
                message.append(str);
                length += str.length();
                if(channel != null)
                    markup.append(str);
            }
            else if(token instanceof EmojiToken)
            {
                String emoji = EmojiParser.parseToUnicode(token.toString());
                message.append(emoji);
                length += token.length(channel);
                if(channel != null)
                    markup.append(emoji);
            }
            else if(token instanceof HandleToken)
            {
                // Exclude @mentions for LinkedIn
                if(channel == null
                    || channel.getProvider() != SocialProvider.LINKEDIN)
                {
                    message.append(token.toString());
                    length += token.length(channel);
                    if(channel != null)
                        markup.append(token.getMarkup(channel));
                }
            }
            else // URLs, Hashtags
            {
                message.append(token.toString());
                length += token.length(channel);
                if(channel != null)
                    markup.append(token.getMarkup(channel));
                if(token instanceof HashtagToken)
                    ++hashtagCount;
            }
        }

        this.message = adjust(channel, message.toString());
        this.markupMessage = adjust(channel, markup.toString());
        this.messageLength = length;
        this.hashtagCount = hashtagCount;

        // Use the official parser to get the tweet length
        if(channel != null && channel.getProvider() == SocialProvider.TWITTER)
            this.messageLength = TwitterTextParser.parseTweet(this.message).weightedLength;
    }

    /**
     * Adjust the given message depending on the channel.
     */
    private String adjust(SocialChannel channel, String message)
    {
        String ret = message;

        if(channel != null && ret != null)
        {
            if(channel.getProvider() == SocialProvider.LINKEDIN)
            {
                // Fix the message text for "input string format is invalid" errors
                ret = ret.replaceAll("\\[", "(").replaceAll("\\]", ")"); // replace square brackets
                ret = ret.replaceAll("C#", "C Sharp"); // replace C#
                ret = ret.replaceAll("_", " "); // replace underscores
            }
        }

        return ret;
    }

    /**
     * Returns the plain message.
     */
    public String getMessage()
    {
        if(message == null)
            createMessages(null);
        return message;
    }

    /**
     * Returns the plain message for the given channel.
     */
    public String getMessage(SocialChannel channel)
    {
        createMessages(channel);
        return message;
    }

    /**
     * Returns the length of the plain message.
     */
    public int getMessageLength()
    {
        return messageLength;
    }

    /**
     * Returns the number of hashtags in the message.
     */
    public int getHashtagCount()
    {
        return hashtagCount;
    }

    /**
     * Returns the marked up message.
     */
    public String getMarkupMessage()
    {
        return markupMessage;
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
        private SocialPostHandler handler = new SocialPostHandler();

        /**
         * Sets the main organisation hashtag for the handler.
         * @param hashtag The hashtag for the handler
         * @return This object
         */
        public Builder withHashtag(String hashtag)
        {
            handler.setHashtag(hashtag);
            return this;
        }

        /**
         * Sets the hashtag list for the handler.
         * @param hashtags The hashtag list for the handler
         * @return This object
         */
        public Builder withHashtags(String hashtags)
        {
            handler.setHashtags(hashtags);
            return this;
        }

        /**
         * Sets the hashtag list for the handler.
         * @param hashtags The hashtag list for the handler
         * @return This object
         */
        public Builder withHashtags(List<Hashtag> hashtags)
        {
            handler.addHashtags(hashtags);
            return this;
        }

        /**
         * Sets the properties for the handler.
         * @param properties The properties for the handler
         * @return This object
         */
        public Builder withProperties(SocialPostProperties properties)
        {
            handler.setProperties(properties);
            return this;
        }

        /**
         * Returns the configured handler instance
         * @return The handler instance
         */
        public SocialPostHandler build()
        {
            return handler;
        }
    }
}