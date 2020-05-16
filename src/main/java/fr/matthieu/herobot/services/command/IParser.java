package fr.matthieu.herobot.services.command;

import net.dv8tion.jda.api.entities.Message;

public interface IParser<T> {
    Class<T> getTarget();
    T parse(String value, Message context) throws Exception;
}
