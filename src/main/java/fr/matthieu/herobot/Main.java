package fr.matthieu.herobot;


import fr.matthieu.herobot.utilities.FileUtilities;
import fr.matthieu.herobot.utilities.ServicesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    private final ServicesContainer container = new ServicesContainer();
    private final Logger logger = LoggerFactory.getLogger(Main.class);

    Main() {

        long currentMillis = System.currentTimeMillis();

        new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/figlet.txt"))).lines()
                .map(x -> x
                        .replace("%version%", "1.0")
                        .replace("%os%", System.getProperty("os.name"))
                        .replace("%arch%", System.getProperty("os.arch"))
                        .replace("%branch%", "master"))
                .forEach(System.out::println);

        logger.info("Loading libraries.");
        // Load HeroBot's services first.
        container.autoLoad("fr.matthieu.herobot");
        logger.info("Loaded the core HeroBot's libraries.");
        // Initialize the current services.
        container.loadCurrentServices();

        FileUtilities.loadJarFilesFolder("plugins", container);

        container.loadCurrentServices();

        logger.info("Loaded HeroBot in {}ms", System.currentTimeMillis() - currentMillis);
    }

    public static void main(String... args) {
        new Main();
    }
}
