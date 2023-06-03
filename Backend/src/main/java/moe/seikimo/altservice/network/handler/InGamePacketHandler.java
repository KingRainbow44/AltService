package moe.seikimo.altservice.network.handler;

import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.utils.objects.Style;
import org.cloudburstmc.protocol.bedrock.packet.DeathInfoPacket;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

public class InGamePacketHandler extends AbstractPacketHandler {
    public InGamePacketHandler(PlayerNetworkSession session) {
        super(session);
    }

    @Override
    public PacketSignal handle(DeathInfoPacket packet) {
        var position = this.getPlayer().getLocation().getPosition();

        // Log the death.
        this.getLogger().info("Died at {} because {}.",
                position, Style.replaceTerminal(packet.getCauseAttackName()));

        // Try to respawn the player.
        this.getPlayer().respawn();

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TextPacket packet) {
        var message = packet.getMessage();

        // Log the message.
        this.getLogger().info(Style.replaceTerminal(message));

        // Check if the message is a command.
        if (message.contains("<") && message.contains(">")) {
            var parsed = message.substring(
                    message.indexOf(">") + 1
            ).trim();

            if (parsed.startsWith(this.getPlayer().getUsername())) {
                AltBackend.getPlayerCommands().invoke(this.getPlayer(), parsed.replaceFirst(
                        this.getPlayer().getUsername(), ""
                ).trim());
            } else if (parsed.startsWith(",")) {
                AltBackend.getPlayerCommands().invoke(parsed.replaceFirst(
                        ",", ""
                ).trim());
            }
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MovePlayerPacket packet) {
        if (packet.getRuntimeEntityId() == this.getPlayer().getEntityId())
            this.getPlayer().setPosition(packet.getPosition());

        return PacketSignal.HANDLED;
    }
}
