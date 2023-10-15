package moe.seikimo.altservice.services;

import lombok.Data;
import moe.seikimo.altservice.proto.Structures.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public final class ServiceInstance {
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
                new Session(session.getId(), session));
    }

    /**
     * Deletes the session for the player.
     *
     * @param sessionId The player's session ID.
     */
    public void onDeleteSession(String sessionId) {
        this.sessions.remove(sessionId);
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
    }
}
