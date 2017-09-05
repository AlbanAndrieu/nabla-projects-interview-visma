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
package com.nabla.project.visma.selenium.tests.pageobjects;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nabla.project.visma.selenium.tests.helper.SeleniumHelper;
import com.nabla.project.visma.selenium.tests.helper.SharedDriver;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * DOCUMENT ME! albandri.
 *
 * @author $Author$
 * @version $Revision$
 * @since $Date$
 */
public class LoanPage extends LoadableComponent<LoanPage>
{

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanPage.class);

    private final String        url    = "/visma/loan.xhtml";
    // private final String title = "JSF 2.0 Visma Loan test";

    @FindBy(name = "loan_form:loanAmount")
    private WebElement          loanAmount;
    @FindBy(name = "loan_form:paybackTime")
    private WebElement          paybackTime;

    @FindBy(name = "loan_form:payment")
    private WebElement          calculate;

    private final WebDriver     webDriver;

    public LoanPage(SharedDriver webDriver)
    {
        this.webDriver = webDriver;
        // SeleniumHelper.SELENIUM = new WebDriverBackedSelenium(SeleniumHelper.REAL_DRIVER, SeleniumHelper.BASE_URL);
        // SeleniumHelper.SELENIUM.waitForPageToLoad(SeleniumHelper.PAGE_TO_LOAD_TIMEOUT);

        PageFactory.initElements(this.webDriver, this);
    }

    public LoanPage() throws InterruptedException
    {
        SeleniumHelper.setUp();
        this.webDriver = SeleniumHelper.getDriver();
        PageFactory.initElements(this.webDriver, this);
    }

    @Override
    protected void load()
    {
        this.The_user_is_on_loan_page();
    }

    @Override
    protected void isLoaded()
    {
        final String url = SeleniumHelper.getDriver().getCurrentUrl();
        Assert.assertTrue("Not on the issue entry page: " + url, url.endsWith("/loan.xhtml"));
        // Assert.assertTrue(this.driver.getTitle().equals(this.title));
    }

    public void close()
    {
        SeleniumHelper.close();
    }

    @Given("the user is on Loan Page")
    public void The_user_is_on_loan_page()
    {

        SeleniumHelper.getDriver().get(SeleniumHelper.BASE_URL + this.url);

        final JavascriptExecutor js = (JavascriptExecutor) SeleniumHelper.getDriver();

        // Get the Load Event End
        final long loadEventEnd = (Long) js.executeScript("return window.performance.timing.loadEventEnd;");

        // Get the Navigation Event Start
        final long navigationStart = (Long) js.executeScript("return window.performance.timing.navigationStart;");

        // Difference between Load Event End and Navigation Event Start is Page Load Time
        LOGGER.info("Page Load Time is {} seconds.", ((loadEventEnd - navigationStart) / 1000));

        // SeleniumHelper.getSelenium().waitForPageToLoad(SeleniumHelper.PAGE_TO_LOAD_TIMEOUT);

        // Wait for the Calculate Button
        new WebDriverWait(SeleniumHelper.getDriver(), 10).until(ExpectedConditions.presenceOfElementLocated(By.id("loan_form:payment")));

        // WebElement myDynamicElement = (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.id("loan_form")));
        Assert.assertEquals("Housing Loan Cost Calculator", SeleniumHelper.getDriver().findElement(By.cssSelector("h3")).getText());

    }

    @When("he enters \"([^\"]*)\" as loan amount")
    public void He_enters_loan_amount(final String aLoanAmount)
    {
        this.loanAmount.clear();
        this.loanAmount.sendKeys(aLoanAmount);
    }

    @And("he enters \"([^\"]*)\" as payback time")
    public void He_enters_payback_time(final String aPaybackTime)
    {
        this.paybackTime.clear();
        this.paybackTime.sendKeys(aPaybackTime);
    }

    @And("he Submits request for Payment's Schedule")
    public void He_submits_request_for_fund_transfer() throws InterruptedException
    {
        final WebDriverWait wait = new WebDriverWait(SeleniumHelper.getDriver(), 10);
        wait.until(ExpectedConditions.elementToBeClickable(By.name("loan_form:payment")));
        SeleniumHelper.getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        this.calculate.click();

        // SeleniumHelper.getSelenium().waitForPageToLoad(SeleniumHelper.PAGE_TO_LOAD_TIMEOUT);
        // SeleniumHelper.getDriver().findElement(By.name("loan_form:payment")).click();
        // SeleniumHelper.getDriver().manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        Thread.sleep(SeleniumHelper.PAGE_TO_LOAD_TIMEOUT); // 30 s
    }

    @Then("ensure the payment schedule total is accurate with \"([^\"]*)\" message")
    public void Ensure_the_fund_transfer_is_complete(final String msg)
    {
        final WebElement message = SeleniumHelper.getDriver().findElement(By.cssSelector("h4"));
        Assert.assertEquals(message.getText(), msg);
    }

    public void Ensure_a_transaction_failure_message(final int i, final String msg)
    {
        final WebElement message = SeleniumHelper.getDriver().findElement(By.xpath("//table[@id='loan_form:panel']/tbody/tr[" + i + "]/td[3]/span"));
        Assert.assertEquals(message.getText(), msg);

    }

    @Then("ensure loan amount failure message \"([^\"]*)\" is displayed")
    public void Ensure_loan_amount_failure_message(final String msg)
    {
        int i = 2;
        final WebElement message = SeleniumHelper.getDriver().findElement(By.xpath("//table[@id='loan_form:panel']/tbody/tr[" + i + "]/td[3]/span"));
        Assert.assertEquals(message.getText(), msg);

    }

    @Then("ensure payback time failure message \"([^\"]*)\" is displayed")
    public void Ensure_payback_time_failure_message(final String msg)
    {
        int i = 3;
        final WebElement message = SeleniumHelper.getDriver().findElement(By.xpath("//table[@id='loan_form:panel']/tbody/tr[" + i + "]/td[3]/span"));
        Assert.assertEquals(message.getText(), msg);

    }

    public void calculatePayments(final String loanAmount, final String paybackTime) throws InterruptedException
    {

        this.He_enters_loan_amount(loanAmount);
        this.He_enters_payback_time(paybackTime);

        // wait for the application to get fully loaded
        /*
         * final WebElement findOwnerLink = (new WebDriverWait(this.driver, 5)).until(new ExpectedCondition<WebElement>()
         * {
         * @Override
         * public WebElement apply(final WebDriver d)
         * {
         * // d.get(baseUrl);
         * return calculate);
         * }
         * });
         * findOwnerLink.click();
         */

        this.He_submits_request_for_fund_transfer();

    }

}
