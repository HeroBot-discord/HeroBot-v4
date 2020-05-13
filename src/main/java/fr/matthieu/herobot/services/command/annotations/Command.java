package fr.matthieu.herobot.services.command.annotations;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Command {
    String name();

    String desciption() default "*pas de description*";

    Permission[] permissions() default {};

    Permission[] botPermissions() default {};

    String[] aliasses() default {};

    boolean ratelimit() default true;
}
