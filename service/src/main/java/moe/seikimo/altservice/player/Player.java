package moe.seikimo.altservice.player;

import lombok.Data;
import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.Configuration;
import moe.seikimo.altservice.MessageReceiver;
import moe.seikimo.altservice.handlers.PacketHandler;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.pathing.Node;
import moe.seikimo.altservice.pathing.Pathfinder;
import moe.seikimo.altservice.player.inventory.Inventory;
import moe.seikimo.altservice.player.inventory.PlayerInventory;
import moe.seikimo.altservice.player.server.ServerEntity;
import moe.seikimo.altservice.player.server.ServerPlayer;
import moe.seikimo.altservice.player.server.ServerWorld;
import moe.seikimo.altservice.proto.Frontend.ChatMessageNotify;
import moe.seikimo.altservice.proto.Frontend.FrontendIds;
import moe.seikimo.altservice.proto.Frontend.SessionActionCsNotify;
import moe.seikimo.altservice.proto.Service.ServiceIds;
import moe.seikimo.altservice.proto.Service.UpdateSessionsCsNotify;
import moe.seikimo.altservice.proto.Structures;
import moe.seikimo.altservice.proto.Structures.Attributes;
import moe.seikimo.altservice.script.ScriptManager;
import moe.seikimo.altservice.utils.EncodingUtils;
import moe.seikimo.altservice.utils.ThreadUtils;
import moe.seikimo.altservice.utils.objects.ConnectionDetails;
import moe.seikimo.altservice.utils.objects.Location;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;
import moe.seikimo.altservice.utils.objects.game.Attributable;
import moe.seikimo.altservice.utils.objects.player.SessionData;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryActionData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/** Represents a Minecraft player instance. */
@Data public final class Player implements Attributable, MessageReceiver {
    private final long creationTime
            = System.currentTimeMillis();
    private final PlayerActions actions
            = new PlayerActions();
    private final PacketHandler<?> handler
            = new PacketHandler<>();

    private final Map<UUID, ServerPlayer> peers
            = new HashMap<>();
    private final Map<Long, ServerEntity> entities
            = new HashMap<>();
    private final Map<Integer, ServerWorld> worlds
            = new HashMap<>();
    private final Map<Integer, Inventory> inventories
            = new HashMap<>();
    private final Map<String, AttributeData> attributes
            = new HashMap<>();

    private final PlayerInventory inventory
            = new PlayerInventory(this);
    private final ScriptManager scriptBackend
            = new ScriptManager(this);

    private final String username;
    private final long lifetime;

    private long ticks = 0;
    private PlayerNetworkSession session = null;
    private Location location = Location.ZERO();
    private boolean canAttack = true;

    private Pathfinder pathfinder = null;
    private ServerPlayer target = null;
    private ServerEntity riding = null;

    /**
     * Creates a new player instance.
     *
     * @param username The username of the player.
     * @param lifetime The lifetime of the player.
     */
    public Player(String username, long lifetime) {
        this.username = username;
        this.lifetime = lifetime;

        this.getHandler().register(
                FrontendIds._ChatMessageNotify,
                (ChatMessageNotify packet) ->
                        this.sendMessage(packet.getMessage()),
                ChatMessageNotify::parseFrom
        );
        this.getHandler().register(
                FrontendIds._SessionActionCsNotify,
                (SessionActionCsNotify packet) ->
                        this.onSessionAction(packet),
                SessionActionCsNotify::parseFrom
        );
    }

    /**
     * Attempts to log in to the configured server.
     */
    public void login() {
        if (this.session == null)
            this.session = new PlayerNetworkSession(this);

        // Get the server details.
        var server = Configuration.get().server;
        // Connect to the server.
        this.getSession().connect(new ConnectionDetails(
                server.getAddress(),
                server.getPort(),
                false
        ));

        // Schedule the lifetime.
        if (this.getLifetime() != -1) {
            new Thread(() -> {
                // Wait for the lifetime to expire.
                ThreadUtils.sleep(this.getLifetime() * 1000L);
                // Disconnect the player.
                PlayerManager.destroyPlayer(this);
            }).start();
        }
    }

    /**
     * Attempts to disconnect from the server.
     */
    public void disconnect() {
        if (this.session == null)
            return;

        try {
            this.session.getClient().disconnect("Disconnected");
        } catch (Exception ignored) { }

        this.getSession().getLogger().info("Disconnected from server.");
        this.session = null;
    }

    /**
     * Sends an update packet to the backend.
     */
    public void sendUpdate() {
        AltBackend.getInstance().send(
                ServiceIds._UpdateSessionsCsNotify,
                UpdateSessionsCsNotify.newBuilder()
                        .addSessions(this.toProto())
        );
    }

    /**
     * Sends a packet to the player.
     *
     * @param packet The packet to send.
     */
    public void sendPacket(BedrockPacket packet) {
        if (this.getSession() == null) return;
        this.getSession().sendPacket(packet);
    }

    /**
     * @return The session data.
     */
    public SessionData getData() {
        return this.getSession() == null ? null :
                this.getSession().getData();
    }

    /**
     * @return The player's entity ID.
     */
    public long getEntityId() {
        return this.getData() == null ? -1 :
                this.getData().getRuntimeId();
    }

    /**
     * @return The player's position.
     */
    public Vector3f getPosition() {
        return this.getLocation().getPosition();
    }

    /**
     * @return The player's rotation.
     */
    public Vector3f getRotation() {
        return this.getLocation().getRotation();
    }

    /**
     * @return The client's current world.
     */
    public ServerWorld getWorld() {
        var dimension = this.getLocation().getDimension();
        return this.getWorlds().computeIfAbsent(
                dimension, k -> new ServerWorld(dimension));
    }

    /**
     * Sets the player's position.
     *
     * @param position The new position.
     */
    public void setPosition(Vector3f position) {
        if (this.getSession() == null) return;
        this.getLocation().setPosition(position);

        this.sendUpdate();
    }

    /**
     * Sets the player's rotation.
     *
     * @param rotation The new rotation.
     */
    public void setRotation(Vector3f rotation) {
        if (this.getSession() == null) return;
        this.getLocation().setRotation(rotation);

        this.sendUpdate();
    }

    /**
     * Fetches a player by their username.
     * This can return null if the player is not found.
     *
     * @param username The username to search for.
     * @return The player, or null if not found.
     */
    @Nullable
    public ServerPlayer getPeerByUsername(String username) {
        return this.peers.values().stream()
                .filter(peer -> peer.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    /**
     * Fetches a player by their entity ID.
     * This can return null if the player is not found.
     *
     * @param runtimeId The entity ID to search for.
     * @return The player, or null if not found.
     */
    @Nullable
    public ServerPlayer getPeerById(long runtimeId) {
        return this.peers.values().stream()
                .filter(peer -> peer.getRuntimeId() == runtimeId)
                .findFirst().orElse(null);
    }

    /**
     * Fetches an entity by their UUID.
     * This can return null if the entity is not found.
     *
     * @param runtimeId The entity ID to search for.
     * @return The entity, or null if not found.
     */
    @Nullable
    public ServerEntity getEntityById(long runtimeId) {
        return this.entities.get(runtimeId);
    }

    /**
     * Invoked when an entity moves.
     *
     * @param entity The entity that moved.
     */
    public void onEntityMove(ServerEntity entity) {
        var actions = this.getActions();
        if (actions.isGuard()) {
            var selfPos = this.getPosition();
            var entityPos = entity.getPosition();

            // Check if the entity is within range.
            if (entityPos.distance(selfPos) < 6) {
                if (
                        (actions.isGuardPlayers() && actions.isGuardMobs()) ||
                                (actions.isGuardPlayers() && entity instanceof ServerPlayer) ||
                                (actions.isGuardMobs() && !(entity instanceof ServerPlayer))
                ) {
                    this.attack(entity);
                }
            }
        }

        if (actions.isFollow()) {
            var target = this.getTarget();
            if (target == null) return;

            // Check if the entity is the target.
            if (!target.isRelated(entity)) return;

            // Move to the entity.
            this.move(target.getPosition(), target.getRotation());
        }
    }

    /**
     * Handles a session action packet.
     *
     * @param packet The packet.
     */
    public void onSessionAction(SessionActionCsNotify packet) {
        switch (packet.getAction()) {
            case Disconnect -> this.getSession().onDisconnect("Panel request.");
            case Reconnect -> {
                if (this.getSession() == null) return;

                var sessionData = this.getData();
                assert sessionData != null;

                // Toggle auto-reconnect.
                sessionData.setReconnect(!sessionData.isReconnect());
            }
        }
    }

    /**
     * Pathfinds to the position.
     *
     * @param position The position to pathfind to.
     * @param callback The callback to invoke when the pathfinder finishes.
     */
    public void pathfindTo(Vector3f position, Consumer<List<Node>> callback) {
        if (this.getSession() == null) return;
        if (this.getPathfinder() != null) return;

        // Create the pathfinder.
        var pathfinder = new Pathfinder(this, position.toInt(), callback);
        // Start the pathfinder.
        pathfinder.start();

        // Set the pathfinder.
        this.setPathfinder(pathfinder);
    }

    /**
     * Respawns the player.
     */
    @SuppressWarnings("DataFlowIssue")
    public void respawn() {
        // Check if the player is connected.
        if (this.getSession() == null) return;

        // Prepare the respawn packet.
        var respawnPacket = new RespawnPacket();
        respawnPacket.setPosition(this.getLocation().getPosition());
        respawnPacket.setRuntimeEntityId(this.getData().getRuntimeId());
        respawnPacket.setState(RespawnPacket.State.CLIENT_READY);

        // Send the packet.
        this.sendPacket(respawnPacket);
    }

    /**
     * Sends a message as the player.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        // Check if the player is connected.
        if (this.getSession() == null) return;

        // Replace '&' with '§'.
        message = message.replace('&', '§');

        // Prepare the text packet.
        var textPacket = new TextPacket();
        textPacket.setType(TextPacket.Type.CHAT);
        textPacket.setNeedsTranslation(false);
        textPacket.setSourceName(this.getUsername());
        textPacket.setMessage(message);
        textPacket.setXuid("");
        textPacket.setPlatformChatId("");

        // Send the packet.
        this.sendPacket(textPacket);
    }

    /**
     * @return The inventory the player is looking at.
     */
    public Inventory getViewingInventory() {
        var keys = this.getInventories().keySet();
        return keys.isEmpty() ? null :
                this.getInventories().get(keys.iterator().next());
    }

    /**
     * Moves the player to the specified position and rotation.
     *
     * @param position The position to send to the server.
     * @param rotation The rotation to send to the server.
     */
    public void move(Vector3f position, Vector3f rotation) {
        // Check if the player is connected.
        if (this.getSession() == null) return;

        if (this.getSession().getData().isServerMovement()) {
            // Prepare the server move packet.
            var movePacket = new PlayerAuthInputPacket();
            movePacket.setPosition(position);
            movePacket.setRotation(rotation);
            movePacket.setMotion(Vector2f.ZERO);
            movePacket.getInputData().add(
                    PlayerAuthInputData.JUMPING
            );
            movePacket.setInputMode(InputMode.MOUSE);
            movePacket.setPlayMode(ClientPlayMode.NORMAL);
            movePacket.setTick(0);
            movePacket.setDelta(Vector3f.ZERO);

            // Send the packet.
            this.sendPacket(movePacket);
        } else {
            // Prepare the move packet.
            var movePacket = new MovePlayerPacket();
            movePacket.setRuntimeEntityId(this.getEntityId());
            movePacket.setPosition(position);
            movePacket.setRotation(rotation);
            movePacket.setMode(MovePlayerPacket.Mode.NORMAL);
            movePacket.setOnGround(true);
            movePacket.setRidingRuntimeEntityId(0);
            movePacket.setTick(0);

            // Send the packet.
            this.sendPacket(movePacket);
        }

        // Update the client's position.
        this.setPosition(position);
        this.setRotation(rotation);
    }

    /**
     * Tries to ride the specified entity.
     *
     * @param target The entity to ride.
     */
    public void ride(ServerEntity target) {
        if (this.getSession() == null) return;

        // Prepare the interact packet.
        var interactPacket = new InteractPacket();
        interactPacket.setRuntimeEntityId(target.getRuntimeId());
        interactPacket.setAction(InteractPacket.Action.INTERACT);
        interactPacket.setMousePosition(Vector3f.ZERO);

        // Set the riding entity.
        this.setRiding(target);
        // Send the packet.
        this.sendPacket(interactPacket);
    }

    /**
     * Dismounts the player from the entity they are riding.
     */
    public void dismount() {
        if (this.getSession() == null) return;
        if (this.getRiding() == null) return;

        // Prepare the interact packet.
        var interactPacket = new InteractPacket();
        interactPacket.setRuntimeEntityId(this.getRiding().getRuntimeId());
        interactPacket.setAction(InteractPacket.Action.LEAVE_VEHICLE);
        interactPacket.setMousePosition(Vector3f.ZERO);

        // Remove the riding entity.
        this.setRiding(null);
        // Send the packet.
        this.sendPacket(interactPacket);
    }

    /**
     * Attacks the specified player.
     */
    public void attack(ServerEntity target) {
        if (this.getSession() == null) return;

        // Check if the target is invulnerable.
        if (GameConstants.INVULNERABLE_ENTITIES
                .contains(target.getIdentifier()))
            return;

        // Create the inventory action.
        var action = new InventoryActionData(
                InventorySource.fromGlobalInventory(), 0,
                ItemData.AIR, ItemData.AIR
        );

        {
            // Prepare the transaction packet.
            var invPacket = new InventoryTransactionPacket();
            invPacket.getActions().add(action);
            invPacket.setLegacyRequestId(0);
            invPacket.setHotbarSlot(0);
            invPacket.setItemInHand(ItemData.AIR);
            invPacket.setRuntimeEntityId(target.getRuntimeId());
            invPacket.setTransactionType(InventoryTransactionType.ITEM_USE_ON_ENTITY);
            invPacket.setBlockPosition(Vector3i.ZERO);
            invPacket.setBlockFace(0);
            invPacket.setPlayerPosition(this.getPosition());
            invPacket.setClickPosition(target.getLocation().getPosition());
            invPacket.setHeadPosition(target.getLocation().getPosition());
            invPacket.setActionType(1); // 1 = attack

            this.sendPacket(invPacket);
        }

        {
            // Prepare the arm swing packet.
            var armPacket = new AnimatePacket();
            armPacket.setRuntimeEntityId(this.getEntityId());
            armPacket.setAction(AnimatePacket.Action.SWING_ARM);
            armPacket.setRowingTime(0);

            this.sendPacket(armPacket);
        }

        this.setCanAttack(false); // Wait for the cooldown to expire.
    }

    /**
     * Breaks the block at the specified position.
     *
     * @param block The position of the block to break.
     */
    public void _break(Vector3i block) {
        if (this.getSession() == null) return;

        {
            // Create the start action packet.
            var startPacket = new PlayerActionPacket();
            startPacket.setRuntimeEntityId(this.getEntityId());
            startPacket.setAction(PlayerActionType.START_BREAK);
            startPacket.setBlockPosition(block);
            startPacket.setResultPosition(block);
            startPacket.setFace(0); // 0 = down

            this.sendPacket(startPacket);
        }

        {
            // Create the inventory action.
            var action = new InventoryActionData(
                    InventorySource.fromGlobalInventory(), 0,
                    ItemData.AIR, ItemData.AIR
            );

            // Prepare the transaction packet.
            var invPacket = new InventoryTransactionPacket();
            invPacket.getActions().add(action);
            invPacket.setLegacyRequestId(0);
            invPacket.setHotbarSlot(0);
            invPacket.setItemInHand(ItemData.AIR);
            invPacket.setRuntimeEntityId(this.getEntityId());
            invPacket.setTransactionType(InventoryTransactionType.ITEM_USE);
            invPacket.setBlockDefinition(GameConstants.AIR_BLOCK);
            invPacket.setBlockPosition(block);
            invPacket.setBlockFace(0); // 0 = down
            invPacket.setPlayerPosition(this.getPosition());
            invPacket.setClickPosition(block.toFloat());
            invPacket.setHeadPosition(Vector3f.ZERO);
            invPacket.setActionType(2); // 2 = break block

            this.sendPacket(invPacket);
        }
    }

    /**
     * Interacts with the block at the specified position.
     * This can also be used to place blocks.
     *
     * @param block The position of the block to interact with.
     */
    public void interact(Vector3i block) {
        this.place(this.getInventory().getItemInMainHand(), block);
    }

    /**
     * Places a block at the specified position.
     * This can also be used to interact with blocks.
     *
     * @param item The item to place.
     * @param block The position of the block to place.
     */
    public void place(ItemData item, Vector3i block) {
        if (this.getSession() == null) return;
        if (item == null) {
            item = this.getInventory().getItemInMainHand();
        }

        // Create the inventory action.
        var action = new InventoryActionData(
                InventorySource.fromGlobalInventory(), 0,
                item, item
        );

        // Prepare the transaction packet.
        var invPacket = new InventoryTransactionPacket();
        invPacket.getActions().add(action);
        invPacket.setLegacyRequestId(0);
        invPacket.setHotbarSlot(0);
        invPacket.setItemInHand(item);
        invPacket.setRuntimeEntityId(this.getEntityId());
        invPacket.setTransactionType(InventoryTransactionType.ITEM_USE);
        invPacket.setBlockDefinition(GameConstants.AIR_BLOCK);
        invPacket.setBlockPosition(block);
        invPacket.setBlockFace(0); // 0 = down
        invPacket.setPlayerPosition(this.getPosition()
                .sub(0, GameConstants.OFFSET, 0));
        invPacket.setClickPosition(Vector3f.ZERO);
        invPacket.setHeadPosition(Vector3f.ZERO);
        invPacket.setActionType(0); // 0 = place block

        this.sendPacket(invPacket);
    }

    /**
     * Called every "client" tick.
     * This is used to fake a "client" tick for the server.
     */
    public void tick() {
        if (this.getSession() == null) return;
        if (!this.getSession().getData().isLoggedIn()) return;

        this.ticks++;

        if (this.getTarget() != null) {
            if (this.getActions().isAttack()) {
                if (this.isCanAttack())
                    this.attack(this.getTarget());
            }
        }

        if (this.ticks % 20 == 0)
            this.getScriptBackend().tickBehaviors();
    }

    /**
     * @return The player serialized as a protobuf.
     */
    public Structures.Player toProto() {
        return Structures.Player.newBuilder()
                .setId(this.getUsername())
                .setName(this.getUsername())
                .setPosition(EncodingUtils.convert(this.getPosition()))
                .setRotation(EncodingUtils.convert(this.getRotation()))
                .setAttributes(Attributes.newBuilder()
                        .setHealth(this.getAttributeValue("minecraft:health", 20f))
                        .setHunger(this.getAttributeValue("minecraft:player.hunger", 20f))
                        .setXpLevel(this.getAttributeValue("minecraft:player.level", 0f))
                        .setXpProgress(Math.round(this.getAttributeValue(
                                "minecraft:player.experience", 0f) * 100))
                        .setArmor(this.getInventory().computeArmor()))
                .setInventory(this.getInventory().toProto())
                .build();
    }

    @Override
    public String toString() {
        return this.getSession().getData().toString();
    }
}
