/*
 * Copyright (c) 2002-2004, Nabla
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Nabla' nor 'Alban' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package com.nabla.project.visma.selenium.tests;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.nabla.project.visma.selenium.tests.helper.SeleniumHelper;

/**
 * DOCUMENT ME! albandri.
 *
 * @author $Author$
 * @version $Revision$
 * @since $Date$
 */
public class GoogleSearchSTest {
  @BeforeClass
  public static void setUp() throws Exception {
    // ProfilesIni allProfiles = new ProfilesIni();
    // FirefoxProfile profile =
    // allProfiles.getProfile("/workspace/users/albandri10/.mozilla/firefox/xhvt8rwp.selenium/");
    // FirefoxProfile profile = new FirefoxProfile();
    // FirefoxBinary binary = new FirefoxBinary(new File(firefoxBin));
    // driver = new FirefoxDriver(binary, profile);
    // this.driver = new FirefoxDriver(profile);
    // this.driver = new FirefoxDriver();

    SeleniumHelper.setUp();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    SeleniumHelper.tearDown();
  }

  @Test
  public void testGoogleSearch() {
    SeleniumHelper.getDriver().get("http://www.google.com");

    // Find the text input element by its name
    final WebElement element = SeleniumHelper.getDriver().findElement(By.name("q"));

    // Enter something to search for
    element.sendKeys("Selenium testing tools cookbook");

    // Now submit the form. WebDriver will find the form for us from the element
    element.submit();

    // Google's search is rendered dynamically with JavaScript.
    // Wait for the page to load, timeout after 10 seconds
    (new WebDriverWait(SeleniumHelper.getDriver(), 10)).until(new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(final WebDriver d) {
        return d.getTitle().toLowerCase().startsWith("selenium testing tools cookbook");
      }
    });

    // Should see: selenium testing tools cookbook - Google Search
    // Assert.assertEquals("Selenium testing tools cookbook - Google Search",
    // this.driver.getTitle());
    Assert.assertEquals("Selenium testing tools cookbook - Recherche Google",
        SeleniumHelper.getDriver().getTitle());
  }
}
