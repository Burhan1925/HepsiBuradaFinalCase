package com.hepsiburada.driver;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.ExecutionContext;
import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.net.MalformedURLException;

public class BaseTest extends DriverManager {
    public static Logger logger;

    public BaseTest() {
        logger = Logger.getLogger(BaseTest.class);
    }

    @BeforeScenario
    public void setup(ExecutionContext context) throws MalformedURLException {
        initDriver(context);
    }

    @AfterScenario
    public void AfterScenario() {
        closeDriver();
    }
}