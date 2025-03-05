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
package com.opsmatters.media.crawler;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriverException;
import com.opsmatters.media.model.content.crawler.CrawlerBrowser;
import com.opsmatters.media.model.content.crawler.ContentRequest;

/**
 * Class representing a pool of Selenium WebDriverInstance objects.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class WebDriverPool
{
    private static final Logger logger = Logger.getLogger(WebDriverPool.class.getName());

    private static final int CAPACITY = 5;

    private static WebDriverPool _pool;

    private Map<CrawlerBrowser,List<WebDriverInstance>> waiting = Collections.synchronizedMap(new HashMap<CrawlerBrowser,List<WebDriverInstance>>(CAPACITY));
    private List<WebDriverInstance> used = Collections.synchronizedList(new ArrayList<WebDriverInstance>(CAPACITY));
    private boolean debug = false;

    private static void checkInstance()
    {
        if(_pool == null)
            _pool = new WebDriverPool();
    }

    public synchronized static WebDriverInstance getInstance(ContentRequest request)
    {
        checkInstance();
        return _pool.get(request);
    }

    public synchronized static void releaseInstance(WebDriverInstance instance)
    {
        if(_pool != null)
            _pool.release(instance);
    }

    public synchronized static void close()
    {
        if(_pool != null)
            _pool.closeInstances();
    }

    public static void setDebug(boolean debug)
    {
        checkInstance();
        _pool.debug = debug;
    }

    /**
     * Returns the next available driver for the given browser.
     */
    private WebDriverInstance get(ContentRequest request)
    {
        CrawlerBrowser browser = request.getBrowser();
        if(browser == null)
            browser = CrawlerBrowser.HTMLUNIT;

        WebDriverInstance ret = null;

        do
        {
            List<WebDriverInstance> instances = waiting.get(browser);
            if(instances == null)
            {
                instances = new ArrayList<WebDriverInstance>(CAPACITY);
                waiting.put(browser, instances);
            }

            if(instances.size() == 0)
                instances.add(new WebDriverInstance(request, true));

            WebDriverInstance instance = null;

            try
            {
                // Get the next instance
                instance = instances.remove(0);

                // Check if the driver can be used
                if(instance.renew() || !instance.isAlive())
                {
                    instance.close();
                }
                else // use the instance
                {
                    used.add(instance);
                    instance.use();
                    ret = instance;
                }
            }
            catch(WebDriverException e)
            {
                // Browser has gone away
                if(instance != null)
                    instance.close();
            }
        }
        while(ret == null);

        return ret;
    }

    /**
     * Releases the given instance back to the pool.
     */
    private void release(WebDriverInstance instance)
    {
        used.remove(instance);
        waiting.get(instance.getBrowser()).add(instance);
    }

    /**
     * Close the instances and release resources.
     */
    public void closeInstances()
    {
        logger.info("Closing web driver instances");
        for(CrawlerBrowser browser : CrawlerBrowser.values())
            closeInstances(waiting.get(browser));
        closeInstances(used);
        waiting.clear();
    }

    /**
     * Close the instances in the given list.
     */
    private void closeInstances(List<WebDriverInstance> instances)
    {
        if(instances != null)
        {
            for(WebDriverInstance instance : instances)
                instance.close();
        }
    }
}