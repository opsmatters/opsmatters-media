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
import java.time.Instant;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import com.gargoylesoftware.htmlunit.WebClient;

import static com.opsmatters.media.crawler.CrawlerBrowser.*;

/**
 * Class representing a pool of Selenium WebDriver objects.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class WebDriverPool
{
    private static final Logger logger = Logger.getLogger(WebDriverPool.class.getName());

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36";

    private static final int CAPACITY = 5;

    private static WebDriverPool _pool;

    private Map<CrawlerBrowser,List<WebDriver>> waiting = Collections.synchronizedMap(new HashMap<CrawlerBrowser,List<WebDriver>>(CAPACITY));
    private List<WebDriver> used = Collections.synchronizedList(new ArrayList<WebDriver>(CAPACITY));
    private Map<String,WebDriverInstance> instances = new HashMap<String,WebDriverInstance>(CAPACITY);
    private boolean debug = false;

    private static void checkInstance()
    {
        if(_pool == null)
            _pool = new WebDriverPool();
    }

    public synchronized static WebDriver getDriver(CrawlerBrowser browser)
    {
        checkInstance();
        return _pool.get(browser);
    }

    public synchronized static void releaseDriver(WebDriver driver)
    {
        if(_pool != null)
            _pool.release(driver);
    }

    public synchronized static void close()
    {
        if(_pool != null)
            _pool.closeDrivers();
    }

    public static void setDebug(boolean debug)
    {
        checkInstance();
        _pool.debug = debug;
    }

    /**
     * Returns the next available driver for the given browser.
     */
    private WebDriver get(CrawlerBrowser browser)
    {
        if(browser == null)
            browser = HTMLUNIT;

        WebDriver ret = null;
        do
        {
            List<WebDriver> drivers = waiting.get(browser);
            if(drivers == null)
            {
                drivers = new ArrayList<WebDriver>(CAPACITY);
                waiting.put(browser, drivers);
            }

            WebDriver driver = null;
            WebDriverInstance instance = null;

            if(drivers.size() == 0)
            {
                if(browser == CHROME)
                    driver = newChromeDriver();
                else if(browser == FIREFOX)
                    driver = newFirefoxDriver();
                else // Defaults to HtmlUnit
                    driver = newHtmlUnitDriver();

                drivers.add(driver);
                instance = new WebDriverInstance(driver);
                instances.put(instance.getHandle(), instance);
            }
            
            driver = drivers.remove(0);
            instance = instances.get(driver.getWindowHandle());

            // Check if the driver can be used
            if(instance.renew() || !isAlive(driver))
            {
                closeDriver(driver);
            }
            else // use the driver
            {
                used.add(driver);
                instance.use();
                ret = driver;
            }
        }
        while(ret == null);

        return ret;
    }

    private boolean isAlive(WebDriver driver)
    {
        try
        {
            return driver.getWindowHandles().size() > 0;
        }
        catch(UnhandledAlertException ex)
        {
            return true;
        }
        catch(WebDriverException ex)
        {
            return false;
        }
    }

    private WebDriver newHtmlUnitDriver()
    {
        return new HtmlUnitDriver()
        {
            @Override
            protected WebClient modifyWebClient(WebClient webClient)
            {
                final WebClient client = super.modifyWebClient(webClient);

                client.setJavaScriptTimeout(30000);
                client.getOptions().setTimeout(60000);
                client.getOptions().setRedirectEnabled(true);
                client.getOptions().setThrowExceptionOnFailingStatusCode(false);
                client.getOptions().setThrowExceptionOnScriptError(false);
                client.getOptions().setCssEnabled(false);
                client.getOptions().setPrintContentOnFailingStatusCode(false);
                client.getCookieManager().setCookiesEnabled(false);
                client.getOptions().setUseInsecureSSL(true);

                return client;
            }
        };
    }

    private WebDriver newChromeDriver()
    {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); 
        options.addArguments("--disable-extensions");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent="+USER_AGENT);
        options.addArguments("--no-proxy-server");
        options.addArguments("--proxy-server='direct://'");
        options.addArguments("--proxy-bypass-list=*");
        options.setHeadless(true);
        return new ChromeDriver(options);
    }

    private WebDriver newFirefoxDriver()
    {
        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent="+USER_AGENT);
        options.addArguments("--no-proxy-server");
        options.addArguments("--proxy-server='direct://'");
        options.addArguments("--proxy-bypass-list=*");
        options.setHeadless(true);
        return new FirefoxDriver(options);
    }

    /**
     * Releases the given driver back to the pool.
     */
    private void release(WebDriver driver)
    {
        used.remove(driver);
        if(driver instanceof ChromeDriver)
            waiting.get(CHROME).add(driver);
        else if(driver instanceof FirefoxDriver)
            waiting.get(FIREFOX).add(driver);
        else // Defaults to HtmlUnit
            waiting.get(HTMLUNIT).add(driver);       
    }

    /**
     * Close the drivers and release resources.
     */
    public void closeDrivers()
    {
        logger.info("Closing web drivers");
        closeDrivers(waiting.get(HTMLUNIT));
        closeDrivers(waiting.get(CHROME));
        closeDrivers(waiting.get(FIREFOX));
        closeDrivers(used);
        waiting.clear();
    }

    /**
     * Close the drivers in the given list.
     */
    private void closeDrivers(List<WebDriver> drivers)
    {
        if(drivers != null)
        {
            for(WebDriver driver : drivers)
                closeDriver(driver);
        }
    }

    /**
     * Close the given driver.
     */
    private void closeDriver(WebDriver driver)
    {
        if(driver != null)
        {
            String handle = driver.getWindowHandle();
            driver.quit();
            instances.remove(handle);
        }
    }
}