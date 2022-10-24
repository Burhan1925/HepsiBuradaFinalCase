package com.hepsiburada.imp;

import com.thoughtworks.gauge.Step;
import io.appium.java_client.MobileElement;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.hepsiburada.methods.Methods;
import java.util.List;


public class StepImp  {
    protected Methods methods;
    public List<WebElement> list;
    protected WebElement webElement;
    public By by;

    public StepImp() {
        methods = new Methods();
    }

    @Step("<key> li elemente tıklanır.")
    public void click(String keyword) {
        methods.click(keyword);
    }

    @Step({"<key> elementine javascript ile tıklanır."})
    public void clickWithJS(String keyword) {
        methods.clickWithJS(keyword);
    }

    @Step("<key> 'li elemente görünür ise tıklanır.")
    public void clickIfExist(String keyword) {
        methods.clickIfExist(keyword);
    }

    @Step("<key> elementi görünür değil ise beklenir.")
    public void waitIfNotExist(String key) {
        methods.waitIfNotExist(key);
    }

    @Step("<second> saniye kadar beklenir.")
    public void waitForsecond(int second) throws InterruptedException {
        methods.waitBySeconds(second);
    }

    @Step("<Key> li elementinin görünürlüğü kontrol edilir.")
    public boolean isElementVisible(String keyword) {
        return methods.isElementVisible(keyword);
    }

    @Step("Yeni sekmede seçilen ürün açılır.")
    public void NewTabs(){
        methods.NewTab();
    }

    @Step("<key> 'li elemente <text> degeri girilir.")
    public void sendKeys(String keyword, String text) {
        methods.sendKeys(keyword, text);
    }

    @Step("Sayfayı yeniden yükler.")
    public void refreshPage() {
        methods.refreshPage();
    }

    @Step("<key> adresine gidilir.")
    public void goToUrl(String url) { methods.goToUrl(url); }

    @Step("elementin <key> , <text> texti seçilir.")
    public void selectByText(String keyword, String text) {
        methods.selectByText(keyword, text);
    }

    @Step("<key> 'li text bilgisi yazılır.")
    public void getSelectText(String keyword){
        methods.getSelectText(keyword);
    }

}
