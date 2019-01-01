import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;
import blackboardbot.ConstraintSet;
import blackboardbot.Constraints;

public class Main {
    public static void main(String[] args) {
        BlackBoardBot botty = new BBB("studentaccess", "S@1ntFp4u");

        Constraints constraints = new ConstraintSet("201720", "OL");

        botty.revStat(constraints);

        botty.stop();
    }
}
