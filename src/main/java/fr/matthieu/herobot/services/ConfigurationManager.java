package fr.matthieu.herobot.services;

import fr.matthieu.herobot.utilities.ServicesContainer;
import fr.matthieu.herobot.utilities.classes.Service;
import fr.matthieu.herobot.utilities.classes.ServicePriority;

public class ConfigurationManager extends Service {

    public ConfigurationManager(ServicesContainer container) {
        super(ServicePriority.HIGHEST, true, container);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void kill() {

    }

    public String getToken() {
        return System.getenv("TOKEN");
    }

    public String getPrefix() {
        return System.getenv("PREFIX");
    }
}
