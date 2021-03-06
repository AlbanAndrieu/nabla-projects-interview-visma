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
package com.nabla.selenium.tests;

import java.io.File;
import java.math.BigDecimal;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
// import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nabla.project.visma.LoanService;
import com.nabla.project.visma.NavigationBean;
import com.nabla.project.visma.Payment;
import com.nabla.project.visma.PaymentSchedule;
import com.nabla.project.visma.QueryBean;
import com.nabla.project.visma.api.ILoanService;

@RunWith(Arquillian.class)
@RunAsClient
public class LoanServiceITest {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoanServiceITest.class);

  @Deployment
  @org.jboss.arquillian.container.test.api.TargetsContainer("arq-jetty-embedded")
  public static Archive<?> createDeployment() {
  //public static Archive<?> createTestArchive() {

	  //ShrinkWrap.create(MavenImporter.class)
	  //.loadPomFromFile("/path/to/pom.xml", "activate-profile-1", "!disable-profile-2")
	  //.importBuildOutput().as(WebArchive.class)	;

	  File[] dependencies = Maven.resolver().loadPomFromFile("pom.xml", "sample", "jacoco", "integration", "jmeter", "run-its", "arq-jetty-embedded", "!arq-weld-ee-embedded", "!arq-jbossas-managed")
      .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
    //MavenResolverSystem resolver = Maven.resolver().loadPomFromFile("pom.xml").resolve();

    WebArchive war = ShrinkWrap.create(WebArchive.class, "visma.war")
        .addClasses(ILoanService.class, LoanService.class, QueryBean.class, Payment.class,
            PaymentSchedule.class, NavigationBean.class)
        .addAsLibraries(dependencies);

    // .setWebXML("WEB-INF/web.xml");

    // .addAsResource("loan.xhtml", "loan.xhtml").addAsResource("payment.xhtml", "payment.xhtml")
    // .addAsWebResource("faces-config.xml")
    // .addAsWebResource(EmptyAsset.INSTANCE, "beans.xml")
    // .addPackage(Package.getPackage("org.jboss.jsfunit.example.hellojsf"))
    // .setWebXML("jsf-web.xml");
    // add sample data
    // .addAsResource("import.sql")
    // enable CDI
    // .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

    System.out.println("WAR : " + war.toString(true));
    return war;
  }

  // @Inject
  ILoanService service;

  // @Inject
  // private static transient Logger LOGGER = Logger.getLogger(LoanServiceITest.class);

  @Test
  @InSequence(2)
  public void testRegister() throws Exception {
    LoanServiceITest.LOGGER.info("this will go to the console if the level is set correctly");

    service = new LoanService(new BigDecimal(200_000), 30);
    final BigDecimal total = this.service.getTotalPayment();

    Assert.assertNotNull(total);
    Assert.assertEquals("408808.080969842113801990388563829760", total.toString());
    ;
  }
}
