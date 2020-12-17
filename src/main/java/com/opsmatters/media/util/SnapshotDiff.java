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
package com.opsmatters.media.util;

import java.util.Map;
import java.util.HashMap;
import java.io.StringReader;
import org.apache.commons.io.LineIterator;
import org.apache.commons.text.diff.StringsComparator;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.model.content.VideoProvider;

/**
 * Compares two snapshot items to produce a HTML document that hightlights all differences.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SnapshotDiff
{
    private static final float COMMONALITY = 0.8f;

    private static Map<String,String> NAMES = new HashMap<String,String>();

    static
    {
        // The display names for the keys
        NAMES.put(Fields.TITLE, "Title");
        NAMES.put(Fields.PUBLISHED_DATE, "Date");
        NAMES.put(Fields.START_DATE, "Date");
        NAMES.put(Fields.URL, "URL");
        NAMES.put(Fields.VIDEO_ID, "Video ID");
    }

    public static String compare(String text1, String text2)
    {
        StringReader reader1 = new StringReader(text1);
        LineIterator iterator1 = new LineIterator(reader1);
        StringReader reader2 = new StringReader(text2);
        LineIterator iterator2 = new LineIterator(reader2);

        TextCommandVisitor textVisitor = new TextCommandVisitor();

        try
        {
            while(iterator1.hasNext() || iterator2.hasNext())
            {
                // Handle different numbers of lines by adding empty strings
                String left = (iterator1.hasNext() ? iterator1.nextLine() : "") + "\n";
                String right = (iterator2.hasNext() ? iterator2.nextLine() : "") + "\n";

                // Markup only if the row is not blank
                boolean empty = left.length() == 1 && right.length() == 1;
                if(empty)
                    textVisitor.append(left); // blank line
                else
                    textVisitor.append("<div class=\"comparison\">");

                // Parse the left hand string
                String leftProperty = getProperty(left, "=");
                String leftUrl = null;
                if(leftProperty != null)
                {
                    left = left.substring(leftProperty.length()+1);
                    if(leftProperty.equals(Fields.URL))
                        leftUrl = left;
                    else if(leftProperty.equals(Fields.VIDEO_ID))
                        leftUrl = String.format(VideoProvider.YOUTUBE.videoUrl(), left);
                    leftProperty = NAMES.get(leftProperty);
                }

                // Parse the right hand string
                String rightProperty = getProperty(right, "=");
                String rightUrl = null;
                if(rightProperty != null)
                {
                    right = right.substring(rightProperty.length()+1);
                    if(rightProperty.equals(Fields.URL))
                        rightUrl = right;
                    else if(rightProperty.equals(Fields.VIDEO_ID))
                        rightUrl = String.format(VideoProvider.YOUTUBE.videoUrl(), right);
                    rightProperty = NAMES.get(rightProperty);
                }

                StringsComparator comparator = new StringsComparator(left, right);
                if(comparator.getScript().getLCSLength() > (Integer.max(left.length(), right.length())*COMMONALITY))
                {
                    // Merge both lines if they have at least 40% commonality
                    appendLine(textVisitor, rightProperty, rightUrl, comparator);
                }
                else
                {
                    // Otherwise show the two lines separately
                    StringsComparator leftComparator = new StringsComparator(left, "\n");
                    appendLine(textVisitor, leftProperty, leftUrl, leftComparator);
                    StringsComparator rightComparator = new StringsComparator("\n", right);
                    appendLine(textVisitor, rightProperty != null && !rightProperty.equals(leftProperty) ? rightProperty : "", rightUrl, rightComparator);
                }

                if(!empty)
                    textVisitor.append("</div>");
            }
        }
        finally
        {
            iterator1.close();
            reader1.close();
            iterator2.close();
            reader2.close();
        }

        return textVisitor.getText();
    }

    /**
     * Returns the property name from the given string.
     */
    private static String getProperty(String str, String separator)
    {
        String ret = null;
        int pos = str.indexOf(separator);
        if(pos != -1)
            ret = str.substring(0, pos);
        return ret;
    }

    /**
     * Appends the given property key to the visitor.
     */
    private static void appendKey(TextCommandVisitor textVisitor, String key)
    {
        if(key != null)
        {
            textVisitor.append("<div class=\"key\">");
            textVisitor.append(key);
            textVisitor.append("</div>");
        }
    }

    /**
     * Appends the given URL to the visitor.
     */
    private static void appendUrl(TextCommandVisitor textVisitor, String url, StringsComparator comparator)
    {
        if(url != null)
        {
            textVisitor.append("<div class=\"value\">");
            textVisitor.append(String.format("<a href=\"%s\" target=\"_blank\">", url.trim()));
            comparator.getScript().visit(textVisitor);
            textVisitor.append("</a>");
            textVisitor.append("</div>");
        }
    }

    /**
     * Appends the given string to the visitor.
     */
    private static void appendString(TextCommandVisitor textVisitor, StringsComparator comparator)
    {
        textVisitor.append("<div class=\"value\">");
        comparator.getScript().visit(textVisitor);
        textVisitor.append("</div>");
    }

    /**
     * Appends the given line to the visitor.
     */
    private static void appendLine(TextCommandVisitor textVisitor, String key, String url, StringsComparator comparator)
    {
        if(key != null)
        {
            textVisitor.append("<div class=\"line\">");
            appendKey(textVisitor, key);
            if(url != null)
                appendUrl(textVisitor, url, comparator);
            else
                appendString(textVisitor, comparator);
            textVisitor.append("</div>");
        }
    }

    /**
     * Returns the % difference between the given strings.
     */
    public static int getDifferencePercent(String before, String after)
    {
        StringsComparator comparator = new StringsComparator(before, after);
        int len = Integer.max(before.length(), after.length());
        int lcs = comparator.getScript().getLCSLength();
        return (int)((1-(lcs/(float)len))*100);
    }
}