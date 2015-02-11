package com.nabla.project.visma.selenium.tests.helper;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * <p>
 * Example of a WebDriver implementation that has delegates all methods to a static instance (REAL_DRIVER) that is only
 * created once for the duration of the JVM. The REAL_DRIVER is automatically closed when the JVM exits. This makes
 * scenarios a lot faster since opening and closing a browser for each scenario is pretty slow.
 * To prevent browser state from leaking between scenarios, cookies are automatically deleted before every scenario.
 * </p>
 * <p>
 * A new instance of SharedDriver is created for each Scenario and passed to yor Stepdef classes via Dependency Injection
 * </p>
 * <p>
 * As a bonus, screenshots are embedded into the report for each scenario. (This only works
 * if you're also using the HTML formatter).
 * </p>
 * <p>
 * A new instance of the SharedDriver is created for each Scenario and then passed to the Step Definition classes'
 * constructor. They all receive a reference to the same instance. However, the REAL_DRIVER is the same instance throughout
 * the life of the JVM.
 * </p>
 */
public class SharedDriver extends /* RemoteWebDriver */EventFiringWebDriver
{
    private static final WebDriver REAL_DRIVER;           // FirefoxDriver
    private static final Thread    CLOSE_THREAD    = new Thread()
                                                   {
                                                       @Override
                                                       public void run()
                                                       {
                                                           REAL_DRIVER.close();
                                                       }
                                                   };

    private static Properties      test_properties = null;
    private static String          browser_name    = null;

    static
    {
        System.out.println("setting property ");
        setProperty();
        browser_name = fetchBrowser();
        System.out.println(browser_name);
        System.setProperty("webdriver.chrome.driver", "/var/lib/chromedriver");
        // REAL_DRIVER = new ChromeDriver();
        REAL_DRIVER = getInstance(browser_name);
        Runtime.getRuntime().addShutdownHook(CLOSE_THREAD);

    }

    public SharedDriver()
    {
        super(REAL_DRIVER);
        REAL_DRIVER.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        REAL_DRIVER.manage().window().setSize(new Dimension(1200, 800));
    }

    public static void setProperty()
    {
        // initialize properties
        System.out.println("***************Initializing Properties file*******************");
        try
        {
            test_properties = new Properties();
            FileInputStream fs = new FileInputStream(System.getProperty("user.dir") + "\\src\\test\\resources\\OR.properties");
            test_properties.load(fs);
        } catch (Exception e)
        {
            System.out.println("Error in initializing Properties file");
        }

    }

    public static String fetchBrowser()
    {
        browser_name = test_properties.getProperty("browser");
        if (null != browser_name)
        {
            System.out.println("values fetched is" + browser_name);
        } else
        {
            browser_name = "chrome";
            System.out.println("values fetched is empty");
        }
        return browser_name;

    }

    public static WebDriver getInstance(String browserName)
    {
        WebDriver driver;
        switch (browserName)
        {
            case "firefox":
                System.out.println("in firefox");
                System.setProperty("webdriver.firefox.bin", "/usr/lib/firefox/firefox");
                driver = new FirefoxDriver();
                break;
            case "chrome":
                System.out.println("in chrome");
                System.setProperty("webdriver.chrome.driver", "/var/lib/chromedriver");
                driver = new ChromeDriver();
                break;
            case "ie":
                System.out.println("in IE");
                driver = new InternetExplorerDriver();
                break;
            default:
                System.out.println("in Default");
                System.setProperty("webdriver.chrome.driver", "/var/lib/chromedriver");
                driver = new ChromeDriver();
                break;
        }
        // maximize browser's window on start
        driver.manage().window().maximize();
        return driver;
    }

    @Override
    public void close()
    {
        if (Thread.currentThread() != CLOSE_THREAD)
        {
            throw new UnsupportedOperationException("You shouldn't close this WebDriver. It's shared and will close when the JVM exits.");
        }
        super.close();
    }

    @Before
    public void deleteAllCookies()
    {
        manage().deleteAllCookies();
    }

    @After
    public void embedScreenshot(Scenario scenario)
    {
        try
        {
            byte[] screenshot = getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png");
        } catch (WebDriverException somePlatformsDontSupportScreenshots)
        {
            System.err.println(somePlatformsDontSupportScreenshots.getMessage());
        }
    }
}
