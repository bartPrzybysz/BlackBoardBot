import blackboardbot.BBB;
import blackboardbot.BlackBoardBot;

public class Main {
    public static void main(String[] args) {
        BlackBoardBot botty = new BBB("studentaccess", "S@1ntFp4u");

        botty.checkDates(
                "https://franciscan.blackboard.com/webapps/blackboard/content/listContentEditable.jsp?content_id=_369615_1&course_id=_13255_1&mode=reset",
                "https://franciscan.blackboard.com/bbcswebdav/courses/Lsand/Test%20Calendar.html");

        //botty.removeIcons("https://franciscan.blackboard.com/webapps/blackboard/execute/announcement?method=search&context=course&course_id=_17070_1&handle=cp_announcements&mode=cpview");
    }
}
