/*
 * Copyright 2025 Gerald Curley
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

package com.opsmatters.media.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Provides statistics about a HTML or text document.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Counter
{
    private long length = -1L;
    private long wordCount = -1L;
    private long linkCount = -1L;
    private long imageCount = -1L;

    /**
     * Default constructor.
     */
    public Counter()
    {
    }

    /**
     * Returns the text length.
     */
    public long getLength()
    {
        return length;
    }

    /**
     * Returns the word count.
     */
    public long getWordCount()
    {
        return wordCount;
    }

    /**
     * Returns the link count.
     */
    public long getLinkCount()
    {
        return linkCount;
    }

    /**
     * Returns the image count.
     */
    public long getImageCount()
    {
        return imageCount;
    }

    /**
     * Parses a HTML document.
     */
    public void parseHtml(String html)
    {
        linkCount = imageCount = 0;

        if(html != null)
        {
            if(html.startsWith("<"))
            {
                Document doc = Jsoup.parse(html);
                String text = doc.body().text();
                linkCount = doc.select("a").size();
                imageCount = doc.select("img").size();
                parseText(text);
            }
            else // html is actually plain text
            {
                parseText(html);
            }
        }
    }

    /**
     * Parses a text document.
     */
    private void parseText(String text)
    {
        if(text != null)
        {
            length = text.length();
            wordCount = text.split("[\\pP\\s&&[^']]+").length;
        }
    }
}