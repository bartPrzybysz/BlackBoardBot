package blackboardbot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class BBB implements BlackBoardBot {

    // -------------------- Constructors -------------------- //
    public BBB() {
        username = "";
        password = "";
    }
    public BBB(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password;
    }


    // -------------------- Fields -------------------- //
    @NotNull private String username;
    @NotNull private String password;

    @Nullable private WebDriver driver;
    @Nullable private WebDriverWait wait;
    @Nullable private JavascriptExecutor js;

    
    // -------------------- Inner Classes -------------------- //
    // Container for content areas in a course
    private class ContentArea {
        @NotNull String url;
        @NotNull String title;
        @Nullable String header;

        ContentArea() {
            url = "";
            title = "";
            header = null;
        }
        ContentArea(@NotNull String url, @NotNull String title) {
            this.url = url;
            this.title = title;
            this.header = null;
        }
        ContentArea(@NotNull String url, @NotNull String title, @Nullable String header) {
            this.url = url;
            this.title = title;
            this.header = header;
        }
    }


    // -------------------- Login Credentials -------------------- //
    @Override
    public boolean hasCredentials(){ return !username.equals("") && !password.equals(""); }

    @Override
    public void username(String username) { this.username = username; }
    @Override
    public String username() { return username; }

    @Override
    public void password(String password) { this.password = password; }
    @Override
    public String password() { return password; }


    // -------------------- Web driver functions -------------------- //
    @Override
    public boolean isRunning() { return driver != null; }

    @Override
    public void stop() {
        if(driver != null) {
            System.out.println("\nReleasing chromedriver\n");
            driver.quit();
            driver = null;
            wait = null;
            System.out.println(" -- END -- ");
        }
    }

    // Initialize Chrome Driver and login
    private void init() {
        System.out.println(" -- Start -- ");

        if (!hasCredentials()) {
            System.out.println("Login Failed - no credentials");
            return;
        }

        System.out.println("\nInitializing chromedriver\n");
        if(driver != null) { driver.quit(); }
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver , 10);
        js = (JavascriptExecutor) driver;

        System.out.println("\nLogging in as " + username);

        driver.get("https://franciscan.blackboard.com/fus.blackboard.com/webapps/login/?action=default_login");
        driver.findElement(By.id("agree_button")).click();
        driver.manage().window().maximize();
        driver.findElement(By.id("user_id")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("entry-login")).click();

        if(elementPresent(By.xpath("//*[@id=\"loginErrorMessage\"]"))) {
            System.out.println("Login Failed!");
        }
    }

    //check for presence of a WebElement
    private boolean elementPresent(By identifier) {
        assert driver != null : " WebDriver must be initialized ";

        try {
            driver.findElement(identifier);
            return true;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }
    //check for presence of a WebElement inside of a parent WebElement
    private boolean elementPresent(WebElement parent, By identifier) {
        assert driver != null : " WebDriver must be initialized ";

        try {
            parent.findElement(identifier);
            return true;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    //check for presence of an alert box
    private boolean alertPresent() {
        assert driver != null : " WebDriver must be initialized ";

        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }


    // -------------------- BlackBoard Basics -------------------- //
    // make sure edit mode is on
    private void editMode() {
        assert driver != null : " WebDriver must be initialized ";

        if (!elementPresent(By.xpath("//*[@id=\"editModeToggleLink\"]"))) {
            return;
        }
        WebElement editModeButton = driver.findElement(By.xpath("//*[@id=\"editModeToggleLink\"]"));

        if(editModeButton.findElement(By.id("statusText")).getText().equals("OFF")) {
            editModeButton.click();
        }
    }

    private void showIconsAndText() throws NoSuchElementException{
        assert driver != null : " WebDriver must be initialized ";

        if(elementPresent(By.className("contentListPlain")) || elementPresent(By.className("iconsOnly"))) {
            driver.findElement(By.id("pageTitleBar")).findElement(
                    By.xpath(".//a[contains(@class, \"cmimg\") and contains(@class, \"editmode\")]")).click();
            if(elementPresent(driver.findElement(By.className("cmdiv")) , By.linkText("Show Icons and Text"))) {
                driver.findElement(By.className("cmdiv")).findElement(By.linkText("Show Icons and Text")).click();
            }
        }
    }

    //return false if page is not workable
    private boolean goodPage() {
        assert driver != null : " WebDriver must be initialized ";

        //catch iframe
        if(elementPresent(driver.findElement(By.xpath("//*[@id=\"globalNavPageContentArea\"]")) , By.xpath("./iframe"))) {
            System.out.println("Cannot edit " + driver.getCurrentUrl() + "\n - Page is in iframe");
            return false;
        }

        //catch Access denied screen
        if(driver.findElement(By.xpath("//*[@id=\"pageTitleBar\"]")).getText().contains("Error")
                || driver.findElement(By.xpath("//*[@id=\"pageTitleBar\"]")).getText().contains("Access Denied")) {
            System.out.println("Cannot edit " + driver.getCurrentUrl() + "\n - Access Denied");
            return false;
        }

        //catch empty class screen
        if(driver.findElement(By.xpath("//*[@id=\"pageTitleBar\"]")).getText().contains(
                "There are no available items in this course")) {
            System.out.println("Cannot edit " + driver.getCurrentUrl() + "\n - No items in class");
            return false;
        }

        return true;
    }

    //get surrogate Id
    private String getSid(String url) {
        String urlEnd = url.substring(url.indexOf("course_id=") + 10);
        if(urlEnd.contains("&")) {
            return urlEnd.substring(0, urlEnd.indexOf('&'));
        } else {
            return urlEnd;
        }
    }
    private String getSid() {
        assert driver != null : " WebDriver must be initialized ";

        String url = driver.getCurrentUrl();

        String urlEnd = url.substring(url.indexOf("course_id=") + 10);
        if(urlEnd.contains("&")) {
            return urlEnd.substring(0, urlEnd.indexOf('&'));
        } else {
            return urlEnd;
        }
    }

    // returns list of all content areas in course menu
    private List<ContentArea> contentAreas() {
        assert driver != null : " WebDriver must be initialized ";

        List<ContentArea> areas = new ArrayList<>();

        WebElement courseMenu = driver.findElement(By.id("courseMenuPalette_contents"));
        //list of course menu items
        List<WebElement> ul;
        try {
            ul = courseMenu.findElements(By.tagName("li"));
        } catch (StaleElementReferenceException e) {
            courseMenu = driver.findElement(By.id("courseMenuPalette_contents"));
            ul = courseMenu.findElements(By.tagName("li"));
        }

        String surrogateId = getSid();

        int counter = 0;
        String subhead = null;
        int retry = 0;

        while(true) {
            try {
                for (int i = counter; i < ul.size(); i++) {
                    // if sub header
                    if (ul.get(i).getAttribute("class").contains("subhead")) {
                        subhead = ul.get(i).getText();
                    } else {
                        String url = ul.get(i).findElement(By.tagName("a")).getAttribute("href");
                        String title = ul.get(i).findElement(By.tagName("a")).getText();

                        //add url only if it links to a content area
                        if (url.contains("listContentEditable") && url.contains("course_id=" + surrogateId)) {
                            areas.add(new ContentArea(url, title, subhead));
                        }
                    }
                }
                break;
            } catch (StaleElementReferenceException e) {
                courseMenu = driver.findElement(By.id("courseMenuPalette_contents"));
                ul = courseMenu.findElements(By.tagName("li"));
                if(++retry == 2) throw e;
            }
        }

        return areas;
    }


    // -------------------- Traversing -------------------- //
    @FunctionalInterface
    private interface Filter {
        boolean test(WebElement item);
    }
    @FunctionalInterface
    private interface Action {
        void perform(String id);
    }

    // Traverses content area and contained subdirectores, performs action on items that pass filter test
    private void traverse(String url, Filter filter, Action action) {
        if (!driver.getCurrentUrl().equals(url)) {
            driver.get(url);
        }

        try{
            showIconsAndText();
        } catch(NoSuchElementException e) {
            System.out.println("Could not edit " + driver.getCurrentUrl() +
                    "\n - Could not find show icons and text option");
            return;
        }
        if(!goodPage()) { return; }

        //all items (id) on web page
        List<WebElement> items = driver.findElements(By.className("liItem"));
        //list of subdirectories (url) found on page
        List<String> subDirs = new ArrayList<>();
        //list of items (id) to be edited
        List<String> liIds = new ArrayList<>();

        //get urls of subdirectories and items that pass filter
        for (WebElement item : items) {

            //if item is a link to a subdirectory (contains the folder icon)
            if (item.findElement(By.className("item_icon")).getAttribute("alt").equals("Content Folder")) {
                subDirs.add(item.findElement(By.cssSelector("h3")).
                        findElement(By.cssSelector("a")).getAttribute("href"));
            }

            if (filter.test(item)) {
                liIds.add(item.getAttribute("id"));
            }
        }

        // perform action on items
        for (String liId : liIds) {
            action.perform(liId);
        }

        // traverse subdirectory
        for (String subdir : subDirs) {
            traverse(subdir, filter, action);
        }
    }


    // -------------------- revStat -------------------- //
    // Filter implementation
    private boolean revStatNotSet(WebElement item) {
        if(elementPresent(item , By.className("detailsValue"))) {

            //cycle through all details divs
            List<WebElement> details = item.findElements(By.className("detailsValue"));
            for (WebElement detail : details) {

                //if one contains review and statistics tracking, remove item from liIds
                if (detail.getText().contains("Review") && detail.getText().contains("Statistics Tracking")) {
                    return false;
                }
            }
        }
        return true;
    }

    // Action implementation
    private void setRevStat(String id) {
        WebElement item = driver.findElement(By.id(id));

        // Content id and course id, to be added to url
        String itemIdentifier =
                id.replace("contentListItem:", "content_id=").concat("&course_id=").concat(getSid());

        boolean needsRev = true;
        boolean needsStat = true;

        if(elementPresent(item , By.className("detailsValue"))) {

            //cycle through all details divs
            List<WebElement> details = item.findElements(By.className("detailsValue"));
            for (WebElement detail : details) {

                //if contains review, no review status action is needed
                if (detail.getText().contains("Review")){
                    needsRev = false;
                }

                //if contains statistics tracking, no statistics tracking action is needed
                if (detail.getText().contains("Statistics Tracking")) {
                    needsStat = false;
                }
            }
        }

        if (needsRev) rev(itemIdentifier);
        if (needsStat) stat(itemIdentifier);
    }

    // Set Review Status
    private void rev(String identifier) {
        driver.get("https://franciscan.blackboard.com/webapps/blackboard/content/manageReview.jsp?" + identifier);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("enableReview_true")));

        js.executeScript("document.getElementById('enableReview_true').checked = true;");

        driver.findElement(By.name("bottom_Submit")).click();
        if(alertPresent()) {
            driver.switchTo().alert().accept();
        }
    }

    // Set Statistics Tracking
    private void stat(String identifier) {
        driver.get("https://franciscan.blackboard.com/webapps/blackboard/content/manageTracking.jsp?" + identifier);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("enableTrackingYes")));

        js.executeScript("document.getElementById('enableTrackingYes').checked = true;");

        driver.findElement(By.name("bottom_Submit")).click();
        if(alertPresent()) {
            driver.switchTo().alert().accept();
        }
    }

    @Override
    public void revStat(String url) {
        init();

        driver.get(url);

        //Make sure page is workable
        if(!goodPage()) { return; }

        //if edit mode is off, turn it on
        editMode();

        List<ContentArea> areas = contentAreas();

        for (ContentArea area : areas) {
            System.out.println("Looking in '" + area.title + "'");
            traverse(area.url, this::revStatNotSet, this::setRevStat);
        }

        System.out.println("\nAll done!");

        stop();
    }
}
