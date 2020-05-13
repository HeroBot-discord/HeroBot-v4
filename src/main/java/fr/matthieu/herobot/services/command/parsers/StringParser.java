package fr.matthieu.herobot.services.command.parsers;

import fr.matthieu.herobot.services.command.IParser;

public class StringParser implements IParser {
    @Override
    public Class getTarget() {
        return String.class;
    }

    @Override
    public Object parse(String remove) {
        return remove.replace("\"", "");
    }
}
