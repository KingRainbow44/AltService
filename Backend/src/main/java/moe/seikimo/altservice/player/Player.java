package moe.seikimo.altservice.player;

import lombok.Data;
import moe.seikimo.altservice.Configuration;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.player.server.ServerEntity;
import moe.seikimo.altservice.player.server.ServerPlayer;
import moe.seikimo.altservice.utils.ThreadUtils;
import moe.seikimo.altservice.utils.objects.ConnectionDetails;
import moe.seikimo.altservice.utils.objects.Location;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;
import moe.seikimo.altservice.utils.objects.player.SessionData;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.ClientPlayMode;
import org.cloudburstmc.protocol.bedrock.data.InputMode;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryActionData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Represents a Minecraft player instance. */
@Data public final class Player {
    private final long creationTime
            = System.currentTimeMillis();
    private final PlayerActions actions
            = new PlayerActions();

    private final Map<UUID, ServerPlayer> peers
            = new HashMap<>();
    private final Map<Long, ServerEntity> entities
            = new HashMap<>();
    private final PlayerInventory inventory
            = new PlayerInventory();

    private final String username;
    private final long lifetime;

    private long ticks = 0;
    private PlayerNetworkSession session = null;
    private Location location = Location.ZERO();
    private boolean canAttack = true;

    private ServerPlayer target = null;

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
     * Sets the player's position.
     *
     * @param position The new position.
     */
    public void setPosition(Vector3f position) {
        if (this.getSession() == null) return;
        this.getLocation().setPosition(position);
    }

    /**
     * Sets the player's rotation.
     *
     * @param rotation The new rotation.
     */
    public void setRotation(Vector3f rotation) {
        if (this.getSession() == null) return;
        this.getLocation().setRotation(rotation);
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
     * Attacks the specified player.
     */
    public void attack(ServerEntity target) {
        if (this.getSession() == null) return;

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
     * Places a block at the specified position.
     *
     * @param block The position of the block to place.
     */
    public void place(Vector3i block) {
        if (this.getSession() == null) return;

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
            this.getActions().tickBehaviors();
    }

    @Override
    public String toString() {
        return this.getSession().getData().toString();
    }
}
