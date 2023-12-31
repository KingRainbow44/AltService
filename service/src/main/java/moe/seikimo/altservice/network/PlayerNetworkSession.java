package moe.seikimo.altservice.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Promise;
import lombok.Data;
import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.network.handler.LoginPacketHandler;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.utils.LoggerUtils;
import moe.seikimo.altservice.utils.ProfileUtils;
import moe.seikimo.altservice.utils.objects.ConnectionDetails;
import moe.seikimo.altservice.utils.objects.absolute.NetworkConstants;
import moe.seikimo.altservice.utils.objects.auth.Authentication;
import moe.seikimo.altservice.utils.objects.player.SessionData;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockClientSession;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockClientInitializer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public final class PlayerNetworkSession {
    private final Player player;

    private final SessionData data
            = new SessionData();
    private final Logger logger;

    private ConnectionDetails server = null;
    private BedrockClientSession client = null;
    private Authentication authenticator = null;
    private Map<String, Integer> packets = new ConcurrentHashMap<>();

    public PlayerNetworkSession(Player player) {
        this.player = player;
        this.logger = LoggerFactory.getLogger(
                player.getUsername());

        // Set the logger in debug mode.
        LoggerUtils.setDebug(this.logger);
    }

    /**
     * @return The username of the player.
     */
    public String getUsername() {
        return this.getPlayer().getUsername();
    }

    /**
     * Initializes a connection with the specified server.
     *
     * @param connectTo The server details.
     */
    public void connect(ConnectionDetails connectTo) {
        this.getLogger().info("Attempting to connect to {}...",
                connectTo.toString());
        this.connect0(connectTo).addListener(
                (Promise<BedrockClientSession> promise)
                        -> this.initialize(promise));
    }

    /**
     * Internal method to connect to a server.
     *
     * @param connectTo The server details.
     * @return A promise that will be completed when the connection is established.
     */
    private Promise<BedrockClientSession> connect0(ConnectionDetails connectTo) {
        this.server = connectTo;

        // Fetch a backend event loop.
        var loop = AltBackend.getEventGroup().next();
        Promise<BedrockClientSession> promise = loop.newPromise();

        // Create a new bootstrap.
        new Bootstrap()
                .group(loop)
                .option(RakChannelOption.RAK_MTU, 1400)
                .option(RakChannelOption.RAK_ORDERING_CHANNELS, 1)
                .option(RakChannelOption.RAK_SESSION_TIMEOUT, 10000L)
                .option(RakChannelOption.RAK_CONNECT_TIMEOUT, 25 * 1000L)
                .option(RakChannelOption.RAK_PROTOCOL_VERSION,
                        NetworkConstants.PACKET_CODEC.getRaknetProtocolVersion())
                .channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
                .handler(new BedrockClientInitializer() {
                    @Override
                    protected void initSession(BedrockClientSession session) {
                        var instance = PlayerNetworkSession.this;
                        instance.getLogger().info("RakNet session initialized.");

                        // Set session properties.
                        session.setCodec(NetworkConstants.PACKET_CODEC);
                        session.setPacketHandler(new LoginPacketHandler(instance));

                        // Fulfill the promise.
                        promise.trySuccess(session);
                    }
                })
                .connect(connectTo.toSocketAddress())
                .addListener((ChannelFuture future) -> {
                    if (!future.isSuccess()) {
                        promise.tryFailure(future.cause());
                        future.channel().close();
                    }
                });

        return promise;
    }

    /**
     * Invoked when the connection is established.
     *
     * @param promise The promise that was completed.
     */
    private void initialize(Promise<BedrockClientSession> promise) {
        if (!promise.isSuccess()) {
            this.getLogger().warn("Unable to connect to server.", promise.cause());

            if (this.getData().isReconnect()) {
                this.connect(this.getServer());
            } else {
                PlayerManager.destroyPlayer(this.getPlayer());
            }
        } else {
            this.client = promise.getNow();
            this.onSessionInitialized();
        }
    }

    /**
     * Invoked when the session has been initialized.
     */
    private void onSessionInitialized() {
        try {
            // Request protocol version from server.
            var requestPacket = new RequestNetworkSettingsPacket();
            requestPacket.setProtocolVersion(client.getCodec().getProtocolVersion());
            this.sendPacket(requestPacket, true);
        } catch (Exception exception) {
            this.getLogger().error("An error occurred while logging in.", exception);
            this.client.close("An error occurred while logging in.");
        }
    }

    /**
     * Logs the client into the server.
     */
    public void loginToServer() {
        // Check if this client is already logged in.
        if (this.getData().isLoggedIn()) return;
        this.getData().setLoggedIn(true);

        try {
            // Attempt to log into server.
            var loginPacket = new LoginPacket();

            // Attempt to authenticate.
            var auth = this.authenticator = new Authentication();
            var chainData = auth.getOfflineChainData(this.getUsername());
            // Pull profile data.
            var profile = ProfileUtils.getProfileData(this);
            if (profile == null) profile = ProfileUtils.SKIN_DATA_BASE_64;

            // Set session data.
            this.data.setDisplayName(auth.getDisplayName());
            this.data.setIdentity(auth.getIdentity());
            this.data.setXuid(auth.getXuid());

            // Set the login properties.
            loginPacket.setProtocolVersion(this.client.getCodec().getProtocolVersion());
            loginPacket.getChain().add(chainData);
            loginPacket.setExtra(profile);

            // Send the packet & update connection.
            this.sendPacket(loginPacket, true);
        } catch (Exception exception) {
            this.getLogger().error("An error occurred while logging in.", exception);
            this.getClient().close("An error occurred while logging in.");
        }
    }

    /**
     * Invoked when the client is disconnected.
     *
     * @param reason The reason for disconnection.
     */
    public void onDisconnect(String reason) {
        if (this.getData().isReconnect()) {
            this.connect(this.getServer());
            return;
        }

        this.close(reason);
        this.getLogger().info("Disconnected from server for {}.", reason);

        // Remove the player from the server.
        PlayerManager.destroyPlayer(this.getPlayer());
    }

    /**
     * Sends a packet to the client.
     * This does not happen immediately.
     * @param packet The packet to send.
     */
    public void sendPacket(BedrockPacket packet) {
        this.sendPacket(packet, false);
    }

    /**
     * Sends a packet to the client.
     * @param packet The packet to send.
     * @param immediate Whether to send the packet immediately.
     */
    public void sendPacket(BedrockPacket packet, boolean immediate) {
        if (immediate)
            this.client.sendPacketImmediately(packet);
        else
            this.client.sendPacket(packet);
    }

    /**
     * Logs a packet to the console.
     *
     * @param packet The packet to log.
     */
    public void logPacket(BedrockPacket packet) {
        var packetName = packet.getClass().getSimpleName();
        var packetHash = packet.hashCode();

        this.getPackets().compute(packetName, (k, v) -> {
            if (v == null || v != packetHash) {
                this.getLogger().debug("Packet received: {} > {}.",
                        packet.getClass().getSimpleName(),
                        packet);
                v = packetHash;
            }

            return v;
        });
    }

    /**
     * Closes the connection to the server.
     *
     * @param reason The reason for closing.
     */
    public void close(String reason) {
        this.getClient().close(reason);
    }

    @Override
    public String toString() {
        return this.getUsername();
    }
}
