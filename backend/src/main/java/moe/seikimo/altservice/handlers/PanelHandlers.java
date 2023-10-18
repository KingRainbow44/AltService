package moe.seikimo.altservice.handlers;

import com.google.protobuf.GeneratedMessageV3;
import moe.seikimo.altservice.AltService;
import moe.seikimo.altservice.client.PanelClient;
import moe.seikimo.altservice.proto.Frontend.ChatMessageNotify;
import moe.seikimo.altservice.proto.Frontend.FrontendIds;

public interface PanelHandlers {
    /**
     * Registers all panel handlers.
     *
     * @param handler The packet handler.
     */
    static void register(PacketHandler<PanelClient> handler) {
        handler.register(
                FrontendIds._FrontendJoinCsReq,
                (PanelClient client, GeneratedMessageV3 message) ->
                        PanelHandlers.handleJoin(client),
                null
        );
        handler.register(
                FrontendIds._ChatMessageNotify,
                (PanelClient client, ChatMessageNotify message) ->
                        PanelHandlers.handleChatMessage(client, message),
                ChatMessageNotify::parseFrom
        );
    }

    /**
     * Handles a join request.
     *
     * @param client The client.
     */
    static void handleJoin(PanelClient client) {
        client.send(FrontendIds._FrontendJoinScRsp, null);
        AltService.getLogger().info("Client {} connected.", client.getAddress());
    }

    /**
     * Handles a chat message.
     *
     * @param client The client.
     * @param packet The packet.
     */
    static void handleChatMessage(PanelClient client, ChatMessageNotify packet) {
        client.getHandle().send(FrontendIds._ChatMessageNotify, packet);
    }
}
