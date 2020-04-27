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

import java.io.StringReader;
import org.apache.commons.io.LineIterator;
import org.apache.commons.text.diff.StringsComparator;

/**
 * Compares two text documents to produce a HTML document that hightlights all differences.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TextDiff
{
    private static final float COMMONALITY = 0.8f;

    public static String compare(String text1, String text2)
    {
        StringReader reader1 = new StringReader(text1);
        LineIterator iterator1 = new LineIterator(reader1);
        StringReader reader2 = new StringReader(text2);
        LineIterator iterator2 = new LineIterator(reader2);

        TextCommandVisitor textVisitor = new TextCommandVisitor();

        try
        {
            while (iterator1.hasNext() || iterator2.hasNext())
            {
                // Handle different numbers of lines by adding empty strings
                String left = (iterator1.hasNext() ? iterator1.nextLine() : "") + "\n";
                String right = (iterator2.hasNext() ? iterator2.nextLine() : "") + "\n";

                StringsComparator comparator = new StringsComparator(left, right);
                if(comparator.getScript().getLCSLength() > (Integer.max(left.length(), right.length())*COMMONALITY))
                {
                    // Merge both lines if they have at least 40% commonality
                    comparator.getScript().visit(textVisitor);
                }
                else
                {
                    // Otherwise show the two lines separately
                    StringsComparator leftComparator = new StringsComparator(left, "\n");
                    leftComparator.getScript().visit(textVisitor);
                    StringsComparator rightComparator = new StringsComparator("\n", right);
                    rightComparator.getScript().visit(textVisitor);
                }
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
}