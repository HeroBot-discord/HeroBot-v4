package fr.matthieu.herobot.services.command;

import fr.matthieu.herobot.services.command.annotations.FromMessage;
import fr.matthieu.herobot.services.command.annotations.Inject;
import fr.matthieu.herobot.services.command.annotations.Name;
import fr.matthieu.herobot.utilities.ObjectInitiator;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nullable;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandParser {
    private final HashMap<Class<?>, IParser<?>> parsers = new HashMap<>();

    public Object[] parseCommandArgs(Message message, SimpleCommand commandMethod, Object[] arguments) throws ParsingError {
        List<String> parsedArguments = parseSeparators(message.getContentRaw().split(" "));
        Object[] objects = new Object[commandMethod.method.getParameters().length];
        int found = 0;
        for (Parameter param : commandMethod.method.getParameters()) {
            boolean done = false;

            if (parsers.containsKey(param.getType()) && 0 == param.getAnnotationsByType(Inject.class).length && parsedArguments.size() > 0) {
                String argumentString = parsedArguments.remove(0);
                try {
                    objects[found] = parsers.get(param.getType()).parse(argumentString, message);
                    found++;
                    done = true;
                } catch (ParsingError error) {
                    throw error;
                }
                catch (Exception error) {
                    throw new ParsingError("Invalid argument supplied `%s`. The argument need to be an `%s`", argumentString, param.getType().getSimpleName());
                }
            }
            if (!done && 0 == param.getAnnotationsByType(FromMessage.class).length) {
                for (Object argument : arguments) {
                    if (param.getType().isAssignableFrom(argument.getClass())) {
                        objects[found] = argument;
                        found++;
                        done = true;
                        break;
                    }
                }
            }
            if (!done && 1 == param.getAnnotationsByType(Nullable.class).length) {
                objects[found] = null;
                found++;
                done = true;
            }
            if (done) continue;
            throw new ParsingError("Invalid command configuration, the argument of type `%s` can't be provided to the method.", param.getType().getSimpleName());
        }
        if (found < commandMethod.method.getParameters().length)
            throw new ParsingError("Invalid syntax for this command `%s`", buildCommandSyntax(commandMethod));
        return objects;
    }

    public String buildCommandSyntax(SimpleCommand command) {
        StringBuilder builder = new StringBuilder()
                .append(command.command.name())
                .append(" ");
        for (Parameter parameter : command.method.getParameters()) {
            if (parsers.containsKey(parameter.getType()) && 0 == parameter.getAnnotationsByType(Inject.class).length) {
                String name = parameter.getName();
                if (parameter.getAnnotation(Name.class) != null) {
                    name = parameter.getAnnotation(Name.class).value();
                }
                builder
                        .append("{")
                        .append(name)
                        .append(parameter.getAnnotation(Nullable.class) == null ? "" : "?")
                        .append(":")
                        .append(parameter.getType().getSimpleName())
                        .append("} ");
            }
        }
        String r = builder.toString();
        return r.substring(0, r.length() - 1);
    }

    public void registerParser(Class<? extends IParser<?>> parser) throws InvocationTargetException, InvalidClassException, InstantiationException, IllegalAccessException {
        IParser<?> implementation = new ObjectInitiator<IParser<?>>(parser).buildObject(new Object[]{});
        parsers.putIfAbsent(implementation.getTarget(), implementation);
    }

    private List<String> parseSeparators(String[] args) {
        List<String> arguments = new ArrayList<>();
        StringBuilder currentArgument = null;
        boolean skip = false;
        for (String arg : args) {
            if (!skip)
            {
                skip = true;
                continue;
            }
            if (currentArgument != null) {
                currentArgument.append(arg);
                if (arg.endsWith("\"")) {
                    String argument = currentArgument.toString();
                    arguments.add(argument.substring(1, argument.length() - 1));
                    currentArgument = null;
                } else {
                    currentArgument.append(" ");
                }
            } else {
                if (arg.startsWith("\"")) {
                    currentArgument = new StringBuilder();
                    currentArgument.append(arg).append(" ");
                } else {
                    arguments.add(arg);
                }
            }
        }
        return arguments;
    }
}
