package moe.seikimo.altservice.handlers;

import com.google.protobuf.GeneratedMessageV3;
import moe.seikimo.altservice.AltService;
import moe.seikimo.altservice.client.PanelClient;
import moe.seikimo.altservice.proto.Frontend;
import moe.seikimo.altservice.proto.Frontend.Action;
import moe.seikimo.altservice.proto.Frontend.ChatMessageNotify;
import moe.seikimo.altservice.proto.Frontend.FrontendIds;
import moe.seikimo.altservice.proto.Frontend.SessionActionCsNotify;
import moe.seikimo.altservice.services.ServiceManager;

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
        handler.register(
                FrontendIds._SessionActionCsNotify,
                (PanelClient client, SessionActionCsNotify message) ->
                        PanelHandlers.handleSessionAction(client, message),
                SessionActionCsNotify::parseFrom
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

    /**
     * Handles a session action.
     *
     * @param client The client.
     * @param packet The packet.
     */
    static void handleSessionAction(PanelClient client, SessionActionCsNotify packet) {
        if (packet.getAction() != Action.Select) {
            client.getHandle().send(FrontendIds._SessionActionCsNotify, packet);
        } else {
            // Try to find the service.
            var service = ServiceManager.getService(packet.getSessionId());
            if (service == null) return;

            // Try to find the session.
            var session = service.hasSession(packet.getSessionId());
            if (session == null) return;

            // Update the client's handle.
            client.setHandle(session);
        }
    }
}
