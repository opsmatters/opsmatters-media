package com.opsmatters.media.model;

/**
 * Represents user messages.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum UserMessage
{
    NONE("", true),
    UNKNOWN("Unknown", false),
    UPDATING("Updating...", true),
    UNAVAILABLE("Unavailable", false),
    CHANGED("Changed", false),
    NOT_CHANGED("Not Changed", false),
    CHANGE("Change", false),
    ALERT("Alert", false),
    REVIEW("Review", false),
    ARCHIVED("Archived", false),
    SYNC("Sync", false),
    WEBCACHE("Webcache", false),
    ERROR("Error", false);

    private String value;
    private boolean temporary;

    /**
     * Constructor that takes the message value.
     * @param value The value for the message
     * @param temporary The message is temporary on the way to another message
     */
    UserMessage(String value, boolean temporary)
    {
        this.value = value;
        this.temporary = temporary;
    }

    /**
     * Returns the value of the message.
     * @return The value of the message.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the message.
     * @return The value of the message.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns <CODE>true</CODE> if the message is temporary.
     * @return <CODE>true</CODE> if the message is temporary.
     */
    public boolean temporary()
    {
        return temporary;
    }

    /**
     * Returns the message for the given value.
     * @param value The message value
     * @return The message for the given value
     */
    public static UserMessage fromValue(String value)
    {
        UserMessage[] messages = values();
        for(UserMessage message : messages)
        {
            if(message.value().equals(value))
                return message;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}