import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;
import blackboardbot.ConstraintSet;
import blackboardbot.Constraints;

public class Main {
    public static void main(String[] args) {
        BlackBoardBot botty = new BBB("studentaccess", "S@1ntFp4u");

        Constraints ol = new ConstraintSet("201820", "OL");
        Constraints ol1 = new ConstraintSet("201820", "OL-1");

        botty.setLanding(ol, "View Announcements");
        botty.setLanding(ol1, "View Announcements");
    }
}
