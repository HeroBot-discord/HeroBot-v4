package fr.matthieu.herobot.services.command.parsers;

import fr.matthieu.herobot.services.command.IParser;

public class IntParser implements IParser {
    @Override
    public Class getTarget() {
        return int.class;
    }

    @Override
    public Object parse(String remove) {
        return Integer.parseInt(remove.replace("\"", ""));
    }
}
