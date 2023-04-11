package com.opsmatters.media.model;

/**
 * Represents user messages.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum UserMessage
{
    UNKNOWN("Unknown"),
    UPDATING("Updating..."),
    UNAVAILABLE("Unavailable"),
    CHANGED("Changed"),
    NOT_CHANGED("Not Changed");

    private String value;

    /**
     * Constructor that takes the message value.
     * @param value The value for the message
     */
    UserMessage(String value)
    {
        this.value = value;
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
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}