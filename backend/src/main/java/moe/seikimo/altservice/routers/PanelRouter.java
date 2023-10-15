package moe.seikimo.altservice.routers;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public interface PanelRouter {
    Logger LOGGER = LoggerFactory.getLogger("Web Panel");

    /**
     * Configures the Javalin router.
     *
     * @param javalin The Javalin instance.
     */
    static void configure(Javalin javalin) {
        javalin.cfg.staticFiles.add(cfg -> {
            cfg.location = Location.EXTERNAL;
            cfg.hostedPath = "/panel";
            cfg.directory = "panel";
        });
        javalin.ws("/socket", PanelRouter::setupSocket);
    }

    /**
     * Configures the WebSocket.
     *
     * @param config The WebSocket configuration.
     */
    static void setupSocket(WsConfig config) {
        config.onClose(PanelRouter::onClose);
        config.onError(PanelRouter::onError);
        config.onConnect(PanelRouter::onConnect);
        config.onMessage(PanelRouter::onMessage);
    }

    /**
     * Invoked when a WebSocket connection is closed.
     *
     * @param ctx The WebSocket context.
     */
    static void onClose(WsCloseContext ctx) {
        LOGGER.info("Connection closed from {}.", ctx.session.getRemoteAddress());
    }

    /**
     * Invoked when a WebSocket connection encounters an error.
     *
     * @param ctx The WebSocket context.
     */
    static void onError(WsErrorContext ctx) {
        LOGGER.error("WebSocket error: {}.",
                ctx.error() == null ? "(no message provided)" :
                ctx.error().getMessage());
    }

    /**
     * Invoked when a WebSocket connection is opened.
     *
     * @param ctx The WebSocket context.
     */
    static void onConnect(WsConnectContext ctx) {
        // Disable the idle timeout.
        ctx.session.setIdleTimeout(Duration.ofDays(1));

        LOGGER.info("New connection from {}.", ctx.session.getRemoteAddress());
    }

    /**
     * Invoked when a WebSocket message is received.
     *
     * @param ctx The WebSocket context.
     */
    static void onMessage(WsMessageContext ctx) {
        LOGGER.info("Received message from {}.", ctx.session.getRemoteAddress());
    }
}
