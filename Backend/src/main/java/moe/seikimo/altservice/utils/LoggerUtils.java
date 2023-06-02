package moe.seikimo.altservice.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public interface LoggerUtils {
    /**
     * Disables all useless loggers.
     */
    static void disableLoggers() {
        // Turn off loggers.
        var logger = (Logger) LoggerFactory.getLogger("org.cloudburstmc.protocol");
        logger.setLevel(Level.OFF);

        logger = (Logger) LoggerFactory.getLogger("io.netty.util");
        logger.setLevel(Level.OFF);

        logger = (Logger) LoggerFactory.getLogger("io.netty.channel");
        logger.setLevel(Level.OFF);
    }
}
