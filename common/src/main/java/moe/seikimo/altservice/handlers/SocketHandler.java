package moe.seikimo.altservice.handlers;

import com.google.protobuf.GeneratedMessageV3;
import org.java_websocket.WebSocket;

@SuppressWarnings("unchecked")
public interface SocketHandler<T extends GeneratedMessageV3> extends BasePacketHandler<T> {
    /**
     * Handles a packet.
     *
     * @param socket The socket.
     * @param packet The packet.
     */
    default void handle(WebSocket socket, Object packet) {
        this.handle(socket, (T) packet);
    }

    /**
     * Handles a packet.
     *
     * @param socket The socket.
     * @param packet The packet.
     */
    void handle(WebSocket socket, T packet);
}
