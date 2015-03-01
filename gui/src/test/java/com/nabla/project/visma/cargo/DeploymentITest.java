package com.nabla.project.visma.cargo;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class DeploymentITest
{

    private static final transient Logger LOGGER          = LoggerFactory.getLogger(DeploymentITest.class);

    private static final int              MAIN_PORT       = 9090;
    private static final String           DEFAULT_CONTEXT = "visma";

    private static final String           DEFAULT_URL     = "http://localhost:" + MAIN_PORT;

    private static String                 BASE_URL        = DeploymentITest.DEFAULT_URL;

    private static String                 VISMA_URL       = DeploymentITest.BASE_URL + "/" + DeploymentITest.DEFAULT_CONTEXT;

    @BeforeClass
    public static void setUp() throws InterruptedException
    {

        DeploymentITest.BASE_URL = System.getProperty("webdriver.base.url");

        if (null == DeploymentITest.BASE_URL)
        {
            System.out.println("Use default webdriver.base.url");
            DeploymentITest.BASE_URL = DeploymentITest.DEFAULT_URL;
            System.setProperty("webdriver.base.url", DeploymentITest.BASE_URL);
        }
        System.out.println("webdriver.base.url is : " + DeploymentITest.BASE_URL + "\n");

        VISMA_URL = DeploymentITest.BASE_URL + "/" + DeploymentITest.DEFAULT_CONTEXT;

        System.out.println("URL updated to : " + DeploymentITest.VISMA_URL + "\n");

    }

    public static int getResponseCode(String urlString) throws MalformedURLException, IOException
    {
        URL u = new URL(urlString);
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection huc = (HttpURLConnection) u.openConnection();
        huc.setRequestMethod("GET");
        // huc.setRequestMethod("HEAD");
        huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
        huc.connect();
        return huc.getResponseCode();
    }

    @Test
    public void testBaseUrl() throws Exception
    {

        DeploymentITest.LOGGER.info("Testing URL : {}", DeploymentITest.VISMA_URL);

        int service_response = getResponseCode(DeploymentITest.VISMA_URL);

        assertEquals(format("Error getting main screen for %s at address %s", DeploymentITest.DEFAULT_CONTEXT, DeploymentITest.VISMA_URL), HttpURLConnection.HTTP_MOVED_TEMP, service_response);

        int good_service_response = getResponseCode(DeploymentITest.VISMA_URL + "/");

        assertEquals(format("Error getting main screen for %s at address %s", DeploymentITest.DEFAULT_CONTEXT, DeploymentITest.VISMA_URL), HttpURLConnection.HTTP_OK, good_service_response);
    }

    @Test
    public void testBaseRest() throws Exception
    {

        DeploymentITest.LOGGER.info("Testing URL : {}", DeploymentITest.VISMA_URL);

        Client webClient = Client.create();

        ClientResponse serverResponse = webClient.resource(DeploymentITest.VISMA_URL).get(ClientResponse.class);
        // serverResponse.getEntity(String.class);

        assertEquals(format("Error REST service is not UP at address %s", DeploymentITest.DEFAULT_CONTEXT, DeploymentITest.BASE_URL), HttpURLConnection.HTTP_OK, serverResponse.getStatus());
    }

}
