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

/**
 * Class for an Atom feed item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AtomFeed<T extends AtomEntry> implements java.io.Serializable
{
    private String encoding;
    private String uri;
    private String published;
    private String updated;
    private String title;
    private String description;
    private String feedType;
    private String webMaster;
    private String managingEditor;
    private String docs;
    private String generator;
    private String styleSheet;
    private String language;
    private AtomImage icon;
    private AtomImage image;
    private List<AtomModule> modules;
    private AtomLink link;
    private List<AtomLink> links;
    private AtomCategory category;
    private List<AtomCategory> categories;
    private AtomPerson author;
    private List<AtomPerson> authors;
    private AtomPerson contributor;
    private List<AtomPerson> contributors;
    private T entry;
    private List<T> entries;

    public AtomFeed()
    {
    }

    public String toString()
    {
        return title;
    }

    public String getFeedType()
    {
        return feedType;
    }

    public void setFeedType(String feedType)
    {
        this.feedType = feedType;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
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

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getDocs()
    {
        return docs;
    }

    public void setDocs(String docs)
    {
        this.docs = docs;
    }

    public String getGenerator()
    {
        return generator;
    }

    public void setGenerator(String generator)
    {
        this.generator = generator;
    }

    public String getManagingEditor()
    {
        return managingEditor;
    }

    public void setManagingEditor(String managingEditor)
    {
        this.managingEditor = managingEditor;
    }

    public String getWebMaster()
    {
        return webMaster;
    }

    public void setWebMaster(String webMaster)
    {
        this.webMaster = webMaster;
    }

    public String getStyleSheet()
    {
        return styleSheet;
    }

    public void setStyleSheet(String styleSheet)
    {
        this.styleSheet = styleSheet;
    }

    public String getPublished()
    {
        return published;
    }

    public void setPublished(String published)
    {
        this.published = published;
    }

    public String getUpdated()
    {
        return updated;
    }

    public void setUpdated(String updated)
    {
        this.updated = updated;
    }

    public AtomImage getIcon()
    {
        return icon;
    }

    public void setIcon(AtomImage icon)
    {
        this.icon = icon;
    }

    public AtomImage getImage()
    {
        return image;
    }

    public void setImage(AtomImage image)
    {
        this.image = image;
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

    public T getEntry()
    {
        return entry;
    }

    public void setEntry(T entry)
    {
        this.entry = entry;
    }

    public List<T> getEntries()
    {
        if(entries == null)
            entries = new ArrayList<T>();
        return entries;
    }

    public void setEntries(List<T> entries)
    {
        this.entries = entries;
    }
}