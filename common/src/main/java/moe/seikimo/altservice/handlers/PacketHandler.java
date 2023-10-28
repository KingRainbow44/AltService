package moe.seikimo.altservice.handlers;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.ProtocolMessageEnum;
import moe.seikimo.altservice.MessageReceiver;
import moe.seikimo.altservice.handlers.BasePacketHandler.Function;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;

public final class PacketHandler<T extends MessageReceiver> {
    private final Map<Integer, BasePacketHandler<?>> handlers = new HashMap<>();
    private final Map<Integer, Function<byte[], GeneratedMessageV3>> decoders = new HashMap<>();

    /**
     * Registers a packet handler.
     *
     * @param id The handler ID.
     * @param handler The handler.
     */
    public void register(
            Object id, SelfHandler<?> handler,
            Function<byte[], GeneratedMessageV3> decoder
    ) {
        if (id instanceof Integer intId) {
            this.register(intId, handler, decoder);
        } else if (id instanceof ProtocolMessageEnum enumId) {
            this.register(enumId.getNumber(), handler, decoder);
        }
    }

    /**
     * Gets a handler.
     *
     * @param id The handler ID.
     * @param handler The handler.
     */
    public void register(
            Object id, SocketHandler<?> handler,
            Function<byte[], GeneratedMessageV3> decoder
    ) {
        if (id instanceof Integer intId) {
            this.register(intId, handler, decoder);
        } else if (id instanceof ProtocolMessageEnum enumId) {
            this.register(enumId.getNumber(), handler, decoder);
        }
    }

    /**
     * Gets a handler.
     *
     * @param id The handler ID.
     * @param handler The handler.
     */
    public void register(
            Object id, ObjectHandler<T, ?> handler,
            Function<byte[], GeneratedMessageV3> decoder
    ) {
        if (id instanceof Integer intId) {
            this.register(intId, handler, decoder);
        } else if (id instanceof ProtocolMessageEnum enumId) {
            this.register(enumId.getNumber(), handler, decoder);
        }
    }

    /**
     * Registers a packet handler.
     *
     * @param id The handler ID.
     * @param handler The handler.
     */
    private void register(
            int id, BasePacketHandler<?> handler,
            Function<byte[], GeneratedMessageV3> decoder
    ) {
        this.handlers.put(id, handler);
        this.decoders.put(id, decoder);
    }

    /**
     * Invokes a handler.
     *
     * @param id The handler ID.
     * @param packet The packet.
     */
    public void invokeHandler(int id, byte[] packet)
            throws Exception {
        var handler = this.handlers.get(id);
        if (handler == null) return;

        var decoder = this.decoders.get(id);
        if (handler instanceof SocketHandler<?>) {
            throw new IllegalArgumentException("SocketHandler is not supported");
        } else if (handler instanceof SelfHandler<?> selfHandler) {
            selfHandler.handle(
                    decoder == null ? null : decoder.apply(packet));
        } else if (handler instanceof ObjectHandler<?, ?>) {
            throw new IllegalArgumentException("ObjectHandler is not supported");
        }
    }

    /**
     * Invokes a handler.
     *
     * @param socket The socket.
     * @param id The handler ID.
     * @param packet The packet.
     */
    public void invokeHandler(WebSocket socket, int id, byte[] packet)
            throws Exception {
        var handler = this.handlers.get(id);
        if (handler == null) return;

        var decoder = this.decoders.get(id);
        if (handler instanceof SocketHandler<?> socketHandler) {
            socketHandler.handle(socket,
                    decoder == null ? null : decoder.apply(packet));
        } else if (handler instanceof SelfHandler<?>) {
            throw new IllegalArgumentException("SelfHandler is not supported");
        } else if (handler instanceof ObjectHandler<?, ?>) {
            throw new IllegalArgumentException("ObjectHandler is not supported");
        }
    }

    /**
     * Invokes a handler.
     *
     * @param object The object.
     * @param id The handler ID.
     * @param packet The packet.
     */
    public void invokeHandler(T object, int id, byte[] packet)
            throws Exception {
        var handler = this.handlers.get(id);
        if (handler == null) return;

        var decoder = this.decoders.get(id);
        if (handler instanceof ObjectHandler<?, ?> objectHandler) {
            objectHandler.handle(object,
                    decoder == null ? null : decoder.apply(packet));
        } else if (handler instanceof SelfHandler<?>) {
            throw new IllegalArgumentException("SelfHandler is not supported");
        } else if (handler instanceof SocketHandler<?>) {
            throw new IllegalArgumentException("SocketHandler is not supported");
        }
    }
}
