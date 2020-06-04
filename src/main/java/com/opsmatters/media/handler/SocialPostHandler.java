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
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.PostTemplate;

/**
 * Class representing a handler used to prepare social media posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialPostHandler
{
    private List<Token> tokens = new ArrayList<Token>();
    private String hashtags = null;
    private Map<String,Hashtag> hashtagMap = new LinkedHashMap<String,Hashtag>();
    private Map<String,String> properties = new LinkedHashMap<String,String>();
    private SocialChannel channel;

    /**
     * Private constructor.
     */
    private SocialPostHandler()
    {
    }

    /**
     * Returns the social channel for the handler.
     */
    public SocialChannel getChannel()
    {
        return channel;
    }

    /**
     * Sets the social channel for the handler.
     */
    public void setChannel(SocialChannel channel)
    {
        this.channel = channel;
    }

    /**
     * Returns the properties for the handler.
     */
    public Map<String,String> getProperties()
    {
        return properties;
    }

    /**
     * Sets the properties for the handler.
     */
    public void setProperties(Map<String,String> properties)
    {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    /**
     * Represents a hashtag in the hashtag list.
     */
    class Hashtag
    {
        String key;
        String value;
        boolean optional = false;

        /**
         * Constructor that takes a hashtag with format: #value[?]
         */
        Hashtag(String str)
        {
            if(str.endsWith("?"))
            {
                optional = true;
                str = str.substring(0, str.length()-1); // Remove trailing "?"
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
    }

    /**
     * Sets the hashtag list.
     */
    private void setHashtags(String hashtags)
    {
        this.hashtags = hashtags;
    }

    /**
     * Populates the hashtag map from the hashtag list.
     */
    private void parseHashtags()
    {
        hashtagMap.clear();
        if(hashtags != null && hashtags.length() > 0)
        {
            for(String str : hashtags.split(" "))
            {
                if(str.startsWith("#") && str.length() > 2)
                {
                    Hashtag hashtag = new Hashtag(str);
                    hashtagMap.put(hashtag.getKey(), hashtag);
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
        Collection<Hashtag> hashtags = hashtagMap.values();
        for(Hashtag hashtag : hashtags)
        {
            if(!hashtag.isOptional())
            {
                if(builder.length() > 0)
                    builder.append(" ");
                builder.append(hashtag);
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
            properties.remove(PostTemplate.HASHTAGS);
            parseTokens(new StringSubstitutor(properties).replace(message));

            // Put back the amended hashtag list and process the message again
            //   to resolve the HASHTAGS property with the new value
            if(hashtags != null)
                properties.put(PostTemplate.HASHTAGS, getHashtags());
            parseTokens(new StringSubstitutor(properties).replace(createMessage(false)));
        }
    }

    /**
     * Returns the plain message constructed from the tokens.
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

            if(c == '#' || c == '@') // Start of a hashtag or handle
            {
                addToken(builder.toString());
                builder.setLength(0);
                inWord = true;
            }
            else if(message.substring(i).startsWith("http")) // Start of URL
            {
                String url = StringUtils.extractUrl(message.substring(i));
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
        for(Token token : tokens)
        {
            if(hashtagMap.containsKey(token.getKey()))
            {
                hashtagMap.remove(token.getKey());
                if(token.getType() == TokenType.STRING)
                    tokens.set(tokens.indexOf(token), new HashtagToken(token.getValue()));
            }
        }
    }

    /**
     * Returns the plain message constructed from the tokens.
     */
    public String createMessage(boolean markup)
    {
        StringBuilder builder = new StringBuilder();
        for(Token token : tokens)
            builder.append(markup ? token.getMarkup() : token.toString());

        // Process any emojis in the message
        String message = builder.toString();
        if(markup)
            message = EmojiParser.parseToHtmlDecimal(message);
        else
            message = EmojiParser.parseToUnicode(message);

        return new StringSubstitutor(properties).replace(message);
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
            else if(token.startsWith("http") && token.length() > 7)
                tokens.add(new UrlToken(token));
            else
                tokens.add(new StringToken(token));
        }
    }

    /**
     * The types of tokens that can be parsed.
     */
    enum TokenType
    {
        STRING,
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
        String getMarkup() 
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
        String getMarkup() 
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
        String getMarkup() 
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
         * Returns the token markup.
         */
        @Override
        String getMarkup() 
        {
            return String.format("<a class=\"link\" target=\"_blank\" href=\"%s\">%s</a>", toString(), toString());
        }
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
         * Sets the social channel for the handler.
         * @param channel The social channel for the handler
         * @return This object
         */
        public Builder withChannel(SocialChannel channel)
        {
            handler.setChannel(channel);
            return this;
        }

        /**
         * Sets the properties for the handler.
         * @param properties The properties for the handler
         * @return This object
         */
        public Builder withProperties(Map<String,String> properties)
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