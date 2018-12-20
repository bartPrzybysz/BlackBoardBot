import blackboardbot.ConstraintSet;
import blackboardbot.Constraints;


public class Main {
    public static void main(String[] args) {
        System.out.println("Hello BlackBoard\n");

        Constraints constraints = new ConstraintSet("{\n" +
                "  \"session\": \"OL\",\n" +
                "  \"term\": \"201810\",\n" +
                "  \"constraints\": [\n" +
                "    {\n" +
                "      \"type\": \"exclude\",\n" +
                "      \"instructorId\": [\n" +
                "        \"Fitz\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"courseGreaterThan\": 400,\n" +
                "      \"type\": \"include\",\n" +
                "      \"department\": [\n" +
                "        \"THE\",\n" +
                "        \"PHL\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        System.out.println(constraints.toString());


//        BlackBoardBot bot = new BBB("FUSSupport", "Gr8fully");
//
//        bot.revStat("https://franciscan.blackboard.com/webapps/blackboard/execute/announcement?method=search&context=course&course_id=_13255_1&handle=cp_announcements&mode=cpview");
    }
}
