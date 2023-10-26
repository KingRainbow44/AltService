package moe.seikimo.altservice.plugin;

import lombok.Getter;
import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Plugin {
    @Getter private final Set<Consumer<? extends Event>> listeners = new HashSet<>();

    @Getter private PluginConfig config;
    @Getter private File dataFolder;
    @Getter private ClassLoader classLoader;
    @Getter private Logger logger;

    /**
     * Called when the plugin is first loaded.
     */
    public void onLoad() {}

    /**
     * Called when the plugin is enabled.
     */
    public void onEnable() {}

    /**
     * Called when the plugin is disabled.
     */
    public void onDisable() {}

    /**
     * @return The plugin name.
     */
    public final String getName() {
        return this.getConfig().getName();
    }

    /**
     * @return The plugin description.
     */
    public final String getVersion() {
        return this.getConfig().getVersion();
    }

    /**
     * @return The plugin's manager.
     */
    public final PluginManager getPluginManager() {
        return AltBackend.getPluginManager();
    }

    /**
     * Initializes the plugin.
     * This also loads the plugin.
     *
     * @param config The plugin configuration.
     * @param dataFolder The data folder.
     * @param classLoader The class loader.
     */
    void initializePlugin(PluginConfig config, File dataFolder, ClassLoader classLoader) {
        this.config = config;
        this.dataFolder = dataFolder;
        this.classLoader = classLoader;

        this.logger = LoggerFactory.getLogger(this.getName());

        this.onLoad();
    }
}
