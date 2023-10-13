package moe.seikimo.altservice;

import lombok.Getter;
import moe.seikimo.altservice.utils.EncodingUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Getter
public final class Configuration {
    private static Configuration instance
            = new Configuration();

    /**
     * @return The configuration.
     */
    public static Configuration get() {
        return Configuration.instance;
    }

    /**
     * Loads the plugin configuration.
     */
    public static void load() {
        var configFile = new File(AltBackend.getConfigFile());

        if (!configFile.exists()) {
            // Save this configuration.
            Configuration.save();
        } else try {
            // Load the configuration.
            Configuration.instance = EncodingUtils.jsonDecode(
                    new FileReader(configFile), Configuration.class);

            // Check if the configuration is null.
            if (Configuration.instance == null) {
                Configuration.instance = new Configuration();
            }
        } catch (IOException ignored) {
            AltBackend.getLogger().error("Unable to load configuration.");
        }
    }

    /**
     * Saves the plugin configuration.
     */
    public static void save() {
        var configFile = new File("config.json");

        try {
            // Save the configuration.
            var json = EncodingUtils.jsonEncode(Configuration.instance);
            Files.write(configFile.toPath(), json.getBytes());
        } catch (IOException ignored) {
            AltBackend.getLogger().error("Unable to save configuration.");
        }
    }

    public boolean respondToCommands = true;
    public Server server = new Server();
    public Server backend = new Server();
    public boolean debug = false;
    public List<String> ignoredDebugPackets = List.of();

    @Getter
    public static class Server {
        public String address = "127.0.0.1";
        public int port = 19132;

        /**
         * @return The server address and port as a pair.
         */
        public String pair() {
            return this.address + ":" + this.port;
        }
    }
}
