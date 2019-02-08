package blackboardbot;

/**
 * Interface for constraint sets. Constraint set specifies conditions for selecting courses.<br>
 * Can be represented using JSON:
 * <pre>
 * {
 *   "term": "201720",
 *   "session": "OL",
 *   "constraints": [
 *     {
 *       "type": "include",
 *       "courseGreaterThan": 200,
 *       "department": ["THE", "PHL"]
 *     },
 *     {
 *       "type": "exclude",
 *       "instructorId": ["rbolster"]
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author Bartosz Przybysz
 */
public interface Constraints {
    /**
     * Add a constraint to the set of constraints
     *
     * @param type Must be either "include" or "exclude".<br>
     *             include - only include courses that satisfy constraint <br>
     *             exclude - don't include courses that satisfy constraint <br>
     *
     * @param description JSON description of constraint<br>
     *                    Can include following variables:
     *                    <ul>
     *                    <li>"courseId" : array of course ids (strings)</li>
     *                    <li>"department" : array of department codes (strings)</li>
     *                    <li>"course" : array of course numbers (integers)</li>
     *                    <li>"courseGreaterThan" : course number that courses must be greater than or equal to
     *                    (integer)</li>
     *                    <li>"courseLessThan" : course number that courses must be less than or equal to (integer)</li>
     *                    <li>"instructorId" : array of instructor ids (strings)</li>
     *                    </ul>
     *                    Example:<br>
     *                    {"courseID": ["201810GA-THE-115-GA-1"],
     *                    "department": ["PHL", "THE"],
     *                    "courseGreaterThan": 200}
     */
    void addConstraint(String type, String description);

    /**
     * @return JSON description of constraint set
     */
    @Override
    String toString();
}
