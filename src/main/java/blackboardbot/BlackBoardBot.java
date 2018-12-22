package blackboardbot;

public interface BlackBoardBot {
    boolean hasCredentials();
    void username(String username);
    String username();
    void password(String password);
    String password();

    void stop();
    boolean isRunning();

    void revStat(String url);
    void revStat(Constraints constraints);
}
