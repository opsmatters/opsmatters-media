/*
 * Copyright 2021 Gerald Curley
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
package com.opsmatters.media.crawler;

import java.util.List;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import static com.opsmatters.media.crawler.ElementType.*;
import static com.opsmatters.media.crawler.ElementDisplay.*;

/**
 * Class representing a parser for an article body.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BodyParser
{
    private BodyElement current = null;
    private List<BodyElement> elements = new ArrayList<BodyElement>();
    private List<String> excludes;

    /**
     * Parses the given HTML text into a list of body elements.
     */
    public static List<BodyElement> parseHtml(String html, List<String> excludes)
    {
        return new BodyParser()
            .withExcludes(excludes)
            .parseHtml(html)
            .getElements();
    }

    /**
     * Adds the given list of exclude tags and classes to the parser.
     */
    public BodyParser withExcludes(List<String> excludes)
    {
        this.excludes = excludes;
        return this;
    }

    /**
     * Parses the given HTML text into a list of body elements.
     */
    public BodyParser parseHtml(String html)
    {
        current = null;
        parseNode(Jsoup.parse(html).getElementsByTag("body").get(0));
        return this;
    }

    private void parseNode(Node node)
    {
        String tag = node.nodeName();

        if(isExcluded(node))
            return;

        if(node.childNodeSize() == 0
            || (!tag.equals("body") && node.childNodeSize() == 1 && node.childNode(0) instanceof TextNode)
            || isLeafNode(tag))
        {
            boolean inline = true;
            String text = getText(node);
            if(text == null) // eg. a comment
                return;

            // If it's the first item or coming after a linefeed, it can't be inline
            if(elements.size() == 0 || text.startsWith("\n")
                || (current != null && current.getDisplay() == BLOCK))
            {
                inline = false;
            }

            String[] strings = text.split("\n", -1); // Include trailing empty strings

            // is this a strong paragraph?
            boolean strong = false;
            if(strings.length == 1)
                strong = isStrong(node);

            for(int i = 0; i < strings.length; i++)
            {
                String string = strings[i].trim();

                // Empty string is a linefeed
                if(string.length() == 0)
                {
                    // Is it at the end of a (non-empty) paragraph?
                    if(i == strings.length-1 && current != null)
                        current.setDisplay(BLOCK);
                    continue;
                }

                BodyElement element = new BodyElement(node.nodeName(), string, strong);

                // Start a new paragraph after a linefeed
                if(i > 0 || !inline)
                    element.setDisplay(INLINE_BLOCK);

                // If it's the first element or a block element
                if(current == null || element.isBlock())
                {
                    // The previous element was a title if it was a strong, block element
                    if(current != null && current.getType() == TEXT 
                        && current.isStrong() && current.isBlock()
                        && element.isBlock())
                    {
                        current.setType(TITLE);
                    }

                    // Add a new element
                    current = element;
                    elements.add(element);
                }
                else // Otherwise append the visible text to the current element
                {
                    current.append(element);
                }
            }
        }
        else // Recurse through the child nodes
        {
            for(Node child : node.childNodes())
                parseNode(child);
        }
    }

    private boolean isStrong(Node node)
    {
        String str = node.toString();
        if(str.indexOf("<p>") != -1)
            str = str.replaceAll("<p(?:.*?)>(.+)</p>", "$1");
        str = str.replaceAll("<br>", "");
        str = str.trim();
        return str.startsWith("<strong>") || str.startsWith("<b>");
    }

    private boolean isLeafNode(String tag)
    {
        return tag.equals("p") || tag.startsWith("h")
            || tag.equals("blockquote") || tag.equals("pre");
    }

    private String getText(Node node)
    {
        if(node instanceof Element)
            return ((Element)node).text();
        else if(node instanceof TextNode)
            return ((TextNode)node).getWholeText();
        return null;
    }

    public List<BodyElement> getElements()
    {
        return elements;
    }

    private boolean isExcluded(Node node)
    {
        boolean ret = false;
        if(node instanceof Element && excludes != null)
        {
            Element element = (Element)node;
            for(String exclude : excludes)
            {
                String tag = exclude;
                String className = "";
                int pos = exclude.indexOf(".");
                if(pos != -1)
                {
                    tag = exclude.substring(0, pos);
                    className = exclude.substring(pos+1);
                }

                ret = (tag.length() == 0 || tag.equals(element.tagName()))
                    && (className.length() == 0 || element.hasClass(className));

                if(ret)
                    break;
            }
        }

        return ret;
    }
}