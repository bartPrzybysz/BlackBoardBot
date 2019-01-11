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

}
