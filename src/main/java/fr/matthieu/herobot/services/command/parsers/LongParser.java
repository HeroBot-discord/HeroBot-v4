package fr.matthieu.herobot.services.command.parsers;

import fr.matthieu.herobot.services.command.IParser;
import net.dv8tion.jda.api.entities.Message;

public class LongParser implements IParser<Long> {
    @Override
    public Class<Long> getTarget() {
        return Long.class;
    }

    @Override
    public Long parse(String value, Message context) throws Exception {
        return Long.parseLong(value);
    }
}
