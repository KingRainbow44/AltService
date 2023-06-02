package moe.seikimo.altservice;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AltService {
    @Getter
    private static final Logger logger
            = LoggerFactory.getLogger("Alt Service");

    static {
        // Set logback configuration file.
        System.setProperty("logback.configurationFile", "logback.xml");
    }

    /**
     * Application entrypoint.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {

    }
}
