package moe.seikimo.altservice.handlers;

import com.google.protobuf.GeneratedMessageV3;

@SuppressWarnings("unchecked")
public interface SelfHandler<T extends GeneratedMessageV3> extends BasePacketHandler<T> {
    /**
     * Handles a packet.
     *
     * @param packet The packet.
     */
    default void handle(Object packet) {
        this.handle((T) packet);
    }

    /**
     * Handles a packet.
     *
     * @param packet The packet.
     */
    void handle(T packet);
}
