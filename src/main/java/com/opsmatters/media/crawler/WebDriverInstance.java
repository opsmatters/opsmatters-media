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
import org.openqa.selenium.WebDriver;

/**
 * Class representing a pool of Selenium WebDriver objects.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class WebDriverInstance
{
    private static final int MAX_USES = 10;

    private WebDriver driver;
    private String handle;
    private Instant started;
    private int uses = 0;

    public WebDriverInstance(WebDriver driver)
    {
        this.driver = driver;
        handle = driver.getWindowHandle();
        started = Instant.now();
    }

    public String toString()
    {
        return getHandle();
    }

    public WebDriver getDriver()
    {
        return driver;
    }

    public String getHandle()
    {
        return handle;
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
}
