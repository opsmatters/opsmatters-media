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
package com.opsmatters.media.handler;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.drupal.TaxonomyTerm;

/**
 * Class representing a handler for taxonomy terms.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TaxonomyHandler
{
    private static final Logger logger = Logger.getLogger(TaxonomyHandler.class.getName());

    public static final String TAGS = "tags";
    public static final String FEATURES = "features";
    public static final String TECHNOLOGIES = "technologies";
    public static final String PRICINGS = "pricing_model";
    public static final String ACTIVITY_TYPES = "activity_type";
    public static final String VIDEO_TYPES = "video_type";
    public static final String ORGANISATIONS = "organisation";

    private Map<String,List<TaxonomyTerm>> terms = new HashMap<String,List<TaxonomyTerm>>();
    private Map<String,Map<String,List<String>>> names = new HashMap<String,Map<String,List<String>>>();

    private Comparator comparator = new Comparator<String>()
    {
        public int compare(String arg1, String arg2)
        {
            return arg1.compareTo(arg2);
        }
    };

    /**
     * Loads the taxonomy terms for the given site.
     */
    public void loadTerms(List<TaxonomyTerm> terms, Site site)
    {
        if(terms != null && terms.size() > 0)
        {
            this.terms.put(site.getId(), terms);
            names.remove(site.getId());
        }
    }

    /**
     * Returns <CODE>true</CODE> if the given site has taxonomy terms.
     */
    public boolean hasTerms(Site site)
    {
        return this.terms.containsKey(site.getId());
    }

    /**
     * Returns the taxonomy terms for the given site.
     */
    public List<TaxonomyTerm> getTerms(Site site)
    {
        return this.terms.get(site.getId());
    }

    /**
     * Returns the term names for the given site and type.
     */
    private List<String> getTermNames(Site site, String type)
    {
        Map<String,List<String>> map = names.get(site.getId());
        if(map == null)
        {
            map = new HashMap<String,List<String>>();
            names.put(site.getId(), map);
        }

        List<String> ret = map.get(type);
        if(ret == null)
        {
            ret = new ArrayList<String>();

            List<TaxonomyTerm> terms = getTerms(site);
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
    public List<String> getTags(Site site)
    {
        return getTermNames(site, TAGS);
    }

    /**
     * Returns the features for the given site.
     */
    public List<String> getFeatures(Site site)
    {
        return getTermNames(site, FEATURES);
    }

    /**
     * Returns the technologies for the given site.
     */
    public List<String> getTechnologies(Site site)
    {
        return getTermNames(site, TECHNOLOGIES);
    }

    /**
     * Returns the pricings for the given site.
     */
    public List<String> getPricings(Site site)
    {
        return getTermNames(site, PRICINGS);
    }

    /**
     * Returns the activity types for the given site.
     */
    public List<String> getActivityTypes(Site site)
    {
        return getTermNames(site, ACTIVITY_TYPES);
    }

    /**
     * Returns the video types for the given site.
     */
    public List<String> getVideoTypes(Site site)
    {
        return getTermNames(site, VIDEO_TYPES);
    }

    /**
     * Returns the term with the given name for the given site.
     */
    private TaxonomyTerm getTerm(Site site, String name, String type)
    {
        TaxonomyTerm ret = null;
        List<TaxonomyTerm> terms = getTerms(site);
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
     * Returns the organisation with the given name for the given site.
     */
    public TaxonomyTerm getOrganisation(Site site, String name)
    {
        return getTerm(site, name, ORGANISATIONS);
    }

    /**
     * Clear the taxonomy terms.
     */
    public void clear()
    {
        terms.clear();
        names.clear();
    }
}