package com.opsmatters.media.model.order;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the frequency of a debit.
 */
public enum Frequency
{
    NONE("None", 0),
    MONTHLY("Monthly", 1),
    QUARTERLY("Quarterly", 3),
    ANNUALLY("Annually", 12);

    private String value;
    private int months;

    /**
     * Constructor that takes the frequency value and number of months.
     * @param value The value for the frequency
     * @param months The number of months for the frequency
     */
    Frequency(String value, int months)
    {
        this.value = value;
        this.months = months;
    }

    /**
     * Returns the value of the frequency.
     * @return The value of the frequency.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the frequency.
     * @return The value of the frequency.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the number of months for the frequency.
     * @return The number of months for the frequency.
     */
    public int months()
    {
        return months;
    }

    /**
     * Returns the frequency for the given value.
     * @param value The frequency value
     * @return The frequency for the given value
     */
    public static Frequency fromValue(String value)
    {
        for(Frequency frequency : values())
        {
            if(frequency.value().equals(value))
                return frequency;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of frequencies.
     * @param value The frequency value
     * @return <CODE>true</CODE> if the given value is contained in the list of frequencies
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }

    /**
     * Returns a list of the frequencies.
     */
    public static List<Frequency> toList()
    {
        List<Frequency> ret = new ArrayList<Frequency>();

        for(Frequency frequency : values())
            ret.add(frequency);

        return ret;
    }
}