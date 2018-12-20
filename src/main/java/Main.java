import blackboardbot.ConstraintSet;
import blackboardbot.Constraints;


public class Main {
    public static void main(String[] args) {
        System.out.println("Hello BlackBoard\n");

        Constraints constraints = new ConstraintSet("201810", "OL");

        constraints.addConstraint("include", "{" +
                "  \"department\": [\"PHL\", \"THE\"],\n" +
                "  \"courseGreaterThan\": 400\n" +
                "}");

        constraints.addConstraint("exclude", "{" +
                "  \"instructorId\": [\"Fitz\"]" +
                "}");

        System.out.println(constraints.toString());


//        BlackBoardBot bot = new BBB("FUSSupport", "Gr8fully");
//
//        bot.revStat("https://franciscan.blackboard.com/webapps/blackboard/execute/announcement?method=search&context=course&course_id=_13255_1&handle=cp_announcements&mode=cpview");
    }
}
