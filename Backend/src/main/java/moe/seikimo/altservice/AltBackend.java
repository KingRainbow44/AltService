package moe.seikimo.altservice;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import moe.seikimo.altservice.command.SimpleCommandMap;
import moe.seikimo.altservice.command.player.DisconnectCommand;
import moe.seikimo.altservice.command.player.RequestCommand;
import moe.seikimo.altservice.command.util.ReloadCommand;
import moe.seikimo.altservice.command.util.StopCommand;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.player.command.PlayerCommandMap;
import moe.seikimo.altservice.utils.LoggerUtils;
import moe.seikimo.altservice.utils.objects.ThreadFactoryBuilder;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;

public final class AltBackend {
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Alt Backend");
    @Getter private static final EventLoopGroup eventGroup
            = new NioEventLoopGroup(0, ThreadFactoryBuilder.base());

    private static LineReader lineReader = null;

    static {
        // Set logback configuration file.
        System.setProperty("logback.configurationFile", "logback.xml");
        // Disable other loggers.
        LoggerUtils.disableLoggers();
    }

    /*
     * Command maps.
     */
    @Getter private static final SimpleCommandMap consoleCommands
            = new SimpleCommandMap();
    @Getter private static final PlayerCommandMap playerCommands
            = new PlayerCommandMap();


    /**
     * Application entrypoint.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Load the configuration.
        Configuration.load();

        // Start the console.
        AltBackend.getConsole();
        new Thread(AltBackend::startConsole).start();

        // Listen for a shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PlayerManager.destroyAll();
            AltBackend.getEventGroup().shutdownGracefully();
            AltBackend.getLogger().info("Stopping backend...");
        }));

        // Register commands.
        registerCommands();

        AltBackend.getLogger().info("Alt Backend started.");
    }

    /**
     * @return The terminal line reader.
     *         Creates a new line reader if not already created.
     */
    public static LineReader getConsole() {
        // Check if the line reader exists.
        if (AltBackend.lineReader == null) {
            Terminal terminal = null; try {
                // Create a native terminal.
                terminal = TerminalBuilder.builder()
                        .jna(true).build();
            } catch (Exception ignored) {
                try {
                    // Fallback to a dumb JLine terminal.
                    terminal = TerminalBuilder.builder()
                            .dumb(true).build();
                } catch (Exception ignored1) { }
            }

            // Set the line reader.
            AltBackend.lineReader = LineReaderBuilder.builder()
                    .terminal(terminal).build();
        }

        return AltBackend.lineReader;
    }

    /**
     * Starts the line reader.
     */
    public static void startConsole() {
        String input = null;
        var isLastInterrupted = false;
        var logger = AltBackend.getLogger();

        while (true) {
            try {
                input = AltBackend.lineReader.readLine("> ");
            } catch (UserInterruptException ignored) {
                if (!isLastInterrupted) {
                    isLastInterrupted = true;
                    logger.info("Press Ctrl-C again to shutdown.");
                    continue;
                } else {
                    Runtime.getRuntime().exit(0);
                }
            } catch (EndOfFileException ignored) {
                continue;
            } catch (IOError e) {
                logger.error("An IO error occurred while trying to read from console.", e);
                return;
            }

            isLastInterrupted = false;

            try {
                // Invoke the command.
                consoleCommands.invoke(input);
            } catch (Exception e) {
                logger.warn("An error occurred while trying to invoke command.", e);
            }
        }
    }

    private static void registerCommands() {
        // Service Commands
        consoleCommands.addCommand(new RequestCommand());
        consoleCommands.addCommand(new DisconnectCommand());
        consoleCommands.addCommand(new StopCommand());
        consoleCommands.addCommand(new ReloadCommand());

        // Player Commands
        playerCommands.addCommand(new RequestCommand());
        playerCommands.addCommand(new DisconnectCommand());
    }
}
