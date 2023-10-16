package moe.seikimo.altservice.handlers;

import moe.seikimo.altservice.AltService;
import moe.seikimo.altservice.proto.Service.CreateSessionCsNotify;
import moe.seikimo.altservice.proto.Service.DeleteSessionCsNotify;
import moe.seikimo.altservice.proto.Service.ServiceIds;
import moe.seikimo.altservice.proto.Service.UpdateSessionsCsNotify;
import moe.seikimo.altservice.services.ServiceManager;
import org.java_websocket.WebSocket;

public interface SessionHandlers {
    /**
     * Registers the session handlers.
     *
     * @param handler The packet handler.
     */
    static void register(PacketHandler<?> handler) {
        handler.register(
                ServiceIds._CreateSessionCsNotify,
                (WebSocket socket, CreateSessionCsNotify packet) ->
                        SessionHandlers.onCreateSession(socket, packet),
                CreateSessionCsNotify::parseFrom
        );
        handler.register(
                ServiceIds._UpdateSessionsCsNotify,
                (WebSocket socket, UpdateSessionsCsNotify packet) ->
                        SessionHandlers.onUpdateSessions(socket, packet),
                UpdateSessionsCsNotify::parseFrom
        );
        handler.register(
                ServiceIds._DeleteSessionCsNotify,
                (WebSocket socket, DeleteSessionCsNotify packet) ->
                        SessionHandlers.onDeleteSession(socket, packet),
                DeleteSessionCsNotify::parseFrom
        );
    }

    /**
     * Invoked when a service requests to create a session.
     *
     * @param socket The socket.
     * @param notify The notification packet.
     */
    static void onCreateSession(WebSocket socket, CreateSessionCsNotify notify) {
        var instance = ServiceManager.getService(socket);
        if (instance == null) {
            AltService.getLogger().warn("Received session creation request from unregistered service.");
            return;
        }

        // Create the session.
        instance.onCreateSession(notify.getSession());
    }

    /**
     * Invoked when a service requests to delete a session.
     *
     * @param socket The socket.
     * @param notify The notification packet.
     */
    static void onUpdateSessions(WebSocket socket, UpdateSessionsCsNotify notify) {
        var instance = ServiceManager.getService(socket);
        if (instance == null) {
            AltService.getLogger().warn("Received session update request from unregistered service.");
            return;
        }

        // Update the sessions.
        instance.onUpdateSessions(notify.getSessionsList());
    }

    /**
     * Invoked when a service requests to delete a session.
     *
     * @param socket The socket.
     * @param notify The notification packet.
     */
    static void onDeleteSession(WebSocket socket, DeleteSessionCsNotify notify) {
        var instance = ServiceManager.getService(socket);
        if (instance == null) {
            AltService.getLogger().warn("Received session deletion request from unregistered service.");
            return;
        }

        // Delete the session.
        instance.onDeleteSession(notify.getId());
    }
}
