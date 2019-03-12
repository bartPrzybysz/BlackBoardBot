package blackboardbot;

/**
 * Interface for BlackBoardBot objects. Automates mundane tasks blackboard using Selenium ChromeDriver.
 *
 * @author Bartosz Przybysz
 */
public interface BlackBoardBot {
    /**
     * Makes sure instance has username and password set
     *
     * @return true if credentials set, false if blank
     */
    boolean hasCredentials();

    /**
     * Set username
     *
     * @param username the username to be used
     */
    void username(String username);

    /**
     * Get username
     *
     * @return username
     */
    String username();

    /**
     * Set password
     *
     * @param password the password to be used
     */
    void password(String password);

    /**
     * Get password
     *
     * @return password
     */
    String password();

    /**
     * Stops function being ran, exits ChromeDriver and closes Chrome window
     */
    void stop();

    /**
     * Checks if a BlackBoardBot function is in progress, or if ChromeDriver is active
     *
     * @return true if ChromeDriver is active, false if not
     */
    boolean isRunning();

    /**
     * Sets review status and statistics tracking to true on all items in course<br><br>
     *
     * <b>pre</b> - login credentials must be set
     * @param url url of the class to be edited, can link to anywhere in the class as long as the course menu is visible
     */
    void revStat(String url);

    /**
     * Sets review status and statistics tracking to true on all items in all courses that satisfy constraint set<br><br>
     *
     * <b>pre</b> - login credentials must be set
     * @param constraints constraints for selecting classes, must be ConstraintSet object
     */
    void revStat(Constraints constraints);

    /**
     * Sets all link colors to blue and all non link titles to black on all items in course<br><br>
     *
     * <b>pre</b> - login credentials must be set
     * @param url url of the class to be edited, can link to anywhere in the class as long as the course menu is visible
     */
    void titleColor(String url);

    /**
     * Sets all link colors to blue and all non link titles to black in all courses that satisfy constraint set<br><br>
     *
     * <b>pre</b> - login credentials must be set
     * @param constraints constraints for selecting classes, must be ConstraintSet object
     */
    void titleColor(Constraints constraints);

    /**
     * Removes icons from all items in course, hides icon from all items titled 'Checklist'.
     * Can also be run to just hide checklist icons <br><br>
     *
     * <b>pre</b> - login credentials must be set
     * @param url url of the class to be edited, can link to anywhere in the class as long as the course menu is visible
     * @param checklistsOnly true to only remove checklist icons and ignore all others
     */
    void removeIcons(String url, boolean checklistsOnly);

    /**
     * Removes icons from all items in course, hides icon from all items titled 'Checklist' in in all courses that
     * satisfy constraint set. Can also be run to just hide checklist icons<br><br>
     *
     * <b>pre</b> - login credentials must be set
     * @param constraints constraints for selecting classes, must be ConstraintSet object
     * @param checklistsOnly true to only remove checklist icons and ignore all others
     */
    void removeIcons(Constraints constraints, boolean checklistsOnly);

    /**
     * Makes all classes that satisfy constraint set available or unavailable<br/><br/>
     *
     * <b>pre</b> - login credentials must be set
     * @param constraints constraints for selecting classes, must be ConstraintSet object
     * @param availability either "ON" or "OFF" <i>(not case sensitive)</i>
     */
    void toggleAvailability(Constraints constraints, String availability);

    /**
     * Sets landing page on all classes that satisfy constraints<br/><br/>
     *
     * <b>pre</b> - login credentials must be set
     * @param constraints constraints for selecting classes, must be ConstraintSet object
     * @param landingPage Landing Page in the form of s string. Must be one of the following:
     *                    <ul>
     *                    <li>"Start Here"</li>
     *                    <li>"Course Information"</li>
     *                    <li>"Faculty Information"</li>
     *                    <li>"Learning Sessions"</li>
     *                    <li>"Proposals"</li>
     *                    <li>"Ask the Teacher"</li>
     *                    <li>"View Announcements"</li>
     *                    <li>"Send  Email"</li>
     *                    <li>"View Grades"</li>
     *                    <li>"Academic Advising"</li>
     *                    <li>"Blackboard Help"</li>
     *                    </ul>
     */
    void setLanding(Constraints constraints, String landingPage);

    /**
     * Matches all dates in course with dates in course calendar.  All dates must be in html class="date" tags.
     * date id in calendar must match date id in course.  Example:
     * <i>&lt;span class="date" id="christmas"&gt;December 25&lt;/span&gt;</i><br/><br/>
     *
     * <b>pre</b> - login credentials must be set
     * @param courseUrl url of the class to be edited, can link to anywhere in the class as long
     *                  as the course menu is visible
     * @param calendarUrl url of the calendar to be used
     */
    void checkDates(String courseUrl, String calendarUrl);
}
