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
package com.opsmatters.media.cache.content.util;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.drupal.TaxonomyTerm;

import static com.opsmatters.media.cache.content.util.TaxonomyTerms.TaxonomyStatus.*;

/**
 * Class representing a handler for taxonomy terms.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TaxonomyTerms
{
    private static final Logger logger = Logger.getLogger(TaxonomyTerms.class.getName());

    private static Map<String,TaxonomyStatus> statuses = new HashMap<String,TaxonomyStatus>();
    private static Map<String,List<TaxonomyTerm>> terms = new HashMap<String,List<TaxonomyTerm>>();
    private static Map<String,Map<String,List<String>>> names = new HashMap<String,Map<String,List<String>>>();

    private static Comparator comparator = new Comparator<String>()
    {
        public int compare(String arg1, String arg2)
        {
            return arg1.compareTo(arg2);
        }
    };

    enum TaxonomyStatus
    {
        NEW,
        VALID,
        INVALID;
    }

    /**
     * Private constructor.
     */
    private TaxonomyTerms()
    {
    }

    /**
     * Loads the taxonomy terms for the given site.
     */
    public static void loadTerms(String siteId, List<TaxonomyTerm> terms)
    {
        if(terms != null && terms.size() > 0)
        {
            TaxonomyTerms.terms.put(siteId, terms);
            names.remove(siteId);
            setStatus(siteId, VALID);
        }
    }

    /**
     * Loads the taxonomy terms for the given site.
     */
    public static void loadTerms(Site site, List<TaxonomyTerm> terms)
    {
        loadTerms(site.getId(), terms);
    }

    /**
     * Returns <CODE>true</CODE> if taxonomy terms have been loaded for the given site.
     */
    public static boolean isValid(String siteId)
    {
        return getStatus(siteId) == VALID;
    }

    /**
     * Returns <CODE>true</CODE> if taxonomy terms have been loaded for the given site.
     */
    public static boolean isValid(Site site)
    {
        return isValid(site.getId());
    }

    /**
     * Sets the taxonomy terms for the given site to require reloading.
     */
    public static void invalidate(String siteId)
    {
        setStatus(siteId, INVALID);
    }

    /**
     * Sets the taxonomy terms for the given site to require reloading.
     */
    public static void invalidate(Site site)
    {
        invalidate(site.getId());
    }

    /**
     * Returns the status of the taxonomy terms for the given site.
     */
    private static TaxonomyStatus getStatus(String siteId)
    {
        TaxonomyStatus status = statuses.get(siteId);
        if(status == null)
            setStatus(siteId, status = NEW);
        return status;
    }

    /**
     * Sets the status of the taxonomy terms for the given site.
     */
    private static void setStatus(String siteId, TaxonomyStatus status)
    {
        statuses.put(siteId, status);
    }

    /**
     * Returns <CODE>true</CODE> if taxonomy terms have been loaded for the given site.
     */
    public static boolean hasTerms(String siteId)
    {
        return terms.containsKey(siteId);
    }

    /**
     * Returns the taxonomy terms for the given site.
     */
    public static List<TaxonomyTerm> getTerms(String siteId)
    {
        return terms.get(siteId);
    }

    /**
     * Returns the term names for the given site and type.
     */
    private static List<String> getTermNames(String siteId, String type)
    {
        Map<String,List<String>> map = names.get(siteId);
        if(map == null)
        {
            map = new HashMap<String,List<String>>();
            names.put(siteId, map);
        }

        List<String> ret = map.get(type);
        if(ret == null)
        {
            ret = new ArrayList<String>();

            List<TaxonomyTerm> terms = getTerms(siteId);
            if(terms != null)
            {
                for(TaxonomyTerm term : terms)
                {
                    if(term.getType().equals(type))
                        ret.add(term.getName());
                }

                Collections.sort(ret, comparator);
                map.put(type, ret);
            }
        }

        return ret;
    }

    /**
     * Returns the tags for the given site.
     */
    public static List<String> getTags(String siteId)
    {
        return getTermNames(siteId, TaxonomyTerm.TAGS);
    }

    /**
     * Returns the tags for the given site.
     */
    public static List<String> getTags(Site site)
    {
        return getTags(site.getId());
    }

    /**
     * Returns the tag with the given name for the given site.
     */
    public static TaxonomyTerm getTag(String siteId, String name)
    {
        return getTerm(siteId, name, TaxonomyTerm.TAGS);
    }

    /**
     * Returns the features for the given site.
     */
    public static List<String> getFeatures(String siteId)
    {
        return getTermNames(siteId, TaxonomyTerm.FEATURES);
    }

    /**
     * Returns the features for the given site.
     */
    public static List<String> getFeatures(Site site)
    {
        return getFeatures(site.getId());
    }

    /**
     * Returns the feature with the given name for the given site.
     */
    public static TaxonomyTerm getFeature(String siteId, String name)
    {
        return getTerm(siteId, name, TaxonomyTerm.FEATURES);
    }

    /**
     * Returns the technologies for the given site.
     */
    public static List<String> getTechnologies(String siteId)
    {
        return getTermNames(siteId, TaxonomyTerm.TECHNOLOGIES);
    }

    /**
     * Returns the technologies for the given site.
     */
    public static List<String> getTechnologies(Site site)
    {
        return getTechnologies(site.getId());
    }

    /**
     * Returns the technology with the given name for the given site.
     */
    public static TaxonomyTerm getTechnology(String siteId, String name)
    {
        return getTerm(siteId, name, TaxonomyTerm.TECHNOLOGIES);
    }

    /**
     * Returns the pricings for the given site.
     */
    public static List<String> getPricings(String siteId)
    {
        return getTermNames(siteId, TaxonomyTerm.PRICINGS);
    }

    /**
     * Returns the pricings for the given site.
     */
    public static List<String> getPricings(Site site)
    {
        return getPricings(site.getId());
    }

    /**
     * Returns the event types for the given site.
     */
    public static List<String> getEventTypes(String siteId)
    {
        return getTermNames(siteId, TaxonomyTerm.EVENT_TYPES);
    }

    /**
     * Returns the event types for the given site.
     */
    public static List<String> getEventTypes(Site site)
    {
        return getEventTypes(site.getId());
    }

    /**
     * Returns the video types for the given site.
     */
    public static List<String> getVideoTypes(String siteId)
    {
        return getTermNames(siteId, TaxonomyTerm.VIDEO_TYPES);
    }

    /**
     * Returns the video types for the given site.
     */
    public static List<String> getVideoTypes(Site site)
    {
        return getVideoTypes(site.getId());
    }

    /**
     * Returns the term with the given name for the given site.
     */
    private static TaxonomyTerm getTerm(String siteId, String name, String type)
    {
        TaxonomyTerm ret = null;
        List<TaxonomyTerm> terms = getTerms(siteId);
        for(TaxonomyTerm term : terms)
        {
            if(term.getType().equals(type)
                && term.getName().equals(name))
            {
                ret = term;
                break;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the term with the given name exists for the given site.
     */
    public static boolean hasTerm(String siteId, String name, String type)
    {
        return getTerm(siteId, name, type) != null;
    }

    /**
     * Returns the organisation with the given name for the given site.
     */
    public static TaxonomyTerm getOrganisation(String siteId, String name)
    {
        return getTerm(siteId, name, TaxonomyTerm.ORGANISATIONS);
    }

    /**
     * Merge the terms from otherTerms into the terms list for the given site.
     * 
     * Ignores terms that do not exist in the taxonomy for the given site.
     */
    private static List<String> mergeTerms(String siteId, List<String> terms, List<String> otherTerms, String type)
    {
        for(String otherTerm : otherTerms)
        {
            if(!terms.contains(otherTerm) && hasTerm(siteId, otherTerm, type))
            {
                terms.add(otherTerm);
            }
        }

        return terms;
    }

    /**
     * Merge the features from otherTerms into the feature list for the given site.
     */
    public static List<String> mergeFeatures(String siteId, List<String> terms, List<String> otherTerms)
    {
        return mergeTerms(siteId, terms, otherTerms, TaxonomyTerm.FEATURES);
    }

    /**
     * Merge the tags from otherTerms into the tag list for the given site.
     */
    public static List<String> mergeTags(String siteId, List<String> terms, List<String> otherTerms)
    {
        return mergeTerms(siteId, terms, otherTerms, TaxonomyTerm.TAGS);
    }

    /**
     * Merge the technologies from otherTerms into the technology list for the given site.
     */
    public static List<String> mergeTechnologies(String siteId, List<String> terms, List<String> otherTerms)
    {
        return mergeTerms(siteId, terms, otherTerms, TaxonomyTerm.TECHNOLOGIES);
    }
}