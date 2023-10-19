package moe.seikimo.altservice.network.handler;

import io.netty.buffer.Unpooled;
import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.player.inventory.Inventory;
import moe.seikimo.altservice.player.server.ServerBlock;
import moe.seikimo.altservice.player.server.ServerEntity;
import moe.seikimo.altservice.player.server.ServerPlayer;
import moe.seikimo.altservice.proto.Frontend.ChatMessageNotify;
import moe.seikimo.altservice.proto.Frontend.FrontendIds;
import moe.seikimo.altservice.script.event.EventType;
import moe.seikimo.altservice.script.event.ScriptArgs;
import moe.seikimo.altservice.utils.objects.Location;
import moe.seikimo.altservice.utils.objects.Style;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleBlockDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
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
        if (message.contains("whispers to you: ")) {
            // Remove color codes from the message.
            if (message.contains("§r§7§o")) {
                message = message.replace("§r§7§o", "");
            }

            var parts = message.split(" ");
            // Get the sender.
            var sender = parts[0];
            // Get the input.
            var input = message.substring(message.indexOf("whispers to you: ") + 17);
            // Invoke the command.
            AltBackend.getPlayerCommands().invoke(this.getPlayer(), sender, input);
        }

        // Send a chat packet to the backend.
        AltBackend.getInstance().forward(
                FrontendIds._ChatMessageNotify,
                ChatMessageNotify.newBuilder()
                        .setMessage(message)
        );

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
            entity.getPassengers().forEach(
                    passenger -> passenger.getLocation().update(packet));
            this.getPlayer().onEntityMove(entity);
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MoveEntityAbsolutePacket packet) {
        if (packet.getRuntimeEntityId() == this.getSession().getData().getRuntimeId()) {
            this.getPlayer().setPosition(packet.getPosition());
            this.getPlayer().setRotation(packet.getRotation());
        }

        var entity = this.getPlayer().getEntityById(packet.getRuntimeEntityId());
        if (entity != null) {
            entity.getLocation().update(packet);
            entity.getPassengers().forEach(
                    passenger -> passenger.getLocation().update(packet));
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
        this.getPlayer().getEntities().put(packet.getRuntimeEntityId(), player);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerListPacket packet) {
        if (packet.getAction() == PlayerListPacket.Action.REMOVE) {
            for (var entry : packet.getEntries()) {
                this.getPlayer().getPeers().remove(entry.getUuid());
                this.getPlayer().getEntities().remove(entry.getEntityId());
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
        var containerId = packet.getContainerId();
        switch (containerId) {
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
                return PacketSignal.HANDLED;
            }
            default -> {
                if (containerId > ContainerId.LAST || containerId < ContainerId.FIRST) {
                    this.getLogger().debug("Received inventory content packet for container {}.", packet.getContainerId());
                } else {
                    var container = this.getPlayer().getInventories().get(containerId);
                    if (container != null) {
                        container.getItems().clear();
                        container.getItems().addAll(packet.getContents());
                    }
                }
            }
        }

        // Update the player.
        this.getPlayer().sendUpdate();

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
            case ContainerId.UI -> { /* Do nothing. This is the UI container. */ }
            default -> this.getLogger().debug("Received inventory slot packet for container {}.", packet.getContainerId());
        }

        // Update the player.
        this.getPlayer().sendUpdate();

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
    public PacketSignal handle(ChangeDimensionPacket packet) {
        // Update the player's position and dimension.
        this.getPlayer().getLocation().update(packet);

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
    public PacketSignal handle(UpdateAttributesPacket packet) {
        var runtimeId = packet.getRuntimeEntityId();
        var attributes = packet.getAttributes();

        // Check if the runtime ID matches the player.
        if (runtimeId == this.getPlayer().getEntityId()) {
            // Update the player's attributes.
            this.getPlayer().updateAttributes(attributes);

            // Update the player.
            this.getPlayer().sendUpdate();
        } else {
            // Update the entity's attributes.
            var entity = this.getPlayer().getEntityById(runtimeId);
            if (entity != null) {
                entity.updateAttributes(attributes);
            }
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerHotbarPacket packet) {
        var inventory = this.getPlayer().getInventory();
        inventory.setHotbarSlot(packet.getSelectedHotbarSlot());

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ContainerOpenPacket packet) {
        var containerId = (int) packet.getId();
        var containerType = packet.getType();

        if (
                containerId > ContainerId.LAST ||
                        containerId < ContainerId.FIRST ||
                        containerType != ContainerType.CONTAINER) {
            return PacketSignal.HANDLED;
        }

        var container = new Inventory(this.getPlayer(),
                containerId, containerType,
                packet.getBlockPosition());
        this.getPlayer().getInventories()
                .put(containerId, container);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ContainerClosePacket packet) {
        var containerId = (int) packet.getId();
        this.getPlayer().getInventories()
                .remove(containerId);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateBlockPacket packet) {
        var blocks = this.getPlayer().getBlocks();
        var position = packet.getBlockPosition();

        // Update blocks on the player.
        var newBlock = ServerBlock.from(position,
                (SimpleBlockDefinition) packet.getDefinition());
        var oldBlock = blocks.put(position, newBlock);
        this.getPlayer().getScriptBackend()
                .invokeEvent(EventType.BLOCK_CHANGE,
                        ScriptArgs.builder()
                                .oldBlock(oldBlock)
                                .block(newBlock)
                                .build()
        );

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityLinkPacket packet) {
        var data = packet.getEntityLink();

        // Resolve the entities from the data.
        var targetEntityId = data.getTo();
        var otherEntityId = data.getFrom();

        var targetEntity = this.getPlayer().getEntityById(targetEntityId);
        var otherEntity = this.getPlayer().getEntityById(otherEntityId);

        if (targetEntity == null || otherEntity == null) return PacketSignal.HANDLED;

        switch (data.getType()) {
            case REMOVE -> {
                targetEntity.setRiding(null);
                otherEntity.getPassengers().remove(targetEntity);

                if (targetEntity.isRelated(this.getPlayer().getTarget())) {
                    this.getPlayer().dismount();
                }
            }
            case RIDER -> {
                targetEntity.setRiding(otherEntity);
                otherEntity.getPassengers().add(targetEntity);

                if (targetEntity.isRelated(this.getPlayer().getTarget())) {
                    this.getPlayer().ride(otherEntity);
                }
            }
            case PASSENGER -> {
                targetEntity.getPassengers().add(otherEntity);
                otherEntity.setRiding(targetEntity);
            }
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelChunkPacket packet) {
        var sectionCount = packet.getSubChunksLength();
        if (sectionCount < -2) return PacketSignal.HANDLED;

        // Determine the chunk.
        var world = this.getPlayer().getWorld();
        var chunkX = packet.getChunkX();
        var chunkZ = packet.getChunkZ();
        var chunk = world.getChunkAt(chunkX, chunkZ);

        // Copy the chunk data into a buffer.
        var chunkData = Unpooled.buffer();
        chunkData.writeBytes(packet.getData());

        // Decode chunk sections.
        chunk.decodeFrom(chunkData, sectionCount);
        this.getLogger().info("Finished decoding chunk ({}, {}) with {} sections.",
                chunkX, chunkZ, chunk.getSections().size());

        // Release the chunk data.
        chunkData.release();

        return PacketSignal.HANDLED;
    }
}
