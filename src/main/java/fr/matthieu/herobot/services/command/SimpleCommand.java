package fr.matthieu.herobot.services.command;

import fr.matthieu.herobot.services.command.annotations.Command;
import fr.matthieu.herobot.utilities.classes.Service;

import java.lang.reflect.Method;

public class SimpleCommand {
    public final Command command;
    public final Method method;
    public final Object instance;
    public final Service service;

    public SimpleCommand(Method method, Object instance, Command commandAnnotation, Service service) {
        this.method = method;
        this.instance = instance;
        this.command = commandAnnotation;
        this.service = service;
    }
}
