package fr.matthieu.herobot.services.command;

public class ParsingError extends Exception {
    public ParsingError(String message, Object... args) {
        super(String.format(message, args));
    }
}
