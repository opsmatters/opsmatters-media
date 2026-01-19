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
import java.util.Iterator;
import java.util.logging.Logger;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.content.util.TaxonomyTerm;
import com.opsmatters.media.model.content.util.TaxonomyType;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing a handler for taxonomy terms.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TaxonomyTerms extends StaticCache
{
    private static final Logger logger = Logger.getLogger(TaxonomyTerms.class.getName());

    private static Map<String,List<TaxonomyTerm>> terms = new HashMap<String,List<TaxonomyTerm>>();
    private static Map<String,Map<TaxonomyType,List<String>>> names = new HashMap<String,Map<TaxonomyType,List<String>>>();

    private static Comparator comparator = new Comparator<String>()
    {
        public int compare(String arg1, String arg2)
        {
            return arg1.compareTo(arg2);
        }
    };

    /**
     * Private constructor.
     */
    private TaxonomyTerms()
    {
    }

    /**
     * Loads the taxonomy terms for the given site.
     */
    public static void load(Site site, List<TaxonomyTerm> terms)
    {
        if(terms != null && terms.size() > 0)
        {
            for(TaxonomyTerm term : terms)
                add(term, term == terms.get(terms.size()-1));

            logger.info(String.format("Loaded %d taxonomy terms for site %s",
                terms.size(), site.getName()));

            setInitialised(true);
        }
    }

    /**
     * Adds the taxonomy term.
     */
    public static void add(TaxonomyTerm term, boolean sort)
    {
        List<TaxonomyTerm> termList = list(term.getSiteId());
        if(termList == null)
        {
            termList = new ArrayList<TaxonomyTerm>();
            terms.put(term.getSiteId(), termList);
        }

        termList.add(term);

        Map<TaxonomyType,List<String>> map = map(term.getSiteId());
        if(map == null)
        {
            map = new HashMap<TaxonomyType,List<String>>();
            names.put(term.getSiteId(), map);
        }

        List<String> namesList = map.get(term.getType());
        if(namesList == null)
        {
            namesList = new ArrayList<String>();
            map.put(term.getType(), namesList);
        }

        if(term.isAvailable())
            namesList.add(term.getName());

        if(sort)
            Collections.sort(namesList, comparator);
    }

    /**
     * Adds the taxonomy term for the given site.
     */
    public static void add(TaxonomyTerm term)
    {
        add(term, true);
    }

    /**
     * Returns the term with the given name for the given site.
     */
    private static TaxonomyTerm get(String siteId, String name, TaxonomyType type)
    {
        TaxonomyTerm ret = null;
        List<TaxonomyTerm> terms = list(siteId);
        if(terms != null)
        {
            for(TaxonomyTerm term : terms)
            {
                if(term.getType() == type && term.getName().equals(name))
                {
                    ret = term;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Returns the given term if it exists.
     */
    public static TaxonomyTerm get(TaxonomyTerm term)
    {
        return get(term.getSiteId(), term.getName(), term.getType());
    }

    /**
     * Returns <CODE>true</CODE> if the term with the given name exists for the given site.
     */
    public static boolean exists(String siteId, String name, TaxonomyType type)
    {
        return get(siteId, name, type) != null;
    }

    /**
     * Returns <CODE>true</CODE> if the given term exists.
     */
    public static boolean exists(TaxonomyTerm term)
    {
        return get(term) != null;
    }

    /**
     * Returns <CODE>true</CODE> if taxonomy terms have been loaded for the given site.
     */
    public static boolean hasTerms(String siteId)
    {
        return terms.containsKey(siteId);
    }

    /**
     * Returns the list of taxonomy terms for the given site.
     */
    public static List<TaxonomyTerm> list(String siteId)
    {
        return terms.get(siteId);
    }

    /**
     * Returns the list of taxonomy terms for the given site and type.
     */
    public static List<TaxonomyTerm> list(String siteId, TaxonomyType type)
    {
        List<TaxonomyTerm> ret = new ArrayList<TaxonomyTerm>();
        for(TaxonomyTerm term : list(siteId))
        {
            if(term.getType() == type)
                ret.add(term);
        }

        return ret;
    }

    /**
     * Returns the map of taxonomy terms for the given site.
     */
    public static Map<TaxonomyType,List<String>> map(String siteId)
    {
        return names.get(siteId);
    }

    /**
     * Returns the term names for the given site and type.
     */
    private static List<String> getNames(String siteId, TaxonomyType type)
    {
        Map<TaxonomyType,List<String>> map = map(siteId);
        return map != null ? map.get(type) : null;
    }

    /**
     * Returns the tags for the given site.
     */
    public static List<String> getTags(String siteId)
    {
        return getNames(siteId, TaxonomyType.TAGS);
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
        return get(siteId, name, TaxonomyType.TAGS);
    }

    /**
     * Returns the features for the given site.
     */
    public static List<String> getFeatures(String siteId)
    {
        return getNames(siteId, TaxonomyType.FEATURES);
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
        return get(siteId, name, TaxonomyType.FEATURES);
    }

    /**
     * Returns the technologies for the given site.
     */
    public static List<String> getTechnologies(String siteId)
    {
        return getNames(siteId, TaxonomyType.TECHNOLOGIES);
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
        return get(siteId, name, TaxonomyType.TECHNOLOGIES);
    }

    /**
     * Returns the pricings for the given site.
     */
    public static List<String> getPricings(String siteId)
    {
        return getNames(siteId, TaxonomyType.PRICINGS);
    }

    /**
     * Returns the pricings for the given site.
     */
    public static List<String> getPricings(Site site)
    {
        return getPricings(site.getId());
    }

    /**
     * Merge the terms from otherTerms into the terms list for the given site.
     * 
     * Ignores terms that do not exist in the taxonomy for the given site.
     */
    private static List<String> mergeTerms(String siteId,
        List<String> terms, List<String> otherTerms, TaxonomyType type)
    {
        for(String otherTerm : otherTerms)
        {
            if(!terms.contains(otherTerm) && exists(siteId, otherTerm, type))
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
        return mergeTerms(siteId, terms, otherTerms, TaxonomyType.FEATURES);
    }

    /**
     * Merge the tags from otherTerms into the tag list for the given site.
     */
    public static List<String> mergeTags(String siteId, List<String> terms, List<String> otherTerms)
    {
        return mergeTerms(siteId, terms, otherTerms, TaxonomyType.TAGS);
    }

    /**
     * Merge the technologies from otherTerms into the technology list for the given site.
     */
    public static List<String> mergeTechnologies(String siteId, List<String> terms, List<String> otherTerms)
    {
        return mergeTerms(siteId, terms, otherTerms, TaxonomyType.TECHNOLOGIES);
    }

    /**
     * Removes the given term.
     */
    public static void remove(TaxonomyTerm term)
    {
        List<TaxonomyTerm> list = list(term.getSiteId());
        if(list != null)
        {
            Iterator<TaxonomyTerm> iterator = list.iterator();
            while(iterator.hasNext())
            {
                TaxonomyTerm item = iterator.next();
                if(item.getName().equals(term.getName()))
                {
                    iterator.remove();
                }
            }
        }

        Map<TaxonomyType,List<String>> map = map(term.getSiteId());
        if(map != null)
        {
            List<String> names = map.get(term.getType());
            if(names != null)
                names.remove(term.getName());
        }
    }
}