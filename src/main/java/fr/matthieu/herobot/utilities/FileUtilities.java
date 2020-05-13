package fr.matthieu.herobot.utilities;

import fr.matthieu.herobot.Main;
import fr.matthieu.herobot.utilities.classes.Service;
import fr.matthieu.herobot.utilities.classes.plugin.PluginManifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class FileUtilities {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static Yaml yaml = new Yaml();

    public static void loadJarFile(String[] files, ServicesContainer container) {
        for (String file : files) {
            try {
                loadJarFile(file, container);
            } catch (IOException error) {
                logger.error("Error!", error);
            }
        }
    }

    public static void loadJarFile(String file, ServicesContainer container) throws IOException {
        JarFile jarFile = new JarFile(file);
        Manifest jarManifest = jarFile.getManifest();
        String pluginManifest = jarManifest.getMainAttributes().getValue("Plugin-Manifest");
        if (pluginManifest == null) throw new InvalidObjectException("Can't find the Plugin-Manifest attribute!");
        ZipEntry entry = jarFile.getEntry(pluginManifest);
        if (entry == null) return;
        InputStream stream = jarFile.getInputStream(entry);
        PluginManifest manifest = yaml.loadAs(stream, PluginManifest.class);
        Enumeration<JarEntry> entries = jarFile.entries();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new URL("jar:file:" + file + "!/")});
        while (entries.hasMoreElements()) {
            JarEntry element = entries.nextElement();
            if (element.isDirectory()) continue;

            if (element.getName().endsWith(".class")) {
                try {
                    classLoader.loadClass(element.getName().substring(0, element.getName().length() - 6).replace('/', '.'));

                } catch (ClassNotFoundException error) {
                    logger.error("Couldn't load the class {} from {}", element.getName(), file, error);
                }
            }
        }

        try {
            Class<? extends Service> clas = (Class<? extends Service>) classLoader.loadClass(manifest.mainClass);
            container.registerService(clas, manifest);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Couldn't load the class {} from {}", manifest.name, manifest.name, e);
        }

        logger.info("Loaded {} from {}", manifest.name, String.join(" & ", manifest.authors));
    }

    public static void loadJarFilesFolder(String folder, ServicesContainer container) {
        try {
            Stream<Path> paths = Files.walk(Paths.get(folder));
            String[] files = paths
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".jar"))
                    .map(x -> x.toString())
                    .toArray(String[]::new);
            loadJarFile(files, container);
            logger.info("Loaded {} plugins from {}", files.length, folder);
        } catch (IOException error) {
            logger.error("Couldn't load the plugins in the plugins folder", error);
        }
    }

}
