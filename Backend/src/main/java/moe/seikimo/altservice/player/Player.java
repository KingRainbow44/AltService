package moe.seikimo.altservice.player;

import lombok.Data;
import moe.seikimo.altservice.Configuration;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.utils.ThreadUtils;
import moe.seikimo.altservice.utils.objects.ConnectionDetails;
import moe.seikimo.altservice.utils.objects.Location;
import moe.seikimo.altservice.utils.objects.player.SessionData;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.RespawnPacket;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;

/** Represents a Minecraft player instance. */
@Data public final class Player {
    private final long creationTime
            = System.currentTimeMillis();

    private final String username;
    private final long lifetime;

    private PlayerNetworkSession session = null;
    private Location location = Location.ZERO;

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

        this.session.getClient().disconnect("Disconnected");
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
     * Sets the player's position.
     *
     * @param position The new position.
     */
    public void setPosition(Vector3f position) {
        if (this.getSession() == null) return;
        this.getLocation().setPosition(position);
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
}
