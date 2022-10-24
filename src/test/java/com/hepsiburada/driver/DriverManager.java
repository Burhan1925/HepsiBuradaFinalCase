package com.hepsiburada.driver;

import com.thoughtworks.gauge.ExecutionContext;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DriverManager {
    public static AppiumDriver appiumDriver;
    public static WebDriver webDriver;
    public static JavascriptExecutor getJSExecutor;
    public static WebDriverWait mobileWait;
    public static WebDriverWait webWait;
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = (OS.contains("win"));
    private static final boolean IS_MAC = (OS.contains("mac"));
    private static final boolean IS_LINUX = (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    public static boolean IS_CHROME;
    public static boolean IS_EDGE;
    public static boolean IS_FIREFOX;
    public static boolean IS_ANDROID;
    public static boolean IS_IOS;

    private final String WEB_URL = "https://www.hepsiburada.com/";
    private final Logger logger;

    public DriverManager() {
        String log4jConfigFile = System.getProperty("user.dir") + File.separator + "src/test/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
        logger = Logger.getLogger(DriverManager.class);

    }

    protected void initDriver(ExecutionContext context) throws MalformedURLException {
        initilazeTest(context);
    }

    private void initilazeTest(ExecutionContext context) throws MalformedURLException {
        setTestType(context);
        setDriver();
    }

    private void setDriver() throws MalformedURLException {
        if(IS_CHROME){
            setChromeDriver();
            setWebWait();
        } else if(IS_FIREFOX){
            setFirefoxDriver();
            setWebWait();
        } else if(IS_EDGE){
            setEdgeDriver();
            setWebWait();
        }
        if(IS_ANDROID){
            setupAndroid();
        } else if(IS_IOS){
            setupIos();
        }
    }
    private void setWebWait() {
        webWait = new WebDriverWait(webDriver, 10);
        getJSExecutor = (JavascriptExecutor) webDriver;
        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        webWait.withTimeout(Duration.ofSeconds(10)).pollingEvery(Duration.ofMillis(300)).ignoring(NoSuchElementException.class);
        webWait.withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class);
        webDriver.manage().window().maximize();
        webDriver.get(WEB_URL);
    }
    private void setTestType(ExecutionContext context){
        List<String> list = context.getCurrentScenario().getTags();
        list = list.stream().map(String::toLowerCase).collect(Collectors.toList());
        if (list.isEmpty()) {
            list = context.getCurrentSpecification().getTags();
            list = list.stream().map(String::toLowerCase).collect(Collectors.toList());
        }
        if (list.contains("chrome")) {
            IS_CHROME = true;
        } else if (list.contains("fırefox") || list.contains("firefox")) {
            IS_FIREFOX = true;
        } else if (list.contains("edge")) {
            IS_EDGE = true;
        } else if (list.contains("android") || list.contains("androıd")) {
            IS_ANDROID = true;
        } else if (list.contains("ios") || list.contains("ıos")) {
            IS_IOS = true;
        } else {
            logger.error("Senaryoda tag bulunamadi veya desteklenmeyen bir tag girildi.");
            Assertions.fail("Senaryoda tag bulunamadı veya desteklenmeyen bir tag girildi.");
        }
    }

    public void closeDriver() {
        if (appiumDriver != null) {
            appiumDriver.quit();
            logger.info("Appium driver kapatıldı.");
        }
        if (webDriver != null) {
            webDriver.quit();
            logger.info("Web driver kapatıldı.");
        }
        IS_CHROME = false;
        IS_EDGE = false;
        IS_FIREFOX = false;
        IS_IOS = false;
        IS_ANDROID = false;
    }

    private void setChromeDriver() {
        if (IS_WINDOWS) {
            WebDriverManager.chromedriver().win().setup();
        } else if (IS_MAC) {
            WebDriverManager.chromedriver().mac().setup();
        } else if (IS_LINUX) {
            WebDriverManager.chromedriver().linux().setup();
        }
        webDriver = new ChromeDriver(getChromeOptions());
        logger.info("Web driver başlatılıyor.");
    }

    private void setFirefoxDriver() {
        if (IS_WINDOWS) {
            WebDriverManager.firefoxdriver().win().setup();
        } else if (IS_MAC) {
            WebDriverManager.firefoxdriver().mac().setup();
        } else if (IS_LINUX) {
            WebDriverManager.firefoxdriver().linux().setup();
        }
        webDriver = new FirefoxDriver(getFirefoxOptions());
    }

    private void setEdgeDriver() {
        if (IS_WINDOWS) {
            WebDriverManager.edgedriver().win().setup();
        } else if (IS_MAC) {
            WebDriverManager.edgedriver().mac().setup();
        } else if (IS_LINUX) {
            WebDriverManager.edgedriver().linux().setup();
        }
        webDriver = new EdgeDriver(getEdgeOptions());
    }

    public ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-translate");
        options.addArguments("start-maximized");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-blink-features=AutomationControlled");
        return options;
    }

    public FirefoxOptions getFirefoxOptions() {
        FirefoxProfile profile = new FirefoxProfile();
        FirefoxOptions options = new FirefoxOptions();
        profile.setPreference("browser.download.folderList", 1);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.manager.focusWhenStarting", false);
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.download.manager.closeWhenDone", true);
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        profile.setPreference("browser.download.manager.useWindow", false);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");
        options.setProfile(profile);
        return options;
    }

    public EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        options.setCapability("acceptSslCerts", true);
        options.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "eager");
        return options;
    }

    protected void setupAndroid() throws MalformedURLException {
        DesiredCapabilities desiredCapabilitiesAndroid = new DesiredCapabilities();
        desiredCapabilitiesAndroid.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
        desiredCapabilitiesAndroid.setCapability(MobileCapabilityType.DEVICE_NAME, "android");
        desiredCapabilitiesAndroid.setCapability(MobileCapabilityType.UDID, "emulator-5554");
        desiredCapabilitiesAndroid.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "example");
        desiredCapabilitiesAndroid.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "example");
        desiredCapabilitiesAndroid.setCapability(MobileCapabilityType.NO_RESET, false);
        desiredCapabilitiesAndroid.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 350);
        desiredCapabilitiesAndroid.setCapability("unicodeKeyboard", true);
        desiredCapabilitiesAndroid.setCapability("resetKeyboard", true);
        URL url = new URL("http://0.0.0.0:4723/wd/hub");
        appiumDriver = new AndroidDriver(url, desiredCapabilitiesAndroid);
        mobileWait =  new WebDriverWait(appiumDriver, 20);
        mobileWait.withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(400))
                .ignoring(NoSuchElementException.class);
        logger.info("Android emulator başlatılıyor.");
    }

    protected void setupIos() throws MalformedURLException {
        DesiredCapabilities desiredCapabilitiesIos = new DesiredCapabilities();
        desiredCapabilitiesIos.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.IOS);
        desiredCapabilitiesIos.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        desiredCapabilitiesIos.setCapability(MobileCapabilityType.UDID, "1235678asdfghjjk1234567asdffgh");
        desiredCapabilitiesIos.setCapability(MobileCapabilityType.DEVICE_NAME, "example");
        desiredCapabilitiesIos.setCapability(MobileCapabilityType.PLATFORM_VERSION, "12.4");
        desiredCapabilitiesIos.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "bundleIdHere");
        desiredCapabilitiesIos.setCapability(MobileCapabilityType.NO_RESET, true);
        desiredCapabilitiesIos.setCapability(MobileCapabilityType.FULL_RESET, false);
        desiredCapabilitiesIos.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 300);
        mobileWait =  new WebDriverWait(webDriver, 15);
        mobileWait.withTimeout(30, TimeUnit.SECONDS).pollingEvery(1000, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);
        URL url = new URL("http://127.0.0.1:4723/wd/hub");
        appiumDriver = new IOSDriver<>(url, desiredCapabilitiesIos);
        logger.info("IOS emulator başlatılıyor.");
    }
}


