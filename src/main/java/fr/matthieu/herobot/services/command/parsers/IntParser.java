package fr.matthieu.herobot.services.command.parsers;

import fr.matthieu.herobot.services.command.IParser;
import net.dv8tion.jda.api.entities.Message;

public class IntParser implements IParser<Integer> {
    @Override
    public Class<Integer> getTarget() {
        return int.class;
    }

    @Override
    public Integer parse(String value, Message context) throws Exception {
        return Integer.parseInt(value);
    }
}
