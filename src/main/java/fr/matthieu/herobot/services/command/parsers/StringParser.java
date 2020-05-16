package fr.matthieu.herobot.services.command.parsers;

import fr.matthieu.herobot.services.command.IParser;
import net.dv8tion.jda.api.entities.Message;

public class StringParser implements IParser<String> {
    @Override
    public Class<String> getTarget() {
        return String.class;
    }

    @Override
    public String parse(String value, Message context) throws Exception {
        return value;
    }
}
