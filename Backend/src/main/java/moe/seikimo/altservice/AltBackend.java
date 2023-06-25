package moe.seikimo.altservice;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import moe.seikimo.altservice.command.SimpleCommandMap;
import moe.seikimo.altservice.command.player.DisconnectCommand;
import moe.seikimo.altservice.command.player.RequestCommand;
import moe.seikimo.altservice.command.util.ReloadCommand;
import moe.seikimo.altservice.command.util.RunScriptCommand;
import moe.seikimo.altservice.command.util.StopCommand;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.player.PlayerTickThread;
import moe.seikimo.altservice.player.command.PlayerCommandMap;
import moe.seikimo.altservice.player.command.action.*;
import moe.seikimo.altservice.player.command.util.*;
import moe.seikimo.altservice.script.ScriptLoader;
import moe.seikimo.altservice.utils.LoggerUtils;
import moe.seikimo.altservice.utils.objects.ThreadFactoryBuilder;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;
import java.io.InputStream;

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

    @Getter private static final PlayerTickThread playerTickThread
            = new PlayerTickThread();

    /*
     * Command maps.
     */
    @Getter private static final SimpleCommandMap consoleCommands
            = new SimpleCommandMap();
    @Getter private static final PlayerCommandMap playerCommands
            = new PlayerCommandMap();

    /**
     * Fetches a resource.
     *
     * @param path The path to the resource.
     *             Prefixed '/' is unneeded.
     * @return The resource as an input stream.
     */
    public static InputStream getResource(String path) {
        try {
            return AltBackend.class.getResourceAsStream("/" + path);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Application entrypoint.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Load the configuration.
        Configuration.load();

        // Load the block palette.
        GameConstants.initializeBlocks();

        // Set the logger in debug mode.
        LoggerUtils.setDebug(AltBackend.getLogger());

        // Start the console.
        AltBackend.getConsole();
        new Thread(AltBackend::startConsole).start();

        // Listen for a shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AltBackend.getPlayerTickThread().shutdown();
            PlayerManager.destroyAll();
            AltBackend.getEventGroup().shutdownGracefully();
            AltBackend.getLogger().info("Stopping backend...");
        }));

        // Start the player tick thread.
        AltBackend.getPlayerTickThread().start();
        // Register commands.
        AltBackend.registerCommands();

        // Initialize the script loader.
        ScriptLoader.initialize();

        AltBackend.getLogger().info("Done! Alt Backend started.");
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

    /**
     * @return Whether the backend is in debug mode.
     */
    public static boolean debug() {
        return Configuration.get().debug;
    }

    private static void registerCommands() {
        // Service Commands
        consoleCommands.addCommand(new RequestCommand());
        consoleCommands.addCommand(new DisconnectCommand());
        consoleCommands.addCommand(new StopCommand());
        consoleCommands.addCommand(new ReloadCommand());
        consoleCommands.addCommand(new RunScriptCommand());

        // Player Commands
        playerCommands.addCommand(new RequestCommand());
        playerCommands.addCommand(new DisconnectCommand());
        playerCommands.addCommand(new MoveCommand());
        playerCommands.addCommand(new LocationCommand());
        playerCommands.addCommand(new RotateCommand());
        playerCommands.addCommand(new FollowCommand());
        playerCommands.addCommand(new AttackCommand());
        playerCommands.addCommand(new InventoryCommand());
        playerCommands.addCommand(new BreakCommand());
        playerCommands.addCommand(new PlaceCommand());
        playerCommands.addCommand(new GuardCommand());
        playerCommands.addCommand(new BehaviorCommand());
        playerCommands.addCommand(new DropCommand());
    }
}
