package fr.matthieu.herobot.services.command.parsers.discord;

import fr.matthieu.herobot.services.command.IParser;
import fr.matthieu.herobot.services.command.ParsingError;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class ChannelParser implements IParser<GuildChannel> {
    @Override
    public Class<GuildChannel> getTarget() {
        return GuildChannel.class;
    }

    @Override
    public GuildChannel parse(String value, Message context) throws Exception {
        if(value.startsWith("<#"))
            return context.getGuild().getGuildChannelById(value.substring(2, value.length() - 1));
        else {
            List<TextChannel> channels = context.getGuild().getTextChannelsByName(value, true);
            if (channels.size() > 0) {
                return channels.get(0);
            } else throw new ParsingError("Can't find a channel with name {}", value);
        }
    }
}
