package com.opsmatters.media.exception;

/**
 * This exception is thrown when an illegal content update is attempted.
 */

public class IllegalContentUpdateException extends Exception
{
    /**
     * Constructor that takes a message.
     */
    public IllegalContentUpdateException(String s)
    {
        super(s);
    }

    /**
     * Default constructor.
     */
    public IllegalContentUpdateException()
    {
        super("Unable to update content");
    }
}