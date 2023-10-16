package moe.seikimo.altservice.handlers;

import com.google.protobuf.GeneratedMessageV3;
import moe.seikimo.altservice.AltService;
import moe.seikimo.altservice.client.PanelClient;
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
}
