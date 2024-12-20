/*
 * Copyright(c) 2024 NTT DATA Group Corporation. Copyright(c) 2013 NTT Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.terasoluna.gfw.tutorial.selenium;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.springframework.context.support.ApplicationObjectSupport;

import jakarta.inject.Inject;

public class WebDriverCreator extends ApplicationObjectSupport {

    @Inject
    private WebDriverManagerConfigurer webDriverManagerConfigurer;

    /**
     * Create a default WebDriver (WebDriver defined in the bean file).
     * @return Default WebDriver
     */
    public WebDriver createDefaultWebDriver() {
        return getApplicationContext().getBean(WebDriver.class);
    }

    /**
     * Create a WebDriver with any locale enabled.
     * <p>
     * Supports FireFox and Chrome only<br>
     * If you specify "en" as an argument, it starts in the English locale.<br>
     * If "" is specified as an argument, it starts without a locale.
     * </p>
     * @param localeStr
     * @return WebDriver Operation target browser
     */
    public WebDriver createLocaleSpecifiedDriver(String localeStr) {
        webDriverManagerConfigurer.setUp();

        for (String activeProfile : getApplicationContext().getEnvironment()
                .getActiveProfiles()) {
            if ("chrome".equals(activeProfile)) {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--lang=" + localeStr);
                return new ChromeDriver(options);
            } else if ("ie".equals(activeProfile)) {
                throw new UnsupportedOperationException("It is not possible to start locale specified browser using InternetExplorer.");
            } else if ("phantomJs".equals(activeProfile)) {
                throw new UnsupportedOperationException("It is not possible to launch locale specified browser using PhantomJS.");
            }
        }

        // The default browser is Firefox
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("intl.accept_languages", localeStr);
        profile.setPreference("browser.startup.homepage_override.mstone",
                "ignore");
        profile.setPreference("network.proxy.type", 0);
        FirefoxOptions options = new FirefoxOptions().setProfile(profile);
        return new FirefoxDriver(options);
    }

    /**
     * Start WebDriver with download function enabled.
     * <p>
     * Supports FireFox only<br>
     * </p>
     * @param downloadTempDirectory : Temporary storage directory for download
     * @return WebDriver instance with download function enabled
     */
    public WebDriver createDownloadableWebDriver(String downloadTempDirectory) {
        webDriverManagerConfigurer.setUp();
        for (String activeProfile : getApplicationContext().getEnvironment()
                .getActiveProfiles()) {
            if ("chrome".equals(activeProfile) || "ie".equals(activeProfile)
                    || "phantomJs".equals(activeProfile)) {
                throw new UnsupportedOperationException("It is not possible to run tests using the download function on browsers other than FireFox.");
            }
        }
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.dir", downloadTempDirectory);
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.lastDir",
                downloadTempDirectory);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.showWhenStarting",
                false);
        profile.setPreference(
                "services.sync.prefs.sync.browser.download.manager.showWhenStarting",
                false);
        profile.setPreference("pdfjs.disabled", true);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "application/pdf, text/csv, application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, text/plain");
        profile.setPreference("browser.startup.homepage_override.mstone",
                "ignore");
        profile.setPreference("network.proxy.type", 0);

        FirefoxOptions options = new FirefoxOptions().setProfile(profile);
        WebDriver webDriver = new FirefoxDriver(options);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        return webDriver;
    }

}
