package moe.seikimo.altservice.network.handler;

import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.player.server.ServerEntity;
import moe.seikimo.altservice.player.server.ServerPlayer;
import moe.seikimo.altservice.utils.objects.Location;
import moe.seikimo.altservice.utils.objects.Style;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponse;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

public class InGamePacketHandler extends DisconnectablePacketHandler {
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
                var command = parsed.replaceFirst(",", "").trim();
                AltBackend.getPlayerCommands().invoke(sender, command);
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
        } else {
            var peer = this.getPlayer().getPeerById(packet.getRuntimeEntityId());
            if (peer != null) {
                var peerLocation = peer.getLocation();
                peerLocation.setPosition(packet.getPosition());
                peerLocation.setRotation(packet.getRotation());

                this.getPlayer().onEntityMove(peer);
            }
        }

        var target = player.getTarget();
        if (target != null && packet.getRuntimeEntityId() == target.getRuntimeId()) {
            if (player.getActions().isFollow()) {
                // Move the player to the target.
                data.setGrounded(packet.isOnGround());
                player.move(packet.getPosition(), packet.getRotation());
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

        var entity = this.getPlayer().getEntityById(packet.getRuntimeEntityId());
        if (entity != null) {
            entity.getLocation().update(packet);
            this.getPlayer().onEntityMove(entity);
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

        var entity = this.getPlayer().getEntityById(packet.getRuntimeEntityId());
        if (entity != null) {
            entity.setPosition(packet.getPosition());
            entity.setRotation(packet.getRotation());
            entity.getLocation().setGrounded(packet.isOnGround());
            this.getPlayer().onEntityMove(entity);
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddPlayerPacket packet) {
        // Add the player to the list of players.
        var player = new ServerPlayer(
                packet.getRuntimeEntityId(), packet.getUsername(), packet.getUuid()
        );
        player.setLocation(new Location(0, packet.getPosition(), packet.getRotation(), true));
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

    @Override
    public PacketSignal handle(SetTitlePacket packet) {
        if (packet.getType() == SetTitlePacket.Type.SUBTITLE) {
            // Parse the text.
            var text = packet.getText().substring(2);
            this.getPlayer().setCanAttack(text.endsWith("§7"));
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventoryContentPacket packet) {
        var inventory = this.getPlayer().getInventory();
        switch (packet.getContainerId()) {
            case ContainerId.INVENTORY -> {
                // Update the player's inventory.
                inventory.getItems().clear();
                inventory.getItems().addAll(packet.getContents());
            }
            case ContainerId.OFFHAND -> // Update the player's offhand.
                    inventory.setOffhand(packet.getContents().get(0));
            case ContainerId.ARMOR -> {
                // Update the player's armor.
                inventory.getArmor().clear();
                inventory.getArmor().addAll(packet.getContents());
            }
            case ContainerId.UI -> {
                // Do nothing. This is the UI container.
            }
            default -> this.getLogger().debug("Received inventory content packet for container {}.", packet.getContainerId());
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventorySlotPacket packet) {
        var inventory = this.getPlayer().getInventory();
        switch (packet.getContainerId()) {
            case ContainerId.INVENTORY -> // Update the player's inventory.
                    inventory.getItems().set(packet.getSlot(), packet.getItem());
            case ContainerId.OFFHAND -> // Update the player's offhand.
                    inventory.setOffhand(packet.getItem());
            case ContainerId.ARMOR -> // Update the player's armor.
                    inventory.getArmor().set(packet.getSlot(), packet.getItem());
            default -> this.getLogger().debug("Received inventory content packet for container {}.", packet.getContainerId());
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RespawnPacket packet) {
        var position = packet.getPosition();
        var state = packet.getState();

        // Log the respawn.
        this.getLogger().debug("Respawning at {}. (state: {})",
                position.toString(), state);

        // Check if the server is waiting for the client.
        if (state == RespawnPacket.State.SERVER_READY) {
            this.getPlayer().setPosition(position);
            this.getPlayer().respawn();
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddEntityPacket packet) {
        this.getPlayer().getEntities().put(
                packet.getRuntimeEntityId(),
                ServerEntity.from(packet)
        );

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RemoveEntityPacket packet) {
        this.getPlayer().getEntities()
                .remove(packet.getUniqueEntityId());

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityDataPacket packet) {
        var entity = this.getPlayer().getEntityById(packet.getRuntimeEntityId());
        if (entity != null) entity.setProperties(packet.getProperties());

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerHotbarPacket packet) {
        var inventory = this.getPlayer().getInventory();
        inventory.setHotbarSlot(packet.getSelectedHotbarSlot());

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddItemEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateBlockPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MobArmorEquipmentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MobEquipmentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ItemStackResponsePacket packet) {
        return PacketSignal.HANDLED;
    }
}
