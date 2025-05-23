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

import java.time.Instant;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.frogking.chromedriver.ChromeDriverBuilder;
import com.opsmatters.media.cache.content.util.ContentProxies;
import com.opsmatters.media.model.content.crawler.CrawlerBrowser;
import com.opsmatters.media.model.content.crawler.ContentRequest;
import com.opsmatters.media.model.content.util.ContentProxy;

import static com.opsmatters.media.model.content.crawler.CrawlerBrowser.*;

/**
 * Class representing a pool of Selenium WebDriver objects.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class WebDriverInstance
{
    private static final Logger logger = Logger.getLogger(WebDriverInstance.class.getName());

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36";

    private static final int MAX_USES = 10;

    private CrawlerBrowser browser;
    private WebDriver driver;
    private String handle;
    private boolean cached;
    private boolean headless = false;
    private boolean useProxy = false;
    private ContentProxy proxy = null;
    private Instant started;
    private int uses = 0;
    private int maxUses = -1;

    public WebDriverInstance(ContentRequest request, boolean cached)
    {
        setBrowser(request.getBrowser());
        setHeadless(request.isHeadless());
        setProxy(request.useProxy());
        setCached(cached);

        CrawlerBrowser browser = getBrowser();
        if(browser == CHROME || browser == UNDETECTED_CHROME)
            driver = newChromeDriver();
        else if(browser == FIREFOX)
            driver = newFirefoxDriver();
        else // Defaults to HtmlUnit
            driver = newHtmlUnitDriver();

        this.handle = driver.getWindowHandle();
        started = Instant.now();
    }

    public String toString()
    {
        return getHandle();
    }

    public CrawlerBrowser getBrowser()
    {
        return browser;
    }

    public void setBrowser(CrawlerBrowser browser)
    {
        if(browser == null)
            browser = HTMLUNIT;
        this.browser = browser;
    }

    public WebDriver getDriver()
    {
        return driver;
    }

    public String getHandle()
    {
        return handle;
    }

    public boolean isCached()
    {
        return cached;
    }

    public void setCached(boolean cached)
    {
        this.cached = cached;
    }

    public boolean isHeadless()
    {
        return headless;
    }

    public void setHeadless(boolean headless)
    {
        this.headless = headless;
    }

    public boolean useProxy()
    {
        return useProxy;
    }

    public void setProxy(boolean useProxy)
    {
        this.useProxy = useProxy;
    }

    public ContentProxy getProxy()
    {
        return proxy;
    }

    public int getUses()
    {
        return uses;
    }

    public void use()
    {
        ++uses;
    }

    public boolean renew()
    {
        return uses >= MAX_USES;
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

                if(useProxy)
                {
                    proxy = ContentProxies.next();
                    if(proxy != null)
                    {
                        ProxyConfig proxyConfig = new ProxyConfig(proxy.getHost(), proxy.getPort(), null);
                        client.getOptions().setProxyConfig(proxyConfig);
                    }
                    else
                    {
                        logger.severe("Unable to find next proxy for htmlunit");
                    }
                }

                return client;
            }
        };
    }

    private WebDriver newChromeDriver()
    {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");

        // Settings for chromedriver (Untected ChromeDriver has its own settings)
        if(browser == CHROME)
        {
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-blink-features=AutomationControlled"); // prevents some 403 errors
            options.addArguments("--disable-extensions");
            options.addArguments("--user-agent="+USER_AGENT);
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-debugging-pipe");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); 
            options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        }

        if(headless)
            options.addArguments("--headless=new");

        if(useProxy)
        {
            proxy = ContentProxies.next();
            if(proxy != null)
            {
                Proxy p = new Proxy();
                p.setHttpProxy(proxy.getHostPort());
                p.setSslProxy(proxy.getHostPort());
                options.setCapability("proxy", p);
            }
            else
            {
                logger.severe("Unable to find next proxy for chrome");
            }
        }

        if(proxy == null)
        {
            options.addArguments("--no-proxy-server");
            options.addArguments("--proxy-server='direct://'");
            options.addArguments("--proxy-bypass-list=*");
        }

        if(browser == UNDETECTED_CHROME)
            return new ChromeDriverBuilder().build(options,
                System.getProperty("webdriver.chrome.driver"));
        else
            return new ChromeDriver(options);
    }

    private WebDriver newFirefoxDriver()
    {
        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent="+USER_AGENT);

        if(headless)
            options.addArguments("--headless");

        if(useProxy)
        {
            proxy = ContentProxies.next();
            if(proxy != null)
            {
                Proxy p = new Proxy();
                p.setHttpProxy(proxy.getHostPort());
                p.setSslProxy(proxy.getHostPort());
                options.setCapability("proxy", p);
            }
            else
            {
                logger.severe("Unable to find next proxy for firefox");
            }
        }

        if(proxy == null)
        {
            options.addArguments("--no-proxy-server");
            options.addArguments("--proxy-server='direct://'");
            options.addArguments("--proxy-bypass-list=*");
        }

        return new FirefoxDriver(options);
    }

    public boolean isAlive()
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

    /**
     * Close the driver.
     */
    public void close()
    {
        try
        {
            if(driver != null)
                driver.quit();
        }
        catch(WebDriverException e)
        {
        }
    }
}