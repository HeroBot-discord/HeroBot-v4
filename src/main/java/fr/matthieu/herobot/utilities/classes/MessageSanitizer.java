package fr.matthieu.herobot.utilities.classes;

public class MessageSanitizer {
    public static String sanitizeMessage(String message) {
        return message.replace("@", "@\u200B");
    }
}
