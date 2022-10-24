package com.hepsiburada.methods;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hepsiburada.driver.BaseTest;
import com.thoughtworks.gauge.Step;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Methods extends BaseTest {
    //private final static String ELEMENTS_PATH = "src/test/resources/elements/elements.json";
    private final static String ELEMENTS_JSON_PATH = "element";
    private final static String WEB = "web";
    private final static String ANDROID = "android";
    private final static String IOS = "ios";

    protected static WebElement webElement;
    public Map<String, Object> elementsMap;
    public Logger logger;
    protected MobileElement mobileElement;

    public Methods() {
        logger = Logger.getLogger(Methods.class);
        initByMap(getFileList());
    }


    public void waitBySeconds(long seconds) {
        try {
            Thread.sleep(seconds * 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void click(String keyword) {
        String device = getDeviceInfo(keyword);
        if (device.equals(ANDROID) || device.equals(IOS)) {
            findElementMobile(keyword).click();
            logger.info(keyword + " ANDROID elementine tıklanır.");
        } else if (device.equals(WEB)) {
            try{
                WebElement element = findElementWeb(keyword);
                element.click();
                logger.info(keyword + " WEB elementine tıklanır.");
            }catch (ElementClickInterceptedException e) {
                webWait.until(ExpectedConditions.elementToBeClickable(getByTypeWithMap(keyword)));
                click(keyword);
            }catch (StaleElementReferenceException e) {
                findElement(keyword).click();
                logger.info(keyword + " WEB elementine tıklanır.");
            }catch (ElementNotInteractableException e) {
                findElement(keyword).click();
                logger.info(keyword + " WEB elementine tıklanır.");
            } }
        else {
            logger.error(keyword + " WEB elementine tıklanamadı.");
        }
    }

    public WebElement findElement(String keyword) {
        By by = getByTypeWithMap(keyword);
        return webWait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public MobileElement findElementMobile(String keyword) {
        By by = getByTypeWithMap(keyword);
        return (MobileElement) mobileWait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement findElementWeb(String keyword) {
        return webWait.until(ExpectedConditions.presenceOfElementLocated(getByTypeWithMap(keyword)));
    }

    public List<WebElement> findElementsWeb(String keyword) {
        By by = getByTypeWithMap(keyword);
        webWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        return webDriver.findElements(by);
    }

    public void NewTab() {
        findElement("ProductSelect").click();
        webDriver.getWindowHandles().forEach(tab -> webDriver.switchTo().window(tab));
        //driver.findElement(By.xpath("//span[text()='xxxx']")).click();
        //driver.get("xxxx"); ---> url adresine gidilir.
    }

    public void sendKeys(String keyword, String text) {
        if (getDeviceInfo(keyword).equals(ANDROID) || getDeviceInfo(keyword).equals(IOS)) {
            findElementMobile(keyword).clear();
            findElementMobile(keyword).sendKeys(text);
            logger.info(keyword + " elementine " + text + " değeri girildi.");
        } else if (getDeviceInfo(keyword).equals(WEB)) {
            findElementWeb(keyword).clear();
            findElementWeb(keyword).sendKeys(text);
            logger.info(keyword + " WEB elementine " + text + " değeri girildi.");
        }
    }

    public Select getSelectText(String keyword) {
        return new Select(findElement(keyword));
    }

    public By getByTypeWithMap(String keyword) {
        ElementInfo elements = (ElementInfo) elementsMap.get(keyword);
        Map<String, By> map = initByMap(elements.getLocatorValue());
        return map.getOrDefault(elements.getLocatorType(), null);
    }

    public Map<String, By> initByMap(String locatorValue) {
        Map<String, By> map = new HashMap<>();
        map.put("id", By.id(locatorValue));
        map.put("css", By.cssSelector(locatorValue));
        map.put("xpath", By.xpath(locatorValue));
        map.put("class", By.className(locatorValue));
        map.put("linktext", By.linkText(locatorValue));
        map.put("name", By.name(locatorValue));
        map.put("partial", By.partialLinkText(locatorValue));
        map.put("tagname", By.tagName(locatorValue));
        return map;
    }

    public void initByMap(File[] fileList) {
        elementsMap = new ConcurrentHashMap<>();
        Type elementType = new TypeToken<List<ElementInfo>>() {
        }.getType();
        Gson gson = new Gson();
        List<ElementInfo> elementInfoList;
        for (File file : fileList) {
            try {
                elementInfoList = gson.fromJson(new FileReader(file), elementType);
                elementInfoList.parallelStream().forEach(elementInfo -> elementsMap.put(elementInfo.getKeyword(), elementInfo));
            } catch (FileNotFoundException e) {
            }
        }
    }

    public File[] getFileList() {
        File[] fileList = new File(this.getClass().getClassLoader().getResource(ELEMENTS_JSON_PATH).getFile()).listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".json"));
        if (fileList == null) {
            throw new NullPointerException("Belirtilen dosya bulunamadı.");
        }
        return fileList;
    }

    public void selectByText(String keyword, String text) {
        getSelectText(keyword).selectByVisibleText(text);
    }

    public void clickWithJS(String keyword) {
        getJSExecutor.executeScript("arguments[0].click();", findElement(keyword));
        logger.info(keyword + " WEB elementine JavaScript ile tıklandı.");
    }

    public boolean isElementVisibleWithoutLog(String keyword) {
        try {
            webDriver.findElement(getByTypeWithMap(keyword));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void waitIfNotExist(String key) {
        if (!isElementVisibleWithoutLog(key)) {
            webWait.until(ExpectedConditions.visibilityOfElementLocated(getByTypeWithMap(key)));
        }
    }

    public void refreshPage() {
        webDriver.navigate().refresh();
    }

    public void goToUrl(String url) {
        webDriver.get(url);
    }

    public boolean isElementVisible(String keyword) {
        if (getDeviceInfo(keyword).equals(ANDROID) || getDeviceInfo(keyword).equals(IOS)) {
            try {
                mobileElement = findElementMobile(keyword);
                logger.info(keyword + " elementi görünür.");
                return true;
            } catch (Exception e) {
                logger.warn(keyword + " Element görünür değil yada bulunamadı." + e);
                return false;
            }
        } else if (getDeviceInfo(keyword).equals(WEB)) {
            try {
                webElement = findElementWeb(keyword);
                waitBySeconds(1);
                logger.info(keyword + " WEB elementi görünür.");
                return true;
            } catch (Exception e) {
                logger.warn(keyword + " WEB Elementi görünür değil yada bulunamadı." + e);
                return false;
            }
        }
        return false;
    }

    public boolean isElementVisibleNoWait(String keyword) {
        By by = getByTypeWithMap(keyword);
        try {
            webDriver.findElement(by);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public String getText(String keyword) {
        return findElement(keyword).getText();
    }

    public void clickIfExist(String keyword) {
        if (isElementVisible(keyword)) {
            click(keyword);
        }
    }

    public String getDeviceInfo(String keyword) {
        ElementInfo elements = (ElementInfo) elementsMap.get(keyword);
        return elements.getDeviceInfo();
    }

    public static class ElementInfo {
        @Getter
        protected String keyword;
        @Getter
        protected String locatorValue;
        @Getter
        protected String locatorType;
        @Getter
        protected String deviceInfo;

        @Override
        public String toString() {
            return "Elements[" + "keyword=" + keyword + ",locatorType=" + locatorType + ",locatorValue=" + locatorValue + "deviceInfo=" + deviceInfo + "]";
        }
    }
}


