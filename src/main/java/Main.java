import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;

public class Main {
    public static void main(String[] args) {
        BlackBoardBot botty = new BBB("studentaccess", "S@1ntFp4u");

        botty.titleColor("https://franciscan.blackboard.com/webapps/blackboard/content/listContentEditable.jsp?content_id=_369614_1&course_id=_13255_1&mode=reset");

        botty.stop();
    }
}
