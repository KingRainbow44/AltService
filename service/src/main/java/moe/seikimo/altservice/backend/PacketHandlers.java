package moe.seikimo.altservice.backend;

import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.handlers.PacketHandler;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.proto.Service;

public interface PacketHandlers {
    /**
     * Registers all packet handlers.
     *
     * @param handler The packet handler.
     */
    static void register(PacketHandler handler) {
        handler.register(
                Service.ServiceIds._GetAllSessionsScReq,
                (packet) -> PacketHandlers.onGetAllSessions(),
                null
        );
    }

    /**
     * Registers the packet handlers.
     */
    static void onGetAllSessions() {
        var players = PlayerManager.getPlayers().stream()
                .map(Player::toProto)
                .toList();

        var socket = AltBackend.getInstance();
        socket.send(
                Service.ServiceIds._GetAllSessionsCsRsp,
                Service.GetAllSessionsCsRsp.newBuilder()
                        .addAllSessions(players)
        );
    }
}
