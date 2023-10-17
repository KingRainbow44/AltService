package moe.seikimo.altservice.client;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.ProtocolMessageEnum;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import lombok.Data;
import moe.seikimo.altservice.AltService;
import moe.seikimo.altservice.MessageReceiver;
import moe.seikimo.altservice.proto.Structures;
import moe.seikimo.altservice.utils.EncodingUtils;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

@Data
public final class PanelClient implements MessageReceiver {
    private static final Map<String, PanelClient> clients
            = new HashMap<>();

    /**
     * Handles a connection.
     *
     * @param ctx The context.
     */
    public static void handle(WsConnectContext ctx) {
        clients.put(ctx.getSessionId(), new PanelClient(ctx.session));
    }

    /**
     * Handles a disconnection.
     *
     * @param ctx The context.
     */
    public static void handle(WsCloseContext ctx) {
        clients.remove(ctx.getSessionId());
    }

    /**
     * Fetches a client.
     *
     * @param ctx The context.
     * @return The client.
     */
    public static PanelClient get(WsMessageContext ctx) {
        return clients.get(ctx.getSessionId());
    }

    /**
     * Broadcasts a packet to all clients.
     *
     * @param id The packet ID.
     * @param builder The packet builder.
     */
    public static void broadcast(Object id, GeneratedMessageV3.Builder<?> builder) {
        clients.values().forEach(client -> client.send(id, builder));
    }

    private final Session session;

    /**
     * @return The client address.
     */
    public String getAddress() {
        return this.getSession().getRemoteAddress().toString();
    }

    /**
     * Sends a packet.
     *
     * @param id The packet ID.
     * @param builder The packet builder.
     */
    public void send(Object id, GeneratedMessageV3.Builder<?> builder) {
        // Create the packet.
        var packet = Structures.Packet.newBuilder()
                .setId(id instanceof ProtocolMessageEnum protoId ?
                        protoId.getNumber() : (int) id);
        if (builder != null) {
            packet.setData(builder.build().toByteString());
        }

        try {
            // Send the encoded packet.
            this.getSession().getRemote().sendString(
                    EncodingUtils.base64Encode(
                            packet.build().toByteArray())
            );
        } catch (Exception exception) {
            AltService.getLogger().warn(
                    "Failed to send packet. {}",
                    exception.getMessage());
        }
    }
}
