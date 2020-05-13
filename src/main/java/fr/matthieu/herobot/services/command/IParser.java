package fr.matthieu.herobot.services.command;

public interface IParser {
    Class getTarget();

    Object parse(String remove) throws Exception;
}
