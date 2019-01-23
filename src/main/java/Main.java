import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;
import blackboardbot.ConstraintSet;
import blackboardbot.Constraints;

public class Main {
    public static void main(String[] args) {
        BlackBoardBot botty = new BBB("studentaccess", "S@1ntFp4u");

        Constraints constraints = new ConstraintSet("201820", "OL");

        //botty.syllabusNames(constraints);


        botty.removeIcons("https://franciscan.blackboard.com/webapps/blackboard/content/listContentEditable.jsp?content_id=_607718_1&course_id=_17898_1");
    }
}
