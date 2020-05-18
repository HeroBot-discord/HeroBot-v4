package fr.matthieu.herobot.services;

import fr.matthieu.herobot.utilities.ServicesContainer;
import fr.matthieu.herobot.utilities.classes.Service;
import fr.matthieu.herobot.utilities.classes.ServicePriority;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class DiscordService extends Service {

    private JDA client;

    private ConfigurationManager configurationManager;

    public DiscordService(ServicesContainer container) {
        super(ServicePriority.LOWEST, true, container);

    }

    @Override
    public void initialize() {
        configurationManager = ((ConfigurationManager) container.getService(ConfigurationManager.class));
    }

    @Override
    public void start() throws LoginException, InterruptedException {
        client = JDABuilder.createLight(configurationManager.getToken())
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.VOICE_STATE, CacheFlag.MEMBER_OVERRIDES, CacheFlag.CLIENT_STATUS)
                .setEnabledIntents(GatewayIntent.GUILD_MESSAGES)
                .setActivity(Activity.of(Activity.ActivityType.DEFAULT, "java.exe"))
                .setEventManager(new AnnotatedEventManager())
                .build();
        client.awaitStatus(JDA.Status.CONNECTED);
    }

    @Override
    public void shutdown() {
        client.shutdown();
    }

    @Override
    public void kill() {
        client = null;
        System.gc();
    }

    public JDA getJda() {
        return client;
    }
}
