package fr.matthieu.herobot.utilities;

import fr.matthieu.herobot.utilities.classes.Service;
import fr.matthieu.herobot.utilities.classes.plugin.PluginManifest;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;

public class ServicesContainer {

    private final HashMap<String, Service> servicesMap;
    private final Logger logger = LoggerFactory.getLogger(ServicesContainer.class);

    public ServicesContainer() {
        this.servicesMap = new HashMap<>();
    }

    private static Map<String, Service> sortByValue(Map<String, Service> unsortMap) {
        List<Map.Entry<String, Service>> list = new LinkedList<>(unsortMap.entrySet());
        list.sort(Comparator.comparingInt(entry -> entry.getValue().getServicePriority().getPriority()));
        Map<String, Service> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Service> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public void registerService(Class<? extends Service> serviceClass) throws InvocationTargetException, InvalidClassException, InstantiationException, IllegalAccessException {
        if ((serviceClass.getModifiers() & Modifier.ABSTRACT) == 0 && !serviceClass.isInterface()) {
            ObjectInitiator<Service> initiator = new ObjectInitiator(serviceClass);
            Service service = initiator.buildObject(new Object[] {
                    this
            });
            servicesMap.putIfAbsent(service.getServiceName(), service);
        }
    }

    public void autoLoad(String pack) {
        logger.info("Auto loading package {}", pack);
        try {
            Reflections reflections = new Reflections(pack);
            Set<Class<? extends Service>> classes = reflections.getSubTypesOf(Service.class);
            for (Class<? extends Service> extendedClass : classes) {
                try {
                    this.registerService(extendedClass);
                } catch (InvocationTargetException | InvalidClassException | InstantiationException | IllegalAccessException e) {
                    this.logger.error("Error while registering service {}", extendedClass.getName(), e);
                }
            }
        } catch (ReflectionsException error) {
            this.logger.error("Error using Reflections while scanning from {}", pack, error);
        }
    }

    public void loadCurrentServices() {
        long sortMillis = System.currentTimeMillis();
        Map<String, Service> sortedMap = sortByValue(this.servicesMap);
        logger.info("Services sorted in {}ms", System.currentTimeMillis() - sortMillis);

        long loadMillis = System.currentTimeMillis();
        for (Service service : sortedMap.values()) {
            try {
                long currentMillis = System.currentTimeMillis();
                FutureTask<Void> task = service.doInitialize();
                task.run();
                task.get(100, TimeUnit.MILLISECONDS);
                logger.info("Service {} started in {}ms", service.getServiceName(), System.currentTimeMillis() - currentMillis);
            } catch (ExecutionException | InterruptedException | TimeoutException error) {
                logger.error("Service {} failed to initialize : {}", service.getServiceName(), error);
            } catch (CancellationException error) { /* Ignore */ }
        }
        logger.info("Services initialized in {}ms", System.currentTimeMillis() - loadMillis);
        long startMillis = System.currentTimeMillis();
        for (Service service : sortedMap.values()) {
            try {
                long currentMillis = System.currentTimeMillis();
                FutureTask<Void> task = service.doStart();
                task.run();
                task.get(6, TimeUnit.SECONDS);
                logger.info("Service {} started in {}ms", service.getServiceName(), System.currentTimeMillis() - currentMillis);
            } catch (ExecutionException | InterruptedException | TimeoutException error) {
                logger.error("Service {} failed to start : {}", service.getServiceName(), error);
            } catch (CancellationException error) { /* Ignore */ }
        }
        logger.info("Services started in {}ms", System.currentTimeMillis() - startMillis);
        System.gc();
    }


    public Service getService(Class<? extends Service> service) {
        return servicesMap.get(service.getName());
    }

    public void shutdown() {
        Map<String, Service> sortedMap = sortByValue(this.servicesMap);
        long stopMillis = System.currentTimeMillis();
        for (Service service : sortedMap.values()) {
            try {
                long currentMillis = System.currentTimeMillis();
                FutureTask<Void> task = service.doShutdown();
                task.run();
                task.get(6, TimeUnit.SECONDS);
                logger.info("Service {} started in {}ms", service.getServiceName(), System.currentTimeMillis() - currentMillis);
            } catch (ExecutionException | InterruptedException | TimeoutException error) {
                logger.error("Service {} failed to start : {}", service.getServiceName(), error);
            } catch (CancellationException error) { /* Ignore */ }
        }
        logger.info("Services stopped in {}ms", System.currentTimeMillis() - stopMillis);
        System.exit(0);
    }

    public void registerService(Class<? extends Service> clas, PluginManifest pluginManifest) throws InvocationTargetException, InvalidClassException, InstantiationException, IllegalAccessException {
        ObjectInitiator<Service> initiator = new ObjectInitiator<Service>(clas);
        Service service = initiator.buildObject(new Object[]{this, pluginManifest});
        servicesMap.putIfAbsent(service.getServiceName(), service);
    }
}
