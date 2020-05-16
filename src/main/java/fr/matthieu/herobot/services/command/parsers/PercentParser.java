package fr.matthieu.herobot.services.command.parsers;

import fr.matthieu.herobot.services.command.IParser;
import fr.matthieu.herobot.services.command.ParsingError;
import fr.matthieu.herobot.services.command.parsers.types.Percents;
import net.dv8tion.jda.api.entities.Message;

public class PercentParser implements IParser<Percents> {
    @Override
    public Class<Percents> getTarget() {
        return Percents.class;
    }

    @Override
    public Percents parse(String value, Message context) throws Exception {
        final long percentValue = Long.parseLong(value);
        if (percentValue >= 0 && percentValue <= 100) {
            return new Percents(percentValue);
        } else throw new ParsingError("A percentage need to > 0 && < 100.");
    }
}
