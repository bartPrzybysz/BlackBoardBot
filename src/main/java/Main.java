import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;

public class Main {
    public static void main(String[] args) {
        BlackBoardBot botty = new BBB("studentaccess", "S@1ntFp4u");

        botty.revStat("https://franciscan.blackboard.com/webapps/blackboard/content/listContentEditable.jsp?content_id=_575604_1&course_id=_16976_1&mode=reset");

        botty.stop();
    }
}
