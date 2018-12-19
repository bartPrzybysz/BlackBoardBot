import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello BlackBoard\n");

        BlackBoardBot bot = new BBB("FUSSupport", "Gr8fully");

        bot.revStat("https://franciscan.blackboard.com/webapps/blackboard/execute/announcement?method=search&context=course&course_id=_13255_1&handle=cp_announcements&mode=cpview");
    }
}
