package blackboardbot;

public interface Constraints {
    void addConstraint(String type, String description);

    @Override
    String toString();
}
