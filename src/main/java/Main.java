import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;
import blackboardbot.ConstraintSet;
import blackboardbot.Constraints;

public class Main {
    public static void main(String[] args) {
        BlackBoardBot botty = new BBB("studentaccess", "S@1ntFp4u");

        Constraints constraints = new ConstraintSet("201710", "OL-2");

        constraints.addConstraint("include", "{\"courseId\": [\"Lsand\", \"SWOP2\", \"FUS_RADIO_SWOP\"]}");

        botty.toggleAvailability(constraints, "off");
    }
}
