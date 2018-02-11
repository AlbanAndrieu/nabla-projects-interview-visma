package com.nabla.project.visma;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class InputTest {
  private static final String PROTOCOL = "http";
  private static final String PASSWORD_TEST = "passwordTest";
  private static final String USERNAME_TEST = "usernameTest";

  @Test
  public void testGoogleFinance() throws Exception {
    URL url = new URL("https://finance.google.com/finance?q=NASDAQ%3AMSFT");
    String urlContent = Resources.toString(url, Charsets.UTF_8);
    System.out.println(urlContent);
  }

  @Test
  public void testPasswordAuthentication() throws UnknownHostException {
    InetAddress addr = InetAddress.getLocalHost();
    PasswordAuthentication pa =
        Authenticator.requestPasswordAuthentication(addr, 8080, PROTOCOL, "prompt", "HTTP");
    Assert.assertNull(pa);
    MockAuthenticator mock = new MockAuthenticator();
    Authenticator.setDefault(mock);
    addr = InetAddress.getLocalHost();
    pa = Authenticator.requestPasswordAuthentication(addr, 80, PROTOCOL, "prompt", "HTTP");
    Assert.assertNotNull(pa);
    Assert.assertEquals(USERNAME_TEST, pa.getUserName());
    Assert.assertEquals(PASSWORD_TEST, String.valueOf(pa.getPassword()));
    Authenticator.setDefault(null);
  }

  class MockAuthenticator extends Authenticator {
    @Override
    public URL getRequestingURL() {
      return super.getRequestingURL();
    }

    @Override
    public Authenticator.RequestorType getRequestorType() {
      return super.getRequestorType();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
      // Get information about the request
      String prompt = getRequestingPrompt();

      System.out.println("Prompt : " + prompt);

      InetAddress addr = getRequestingSite();

      // String hostname = getRequestingHost();
      String hostname = addr.getHostName();

      System.out.println("Hostname : " + hostname);

      InetAddress ipaddr = getRequestingSite();

      System.out.println("IP address : " + ipaddr);

      int port = getRequestingPort();

      System.out.println("Port : " + port);

      try {
        URL tracker_url = new URL(PROTOCOL + "://" + hostname + ":" + port + "/");

        System.out.println("URL : " + tracker_url);

      } catch (MalformedURLException e) {
        System.out.println("Error" + e);
      }

      String username = USERNAME_TEST;

      String password = PASSWORD_TEST;

      // Return the information (a data holder that is used by Authenticator)

      return new PasswordAuthentication(username, password.toCharArray());

      // return super.getPasswordAuthentication();
    }
  }
}
