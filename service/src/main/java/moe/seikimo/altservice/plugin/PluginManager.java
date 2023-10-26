package moe.seikimo.altservice.plugin;

import lombok.Getter;
import moe.seikimo.altservice.event.Event;
import moe.seikimo.altservice.utils.EncodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarFile;

public final class PluginManager {
    @Getter private final Logger logger = LoggerFactory.getLogger("Plugin Manager");

    private final List<Plugin> loadedPlugins = new ArrayList<>();
    private final List<Plugin> enabledPlugins = new ArrayList<>();
    private final Map<Class<? extends Event>, Set<PluginListener<? extends Event>>> eventListeners = new HashMap<>();

    /**
     * Attempts to load all plugins in the plugins folder.
     */
    public void loadAllPlugins() {
        var pluginsFolder = new File(System.getProperty("user.dir"), "plugins");
        if (!pluginsFolder.exists()) {
            if (!pluginsFolder.mkdirs()) {
                this.getLogger().warn("Unable to create the plugins directory.");
            }

            return;
        }

        // Scan all JAR files in the directory.
        var jarFiles = pluginsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            return;
        }

        // Determine the URLs of all plugins.
        var urls = new ArrayList<URL>();
        Arrays.stream(jarFiles).forEach(file -> {
            try {
                urls.add(file.toURI().toURL());
            } catch (Exception e) {
                this.getLogger().warn("Unable to load plugin: " + file.getName(), e);
            }
        });

        // Load all plugins.
        var classLoader = new URLClassLoader(urls.toArray(new URL[0]));
        for (var plugin : jarFiles) try {
            var url = plugin.toURI().toURL();
            try (var pluginLoader = new URLClassLoader(new URL[] { url })) {
                // Attempt to read the plugin configuration.
                var configStream = pluginLoader.findResource("plugin.json");
                var configReader = new InputStreamReader(configStream.openStream());
                var pluginConfig = EncodingUtils.jsonDecode(configReader, PluginConfig.class);
                if (pluginConfig == null || !pluginConfig.validPlugin()) {
                    this.getLogger().warn("Unable to load plugin: " + plugin.getName());
                    continue;
                }

                // Load all classes in the JAR file.
                var jarFile = new JarFile(plugin);
                var entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    var entry = entries.nextElement();
                    if (entry.isDirectory()
                            || !entry.getName().endsWith(".class")
                            || entry.getName().contains("module-info")) {
                        continue;
                    }

                    classLoader.loadClass(entry.getName()
                            .replace(".class", "")
                            .replace("/", "."));
                }

                // Create the plugin's instance.
                var pluginClass = classLoader.loadClass(pluginConfig.getMainClass());
                var classInstance = pluginClass.getDeclaredConstructor().newInstance();
                if (!(classInstance instanceof Plugin pluginInstance))
                    throw new IllegalArgumentException("Plugin does not extend Plugin class.");

                // Initialize the plugin.
                this.getLogger().info("Loading plugin: {} v{}",
                        pluginConfig.getName(), pluginConfig.getVersion());
                pluginInstance.initializePlugin(pluginConfig, plugin, classLoader);
                this.loadedPlugins.add(pluginInstance);

                // Clean-up.
                configReader.close();
            }
        } catch (Exception exception) {
            this.getLogger().warn("Failed to enable plugin.", exception);
        }
    }

    /**
     * Attempts to enable all plugins.
     */
    public void enableAllPlugins() {
        this.loadedPlugins.forEach(plugin -> {
            try {
                this.getLogger().info("Enabling {} v{}",
                        plugin.getName(),
                        plugin.getVersion());

                plugin.onEnable();
                this.enabledPlugins.add(plugin);
            } catch (Exception exception) {
                this.getLogger().warn("Failed to enable plugin.", exception);
            }
        });
    }

    /**
     * Attempts to disable all plugins.
     */
    public void disableAllPlugins() {
        this.enabledPlugins.forEach(plugin -> {
            try {
                this.getLogger().info("Disabling {} v{}",
                        plugin.getName(),
                        plugin.getVersion());

                plugin.onDisable();
            } catch (Exception exception) {
                this.getLogger().warn("Failed to disable plugin.", exception);
            }
        });

        this.enabledPlugins.clear();
    }

    /**
     * Registers an event listener.
     *
     * @param event The event to listen for.
     * @param listener The listener.
     * @param plugin The plugin registering the listener.
     */
    public <T extends Event> void registerEvents(Class<T> event, Consumer<T> listener, Plugin plugin) {
        this.eventListeners
                .computeIfAbsent(event, k -> new HashSet<>())
                .add(new PluginListener<>(plugin, listener));
    }

    /**
     * Removes the listeners registered by a plugin.
     *
     * @param plugin The plugin to remove listeners for.
     */
    public void removeListeners(Plugin plugin) {
        this.eventListeners.values().forEach(listeners -> listeners.removeIf(
                listener -> listener.registrar().equals(plugin)));
    }

    /**
     * Calls an event.
     *
     * @param event The event to call.
     */
    public void callEvent(Event event) {
        this.eventListeners.getOrDefault(event.getClass(), Collections.emptySet())
                .forEach(listener -> {
                    try {
                        listener.invoke(event);
                    } catch (Exception exception) {
                        this.getLogger().warn("Failed to call event.", exception);
                    }
                });
    }
}
