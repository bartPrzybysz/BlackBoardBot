import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;
import blackboardbot.ConstraintSet;
import blackboardbot.Constraints;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello BlackBoard\n");

        Constraints constraintSet = new ConstraintSet("201810", "all");

        constraintSet.addConstraint("include", "{\n" +
                "  \"department\": [\"PHL\", \"THE\"],\n" +
                "  \"courseLessThan\": 200\n" +
                "}");

        constraintSet.addConstraint("include", "{\"courseId\": [\"201810GA-THE-115-GA-1\"]}");

        BlackBoardBot bot = new BBB("studentaccess", "S@1ntFp4u");

        bot.revStat(constraintSet);
    }
}
