import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;
import blackboardbot.ConstraintSet;
import blackboardbot.Constraints;

public class Main {
    public static void main(String[] args) {
        BlackBoardBot botty = new BBB("studentaccess", "S@1ntFp4u");

        Constraints constraints = new ConstraintSet("" +
                "{\n" +
                "  \"term\": \"201720\",\n" +
                "  \"session\": \"OL\",\n" +
                "  \"constraints\": [\n" +
                "    {\n" +
                "      \"type\": \"include\",\n" +
                "      \"courseGreaterThan\": 200,\n" +
                "      \"department\": [\"THE\", \"PHL\"]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"exclude\",\n" +
                "      \"instructorId\": [\"rbolster\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        botty.titleColor(constraints);
    }
}
