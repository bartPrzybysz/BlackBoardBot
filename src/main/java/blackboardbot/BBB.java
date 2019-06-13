package blackboardbot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;

public class BBB implements BlackBoardBot {

    // -------------------- Constructors -------------------- //

    /**
     * Creates instance with blank login credentials, <b>not recommended, use {@link #BBB(String, String)} instead</b>
     */
    public BBB() {
        username = "";
        password = "";
    }

    /**
     * Creates instance with BlackBoard login credentials stored
     *
     * @param username BlackBoard username
     * @param password password
     */
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

        @NotNull String url;
        @NotNull String title;
        @Nullable String header;
    }

    // Container for course information, method for checking if satisfies constraints
    private class Course implements Comparable<String> {
        @Nullable String courseId;
        @Nullable String surrogateId;
        @Nullable String url;
        @Nullable String title;
        @Nullable HashSet<String> instructorId;
        @Nullable String term;
        @Nullable String department;
        @Nullable Integer course;
        @Nullable String section;
        @Nullable String session;

        @Override
        public int compareTo(@NotNull String o) {
            if (courseId == null) return -1;

            return courseId.compareTo(o);
        }

        void inputUrl(String url) {
            this.url = url;
            this.surrogateId = getSid(url);
        }

        void inputId(String id) {
            courseId = id;

            String[] splitId = id.split("-");

            if (splitId.length < 3) {
                return;
            }

            if (splitId[0].length() == 6 && isNumber(splitId[0])) {
                term = splitId[0];
            } else if (splitId[0].length() < 6) {
                term = "0";
            } else if (isNumber(splitId[0].substring(0, 6))) {
                term = splitId[0].substring(0, 6);
            }

            if (splitId[1].length() == 3 && !isNumber(splitId[1])) {
                department = splitId[1];
            }

            if (isNumber(splitId[2])) {
                course = Integer.parseInt(splitId[2]);
            }

            if (splitId[3].equals("OL")) {
                if (splitId.length == 4) {
                    session = "OL";
                    return;
                } else {
                    if (splitId[4].contains("1")){
                        session = "OL-1";

                        if (splitId[4].length() > 1) {
                            section = splitId[4].substring(1);
                        }
                    } else if(splitId[4].contains("2")) {
                        session ="OL-2";

                        if (splitId[4].length() > 1) {
                            section = splitId[4].substring(1);
                        }
                    } else {
                        session = "OL";
                    }
                }
            } else if (splitId[3].equals("GA")) {
                session = "GA";

                if (splitId.length == 5) {
                    if (splitId[4].equals("1")) {
                        session = "GA-1";
                    }
                    if (splitId[4].equals("2")) {
                        session = "GA-2";
                    }
                }
            } else if (splitId[3].length() == 1 && splitId[3].charAt(0) >= 'A' && splitId[3].charAt(0) <= 'N') {
                section = splitId[3];
            }

            if (splitId.length == 5) {
                if(section == null && splitId[4].charAt(0) >= 'A' && splitId[4].charAt(0) <= 'N') {
                    section = splitId[4];
                }
            }
        }

        void inputInstructorId(String instructors) {
            String[] instructorArray = instructors.split(",");

            if (instructorId == null) instructorId = new HashSet<>();

            for (String i : instructorArray) {
                instructorId.add(i.trim());
            }
        }

        private boolean isNumber(String text) {
            for (char a : text.toCharArray()) {
                if (!Character.isDigit(a)) {
                    return false;
                }
            }

            return true;
        }

        boolean satisfies(ConstraintSet.Constraint constraint) {
            switch (constraint.type) {
                case INCLUDE:
                    if (courseId != null && constraint.courseId != null) {
                        if (!constraint.courseId.contains(courseId)) return false;
                    }

                    if (department != null && constraint.department != null) {
                        if (!constraint.department.contains(department)) return false;
                    }

                    if (course != null && constraint.course != null) {
                        if (!constraint.course.contains(course)) return false;
                    }

                    if (course != null && constraint.courseGreaterThan != null) {
                        if (course < constraint.courseGreaterThan) return false;
                    }

                    if (course != null && constraint.courseLessThan != null) {
                        if (course > constraint.courseLessThan) return false;
                    }

                    if (instructorId != null && constraint.instructorId != null) {
                        if (Collections.disjoint(instructorId, constraint.instructorId)) return false;
                    }
                    break;
                case EXCLUDE:
                    if (courseId != null && constraint.courseId != null) {
                        if (constraint.courseId.contains(courseId)) return false;
                    }

                    if (department != null && constraint.department != null) {
                        if (constraint.department.contains(department)) return false;
                    }

                    if (course != null && constraint.course != null) {
                        if (constraint.course.contains(course)) return false;
                    }

                    if (course != null && constraint.courseGreaterThan != null) {
                        if (course >= constraint.courseGreaterThan) return false;
                    }

                    if (course != null && constraint.courseLessThan != null) {
                        if (course <= constraint.courseLessThan) return false;
                    }

                    if (instructorId != null && constraint.instructorId != null) {
                        if (!Collections.disjoint(instructorId, constraint.instructorId)) return false;
                    }
                    break;
            }

            return true;
        }

        boolean satisfies(ConstraintSet constraintSet) {
            if (term != null) {
                if (!term.equals(constraintSet.term)) return false;
            }

            if (session != null && !constraintSet.session.equalsIgnoreCase("all")) {
                if (!session.equalsIgnoreCase(constraintSet.session)) return false;
            }

            if (constraintSet.constraints == null) return true;

            for (ConstraintSet.Constraint constraint : constraintSet.constraints) {
                if (!satisfies(constraint)) return false;
            }

            return true;
        }
    }

    // Exception for edit html button not found
    private class EditHtmlNotFoundException extends Exception {
        EditHtmlNotFoundException(String itemTitle) {
            super(itemTitle);
            System.out.println("Could not edit '" + itemTitle + "', could not find html editor");
        }
    }

    // Exception for login failed
    private class LoginFailedException extends Exception {
        LoginFailedException(String message) {
            super(message);
            System.out.println(message);
        }
    }


    // -------------------- Login Credentials -------------------- //
    @Override
    public boolean hasCredentials(){ return !username.equals("") && !password.equals(""); }

    @Override
    public void username(@NotNull String username) { this.username = username; }
    @Override
    public String username() { return username; }

    @Override
    public void password(@NotNull String password) { this.password = password; }
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
            js = null;
            System.out.println(" -- END -- ");
        }
    }

    // Initialize Chrome Driver and login
    private void init() throws LoginFailedException {
        System.out.println(" -- START -- ");

        if (!hasCredentials()) {
            throw new LoginFailedException("\nLogin Failed - no credentials\n");
        }

        System.out.println("\nInitializing chromedriver\n");
        if(driver != null) { driver.quit(); }
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver , 10);
        js = (JavascriptExecutor) driver;

        System.out.println("\nLogging in as " + username + "\n");

        driver.get("https://franciscan.blackboard.com/fus.blackboard.com/webapps/login/?action=default_login");
        driver.findElement(By.id("agree_button")).click();
        driver.manage().window().maximize();
        driver.findElement(By.id("user_id")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("entry-login")).click();

        if(elementPresent(By.xpath("//*[@id=\"loginErrorMessage\"]"))) {
            throw new LoginFailedException("Login Failed!\n");
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

        return getSid(url);
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

    private Course getCourse(String courseId) {
        assert driver != null : " WebDriver must be initialized ";

        System.out.println("Getting course: \n" + courseId);

        //Navigate to courseManager
        driver.get("https://franciscan.blackboard.com/webapps/blackboard/execute/courseManager");
        //Search constrained classes
        driver.findElement(By.id("courseInfoSearchKeyString")).sendKeys("Organization ID");

        driver.findElement(By.id("courseInfoSearchText")).sendKeys(courseId);
        driver.findElement(By.className("button-4")).click();

        List<WebElement> rows = driver.findElement(By.id("listContainer_databody")).findElements(By.tagName("tr"));

        if (rows.size() > 1) {
            System.out.println("There are more courses than expected, picking first one off list");
        }

        WebElement row = rows.get(0);

        if (!elementPresent(row, By.tagName("a"))) {
            return new Course();
        }

        String link = row.findElement(By.tagName("a")).getAttribute("href");

        String id = row.findElement(By.tagName("a")).getText();

        String courseName = row.findElement(By.xpath("td[3]/span[@class='table-data-cell-value']")).getText();

        String instructorId = row.findElement(By.xpath("td[5]/span[@class='table-data-cell-value']")).getText();

        Course c = new Course();
        c.inputId(id);
        c.inputUrl(link);
        c.inputInstructorId(instructorId);
        c.title = courseName;

        return c;
    }

    private HashSet<Course> getCourses(ConstraintSet constraintSet) {
        assert driver != null : " WebDriver must be initialized ";

        System.out.println("Getting courses that satisfy the constraints: \n" + constraintSet);

        //Navigate to courseManager
        driver.get("https://franciscan.blackboard.com/webapps/blackboard/execute/courseManager");
        //Search constrained classes
        driver.findElement(By.id("courseInfoSearchKeyString")).sendKeys("Organization ID");

        String searchText = constraintSet.term;
        if (constraintSet.session.equalsIgnoreCase("all")) {
            searchText = searchText.concat(" -OL");
        } else {
            searchText = searchText.concat(" -").concat(constraintSet.session);
        }

        driver.findElement(By.id("courseInfoSearchText")).sendKeys(searchText);
        driver.findElement(By.className("button-4")).click();
        try {
            driver.findElement(By.id("listContainer_showAllButton")).click();
        } catch (NoSuchElementException e) {
            // just ignore this
        }

        //Extract course information and links
        List<WebElement> rows = new ArrayList<>();
        try {
            rows = driver.findElement(By.id("listContainer_databody")).findElements(By.tagName("tr"));
        } catch (NoSuchElementException e) {
            // just ignore this
        }
        HashSet<Course> courses = new HashSet<>();

        for (WebElement row : rows) {
            if (!elementPresent(row, By.tagName("a"))) {
                continue;
            }

            String link = row.findElement(By.tagName("a")).getAttribute("href");

            String id = row.findElement(By.tagName("a")).getText();

            String courseName = row.findElement(By.xpath("td[3]/span[@class='table-data-cell-value']")).getText();

            String instructorId = row.findElement(By.xpath("td[5]/span[@class='table-data-cell-value']")).getText();

            Course c = new Course();
            c.inputId(id);
            c.inputUrl(link);
            c.inputInstructorId(instructorId);
            c.title = courseName;

            if(c.satisfies(constraintSet)) courses.add(c);
        }

        //If a constraint specifies include specific ids, make sure they're all included
        if (constraintSet.constraints == null) {
            printCourseSearchResults(courses);
            return courses;
        }

        HashSet<String> neededCourses = null;

        for (ConstraintSet.Constraint constraint : constraintSet.constraints) {
            if (constraint.type == ConstraintSet.ConstraintType.INCLUDE && constraint.courseId != null) {
                if (neededCourses == null) neededCourses = new HashSet<>();

                neededCourses.addAll(constraint.courseId);
            }
        }

        if (neededCourses == null) {
            printCourseSearchResults(courses);
            return courses;
        }

        HashSet<String> currentCourses = new HashSet<>();

        for (Course course : courses) {
            currentCourses.add(course.courseId);
        }

        if (!currentCourses.containsAll(neededCourses)) {
            HashSet<String> toAdd = new HashSet<>();

            for(String cId : neededCourses) {
                if (!currentCourses.contains(cId)) toAdd.add(cId);
            }

            for (String cId : toAdd) {
                driver.findElement(By.id("courseInfoSearchText")).clear();
                driver.findElement(By.id("courseInfoSearchText")).sendKeys(cId);
                driver.findElement(By.className("button-4")).click();
                driver.findElement(By.id("listContainer_showAllButton")).click();

                //Extract course information and links
                rows = driver.findElement(By.id("listContainer_databody")).findElements(By.tagName("tr"));

                for (WebElement row : rows) {
                    if (!elementPresent(row, By.tagName("a"))) {
                        continue;
                    }

                    String link = row.findElement(By.tagName("a")).getAttribute("href");

                    String id = row.findElement(By.tagName("a")).getText();

                    String courseName = row.findElement(By.xpath("td[3]/span[@class='table-data-cell-value']")).getText();

                    String instructorId = row.findElement(By.xpath("td[5]/span[@class='table-data-cell-value']")).getText();

                    Course c = new Course();
                    c.inputId(id);
                    c.inputUrl(link);
                    c.inputInstructorId(instructorId);
                    c.title = courseName;

                    if (c.courseId.trim().equals(cId.trim())) courses.add(c);
                }
            }
        }

        printCourseSearchResults(courses);
        return courses;
    }
    // utility of getCourses()
    private void printCourseSearchResults(HashSet<Course> courses) {
        System.out.println("Courses found: ");
        if (courses.isEmpty()) System.out.println("(none)");
        for (Course course : courses) {
            System.out.println(course.courseId);
        }
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

    // Traverses content area and contained subdirectories, performs action on items that pass filter test
    private void traverse(String url, Filter filter, Action action) {
        assert driver != null : " WebDriver must be initialized ";

        if (!driver.getCurrentUrl().equals(url)) driver.get(url);

        try{
            showIconsAndText();
        } catch(NoSuchElementException e) {
            System.out.println("Could not edit " + driver.getCurrentUrl() +
                    "\n - Could not find show icons and text option");
            return;
        }

        if(!goodPage()) { return; }

        //all items on web page
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
        String title = item.findElement(By.tagName("h3")).getText();

        // Omit checklists and optional items
        if (title.contains("Checklist") || title.contains("Optional")) return false;

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
        assert driver != null : " WebDriver must be initialized ";

        WebElement item = driver.findElement(By.id(id));

        // Content id and course id, to be added to url
        String itemIdentifier =
                id.replace("contentListItem:", "content_id=").concat("&course_id=").concat(getSid());

        boolean needsRev = true;
        boolean needsStat = true;

        // make sure review status and statistics tracking successfully set, if not, try again
        do {
            if (elementPresent(item, By.className("detailsValue"))) {

                //cycle through all details divs
                List<WebElement> details = item.findElements(By.className("detailsValue"));
                for (WebElement detail : details) {

                    //if contains review, no review status action is needed
                    if (detail.getText().contains("Review")) {
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

            item = driver.findElement(By.id(id));
        } while (revStatNotSet(item));
    }

    // Set Review Status
    private void rev(String identifier) {
        assert driver != null : " WebDriver must be initialized ";

        driver.get("https://franciscan.blackboard.com/webapps/blackboard/content/manageReview.jsp?" + identifier);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("enableReview_true")));

        // check box
        js.executeScript("document.getElementById('enableReview_true').checked = true;");

        // wait until page done loading
        wait.until(
                webDriver -> ((JavascriptExecutor) webDriver).
                        executeScript("return document.readyState").equals("complete"));

        while (elementPresent(By.name("bottom_Submit"))) {
            driver.findElement(By.name("bottom_Submit")).click();
        }

        if(alertPresent()) driver.switchTo().alert().accept();
    }

    // Set Statistics Tracking
    private void stat(String identifier) {
        assert driver != null : " WebDriver must be initialized ";

        driver.get("https://franciscan.blackboard.com/webapps/blackboard/content/manageTracking.jsp?" + identifier);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("enableTrackingYes")));

        // check box
        js.executeScript("document.getElementById('enableTrackingYes').checked = true;");

        // wait until page done loading
        wait.until(
                webDriver -> ((JavascriptExecutor) webDriver).
                        executeScript("return document.readyState").equals("complete"));

        while (elementPresent(By.name("bottom_Submit"))) {
            driver.findElement(By.name("bottom_Submit")).click();
        }

        if(alertPresent()) driver.switchTo().alert().accept();
    }

    @Override
    public void revStat(@NotNull String url, boolean learningSessionsOnly) {
        try {
            init();
        } catch (LoginFailedException e) {
            System.out.println(" -- END --");
            return;
        }

        System.out.println("Setting review status and statistics tracking in " + url);

        driver.get(url);

        //Make sure page is workable
        if(!goodPage()) { return; }

        //if edit mode is off, turn it on
        editMode();

        List<ContentArea> areas = contentAreas();

        // If running on learning sessions only, remove all non learning sessions content areas from list
        if (learningSessionsOnly) {
            areas.removeIf(area -> !area.title.equalsIgnoreCase("Learning Sessions"));
        }

        for (ContentArea area : areas) {
            System.out.println("Looking in '" + area.title + "'");
            traverse(area.url, this::revStatNotSet, this::setRevStat);
        }

        System.out.println("\nAll done!");

        stop();
    }

    @Override
    public void revStat(@NotNull Constraints constraints, boolean learningSessionsOnly) {
        try {
            init();
        } catch (LoginFailedException e) {
            System.out.println(" -- END --");
            return;
        }

        System.out.println("Setting Review Status and Statistics Tracking");

        HashSet<Course> courses = getCourses((ConstraintSet) constraints);

        for (Course course : courses) {
            System.out.println("\nWorking on " + course.courseId);

            driver.get(course.url);

            //Make sure page is workable
            if(!goodPage()) { continue; }

            //if edit mode is off, turn it on
            editMode();

            List<ContentArea> areas = contentAreas();

            // If running on learning sessions only, remove all non learning sessions content areas from list
            if (learningSessionsOnly) {
                areas.removeIf(area -> !area.title.equalsIgnoreCase("Learning Sessions"));
            }

            for (ContentArea area : areas) {
                System.out.println("Looking in '" + area.title + "'");
                traverse(area.url, this::revStatNotSet, this::setRevStat);
            }

            System.out.println("Done with " + course.courseId);
        }

        System.out.println("\nAll done!");

        stop();
    }


    // -------------------- titleColor -------------------- //
    // Filter implementation
    private boolean wrongTitleColor(WebElement item) {
        WebElement title = item.findElement(By.tagName("h3"));

        String style;

        // If title is a link
        if(elementPresent(title , By.tagName("a"))) {
            // sometimes font color is in font tag <font color="#00748b">
            if (elementPresent(title, By.tagName("font"))) {
                style = title.findElement(By.tagName("font")).getAttribute("color");
            } else {//but most of the time its in <span style="...">
                style = title.findElement(By.xpath(".//a/span")).getAttribute("style");
                style += title.getAttribute("innerHTML");
            }

            // should be blue
            if(!style.contains("rgb(0,116,139)") && !style.contains("#00748b")) {
                return true;
            }
        } else { // if title is not a link
            // sometimes font color is in font tag <font color="#00748b">
            if (elementPresent(title, By.tagName("font"))) {
                style = title.findElement(By.tagName("font")).getAttribute("color");
            } else {//but most of the time its in <span style="...">
                style = title.findElement(By.xpath("./span[2]")).getAttribute("style");
            }

            // should be black
            if(!style.contains("rgb(0, 0, 0)") && !style.contains("#000000")) {
                return true;
            }
        }

        return false;
    }

    // Action implementation
    private void setTitleColor(String id) {
        assert driver != null : " WebDriver must be initialized ";

        WebElement item = driver.findElement(By.id(id));

        // make sure color successfully set, if not, try again
        do {
            WebElement title = item.findElement(By.tagName("h3"));

            // If title is a link
            if (elementPresent(title, By.tagName("a"))) {
                changeTitleColor(id, "#00748b");
            } else { // if title is not a link
                changeTitleColor(id, "#000000");
            }

            item = driver.findElement(By.id(id));
        } while (wrongTitleColor(item));
    }

    // Change title color
    private void changeTitleColor(String itemId, String color) {
        assert driver != null : " WebDriver must be initialized ";

        String url = driver.getCurrentUrl();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(itemId)));
        WebElement item = driver.findElement(By.id(itemId));

        //change link color
        item.findElement(By.className("cmimg")).click();
        WebElement cmdiv = driver.findElement(By.className("cmdiv"));
        if(elementPresent(cmdiv , By.linkText("Edit"))) { 
            cmdiv.findElement(By.linkText("Edit")).click(); //TODO - Find more robust solution
        } else {
            cmdiv.findElement(By.xpath(".//ul/li[3]")).click();
        }

        String colorWithHashtag = (color.charAt(0) == '#') ? color : "#".concat(color);
        String colorNoHashTag = (color.charAt(0) == '#') ? color.substring(1) : color;

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("submitStepBottom")));
        //if color palette link is present, use it
        if(elementPresent(By.className("colorChip"))) {
            //scroll down
            Actions actions = new Actions(driver);
            actions.moveToElement(driver.findElement(By.id("step2")));
            actions.perform();

            driver.findElement(By.className("colorChip")).click();
            driver.findElement(By.className("title_color")).clear();
            driver.findElement(By.className("title_color")).sendKeys(colorNoHashTag);
            driver.findElement(By.xpath("//div[contains(@class , \"quickAddPal\")]/div/input[2]")).click();
        } else { //if the color palette isn't there, add html to item title
            // find title textbox
            String textBoxId;
            if(elementPresent(By.id("user_title"))) { textBoxId = "user_title"; }
            else if(elementPresent(By.id("specific_link_name"))) { textBoxId = "specific_link_name"; }
            else {
                System.out.println("Couldn't find a text box on " + driver.getCurrentUrl());
                driver.findElement(By.name("bottom_Cancel")).click();
                return;
            }

            WebElement textBox = driver.findElement(By.id(textBoxId));

            // add color tag
            String textValue = textBox.getAttribute("value");

            // make sure the textbox doesn't already have a span tag
            if (textValue.contains("<span")) {
                Document doc = Jsoup.parse(textValue);
                Element span = doc.select("span").get(0);
                textValue = span.text();
            }

            textValue = String.format("<span style=\"color:%s;\">%s</span>", colorWithHashtag, textValue);

            js.executeScript(String.format("document.getElementById('%s').value = '%s'", textBoxId, textValue));

            // wait until page done loading
            wait.until(
                    webDriver -> ((JavascriptExecutor) webDriver).
                            executeScript("return document.readyState").equals("complete"));
        }

        while (elementPresent(By.name("bottom_Submit"))) {
            try {
                driver.findElement(By.name("bottom_Submit")).click();
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                driver.get(url);
            }
        }

        if(alertPresent()) driver.switchTo().alert().accept();
    }

    @Override
    public void titleColor(@NotNull String url) {
        try {
            init();
        } catch (LoginFailedException e) {
            System.out.println(" -- END --");
            return;
        }

        System.out.println("Making links blue in " + url);

        driver.get(url);

        //Make sure page is workable
        if(!goodPage()) { return; }

        //if edit mode is off, turn it on
        editMode();

        List<ContentArea> areas = contentAreas();

        for (ContentArea area : areas) {
            System.out.println("Looking in '" + area.title + "'");
            traverse(area.url, this::wrongTitleColor, this::setTitleColor);
        }

        System.out.println("\nAll done!");

        stop();
    }

    @Override
    public void titleColor(@NotNull Constraints constraints) {
        try {
            init();
        } catch (LoginFailedException e) {
            System.out.println(" -- END --");
            return;
        }

        System.out.println("Making links blue");

        HashSet<Course> courses = getCourses((ConstraintSet) constraints);

        for (Course course : courses) {
            System.out.println("\nWorking on " + course.courseId);

            driver.get(course.url);

            //Make sure page is workable
            if(!goodPage()) { continue; }

            //if edit mode is off, turn it on
            editMode();

            List<ContentArea> areas = contentAreas();

            for (ContentArea area : areas) {
                System.out.println("Looking in '" + area.title + "'");
                traverse(area.url, this::wrongTitleColor, this::setTitleColor);
            }

            System.out.println("Done with " + course.courseId);
        }

        System.out.println("\nAll done!");

        stop();
    }


    // -------------------- removeIcon -------------------- //
    // Filter implementation
    private boolean hasIcon(WebElement item) {
        String title = item.findElement(By.tagName("h3")).getText();

        // if item has no content, return false, or true if item is a checklist
        if (!elementPresent(item, By.className("vtbegenerated"))) {
            return title.toLowerCase().contains("checklist");
        }

        // parse html content of item
        String htmlText = item.findElement(By.className("vtbegenerated")).getAttribute("innerHTML");
        Document htmlDoc = Jsoup.parse(htmlText);

        // if item is checklist
        if (title.toLowerCase().contains("checklist")) {
            // needs a hidemyicon element
            return htmlDoc.getElementsByClass("hidemyicon").isEmpty();
        } else {
            // can't have a replacemyicon element
            return !htmlDoc.getElementsByClass("replacemyicon").isEmpty();
        }
    }

    // Action implementation
    private void removeIcon(String id) {
        assert driver != null : " WebDriver must be initialized ";

        WebElement item = driver.findElement(By.id(id));
        String title = item.findElement(By.tagName("h3")).getText();

        do {
            try {
                if (title.toLowerCase().contains("checklist")) {
                    insertHideIcon(id);
                } else {
                    removeReplaceIcon(id);
                }
            } catch (EditHtmlNotFoundException e) {
                break;
            }

            item = driver.findElement(By.id(id));
        } while(hasIcon(item));
    }


    // Filter implementation
    private boolean checklistHasIcon(WebElement item) {
        String title = item.findElement(By.tagName("h3")).getText();

        // if item is not a checklist, return false
        if (!title.toLowerCase().contains("checklist")) {
            return false;
        }

        // if checklist item has no content, return true
        if (!elementPresent(item, By.className("vtbegenerated"))) {
            return true;
        }

        // parse html content of item
        String htmlText = item.findElement(By.className("vtbegenerated")).getAttribute("innerHTML");
        Document htmlDoc = Jsoup.parse(htmlText);

        return htmlDoc.getElementsByClass("hidemyicon").isEmpty();
    }

    // Action implementation
    private void removeChecklistIcon(String id) {
        assert driver != null : " WebDriver must be initialized ";

        WebElement item = driver.findElement(By.id(id));
        String title = item.findElement(By.tagName("h3")).getText();

        do {
            try {
                insertHideIcon(id);
            } catch (EditHtmlNotFoundException e) {
                break;
            }

            item = driver.findElement(By.id(id));
        } while (checklistHasIcon(item));
    }

    private void insertHideIcon(String id) throws EditHtmlNotFoundException {
        String url = driver.getCurrentUrl();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        WebElement item = driver.findElement(By.id(id));
        String title = item.findElement(By.tagName("h3")).getText();

        //Open edit screen
        item.findElement(By.className("cmimg")).click();
        WebElement cmdiv = driver.findElement(By.className("cmdiv"));
        if (elementPresent(cmdiv, By.linkText("Edit"))) {
            driver.get(cmdiv.findElement(By.linkText("Edit")).getAttribute("href"));
        } else {
            cmdiv.findElement(By.xpath(".//ul/li[3]")).click();
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("submitStepBottom")));

        //scroll down
        if (elementPresent(By.id("step2"))) {
            Actions actions = new Actions(driver);
            actions.moveToElement(driver.findElement(By.id("step2")));
            actions.perform();
        }

        // Store the current window handle
        String winHandleBefore = driver.getWindowHandle();

        // if there is no html edit field, go back
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//table[@class=\"mceLayout\"]/tbody/tr/td/div/span/div[2]/table[3]/tbody/tr/td[27]/a")));
        } catch (NoSuchElementException | TimeoutException e) {
            driver.findElement(By.name("bottom_Submit")).click();

            if (alertPresent()) {
                driver.switchTo().alert().accept();
            }

            if (elementPresent(By.name("bottom_Submit"))) {
                try {
                    driver.findElement(By.name("bottom_Submit")).click();
                } catch (StaleElementReferenceException f) {
                    driver.get(url);
                    return;
                }
            }

            throw new EditHtmlNotFoundException(title);
        }

        //click edit html button
        driver.findElement(By.xpath(
                "//table[@class=\"mceLayout\"]/tbody/tr/td/div/span/div[2]/table[3]/tbody/tr/td[27]/a")).click();

        // Switch to new window opened
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }

        // Replace all html with hide my icon div
        driver.findElement(By.id("htmlSource")).clear();
        driver.findElement(By.id("htmlSource")).sendKeys("<div class=\"hidemyicon\"></div>");

        // Submit
        driver.findElement(By.xpath("//*[@id=\"insert\"]")).click();

        // Switch back to original browser
        driver.switchTo().window(winHandleBefore);

        wait.until(
                webDriver -> ((JavascriptExecutor) webDriver).
                        executeScript("return document.readyState").equals("complete"));
        driver.findElement(By.name("bottom_Submit")).click();
        if (elementPresent(By.name("bottom_Submit"))) {
            try {
                driver.findElement(By.name("bottom_Submit")).click();
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                // ignore this
            }
        }
        if (alertPresent()) {
            driver.switchTo().alert().accept();
        }

        driver.get(url);
    }

    private void removeReplaceIcon(String id) throws EditHtmlNotFoundException {
        String url = driver.getCurrentUrl();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        WebElement item = driver.findElement(By.id(id));
        String title = item.findElement(By.tagName("h3")).getText();

        //Open edit screen
        item.findElement(By.className("cmimg")).click();
        WebElement cmdiv = driver.findElement(By.className("cmdiv"));
        if (elementPresent(cmdiv, By.linkText("Edit"))) {
            driver.get(cmdiv.findElement(By.linkText("Edit")).getAttribute("href"));
        } else {
            cmdiv.findElement(By.xpath(".//ul/li[3]")).click();
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("submitStepBottom")));

        //scroll down
        if (elementPresent(By.id("step2"))) {
            Actions actions = new Actions(driver);
            actions.moveToElement(driver.findElement(By.id("step2")));
            actions.perform();
        }

        // Store the current window handle
        String winHandleBefore = driver.getWindowHandle();

        // if there is no html edit field, go back
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//table[@class=\"mceLayout\"]/tbody/tr/td/div/span/div[2]/table[3]/tbody/tr/td[27]/a")));
        } catch (NoSuchElementException | TimeoutException e) {
            driver.findElement(By.name("bottom_Submit")).click();

            if (alertPresent()) {
                driver.switchTo().alert().accept();
            }

            if (elementPresent(By.name("bottom_Submit"))) {
                try {
                    driver.findElement(By.name("bottom_Submit")).click();
                } catch (StaleElementReferenceException f) {
                    driver.get(url);
                    return;
                }
            }

            throw new EditHtmlNotFoundException(title);
        }

        // click edit html button
        driver.findElement(By.xpath(
                "//table[@class=\"mceLayout\"]/tbody/tr/td/div/span/div[2]/table[3]/tbody/tr/td[27]/a")).click();

        // Switch to new window opened
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }

        // parse html
        String htmlText = driver.findElement(By.id("htmlSource")).getAttribute("value");
        Document htmlDoc = Jsoup.parse(htmlText);

        // Remove icons
        Elements icons = htmlDoc.getElementsByClass("replacemyicon");
        icons.remove();

        // trim html
        htmlText = htmlDoc.html();
        htmlText = htmlText.substring(htmlText.indexOf("<body>") + 6, htmlText.indexOf("</body>")).trim()
                .replace("\n", "").replace("   ", " ");

        // replace html in window
        driver.findElement(By.id("htmlSource")).clear();
        driver.findElement(By.id("htmlSource")).sendKeys(htmlText);

        // submit
        driver.findElement(By.xpath("//*[@id=\"insert\"]")).click();

        // Switch back to original browser
        driver.switchTo().window(winHandleBefore);

        wait.until(
                webDriver -> ((JavascriptExecutor) webDriver).
                        executeScript("return document.readyState").equals("complete"));
        driver.findElement(By.name("bottom_Submit")).click();
        if (elementPresent(By.name("bottom_Submit"))) {
            try {
                driver.findElement(By.name("bottom_Submit")).click();
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                // ignore this
            }
        }
        if (alertPresent()) {
            driver.switchTo().alert().accept();
        }

        driver.get(url);
    }

    @Override
    public void removeIcons(@NotNull String url, boolean checklistsOnly) {
        try {
            init();
        } catch (LoginFailedException e) {
            System.out.println(" -- END --");
            return;
        }

        System.out.println("Removing icons from " + (checklistsOnly ? "checklists" : "all items") + " in " + url);

        driver.get(url);

        //Make sure page is workable
        if(!goodPage()) { return; }

        //if edit mode is off, turn it on
        editMode();

        List<ContentArea> areas = contentAreas();

        for (ContentArea area : areas) {
            System.out.println("Looking in '" + area.title + "'");
            if (checklistsOnly){
                traverse(area.url, this::checklistHasIcon, this::removeChecklistIcon);
            } else {
                traverse(area.url, this::hasIcon, this::removeIcon);
            }
        }

        System.out.println("\nAll done!");

        stop();
    }

    @Override
    public void removeIcons(@NotNull Constraints constraints, boolean checklistsOnly) {
        try {
            init();
        } catch (LoginFailedException e) {
            System.out.println(" -- END --");
            return;
        }

        System.out.println("Removing Icons");

        HashSet<Course> courses = getCourses((ConstraintSet) constraints);

        for (Course course : courses) {
            System.out.println("\nWorking on " + course.courseId);

            driver.get(course.url);

            //Make sure page is workable
            if(!goodPage()) { continue; }

            //if edit mode is off, turn it on
            editMode();

            List<ContentArea> areas = contentAreas();

            for (ContentArea area : areas) {
                System.out.println("Looking in '" + area.title + "'");
                if (checklistsOnly){
                    traverse(area.url, this::checklistHasIcon, this::removeChecklistIcon);
                } else {
                    traverse(area.url, this::hasIcon, this::removeIcon);
                }
            }

            System.out.println("Done with " + course.courseId);
        }

        System.out.println("\nAll done!");

        stop();
    }


    // -------------------- toggleAvailability -------------------- //
    // navigate to class options page, turn class on or off
    private void toggle(String link, boolean classOn) {
        // Go to edit page
        driver.get("https://franciscan.blackboard.com/webapps/blackboard/execute/cp/courseProperties?" +
                "dispatch=editProperties&family=cp_edit_properties&course_id=" + getSid(link));

        //Make sure page is workable
        if(!goodPage()) { return; }

        List<WebElement> buttons = driver.findElements(By.xpath("//*[@id=\"available\"]"));

        //Check if availability is already set
        boolean classIsOn = false;
        wait.until(ExpectedConditions.elementToBeClickable(buttons.get(0)));
        for(WebElement button : buttons) {
            if(button.getAttribute("checked") != null) {
                classIsOn = Boolean.valueOf(button.getAttribute("value"));
                break;
            }
        }

        // wait until page done loading
        wait.until(
                webDriver -> ((JavascriptExecutor) webDriver).
                        executeScript("return document.readyState").equals("complete"));

        //scroll down
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.id("steptitle4")));
        actions.perform();

        // Click radio Button
        if (classOn != classIsOn) {
            driver.findElement(By.xpath("//input[@id=\"available\" and @value=\"" + String.valueOf(classOn) + "\"]"))
                    .click();
        }

        driver.findElement(By.id("bottom_Submit")).click();

        System.out.println(" - Success");
    }

    @Override
    public void toggleAvailability(@NotNull Constraints constraints, @NotNull String availability) {
        assert availability.equalsIgnoreCase("on") || availability.equalsIgnoreCase("off") :
                "Availability can only be 'on' or 'off'";

        try {
            init();
        } catch (LoginFailedException e) {
            System.out.println(" -- END --");
            return;
        }

        System.out.println("Setting course availability to " + availability);

        boolean classOn = (availability.equalsIgnoreCase("on"));

        HashSet<Course> courses = getCourses((ConstraintSet) constraints);

        for(Course course : courses) {
            System.out.println("\nWorking on " + course.courseId);

            toggle(course.url, classOn);

            System.out.println("Done with " + course.courseId);
        }

        System.out.println("\nAll done!");

        stop();
    }


    // -------------------- setLanding -------------------- //
    private void setEntryPoint(String link, String page) {
        // Go to edit page
        driver.get("https://franciscan.blackboard.com/webapps/blackboard/execute/cp/manageCourseDesign?" +
                "cmd=display&course_id=" + getSid(link));

        //Make sure page is workable
        if(!goodPage()) { return; }

        //get all options for a landing page
        List<WebElement> options = driver.findElement(By.id("entryCourseTocIdStr")).findElements(By.tagName("option"));
        List<String> landingPages = new ArrayList<>();

        for (WebElement option : options) {
            landingPages.add(option.getText());
        }

        // Print error message if requested landing page is not an option
        if(!landingPages.contains(page)) {
            System.out.println(" - Landing page cannot be set to '" + page + "'");
            return;
        }

        // Send keys to dropdown menu
        driver.findElement(By.id("entryCourseTocIdStr")).sendKeys(page);
        driver.findElement(By.id("bottom_Submit")).click();

        System.out.println(" - Success");
    }

    @Override
    public void setLanding(@NotNull Constraints constraints, @NotNull String landingPage) {
        try {
            init();
        } catch (LoginFailedException e) {
            System.out.println(" -- END --");
            return;
        }

        System.out.println("Setting landing page to " + landingPage);

        HashSet<Course> courses = getCourses((ConstraintSet) constraints);

        for(Course course : courses) {
            System.out.println("\nWorking on " + course.courseId);

            setEntryPoint(course.url, landingPage);

            System.out.println("Done with " + course.courseId);
        }

        System.out.println("\nAll done!");

        stop();
    }


    // -------------------- checkDates -------------------- //
    @Nullable private HashMap<String, String> dates = null;

    // Filter implementation
    private boolean needsDate(WebElement item) {
        String title = item.findElement(By.tagName("h3")).getText();

        // omit course calendars
        if (title.toLowerCase().contains("calendar")) return false;

        // parse title text
        Document htmlDoc = Jsoup.parse(item.findElement(By.tagName("h3")).getAttribute("innerHTML"));
        // get date elements
        Elements dateElements = htmlDoc.getElementsByClass("date");
        // if date elements exist
        if (!dateElements.isEmpty()) {
            for (Element titleDate : dateElements) {
                // return true if calendar date doesn't match date in title
                if (!dates.get(titleDate.id()).equals(titleDate.text())) return true;
            }
        }

        // if item has no content, return false
        if (!elementPresent(item, By.className("vtbegenerated"))) return false;

        // parse html content of item
        String htmlText = item.findElement(By.className("vtbegenerated")).getAttribute("innerHTML");
        htmlDoc = Jsoup.parse(htmlText);
        // get date elements
        dateElements = htmlDoc.getElementsByClass("date");
        // if date elements exist
        if (!dateElements.isEmpty()) {
            for (Element titleDate : dateElements) {
                // return true if calendar date doesn't match date in item content
                if (!dates.get(titleDate.id()).equals(titleDate.text())) return true;
            }
        }

        return false;
    }

    // Action implementation
    private void changeDate(String id){
        assert driver != null : " WebDriver must be initialized ";

        WebElement item = driver.findElement(By.id(id));
        boolean dateInTitle = false, dateInHtml = false;

        do {
            // parse title text
            Document htmlDoc = Jsoup.parse(item.findElement(By.tagName("h3")).getAttribute("innerHTML"));
            // get date elements
            Elements dateElements = htmlDoc.getElementsByClass("date");
            // if date elements exist
            if (!dateElements.isEmpty()) {
                for (Element titleDate : dateElements) {
                    // needs title date if calendar date doesn't match date in title
                    if (!dates.get(titleDate.id()).equals(titleDate.text())) dateInTitle = true;
                }
            }

            // if item has no content, no html action needed
            if (!elementPresent(item, By.className("vtbegenerated"))) {
                dateInHtml = false;
            } else {
                // parse html content of item
                String htmlText = item.findElement(By.className("vtbegenerated")).getAttribute("innerHTML");
                htmlDoc = Jsoup.parse(htmlText);
                // get date elements
                dateElements = htmlDoc.getElementsByClass("date");
                // if date elements exist
                if (!dateElements.isEmpty()) {
                    for (Element titleDate : dateElements) {
                        // needs date in html if calendar date doesn't match date in item content
                        if (!dates.get(titleDate.id()).equals(titleDate.text())) dateInHtml = true;
                    }
                }
            }

            matchDate(id, dateInTitle, dateInHtml);

            item = driver.findElement(By.id(id));
        } while (needsDate(item));
    }

    private void matchDate(String itemId, boolean dateInTitle, boolean dateInHtml) {
        String url = driver.getCurrentUrl();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(itemId)));
        WebElement item = driver.findElement(By.id(itemId));

        item.findElement(By.className("cmimg")).click();
        WebElement cmdiv = driver.findElement(By.className("cmdiv"));
        if(elementPresent(cmdiv , By.linkText("Edit"))) {
            driver.get(cmdiv.findElement(By.linkText("Edit")).getAttribute("href"));
        } else {
            cmdiv.findElement(By.xpath(".//ul/li[3]")).click();
        }

        WebElement itemTitle;
        String htmlText;
        Document htmlDoc;
        Elements dateElements;

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("submitStepBottom")));

        if(dateInTitle) {
            //Text box
            itemTitle = driver.findElement(By.xpath("//input[@type=\"text\"]"));

            //item title text
            htmlText = itemTitle.getAttribute("value");

            htmlDoc = Jsoup.parse(htmlText);

            dateElements = htmlDoc.getElementsByClass("date");

            //if title contains a date
            if (!dateElements.isEmpty()) {
                for (Element date : dateElements) {
                    String dateId = date.id();

                    System.out.println("Editing " + dateId);

                    try {
                        date.text(dates.get(dateId));
                    } catch (IllegalArgumentException e) {
                        System.out.println("DATE ID NOT FOUND: " + dateId + "\n-\t" + driver.getCurrentUrl());
                    }

                    htmlText = htmlDoc.html();
                    htmlText = htmlText.substring(htmlText.indexOf("<body>") + 6, htmlText.indexOf("</body>")).trim()
                            .replace("\n", "").replace("   ", " ");
                }

                itemTitle.clear();
                itemTitle.sendKeys(htmlText);
            }
        }

        //if there is no html edit field, go back
        if (!elementPresent(By.xpath(
                "//table[@class=\"mceLayout\"]/tbody/tr/td/div/span/div[2]/table[3]/tbody/tr/td[27]/a"))) {
            driver.findElement(By.name("bottom_Submit")).click();
            if (alertPresent()) {
                driver.switchTo().alert().accept();
            }
            if (elementPresent(By.name("bottom_Submit"))) {
                try {
                    driver.findElement(By.name("bottom_Submit")).click();
                } catch (StaleElementReferenceException e) {
                    driver.get(url);
                    return;
                }
            }
        }

        if(dateInHtml) {
            //scroll down
            if (elementPresent(By.id("step2"))) {
                Actions actions = new Actions(driver);
                actions.moveToElement(driver.findElement(By.id("step2")));
                actions.perform();
            }

            // Store the current window handle
            String winHandleBefore = driver.getWindowHandle();

            try {
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                        "//table[@class=\"mceLayout\"]/tbody/tr/td/div/span/div[2]/table[3]/tbody/tr/td[27]/a")));
            } catch (NoSuchElementException | TimeoutException e) {
                return;
            }

            //click edit html button
            driver.findElement(By.xpath(
                    "//table[@class=\"mceLayout\"]/tbody/tr/td/div/span/div[2]/table[3]/tbody/tr/td[27]/a")).click();

            // Switch to new window opened
            for (String winHandle : driver.getWindowHandles()) {
                driver.switchTo().window(winHandle);
            }

            htmlText = driver.findElement(By.id("htmlSource")).getAttribute("value");

            htmlDoc = Jsoup.parse(htmlText);

            dateElements = htmlDoc.getElementsByClass("date");

            //if text contains a date
            if (!dateElements.isEmpty()) {
                for (Element date : dateElements) {
                    String dateId = date.id();

                    System.out.println("Editing " + dateId);

                    try {
                        date.text(dates.get(dateId));
                    } catch (IllegalArgumentException e) {
                        System.out.println("DATE ID NOT FOUND: " + dateId + "\n\t" + driver.getCurrentUrl());
                    }

                    htmlText = htmlDoc.html();
                    htmlText = htmlText.substring(htmlText.indexOf("<body>") + 6, htmlText.indexOf("</body>")).trim()
                            .replace("\n", "").replace("   ", " ");
                }

                driver.findElement(By.id("htmlSource")).clear();
                driver.findElement(By.id("htmlSource")).sendKeys(htmlText);

                driver.findElement(By.xpath("//*[@id=\"insert\"]")).click();
            } else {
                //close html window
                driver.close();
            }

            // Switch back to original browser
            driver.switchTo().window(winHandleBefore);
        }

        try {
            driver.findElement(By.name("bottom_Submit")).click();
        } catch (StaleElementReferenceException e) {
            driver.get(url);
            return;
        }
        if (alertPresent()) {
            driver.switchTo().alert().accept();
        }

        driver.get(url);
    }

    @Override
    public void checkDates(@NotNull String courseUrl, @NotNull String calendarUrl) {
        try {
            init();
        } catch (LoginFailedException e) {
            System.out.println(" -- END --");
            return;
        }

        System.out.println("\nRetrieving dates from course calendar");

        dates = new HashMap<String, String>();

        driver.get(calendarUrl);

        //Parse calendar html
        Document calendar = Jsoup.parse(driver.getPageSource());

        Elements dateElements = calendar.getElementsByClass("date");

        for(Element date : dateElements) {
            System.out.println(date.id() + " - " + date.text());
            dates.put(date.id() , date.text());
        }

        System.out.print("\n");

        driver.get(courseUrl);

        //Make sure page is workable
        if(!goodPage()) { return; }

        //if edit mode is off, turn it on
        editMode();

        List<ContentArea> areas = contentAreas();

        for (ContentArea area : areas) {
            System.out.println("Looking in '" + area.title + "'");
            traverse(area.url, this::needsDate, this::changeDate);
        }

        dates = null;

        System.out.println("\nAll done!");

        stop();
    }
}
