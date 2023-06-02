package moe.seikimo.altservice;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import moe.seikimo.altservice.command.CommandMap;
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
                CommandMap.invoke(input);
            } catch (Exception e) {
                logger.warn("An error occurred while trying to invoke command.", e);
            }
        }
    }
}
