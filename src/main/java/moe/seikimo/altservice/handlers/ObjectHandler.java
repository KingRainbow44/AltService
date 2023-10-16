package moe.seikimo.altservice.handlers;

import com.google.protobuf.GeneratedMessageV3;
import moe.seikimo.altservice.MessageReceiver;

@SuppressWarnings("unchecked")
public interface ObjectHandler<R extends MessageReceiver, T extends GeneratedMessageV3> extends BasePacketHandler<T> {
    /**
     * Handles a packet.
     *
     * @param receiver The receiver.
     * @param packet The packet.
     */
    default void handle(Object receiver, Object packet) {
        this.handle((R) receiver, (T) packet);
    }

    /**
     * Handles a packet.
     *
     * @param socket The socket.
     * @param packet The packet.
     */
    void handle(R socket, T packet);
}
