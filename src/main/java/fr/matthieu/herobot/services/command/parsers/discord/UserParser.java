package fr.matthieu.herobot.services.command.parsers.discord;

import fr.matthieu.herobot.services.command.IParser;
import fr.matthieu.herobot.services.command.ParsingError;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class UserParser implements IParser<User> {
    @Override
    public Class<User> getTarget() {
        return User.class;
    }

    @Override
    public User parse(String value, Message context) throws Exception {
        value = value.replace("!", "");
        if(value.startsWith("<@"))
            return context.getJDA().getUserById(value.substring(2, value.length() - 1));
        else {
            List<User> channels = context.getJDA().getUsersByName(value, true);
            if (channels.size() > 0) {
                return channels.get(0);
            } else throw new ParsingError("Can't find a user with name {}", value);
        }
    }
}
