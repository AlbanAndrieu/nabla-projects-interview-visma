package com.nabla.project.visma.cargo;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentITest {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(DeploymentITest.class);

	private static final int MAIN_PORT = 9090;
	private static final String DEFAULT_SERVER_CONTEXT = "/visma";
	
    private static String SERVER_CONTEXT = DEFAULT_SERVER_CONTEXT;

	private static final String DEFAULT_URL = "http://localhost:" + MAIN_PORT;

	private static String BASE_URL = DeploymentITest.DEFAULT_URL;

	private static String VISMA_URL = DeploymentITest.BASE_URL + DeploymentITest.SERVER_CONTEXT;

	private static long DEPLOY_WAIT = 10;

	/*
	 * fix for Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	 * sun.security.validator.ValidatorException: PKIX path building failed:
	 * sun.security.provider.certpath.SunCertPathBuilderException: unable to
	 * find valid certification path to requested target
	 */
	static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

	} };

	public static Client getUnsecureClient() throws Exception {
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());

		HostnameVerifier allowAll = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		return ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier(allowAll).build();
	}

	@BeforeClass
	public static void setUp() throws InterruptedException, NoSuchAlgorithmException, KeyManagementException {

        System.out.println("SERVER_CONTEXT = " + System.getenv("SERVER_CONTEXT"));
        DeploymentITest.SERVER_CONTEXT = System.getenv("SERVER_CONTEXT");        
        if (null == DeploymentITest.SERVER_CONTEXT) {
            System.out.println("Use default SERVER_CONTEXT");
            DeploymentITest.SERVER_CONTEXT = DeploymentITest.DEFAULT_SERVER_CONTEXT;
        }
        
    	System.out.println("SERVER_URL = " + System.getenv("SERVER_URL"));
    	DeploymentITest.BASE_URL = System.getProperty("webdriver.base.url");   

		if (null == DeploymentITest.BASE_URL) {
			System.out.println("Use default webdriver.base.url");
			DeploymentITest.BASE_URL = DeploymentITest.DEFAULT_URL;
			System.setProperty("webdriver.base.url", DeploymentITest.BASE_URL);
		}
		System.out.println("webdriver.base.url is : " + DeploymentITest.BASE_URL + "\n");

		VISMA_URL = DeploymentITest.BASE_URL + DeploymentITest.SERVER_CONTEXT;

		System.out.println("URL updated to : " + DeploymentITest.VISMA_URL + "\n");

		DeploymentITest.LOGGER.info("Wainting for deploy to be finished before starting test (in seconds) : {}",
				DEPLOY_WAIT);
		TimeUnit.SECONDS.sleep(DEPLOY_WAIT);

		SSLContext sc = SSLContext.getInstance("SSL");

		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

	public static int getResponseCode(String urlString) throws MalformedURLException, IOException {
		URL u = new URL(urlString);
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET");
		// huc.setRequestMethod("HEAD");
		huc.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
		huc.connect();
		return huc.getResponseCode();
	}

	@Test
	public void testBaseUrl() throws Exception {

		DeploymentITest.LOGGER.info("Testing URL : {}", DeploymentITest.VISMA_URL);

		int service_response = getResponseCode(DeploymentITest.VISMA_URL);

		assertEquals(format("Error getting main screen for %s at address %s", DeploymentITest.SERVER_CONTEXT,
				DeploymentITest.VISMA_URL), HttpURLConnection.HTTP_MOVED_TEMP, service_response);

		int good_service_response = getResponseCode(DeploymentITest.VISMA_URL + "/loan.xhtml");

		assertEquals(
				format("Error getting main screen for %s at address %s", DeploymentITest.SERVER_CONTEXT,
						DeploymentITest.VISMA_URL + "/loan.xhtml"),
				HttpURLConnection.HTTP_OK/* HTTP_FORBIDDEN */, good_service_response);
	}

	@Test
	public void testBaseRest() throws Exception {

		DeploymentITest.LOGGER.info("Testing URL : {}", DeploymentITest.VISMA_URL);

		//Client client = ClientBuilder.newClient();
		Client client = getUnsecureClient();

		WebTarget webTarget = client.target(DeploymentITest.VISMA_URL + "/");
		// webTarget.register(FilterForExampleCom.class);
		WebTarget resourceWebTarget = webTarget.path("rest");
		WebTarget booksTarget = resourceWebTarget.path("loan");
		WebTarget booksTestTarget = booksTarget.path("test");

		// WebTarget target =
		// client.target(DeploymentITest.BASE_URL).path("rest/{param}");
		// String result = target.queryParam("param",
		// "value").get(String.class);
		// WebTarget booksTestTarget = client.target(DeploymentITest.BASE_URL +
		// "/rest/books/test");
		// Response responseTest = booksTestTarget.request("text/plain").get();

		// System.out.println("Status : " + responseTest.getStatus());
		// assertEquals(responseTest.getStatus(), 200);
		// System.out.println("Response : " +
		// responseTest.readEntity(String.class));

		// WebTarget helloworldWebTargetWithQueryParam =
		// booksTarget.queryParam("test", "10");

		Invocation.Builder invocationBuilder = booksTestTarget.request(MediaType.TEXT_PLAIN_TYPE);
		invocationBuilder.header("some-header", "true");

		Response responseFull = invocationBuilder.get();
		System.out.println("Status : " + responseFull.getStatus());
		String resultTest = responseFull.readEntity(String.class);
		System.out.println("Response : " + resultTest);

		assertEquals(
				format("Error getting login screen for %s at address %s", SERVER_CONTEXT, DeploymentITest.BASE_URL),
				HttpURLConnection.HTTP_OK, responseFull.getStatus());

		assertEquals(resultTest, "Test");
	}

}
