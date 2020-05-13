package fr.matthieu.herobot.utilities.classes.plugin;

import fr.matthieu.herobot.utilities.ServicesContainer;
import fr.matthieu.herobot.utilities.classes.Service;
import fr.matthieu.herobot.utilities.classes.ServicePriority;

public abstract class Plugin extends Service {

    protected final PluginManifest manifest;

    public Plugin(ServicePriority priority, ServicesContainer container, PluginManifest manifest) {
        super(priority, false, container);
        this.manifest = manifest;
    }

    public PluginManifest getManifest() {
        return manifest;
    }
}
