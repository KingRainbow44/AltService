package moe.seikimo.altservice.network.handler;

import moe.seikimo.altservice.network.PlayerNetworkSession;
import org.cloudburstmc.protocol.bedrock.packet.DisconnectPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

public class DisconnectablePacketHandler extends AbstractPacketHandler {

    public DisconnectablePacketHandler(PlayerNetworkSession session) {
        super(session);
    }

    @Override
    public PacketSignal handle(DisconnectPacket packet) {
        super.handle(packet);
        // Disconnect the player.
        this.session.onDisconnect(packet.isMessageSkipped() ?
                "No reason provided." : packet.getKickMessage());

        return PacketSignal.HANDLED;
    }
}
