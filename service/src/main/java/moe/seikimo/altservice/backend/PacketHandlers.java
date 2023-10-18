package moe.seikimo.altservice.backend;

import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.handlers.PacketHandler;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.proto.Service;
import moe.seikimo.altservice.proto.Service.ServiceCmdNotify;
import moe.seikimo.altservice.proto.Service.ServiceIds;

public interface PacketHandlers {
    /**
     * Registers all packet handlers.
     *
     * @param handler The packet handler.
     */
    static void register(PacketHandler<?> handler) {
        handler.register(
                ServiceIds._GetAllSessionsScReq,
                (packet) -> PacketHandlers.onGetAllSessions(),
                null
        );
        handler.register(
                ServiceIds._ServiceCmdNotify,
                (ServiceCmdNotify packet) ->
                        PacketHandlers.onServiceCommand(packet),
                ServiceCmdNotify::parseFrom
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
                ServiceIds._GetAllSessionsCsRsp,
                Service.GetAllSessionsCsRsp.newBuilder()
                        .addAllSessions(players)
        );
    }

    /**
     * Handles a service command.
     *
     * @param packet The packet.
     */
    static void onServiceCommand(ServiceCmdNotify packet) {
        try {
            var target = PlayerManager.getPlayer(packet.getTarget().getId());
            if (target == null) throw new IllegalArgumentException("Target player not found.");

            var message = packet.getPacket();
            target.getHandler().invokeHandler(message.getId(),
                    message.getData().toByteArray());
        } catch (Exception exception) {
            AltBackend.getLogger().warn("Unable to handle service command.", exception);
        }
    }
}
