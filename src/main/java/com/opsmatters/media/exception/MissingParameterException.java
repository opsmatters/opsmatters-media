package com.opsmatters.media.exception;

/**
 * This exception is thrown when a missing mandatory parameter is found for an operation.
 */

public class MissingParameterException extends Exception
{
    /**
     * Constructor that takes a message.
     */
    public MissingParameterException(String s)
    {
        super(s);
    }

    /**
     * Default constructor.
     */
    public MissingParameterException()
    {
        super("Missing Parameter");
    }
}