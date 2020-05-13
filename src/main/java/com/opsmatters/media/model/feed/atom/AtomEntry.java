/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.model.feed.atom;

import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing an Atom feed entry.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AtomEntry implements java.io.Serializable
{
    private String id;
    private String uri;
    private String comments;
    private String published;
    private String updated;
    private String title;
    private String description;
    private List<AtomModule> modules;
    private AtomLink link;
    private List<AtomLink> links;
    private AtomCategory category;
    private List<AtomCategory> categories;
    private AtomPerson author;
    private List<AtomPerson> authors;
    private AtomPerson contributor;
    private List<AtomPerson> contributors;

    public AtomEntry()
    {
    }

    @Override
    public String toString()
    {
        return getTitle();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public String getPublished()
    {
        return published;
    }

    public Instant getPublishedDate()
    {
        return TimeUtils.toInstantUTC(published, Formats.ISO8601_FORMAT);
    }

    public void setPublished(String published)
    {
        this.published = published;
    }

    public String getUpdated()
    {
        return updated;
    }

    public Instant getUpdatedDate()
    {
        return TimeUtils.toInstantUTC(updated, Formats.ISO8601_FORMAT);
    }

    public void setUpdated(String updated)
    {
        this.updated = updated;
    }

    public List<AtomModule> getModules()
    {
        if(modules == null)
            modules = new ArrayList<AtomModule>();
        return modules;
    }

    public void setModules(List<AtomModule> modules)
    {
        this.modules = modules;
    }

    public AtomLink getLink()
    {
        return link;
    }

    public void setLink(AtomLink link)
    {
        this.link = link;
    }


    public List<AtomLink> getLinks()
    {
        if(links == null)
            links = new ArrayList<AtomLink>();
        return links;
    }

    public void setLinks(List<AtomLink> links)
    {
        this.links = links;
    }

    public AtomCategory getCategory()
    {
        return category;
    }

    public void setCategory(AtomCategory category)
    {
        this.category = category;
    }

    public List<AtomCategory> getCategories()
    {
        if(categories == null)
            categories = new ArrayList<AtomCategory>();
        return categories;
    }

    public void setCategories(List<AtomCategory> categories)
    {
        this.categories = categories;
    }

    public AtomPerson getAuthor()
    {
        return author;
    }

    public void setAuthor(AtomPerson author)
    {
        this.author = author;
    }


    public List<AtomPerson> getAuthors()
    {
        if(authors == null)
            authors = new ArrayList<AtomPerson>();
        return authors;
    }

    public void setAuthors(List<AtomPerson> authors)
    {
        this.authors = authors;
    }

    public AtomPerson getContributor()
    {
        return contributor;
    }

    public void setContributor(AtomPerson contributor)
    {
        this.contributor = contributor;
    }

    public List<AtomPerson> getContributors()
    {
        if(contributors == null)
            contributors = new ArrayList<AtomPerson>();
        return contributors;
    }

    public void setContributors(List<AtomPerson> contributors)
    {
        this.contributors = contributors;
    }
}