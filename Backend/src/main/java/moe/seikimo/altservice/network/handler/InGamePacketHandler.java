package moe.seikimo.altservice.network.handler;

import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.player.server.ServerPlayer;
import moe.seikimo.altservice.utils.objects.Location;
import moe.seikimo.altservice.utils.objects.Style;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

public class InGamePacketHandler extends DisconnectablePacketHandler {
    public InGamePacketHandler(PlayerNetworkSession session) {
        super(session);
    }

    @Override
    public PacketSignal handle(DeathInfoPacket packet) {
        super.handle(packet);
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
        super.handle(packet);
        var message = packet.getMessage();

        // Log the message.
        this.getLogger().info(Style.replaceTerminal(message));

        // Check if the message is a command.
        if (message.contains("<") && message.contains(">")) {
            // Get the target and sender.
            var username = this.getPlayer().getUsername();
            var sender = message.substring(
                    message.indexOf("<") + 1,
                    message.indexOf(">")
            ).trim();

            // Parse the command's label and arguments.
            var parsed = message.substring(
                    message.indexOf(">") + 1
            ).trim();

            if (parsed.startsWith(username)) {
                var command = parsed.replaceFirst(username, "").trim();
                AltBackend.getPlayerCommands().invoke(this.getPlayer(), sender, command);
            } else if (parsed.startsWith(",")) {
                AltBackend.getPlayerCommands().invoke(
                        parsed.replaceFirst(",", "").trim());
            }
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MovePlayerPacket packet) {
        var player = this.getPlayer();
        var data = this.getSession().getData();

        if (packet.getRuntimeEntityId() == player.getEntityId()) {
            data.setGrounded(packet.isOnGround());
            player.move(packet.getPosition(), packet.getRotation());
        }

        var target = player.getTarget();
        if (target != null && packet.getRuntimeEntityId() == target.getRuntimeId()) {
            switch (player.getTargetAction()) {
                case FOLLOW -> {
                    // Move the player to the target.
                    data.setGrounded(packet.isOnGround());
                    player.move(packet.getPosition(), packet.getRotation());
                }
            }
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CorrectPlayerMovePredictionPacket packet) {
        System.out.println(packet.toString());
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MoveEntityDeltaPacket packet) {
        if (packet.getRuntimeEntityId() == this.getSession().getData().getRuntimeId()) {
            this.getPlayer().getPosition().add(
                    packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ());
            System.out.println("Server requested to update player to " + this.getPlayer().getPosition());
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MoveEntityAbsolutePacket packet) {
        if (packet.getRuntimeEntityId() == this.getSession().getData().getRuntimeId()) {
            this.getPlayer().setPosition(packet.getPosition());
            this.getPlayer().setRotation(packet.getRotation());
            System.out.println("Server requested to update player to " + this.getPlayer().getPosition());
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddPlayerPacket packet) {
        // Add the player to the list of players.
        var player = new ServerPlayer(
                packet.getRuntimeEntityId(), packet.getUsername(), packet.getUuid()
        );
        player.setLocation(new Location(0, packet.getPosition(), packet.getRotation()));
        this.getPlayer().getPeers().put(packet.getUuid(), player);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerListPacket packet) {
        if (packet.getAction() == PlayerListPacket.Action.REMOVE) {
            for (var entry : packet.getEntries()) {
                this.getPlayer().getPeers().remove(entry.getUuid());
            }
        }

        return PacketSignal.HANDLED;
    }
}
