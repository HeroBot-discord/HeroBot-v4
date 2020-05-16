package fr.matthieu.herobot.services.command.parsers.types;

public class Percents {
    private final Long value;

    public Long getValue() {
        return value;
    }

    public Percents(Long val) {
        this.value = val;
    }
}
