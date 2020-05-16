package fr.matthieu.herobot.services;

import fr.matthieu.herobot.utilities.ServicesContainer;
import fr.matthieu.herobot.utilities.classes.Service;
import fr.matthieu.herobot.utilities.classes.ServicePriority;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisService extends Service {
    private Config config;
    private RedissonClient client;

    public RedisService(ServicesContainer container) {
        super(ServicePriority.HIGHT, true, container);
    }

    @Override
    public void initialize() throws Exception {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost");
        this.config = config;
    }

    @Override
    public void start() throws Exception {
        this.client = Redisson.create(this.config);
        this.config = null;
    }

    private RedissonClient getClient() {
        return client;
    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public void kill() throws Exception {

    }
}
