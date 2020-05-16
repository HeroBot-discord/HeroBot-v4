package fr.matthieu.herobot.utilities.classes;

import fr.matthieu.herobot.services.DiscordService;
import fr.matthieu.herobot.utilities.ServicesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.FutureTask;

public abstract class Service {

    protected final String serviceName;
    protected final ServicePriority servicePriority;
    protected final boolean isCriticalService;
    protected final Logger logger;
    protected final ServicesContainer container;

    private ServiceState state = ServiceState.INITIAL;

    public Service(ServicePriority priority, boolean critical, ServicesContainer container) {
        this.serviceName = this.getClass().getName();
        this.servicePriority = priority;
        this.isCriticalService = critical;
        this.container = container;

        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public abstract void initialize() throws Exception;

    public abstract void start() throws Exception;

    public abstract void shutdown() throws Exception;

    public abstract void kill() throws Exception;

    public FutureTask<Void> doInitialize() {
        FutureTask<Void> future = new FutureTask<>(() -> {
            this.initialize();
            this.state = ServiceState.INITIALIZED;
            return null;
        });
        if (this.state != ServiceState.INITIAL)
            future.cancel(false);
        return future;
    }

    public FutureTask<Void> doStart() {
        FutureTask<Void> future = new FutureTask<>(() -> {
            this.start();
            this.state = ServiceState.LOADED;
            return null;
        });
        if (this.state != ServiceState.INITIALIZED)
            future.cancel(false);
        return future;
    }

    public FutureTask<Void> doShutdown() {
        FutureTask<Void> future = new FutureTask<>(() -> {
            this.shutdown();
            this.state = ServiceState.SHUTDOWN;
            return null;
        });
        if (this.state != ServiceState.LOADED)
            future.cancel(false);
        return future;
    }

    public FutureTask<Void> doKill() {
        FutureTask<Void> future = new FutureTask<>(() -> {
            this.kill();
            this.state = ServiceState.KILLED;
            return null;
        });
        if (this.state != ServiceState.LOADED)
            future.cancel(false);
        return future;
    }

    public void registerEventHandlers(Object... listeners) {
        DiscordService service = (DiscordService) this.container.getService(DiscordService.class);
        service.getJda()
                .addEventListener(listeners);
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServicePriority getServicePriority() {
        return servicePriority;
    }
}
