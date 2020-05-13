package fr.matthieu.herobot.utilities.classes;

public enum ServicePriority {
    LOWEST(0),
    LOW(1),
    MEDIUM(2),
    HIGHT(3),
    HIGHEST(4);

    private int priority;

    ServicePriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }
}
