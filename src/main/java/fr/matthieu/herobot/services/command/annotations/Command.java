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

    String description() default "*pas de description*";
    Permission[] permissions() default {};
    Permission[] botPermissions() default {};
    boolean rateLimit() default true;
    boolean adminOnly() default false;
    boolean enabled() default true;
    int price() default 1;
}
