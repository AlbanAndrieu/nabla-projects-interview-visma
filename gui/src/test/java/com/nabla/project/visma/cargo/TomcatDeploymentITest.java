package com.nabla.project.visma.cargo;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.codehaus.cargo.container.State.STARTED;
import static org.codehaus.cargo.container.State.STARTING;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.installer.Installer;
import org.codehaus.cargo.container.installer.ZipURLInstaller;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.util.log.LogLevel;
import org.codehaus.cargo.util.log.SimpleLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class TomcatDeploymentITest
{

    private static InstalledLocalContainer WEBAPI_CONTAINER;
    private static final Logger            LOGGER               = LoggerFactory.getLogger(TomcatDeploymentITest.class);

    private final static int               WEBAPI_PORT          = getFreePort();
    private final static int               AJP_PORT             = getFreePort();
    private final static int               RMI_PORT             = getFreePort();
    // private final static String pricerServiceBase = "http://localhost:" + applicationPort + "/scripting/services";
    private final static String            WEBAPI_BASE          = "http://localhost:" + WEBAPI_PORT + "/api";
    private final static String            WEBAPI_WAR           = "target/visma.war";

    private final static String            CONTAINER_TOMCAT8_ID = "tomcat8x";
    // private final static String CONTAINER_TOMCAT6_ID = "tomcat6x";
    private final static String            CONTAINER_ID         = CONTAINER_TOMCAT8_ID;
    private final static String            TOMCAT8_VERSION      = "8.0.3";
    private static final String            TOMCAT8_URL          = "http://archive.apache.org/dist/tomcat/tomcat-8/v" + TOMCAT8_VERSION + "/bin/apache-tomcat-" + TOMCAT8_VERSION + ".zip";
    // private final static String TOMCAT6_VERSION = "6.0.32";
    // private static final String TOMCAT6_URL = "http://archive.apache.org/dist/tomcat/tomcat-6/v" + TOMCAT6_VERSION + "/bin/apache-tomcat-" + TOMCAT6_VERSION + ".zip";
    private static final String            TOMCAT_URL           = TOMCAT8_URL;
    private final static String            TOMCAT_VERSION       = TOMCAT8_VERSION;

    private final static int getFreePort()
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();
            serverSocket.close();
            return port;
        } catch (IOException e)
        {
            throw Throwables.propagate(e);
        }
    }

    @BeforeClass
    public static void setup() throws Exception
    {

        // See http://cargo.codehaus.org/Functional+testing
        // Install tomcat to host web api in remote process
        // Note: cannot get this to work with an embedded server due to classpath problems
        final String tomcatLocation = TOMCAT_URL;
        Installer installer = new ZipURLInstaller(new URL(tomcatLocation), "target", "target");
        installer.install();

        // Create the Cargo Container instance
        LocalConfiguration configuration = (LocalConfiguration) new DefaultConfigurationFactory().createConfiguration(CONTAINER_ID, ContainerType.INSTALLED, ConfigurationType.STANDALONE, "target/" + TOMCAT_VERSION);
        // See properties at http://cargo.codehaus.org/Configuration+properties
        // System.setProperty("cargo.servlet.port", "");
        System.out.println("cargo.servlet.port system property is : " + System.getProperty("cargo.servlet.port") + "\n");
        // System.setProperty("cargo.tomcat.ajp.port", "");
        System.out.println("cargo.tomcat.ajp.port system property is : " + System.getProperty("cargo.tomcat.ajp.port") + "\n");
        // System.setProperty("cargo.rmi.port", "");
        System.out.println("cargo.rmi.port system property is : " + System.getProperty("cargo.rmi.port") + "\n");
        System.setProperty("cargo.rmi.port", valueOf(RMI_PORT));
        System.out.println("cargo.rmi.port system property is : " + System.getProperty("cargo.rmi.port") + "\n");
        System.out.println("Random SERVER port is : " + WEBAPI_PORT);
        configuration.setProperty(ServletPropertySet.PORT, valueOf(WEBAPI_PORT));
        System.out.println("Random AJP port is : " + AJP_PORT);
        configuration.setProperty(TomcatPropertySet.AJP_PORT, valueOf(AJP_PORT));
        System.out.println("Random RMI port is : " + RMI_PORT);
        configuration.setProperty(GeneralPropertySet.RMI_PORT, valueOf(RMI_PORT));

        WEBAPI_CONTAINER = (InstalledLocalContainer) new DefaultContainerFactory().createContainer(CONTAINER_ID, ContainerType.INSTALLED, configuration);
        WEBAPI_CONTAINER.setHome(installer.getHome());

        // Set extra classpath (h2 etc)
        Class<?> h2Class = Class.forName("org.h2.jdbcx.JdbcDataSource");
        URL location = h2Class.getProtectionDomain().getCodeSource().getLocation();
        WEBAPI_CONTAINER.addExtraClasspath(location.getFile().toString());

        // Statically deploy webAPI
        WAR war = new WAR(WEBAPI_WAR);
        war.setContext("/api");
        configuration.addDeployable(war);

        // Setup logger for remote process
        SimpleLogger simpleLogger = new SimpleLogger();
        simpleLogger.setLevel(LogLevel.INFO);
        WEBAPI_CONTAINER.setLogger(simpleLogger);

        /*
         * Pass in system properties
         * <application.configuration.file>${basedir}/src/test/resources/application.properties</application.configuration.file>
         */
        Map<String, String> properties = Maps.newHashMap();
        properties.put("application.configuration.file", Paths.get("src/test/resources/application.properties").toAbsolutePath().toString());
        for (Entry<String, String> p : properties.entrySet())
        {
            System.setProperty(p.getKey(), p.getValue());
        }
        WEBAPI_CONTAINER.setSystemProperties(properties);

        // Set authentication (required for web api)
        Authenticator.setDefault(new Authenticator()
        {
            @Override
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication("albandri", "cooldude".toCharArray());
            }
        });

        // 1. Start Web API
        LOGGER.info("Starting WebAPI");
        WEBAPI_CONTAINER.start();
    }

    @AfterClass
    public static void tearDown() throws Exception
    {
        if (Sets.newHashSet(STARTED, STARTING).contains(WEBAPI_CONTAINER.getState()))
        {
            WEBAPI_CONTAINER.stop();
        }
    }

    /*
     * private String loadScriptDetails(String scriptId) throws IOException
     * {
     * Client webClient = Client.create();
     * ClientResponse docResponse = webClient.resource(format("%s/storage/scripts/%s/", pricerServiceBase, scriptId)).get(ClientResponse.class);
     * return docResponse.getEntity(String.class);
     * }
     */

    protected void test(final String scriptId, String testDeal) throws Exception
    {

        // Create a web client that can be used to make requests
        Client webClient = Client.create();

        // 2. Generate contract from trade
        // WebResource contractDefResource = webClient.resource(format("%s/storage/fields/%s/", pricerServiceBase, scriptId));
        // String scriptDetails = loadScriptDetails(scriptId);
        // ClientResponse fieldResponse = contractDefResource.type("application/json").post(ClientResponse.class, scriptDetails);

        // assertEquals(format("Error getting contract from script %s", scriptId), 200, fieldResponse.getStatus());

        // String contractS = fieldResponse.getEntity(String.class);
        // ObjectMapper mapper = new ObjectMapper();

        // ManageFieldsResponse contract = mapper.readValue(contractS, ManageFieldsResponse.class);

        // Hackily get the value of the private field 'fields' using reflection
        // This is normally done after the object is converted to json (so field visibility is lost)
        // Field fieldsField = contract.getClass().getDeclaredField("fields");
        // fieldsField.setAccessible(true);
        // String fields = (String) fieldsField.get(contract);

        // 3. Publish contract (publishes to correct directory)
        // WebResource contractPublishResource = webClient.resource(format("%s/storage/publish/%s/", pricerServiceBase, scriptId));
        // ClientResponse publishResponse = contractPublishResource.type("application/json").post(ClientResponse.class, fields);

        // assertEquals(format("Error publishing contract for script %s", scriptId), 200, publishResponse.getStatus());

        // Copy output from script into the web api container
        // FileConfig scriptDataConfig = new FileConfig();
        // scriptDataConfig.setFile("target/site");
        // scriptDataConfig.setToDir("data/site/");
        // WEBAPI_CONTAINER.getConfiguration().setConfigFileProperty(scriptDataConfig);

        // 4. Restart WebApi
        WEBAPI_CONTAINER.restart();

        // 5. Test documentation for contract
        // ClientResponse docResponse = webClient.resource(format("%s/doc/1.0/contract/%s/", WEBAPI_BASE, scriptId)).get(ClientResponse.class);
        // assertEquals(format("Error getting documentation for script %s", scriptId), 200, docResponse.getStatus());

        // 6. Insert deal
        // WebResource insertResource = webClient.resource(format("%s/data/1.0/Trade?action=create", WEBAPI_BASE));
        // ClientResponse insertResponse = insertResource.type("application/json").post(ClientResponse.class, testDeal);

        // 7. Assert no errors
        // assertEquals(format("Error inserting deal for script %s", scriptId), 200, insertResponse.getStatus());

        // 8. Retrieve deal from web api database
        // Connection con = DriverManager.getConnection("jdbc:h2:tcp://localhost:5055/test;IFEXISTS=TRUE;FILE_LOCK=NO", "admin", "cooldude");
        // Statement statement = con.createStatement();
        // String sql = "SELECT * FROM Trade WHERE TypeOfDeal = '" + scriptId + "';";
        // ResultSet select = statement.executeQuery(sql);
        // Assert.assertTrue("No records found in DB", select.next());

        // 9. Insert deal into pricer
        // Clob dealDataClob = select.getClob("DEALDATA");
        // final String dealData = dealDataClob.getSubString(1, (int) dealDataClob.length());

        // Run the code
        // TODO
        // ClientResponse docResponse = webClient.resource(format("%s/doc/1.0/contract/%s/", WEBAPI_BASE, scriptId)).get(ClientResponse.class);
        // assertEquals(format("Error getting documentation for script %s", scriptId), 200, docResponse.getStatus());
    }

    @Test
    // @Ignore
    public void testDeployment() throws Exception
    {
        final String input = Resources.toString(TomcatDeploymentITest.class.getClassLoader().getResource("data/input.json"), Charsets.UTF_8);
        test("TEST", input);
    }

}
