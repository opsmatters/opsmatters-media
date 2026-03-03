/*
 * Copyright 2026 Gerald Curley
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

package com.opsmatters.media.model.order.contact;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a contact risk rating.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum RiskRating
{
    UNDEFINED("Undefined", 0, ""),
    VERY_LOW("Very Low", 1, "risk-very-low"),
    LOW("Low", 2, "risk-low"),
    MEDIUM("Medium", 3, ""),
    HIGH("High", 4, "risk-high"),
    VERY_HIGH("Very High", 5, "risk-very-high"),
    BAD("Bad"), // Pseudo status
    ALL("All"); // Pseudo status

    private String value;
    private int score;
    private String css;

    /**
     * Constructor that takes the rating value, score and css.
     * @param value The value for the rating
     * @param score The score for the rating
     * @param css The css for the rating
     */
    RiskRating(String value, int score, String css)
    {
        this.value = value;
        this.score = score;
        this.css = css;
    }

    /**
     * Constructor that takes the rating value.
     * @param value The value for the rating
     */
    RiskRating(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the rating.
     * @return The value of the rating.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the rating.
     * @return The value of the rating.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the score of the rating.
     * @return The score of the rating.
     */
    public int score()
    {
        return score;
    }

    /**
     * Returns the css of the rating.
     * @return The css of the rating.
     */
    public String css()
    {
        return css;
    }

    /**
     * Returns <CODE>true</CODE> if the score is high.
     * @return <CODE>true</CODE> if the score is high
     */
    public boolean bad()
    {
        return score >= HIGH.score();
    }

    /**
     * Returns <CODE>true</CODE> if the score is very high.
     * @return <CODE>true</CODE> if the score is very high
     */
    public boolean veryBad()
    {
        return score == VERY_HIGH.score();
    }

    /**
     * Returns <CODE>true</CODE> if the given rating is included by this rating.
     * @return <CODE>true</CODE> if the given rating is included by this rating
     */
    public boolean selects(RiskRating rating)
    {
        boolean ret = false;

        if(rating != null)
        {
            if(this == BAD)
                ret = rating.bad();
            else
                ret = this == rating;
        }

        return ret;
    }

    /**
     * Returns the rating for the given value.
     * @param value The rating value
     * @return The rating for the given value
     */
    public static RiskRating fromValue(String value)
    {
        RiskRating[] ratings = values();
        for(RiskRating rating : ratings)
        {
            if(rating.value().equals(value))
                return rating;
        }

        return null;
    }

    /**
     * Returns the rating for the given score.
     * @param score The rating value
     * @return The rating for the given score
     */
    public static RiskRating fromScore(int score)
    {
        RiskRating[] ratings = values();
        for(RiskRating rating : ratings)
        {
            if(rating.score() == score)
                return rating;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of ratings.
     * @param value The rating value
     * @return <CODE>true</CODE> if the given value is contained in the list of ratings
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }

    /**
     * Returns a list of the risk ratings.
     */
    public static List<RiskRating> toList(boolean extended)
    {
        List<RiskRating> ret = new ArrayList<RiskRating>();

        if(extended)
        {
            ret.add(BAD);
        }

        ret.add(UNDEFINED);
        ret.add(VERY_LOW);
        ret.add(LOW);
        ret.add(MEDIUM);
        ret.add(HIGH);
        ret.add(VERY_HIGH);

        return ret;
    }
}