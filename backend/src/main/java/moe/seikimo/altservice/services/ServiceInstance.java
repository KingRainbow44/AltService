package moe.seikimo.altservice.services;

import lombok.Data;
import moe.seikimo.altservice.client.PanelClient;
import moe.seikimo.altservice.proto.Frontend.FrontendIds;
import moe.seikimo.altservice.proto.Frontend.UpdateSessionsScNotify;
import moe.seikimo.altservice.proto.Structures.Player;
import org.java_websocket.WebSocket;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public final class ServiceInstance {
    private final WebSocket session;
    private final String serverAddress;
    private final short serverPort;

    private final Map<String, Session> sessions
            = new ConcurrentHashMap<>();

    /**
     * @return The server address and port.
     */
    public String getServer() {
        return this.serverAddress + ":" + this.serverPort;
    }

    /**
     * Creates a new session for the player.
     *
     * @param session The player's session.
     */
    public void onCreateSession(Player session) {
        this.sessions.put(session.getId(),
                new Session(this, session.getId(), session));

        // Broadcast to all clients.
        this.updateAllSessions();
    }

    /**
     * Deletes the session for the player.
     *
     * @param sessionId The player's session ID.
     */
    public void onDeleteSession(String sessionId) {
        this.sessions.remove(sessionId);

        // Broadcast to all clients.
        this.updateAllSessions();
    }

    /**
     * Updates all sessions.
     *
     * @param sessions The sessions.
     */
    public void onUpdateSessions(List<Player> sessions) {
        sessions.forEach(session -> {
            var id = session.getId();
            var handle = this.sessions.get(id);

            if (handle == null) {
                this.onCreateSession(session);
            } else {
                handle.updateSession(session);
            }
        });

        this.updateAllSessions(sessions);
    }

    /**
     * Updates all sessions.
     */
    public void updateAllSessions() {
        var sessions = this.getSessions().values()
                .stream().map(Session::getHandle).toList();

        this.updateAllSessions(sessions);
    }

    /**
     * Sends a session update packet.
     */
    public void updateAllSessions(Collection<Player> sessions) {
        PanelClient.broadcast(
                FrontendIds._UpdateSessionsScNotify,
                UpdateSessionsScNotify.newBuilder()
                        .addAllSessions(sessions)
        );
    }
}
