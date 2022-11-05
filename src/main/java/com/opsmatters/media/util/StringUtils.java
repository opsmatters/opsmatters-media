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

package com.opsmatters.media.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.codec.binary.Base64;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * A set of utility methods to perform miscellaneous tasks related to strings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class StringUtils
{
    private static final Logger logger = Logger.getLogger(StringUtils.class.getName());

    public static final String YEAR   = "y";
    public static final String MONTH  = "M";
    public static final String WEEK   = "w";
    public static final String DAY    = "d";
    public static final String HOUR   = "h";
    public static final String MINUTE = "m";
    public static final String SECOND = "s";

    public static final String EMPTY = "<p></p>";

    // Pattern to detect URLs in strings
    private static final String URL_REGEX = "\\(?\\b(https?://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
    private static final Pattern urlPattern = Pattern.compile(URL_REGEX);

    // Pattern to detect properties in strings
    private static final String PROPERTY_REGEX = "\\$\\{\\w+\\}";
    private static final Pattern propertyPattern = Pattern.compile(PROPERTY_REGEX);

    // Pattern to detect emojis in strings
    private static final String EMOJI_REGEX = "\\:[\\w\\+]+\\:";
    private static final Pattern emojiPattern = Pattern.compile(EMOJI_REGEX);

    /**
     * The default depth for stack traces.
     */
    public static final int DEFAULT_DEPTH    = 20;

    static
    {
    }

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private StringUtils()
    {
    }    

    /**
     * Serializes the given exception as a stack trace string.
     * @param ex The exception to be serialized
     * @return The serialized exception
     */
    static public String serialize(Throwable ex)
    {
        return serialize(ex, DEFAULT_DEPTH, 0);
    }

    /**
     * Serializes the given exception as a stack trace string.
     * @param ex The exception to be serialized
     * @param depth The maximum depth of the stack trace returned
     * @return The serialized exception
     */
    static public String serialize(Throwable ex, int depth)
    {
        return serialize(ex, depth, 0);
    }

    /**
     * Serializes the given exception as a stack trace string.
     * @param ex The exception to be serialized
     * @param depth The maximum depth of the stack trace returned
     * @param level The current level of nesting of the exception
     * @return The serialized exception
     */
    static private String serialize(Throwable ex, int depth, int level)
    {
        StringBuffer buff = new StringBuffer();
        String str = ex.toString();

        // Split the first line if it's too long
        int pos = str.indexOf(":");
        if(str.length() < 80 || pos == -1)
        {
            buff.append(str);
        }
        else
        {
            String str1 = str.substring(0, pos);
            String str2 = str.substring(pos+2);
            if(str2.indexOf(str1) == -1)
            {
                buff.append(str1);
                buff.append(": \n\t");
            }
            buff.append(str2);
        }

        if(depth > 0)
        {
            StackTraceElement[] elements = ex.getStackTrace();
            for(int i = 0; i < elements.length; i++)
            {
                buff.append("\n\tat ");
                buff.append(elements[i]);
                if(i == (depth-1) && elements.length > depth)
                {
                    buff.append("\n\t... "+(elements.length-depth)+" more ...");
                    i = elements.length;
                }
            }
        }

        if(ex.getCause() != null && level < 3)
        {
            buff.append("\nCaused by: ");
            buff.append(serialize(ex.getCause(), depth, ++level));
        }

        return buff.toString();
    }

    /**
     * Returns <CODE>true</CODE> if the given string matches the given regular expression.
     * @param str The string against which the expression is to be matched
     * @param expr The regular expression to match with the input string
     * @return <CODE>true</CODE> if the given string matches the given regular expression
     */
    public static boolean getMatchResult(String str, String expr)
    {
        Pattern pattern = Pattern.compile(expr, Pattern.DOTALL);
        return pattern.matcher(str).matches();
    }

    /**
     * Returns the given array serialized as a string.
     * @param objs The array to be serialized
     * @return The given array serialized as a string
     */
    public static String serialize(Object[] objs)
    {
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < objs.length; i++)
        {
            if(objs[i] != null)
            {
                buff.append(objs[i].toString());
                if(i != objs.length-1)
                    buff.append(",");
            }
        }
        return buff.toString();
    }

    /**
     * Returns the given string after if it has been encoded.
     * @param str The string to be encoded
     * @return The encoded string
     */
    public static String encode(String str)
    {
        String ret = str;

        try
        {
            // Obfuscate the string
            if(ret != null)
                ret = new String(Base64.encodeBase64(ret.getBytes()));
        }
        catch(NoClassDefFoundError e)
        {
            System.out.println("WARNING: unable to encode: "
                +e.getClass().getName()+": "+e.getMessage());
        }

        return ret;
    }

    /**
     * Returns the given byte array after if it has been encoded.
     * @param bytes The byte array to be encoded
     * @return The encoded string
     */
    public static String encodeBytes(byte[] bytes)
    {
        String ret = null;

        try
        {
            // Obfuscate the string
            if(bytes != null)
                ret = new String(Base64.encodeBase64(bytes));
        }
        catch(NoClassDefFoundError e)
        {
            ret = new String(bytes);
            System.out.println("WARNING: unable to encode: "
                +e.getClass().getName()+": "+e.getMessage());
        }

        return ret;
    }

    /**
     * Returns the given string after if it has been decoded.
     * @param str The string to be decoded
     * @return The decoded string
     */
    public static String decode(String str)
    {
        String ret = str;

        try
        {
            // De-obfuscate the string
            if(ret != null)
                ret = new String(Base64.decodeBase64(ret.getBytes()));
        }
        catch(NoClassDefFoundError e)
        {
            System.out.println("WARNING: unable to decode: "
                +e.getClass().getName()+": "+e.getMessage());
        }

        return ret;
    }

    /**
     * Returns the given byte array after if it has been decoded.
     * @param str The string to be decoded
     * @return The decoded bytes
     */
    public static byte[] decodeBytes(String str)
    {
        byte[] ret = null;

        try
        {
            // De-obfuscate the string
            if(str != null)
                ret = Base64.decodeBase64(str.getBytes());
        }
        catch(NoClassDefFoundError e)
        {
            ret = str.getBytes();
            System.out.println("WARNING: unable to decode: "
                +e.getClass().getName()+": "+e.getMessage());
        }

        return ret;
    }

    /**
     * Returns the given string truncated at a word break before the given number of characters.
     * @param str The string to be truncated
     * @param count The maximum length of the truncated string
     * @return The truncated string
     */
    public static String truncate(String str, int count)
    {
        if(count < 0 || str.length() <= count)
            return str;

        int pos = count;
        for(int i = count; i >= 0 && !Character.isWhitespace(str.charAt(i)); i--, pos--);
        return str.substring(0, pos)+"...";
    }

    /**
     * Returns the number of occurences of the given character in the given string.
     * @param c The character to look for occurrences of
     * @param s The string to search
     * @return The number of occurences
     */
    public static int getOccurrenceCount(char c, String s) 
    {
        int ret = 0;

        for(int i = 0; i < s.length(); i++)
        {
            if(s.charAt(i) == c)
                ++ret;
        }

        return ret;
    }

    /**
     * Returns the number of occurrences of the substring in the given string.
     * @param expr The string to look for occurrences of
     * @param str The string to search
     * @return The number of occurences
     */
    public static int getOccurrenceCount(String expr, String str)
    {
        int ret = 0;
        Pattern p = Pattern.compile(expr);
        Matcher m = p.matcher(str);
        while(m.find()) 
            ++ret;
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given string matches the given regular expression.
     * @param str The string against which the expression is to be matched
     * @param expr The regular expression to match with the input string
     * @param whole Indicates that a whole word match is required
     * @return An object giving the results of the search (or null if no match found)
     */
    public static Matcher getWildcardMatcher(String str, String expr, boolean whole)
    {
        expr = expr.replaceAll("\\?",".?");
        expr = expr.replaceAll("\\*",".*?");
        if(whole)
            expr = "^"+expr+"$";
        Pattern pattern = Pattern.compile(expr/*, Pattern.DOTALL*/);
        return pattern.matcher(str);
    }

    /**
     * Returns <CODE>true</CODE> if the given string matches the given regular expression.
     * @param str The string against which the expression is to be matched
     * @param expr The regular expression to match with the input string
     * @return An object giving the results of the search (or null if no match found)
     */
    public static Matcher getWildcardMatcher(String str, String expr)
    {
        return getWildcardMatcher(str, expr, false);
    }

    /**
     * Returns <CODE>true</CODE> if the given string matches the given regular expression.
     * @param str The string against which the expression is to be matched
     * @param expr The regular expression to match with the input string
     * @param whole Indicates that a whole word match is required
     * @return <CODE>true</CODE> if a match was found
     */
    public static boolean isWildcardMatch(String str, String expr, boolean whole)
    {
        return getWildcardMatcher(str, expr, whole).find();
    }

    /**
     * Returns <CODE>true</CODE> if the given string matches the given regular expression.
     * @param str The string against which the expression is to be matched
     * @param expr The regular expression to match with the input string
     * @return <CODE>true</CODE> if a match was found
     */
    public static boolean isWildcardMatch(String str, String expr)
    {
        return isWildcardMatch(str, expr, false);
    }

    /**
     * Checks that a string buffer ends up with a given string.
     * @param buffer The buffer to perform the check on
     * @param suffix The suffix
     * @return <code>true</code> if the character sequence represented by the
     * argument is a suffix of the character sequence represented by
     * the StringBuffer object; <code>false</code> otherwise. Note that the
     * result will be <code>true</code> if the argument is the empty string.
     */
    public static boolean endsWith(StringBuffer buffer, String suffix)
    {
        if (suffix.length() > buffer.length()) 
            return false;
        int endIndex = suffix.length() - 1;
        int bufferIndex = buffer.length() - 1;
        while (endIndex >= 0)
        {
            if (buffer.charAt(bufferIndex) != suffix.charAt(endIndex))
                return false;
            bufferIndex--;
            endIndex--;
        }
        return true;
    }

    /**
     * Converts the given string with CR and LF character correctly formatted.
     * @param str The string to be converted
     * @return The converted string
     */
    public static String toReadableForm(String str)
    {
        String ret = str;
        if(str != null && str.length() > 0 
            && str.indexOf("\n") != -1 
            && str.indexOf("\r") == -1)
        {
            str.replaceAll("\n", "\r\n");
        }

        return ret;
    }

    /**
     * Normalises the given string, replacing certain special characters with their HTML escape sequences.
     * <UL>
     * <LI> <B><CODE>&lt;</CODE></B> becomes <B><CODE>&amp;lt;</CODE></B>
     * <LI> <B><CODE>&gt;</CODE></B> becomes <B><CODE>&amp;gt;</CODE></B>
     * <LI> <B><CODE>&amp;</CODE></B> becomes <B><CODE>&amp;amp;</CODE></B>
     * <LI> <B><CODE>&quot;</CODE></B> becomes <B><CODE>&amp;quot;</CODE></B>
     * <LI> <B><CODE>'</CODE></B> becomes <B><CODE>&amp;apos;</CODE></B>
     * </UL>
     * @param s The string to be normalised
     * @return The normalised string
     */
    static public String normalise(String s) 
    {
        StringBuffer str = new StringBuffer();

        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) 
        {
            char ch = s.charAt(i);
            switch (ch) 
            {
                case '<': 
                {
                    str.append("&lt;");
                    break;
                }
                case '>': 
                {
                    str.append("&gt;");
                    break;
                }
                case '&': 
                {
                    str.append("&amp;");
                    break;
                }
                case '"': 
                {
                    str.append("&quot;");
                    break;
                }
                case '\'': 
                {
                    str.append("&apos;");
                    break;
                }
                default: 
                {
                    str.append(ch);
                }
            }
        }
        return str.toString();    
    }

    /**
     * Encode the special characters in the string to its URL encoded representation.
     * @param str The string to encode
     * @return The encoded string
     */
    public static String urlEncode(String str)
    {
        String ret = str;

        try 
        {
            ret = URLEncoder.encode(str, "UTF-8");
        } 
        catch (UnsupportedEncodingException e) 
        {
            logger.severe("Failed to encode value: "+str);
        }

        return ret;
    }

    /**
     * Returns the given string with all spaces removed.
     * @param s The string to have spaces removed
     * @return The given string with all spaces removed
     */
    public static String stripSpaces(String s)
    {
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(c != ' ')
                buff.append(c);
        }
        return buff.toString();
    }

    /**
     * Returns the given string with all non-ASCII characters removed.
     * @param s The string to have all non-ASCII characters removed
     * @return The given string with all non-ASCII characters removed
     */
    public static String stripNonAsciiPrintable(String s)
    {
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            int c = (int)s.charAt(i);
            if(c >= 32 && c < 127)
                buff.append((char)c);
        }
        return buff.toString();
    }

    /**
     * Returns the given string with all non-ASCII characters replaced by their HTML escape sequences.
     * @param s The string to have all non-ASCII characters replaced
     * @param isHtml <CODE>true</CODE> if the string contains HTML markup
     * @param diacritics <CODE>true</CODE> if Extended  Diacritics (256-383) should be included
     * @return The given string with all non-ASCII characters replaced
     */
    public static String convertToAscii(String s, boolean isHtml, boolean diacritics)
    {
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            int c = (int)s.charAt(i);
            if((c >= 32 && c < 127) // basic ASCII
                || c == 10)         // LF
            {
                buff.append((char)c);
            }
            else if(c >= 128)
            {
                if(isHtml)
                {
                    buff.append(String.format("&#%04d;", c));
                }
                else // Not HTML
                {
                    if(c == 8211 || c == 8212) // Replace en/em dash
                        buff.append("-");
                    else if(c >= 8216 && c <= 8218) // Replace forward/backward quote
                        buff.append("'");
                    else if(c >= 8220 && c <= 8222) // Replace forward/backward double quote
                        buff.append("\"");
                    else if(c == 8230)         // Replace ellipsis
                        buff.append("...");
                    else if(c == 8364)         // Replace Euro
                        buff.append("Euro");
                    else if(c <= 255)          // ISO Latin 1 (128-255)
                        buff.append((char)c);
                    else if(c <= 383)          // ISO Latin 1 Extended  Diacritics (256-383)
                        buff.append(!diacritics ? '?' : (char)c);
                }
            }
        }

        return buff.toString();
    }

    /**
     * Returns the given string with all non-ASCII characters replaced by their HTML escape sequences.
     * @param s The string to have all non-ASCII characters replaced
     * @param isHtml <CODE>true</CODE> if the string contains HTML markup
     * @return The given string with all non-ASCII characters replaced
     */
    public static String convertToAscii(String s, boolean isHtml)
    {
        return convertToAscii(s, isHtml, true);
    }

    /**
     * Returns <CODE>true</CODE> if the the given string contains only numeric digits [0-9].
     * @param s The string to be checked
     * @return <CODE>true</CODE> if the the given string contains only numeric digits [0-9]
     */
    public static boolean isNumeric(String s)
    {
        boolean ret = true;
        for(int i = 0; i < s.length() && ret; i++)
        {
            char c = s.charAt(i);
            ret = (c >= '0' && c <= '9');
        }
        return ret;
    }

    /**
     * Prints the character codes for the given string.
     * @param s The string to be printed
     */
    public static void printCharacters(String s)
    {
        if(s != null)
        {
            logger.info("string length="+s.length());
            for(int i = 0; i < s.length(); i++)
            {
                char c = s.charAt(i);
                logger.info("char["+i+"]="+c+" ("+(int)c+")");
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the given string has leading and trailing double quotes.
     * @param s The string to be checked
     * @return <CODE>true</CODE> if the given string has leading and trailing double quotes
     */
    public static boolean hasDoubleQuotes(String s)
    {
        return s != null && s.startsWith("\"") && s.endsWith("\"") && s.length() > 2;
    }

    /**
     * Returns the given string with leading and trailing quotes removed.
     * @param s The string to have leading and trailing quotes removed
     * @return The given string with leading and trailing quotes removed
     */
    public static String stripDoubleQuotes(String s)
    {
        String ret = s;
        if(hasDoubleQuotes(s))
            ret = s.substring(1, s.length()-1);
        return ret;
    }

    private static final String STRICT_EMAIL_REGEX = "[A-Z0-9a-z.'_%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,7}";
    private static final String LAX_EMAIL_REGEX = ".+@.+\\.[A-Za-z]{2}[A-Za-z]*";

    private static Pattern strictEmailPattern = Pattern.compile(STRICT_EMAIL_REGEX);
    private static Pattern laxEmailPattern = Pattern.compile(LAX_EMAIL_REGEX);

    /**
     * Returns <CODE>true</CODE> if the given string is a valid email address.
     * @param email The email address to be checked
     * @param strict <CODE>true</CODE> if strict rules should be applied when checking the email address
     * @return <CODE>true</CODE> if the given string is a valid email address
     */
    public static boolean isValidEmailAddress(String email, boolean strict)
    {
        Pattern p = strict ? strictEmailPattern : laxEmailPattern;
        return p.matcher(email).matches();
    }

    /**
     * Returns <CODE>true</CODE> if the given string is a valid email address.
     * @param email The email address to be checked
     * @return <CODE>true</CODE> if the given string is a valid email address
     */
    public static boolean isValidEmailAddress(String email)
    {
        return isValidEmailAddress(email, true);
    }

    /**
     * Strips class name prefixes from the start of the given string.
     * @param str The string to be converted
     * @return The string with class name prefixes removed
     */
    public static String stripClassNames(String str)
    {
        String ret = str;
        if(ret != null)
        {
            while(ret.startsWith("java.security.PrivilegedActionException:")
                || ret.startsWith("com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl:")
                || ret.startsWith("javax.jms.JMSSecurityException:"))
            {
                ret = ret.substring(ret.indexOf(":")+1).trim();
            }
        }
        return ret;
    }

    /**
     * Returns the given hostname with the domain removed.
     * @param hostname The hostname to be converted
     * @return The hostname with the domain removed
     */
    public static String stripDomain(String hostname)
    {
        String ret = hostname;
        int pos = hostname.indexOf(".");
        if(pos != -1)
            ret = hostname.substring(0, pos);
        return ret;
    }

    /**
     * This method ensures that the output String has only
     * valid XML unicode characters as specified by the
     * XML 1.0 standard. For reference, please see
     * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
     * standard</a>. This method will return an empty
     * String if the input is null or empty.
     *
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */
    public static String stripNonValidXMLCharacters(String in)
    {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if(in == null || ("".equals(in))) return ""; // vacancy test.
        for(int i = 0; i < in.length(); i++)
        {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if ((current == 0x9)
                || (current == 0xA)
                || (current == 0xD)
                || ((current >= 0x20) && (current <= 0xD7FF))
                || ((current >= 0xE000) && (current <= 0xFFFD))
                || ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }

    /**
     * Returns the given error message with special characters removed or replaced.
     */
    public static String formatErrorMessage(String str)
    {
        String ret = str;
        if(ret != null)
        {
            ret = ret.replaceAll("\r|\n", ""); // Remove LFs
            ret = ret.replaceAll("\\\\", "/"); // Replace backslashes
        }
        return ret;
    }

    /**
     * Returns the last context of the given URL.
     */
    public static String getUrlContext(String url)
    {
        String ret = url;

        if(ret != null)
        {
            // Strip any trailing slash
            if(ret.endsWith("/"))
                ret = ret.substring(0, ret.length()-1);

            // Extract the portion after the last slash
            int pos = ret.lastIndexOf("/");
            if(pos != -1)
                ret = ret.substring(pos+1);

            // Remove any extension in the context eg ".html"
            pos = ret.indexOf(".");
            if(pos != -1)
                ret = ret.substring(0, pos);
        }

        return ret;
    }

    /**
     * Returns the UUID for the given string.
     */
    public static String getUUID(String str)
    {
        UUID uuid = null;
        if(str != null)
            uuid = UUID.nameUUIDFromBytes(str.getBytes());
        else
            uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Returns a list from the given string using the given delimiter.
     */
    public static List<String> toList(String str, String delimiter)
    {
        List<String> ret = new ArrayList<String>();
        if(str != null && str.length() > 0)
        {
            String[] tokens = str.split(delimiter);
            for(String token : tokens)
                ret.add(token.trim());
        }
        return ret;
    }

    /**
     * Returns a list from the given string using a comma as delimiter.
     */
    public static List<String> toList(String str)
    {
        return toList(str, ",");
    }

    /**
     * Returns a string from the given list using the given delimiter.
     */
    public static String fromList(List<String> tokens, String delimiter)
    {
        StringBuilder builder = new StringBuilder();
        for(String token : tokens)
        {
            if(builder.length() > 0)
                builder.append(delimiter);
            builder.append(token);
        }
        return builder.toString();
    }

    /**
     * Returns a string from the given list using a comma as delimiter.
     */
    public static String fromList(List<String> list)
    {
        return fromList(list, ",");
    }

    /**
     * Returns the prefix for the given property, substituting the current environment.
     */
    public static String getEnvProperty(String name, String env, String deflt)
    {
        return System.getProperty(String.format(name, env), deflt);
    }

    /**
     * Returns the prefix for the given property, substituting the current environment.
     */
    public static String getEnvProperty(String name, String env)
    {
        return getEnvProperty(name, env, null);
    }

    /**
     * Returns the first URL extracted from the given text.
     */
    public static String extractUrl(String text)
    {
        Matcher m = urlPattern.matcher(text);
        return m.find() ? m.group() : null;
    }

    static class Match
    {
        Match(int start, int end, String text)
        {
            this.start = start;
            this.end = end;
            this.text = text;
        }

        int start;
        int end;
        String text;
    }

    /**
     * Replaces all URLs in the given text with hyperlinks.
     */
    public static String replaceUrls(String text)
    {
        // Get a list of the URL matches
        List<Match> matches = new ArrayList<Match>();
        Matcher m = urlPattern.matcher(text);
        while(m.find())
            matches.add(new Match(m.start(), m.end(), m.group()));

        // Traverse in reverse order as we're changing the text
        Collections.reverse(matches);

        // Replace each URL match in the text with a hyperlink
        for(Match match : matches)
        {
            int pos = match.text.indexOf("?");
            String value = pos != -1 ? match.text.substring(0, pos) : match.text; // Remove query parameters
            String replacement = String.format("<a href=\"%s\" target=\"_blank\" rel=\"nofollow\">%s</a>", match.text, value);
            text = new StringBuilder(text).replace(match.start, match.end, replacement).toString();
        }

        return text;
    }

    /**
     * Returns the first property extracted from the given text.
     */
    public static String extractProperty(String text)
    {
        Matcher m = propertyPattern.matcher(text);
        return m.find() ? m.group() : null;
    }

    /**
     * Returns the first emoji alias extracted from the given text.
     */
    public static String extractEmoji(String text)
    {
        Matcher m = emojiPattern.matcher(text);
        return m.find() ? m.group() : null;
    }

    /**
     * Converts the given markdown to HTML.
     */
    public static String markdownToHtml(String text)
    {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(text);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * Returns the name normalized for a URL context.
     */
    public static String getNormalisedName(String name)
    {
        String ret = name;

        if(ret != null)
        {
            ret = ret.toLowerCase()
                .replaceAll(" ","-")
                .replaceAll("\\.","-")
                .replaceAll("&","");
        }

        return ret;
    }
}