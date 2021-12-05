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

import static com.opsmatters.media.handler.TaxonomyHandler.TaxonomyStatus.*;

/**
 * Class representing a handler for taxonomy terms.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TaxonomyHandler
{
    private static final Logger logger = Logger.getLogger(TaxonomyHandler.class.getName());

    private Map<String,TaxonomyStatus> statuses = new HashMap<String,TaxonomyStatus>();
    private Map<String,List<TaxonomyTerm>> terms = new HashMap<String,List<TaxonomyTerm>>();
    private Map<String,Map<String,List<String>>> names = new HashMap<String,Map<String,List<String>>>();

    private Comparator comparator = new Comparator<String>()
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
     * Returns the status of the taxonomy terms for the given site.
     */
    private TaxonomyStatus getStatus(Site site)
    {
        TaxonomyStatus status = statuses.get(site.getId());
        if(status == null)
            setStatus(site, status = NEW);
        return status;
    }

    /**
     * Sets the status of the taxonomy terms for the given site.
     */
    private void setStatus(Site site, TaxonomyStatus status)
    {
        statuses.put(site.getId(), status);
    }

    /**
     * Returns <CODE>true</CODE> if taxonomy terms have been loaded for the given site.
     */
    public boolean isValid(Site site)
    {
        return getStatus(site) == VALID;
    }

    /**
     * Sets the taxonomy terms for the given site to require reloading.
     */
    public void invalidate(Site site)
    {
        setStatus(site, INVALID);
    }

    /**
     * Loads the taxonomy terms for the given site.
     */
    public void loadTerms(Site site, List<TaxonomyTerm> terms)
    {
        if(terms != null && terms.size() > 0)
        {
            this.terms.put(site.getId(), terms);
            names.remove(site.getId());
            setStatus(site, VALID);
        }
    }

    /**
     * Returns <CODE>true</CODE> if taxonomy terms have been loaded for the given site.
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
        return getTermNames(site, TaxonomyTerm.TAGS);
    }

    /**
     * Returns the features for the given site.
     */
    public List<String> getFeatures(Site site)
    {
        return getTermNames(site, TaxonomyTerm.FEATURES);
    }

    /**
     * Returns the technologies for the given site.
     */
    public List<String> getTechnologies(Site site)
    {
        return getTermNames(site, TaxonomyTerm.TECHNOLOGIES);
    }

    /**
     * Returns the pricings for the given site.
     */
    public List<String> getPricings(Site site)
    {
        return getTermNames(site, TaxonomyTerm.PRICINGS);
    }

    /**
     * Returns the activity types for the given site.
     */
    public List<String> getActivityTypes(Site site)
    {
        return getTermNames(site, TaxonomyTerm.ACTIVITY_TYPES);
    }

    /**
     * Returns the video types for the given site.
     */
    public List<String> getVideoTypes(Site site)
    {
        return getTermNames(site, TaxonomyTerm.VIDEO_TYPES);
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
        return getTerm(site, name, TaxonomyTerm.ORGANISATIONS);
    }
}