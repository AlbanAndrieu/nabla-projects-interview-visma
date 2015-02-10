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

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.time.StopWatch;
import org.jboss.arquillian.junit.InSequence;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.nabla.project.visma.selenium.tests.helper.SeleniumHelper;
import com.nabla.project.visma.selenium.tests.pageobjects.LoanPage;

@RunWith(value = Parameterized.class)
@net.jcip.annotations.NotThreadSafe
public class SimpleParametrizedWebDriverSTest
{

    private final SeleniumHelper helper = new SeleniumHelper();

    private final String         loanAmount;
    private final String         paybackTime;

    private final String         totalPayments;
    private final String         firstPayment;
    private final int            expectedPayments;

    public SimpleParametrizedWebDriverSTest(final String aLoanAmount, final String aPaybackTime, final String aTotalPayments, final String aFirstPayment, final int aExpectedPayments)
    {
        this.loanAmount = aLoanAmount;
        this.paybackTime = aPaybackTime;
        this.totalPayments = aTotalPayments;
        this.firstPayment = aFirstPayment;
        this.expectedPayments = aExpectedPayments;
    }

    @Parameters
    public static Collection<Object[]> testData()
    {
        return Arrays.asList(new Object[][]
        {
        { "1000000", "10", "1302315.33552576902309236382167649640", "10852.62779604807519243636518063747", 121 },
        { "200000", "30", "408808.080969842113801990388563829760", "1135.578002694005871672195523788416", 361 } });
    }

    @Before
    public void setUp() throws Exception
    {

        this.helper.setUp();
    }

    @After
    public void tearDown() throws Exception
    {
        this.helper.tearDown();
    }

    /*
     * @Before
     * public void homePageRefresh() throws IOException
     * {
     * driver.manage().deleteAllCookies();
     * // driver.get(propertyKeysLoader("login.base.url"));
     * }
     */

    @Test
    @InSequence(1)
    public void testWithGoodInputS() throws Exception
    {
        // Get the StopWatch Object and start the StopWatch
        final StopWatch pageLoad = new StopWatch();
        pageLoad.start();
        
        // Create an instance of Loan Page class
        // and provide the driver
        final LoanPage loanPage = new LoanPage(/* SeleniumHelper.getDriver() */);

        // Open the Loan Calculator Page
        loanPage.get();
        
        loanPage.calculatePayments(this.loanAmount, this.paybackTime);

        Assert.assertEquals("Housing Loan Cost Calculator (Results)", SeleniumHelper.getDriver().findElement(By.cssSelector("h3")).getText());
        loanPage.Ensure_the_fund_transfer_is_complete("Payments total is : " + this.totalPayments);
        final WebElement simpleTable = SeleniumHelper.getDriver().findElement(By.id("payments"));
        SeleniumHelper.testWebTable(simpleTable, this.expectedPayments);
        Assert.assertEquals(this.firstPayment, SeleniumHelper.getDriver().findElement(By.xpath("//td[2]")).getText());

        pageLoad.stop();

        System.out.println("Total Page Load Time: " + pageLoad + " milliseconds");
        
        // SeleniumHelper.testTakesScreenshot("testWithGoodInputS.png", SeleniumHelper.getDriver());
        // Thread.sleep(1000);

        SeleniumHelper.getSelenium().open("/visma/");
        SeleniumHelper.getSelenium().waitForPageToLoad("1500");
    }

}
