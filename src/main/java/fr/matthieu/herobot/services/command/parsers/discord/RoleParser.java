package fr.matthieu.herobot.services.command.parsers.discord;

import fr.matthieu.herobot.services.command.IParser;
import fr.matthieu.herobot.services.command.ParsingError;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class RoleParser implements IParser<Role> {
    @Override
    public Class<Role> getTarget() {
        return Role.class;
    }

    @Override
    public Role parse(String value, Message context) throws Exception {
        if(value.startsWith("<@&"))
            return context.getGuild().getRoleById(value.substring(3, value.length() - 1));
        else {
            List<Role> channels = context.getGuild().getRolesByName(value, true);
            if (channels.size() > 0) {
                return channels.get(0);
            } else throw new ParsingError("Can't find a role with name {}", value);
        }
    }
}
