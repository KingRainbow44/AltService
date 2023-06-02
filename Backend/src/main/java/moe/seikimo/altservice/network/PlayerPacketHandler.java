package moe.seikimo.altservice.network;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.command.CommandMap;
import moe.seikimo.altservice.utils.EncodingUtils;
import moe.seikimo.altservice.utils.ThreadUtils;
import moe.seikimo.altservice.utils.objects.Style;
import moe.seikimo.altservice.utils.objects.absolute.NetworkConstants;
import moe.seikimo.altservice.utils.objects.network.HandshakeHeader;
import moe.seikimo.altservice.utils.objects.network.HandshakePayload;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.slf4j.Logger;

@Data
@RequiredArgsConstructor
public final class PlayerPacketHandler implements BedrockPacketHandler {
    private final PlayerNetworkSession session;

    /**
     * @return The session's logger.
     */
    private Logger getLogger() {
        return this.getSession().getLogger();
    }

    /**
     * @return The session's player.
     */
    private Player getPlayer() {
        return this.getSession().getPlayer();
    }

    @Override
    public PacketSignal handle(NetworkSettingsPacket packet) {
        // Apply network settings.
        var client = this.getSession().getClient();
        client.setCompression(packet.getCompressionAlgorithm());
        client.setCompressionLevel(NetworkConstants.COMPRESSION_LEVEL);

        this.getLogger().info("Established network settings.");
        this.getSession().loginToServer();

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerToClientHandshakePacket packet) {
        try {
            // Decode the JWT token.
            var jwt = packet.getJwt().split("\\.");
            var header = EncodingUtils.base64Decode(jwt[0]);
            var payload = EncodingUtils.base64Decode(jwt[1]);
            // Parse the objects.
            var headerObject = EncodingUtils.jsonDecode(header, HandshakeHeader.class);
            var payloadObject = EncodingUtils.jsonDecode(payload, HandshakePayload.class);

            // Create an encryption key from the payload.
            var salt = EncodingUtils.base64DecodeToBytes(payloadObject.getSalt());
            var privateKey = this.session.getAuthenticator().getPrivateKey();
            var publicKey = EncryptionUtils.generateKey(headerObject.getPublicKey());
            var secretKey = EncryptionUtils.getSecretKey(privateKey, publicKey, salt);

            // Set the encryption key.
            session.getClient().enableEncryption(secretKey);
        } catch (Exception exception) {
            this.getLogger().error("Failed to decode handshake packet.", exception);
        }

        // Send handshake response packet.
        var response = new ClientToServerHandshakePacket();
        this.session.sendPacket(response, true);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayStatusPacket packet) {
        if (packet.getStatus().equals(PlayStatusPacket.Status.PLAYER_SPAWN)) {
            // Wait for the player to initialize.
            while (!this.session.getData().isInitialized()) {
                ThreadUtils.sleep(3000L);
            }

            // Complete server-side initialization.
            var tickPacket = new TickSyncPacket();
            this.session.sendPacket(tickPacket, true);

            var completePacket = new SetLocalPlayerAsInitializedPacket();
            completePacket.setRuntimeEntityId(this.session.getData().getRuntimeId());
            this.session.sendPacket(completePacket, true);
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePacksInfoPacket packet) {
        // Send resource pack cache data.
        this.session.sendPacket(new ClientCacheStatusPacket(), true);
        // Create resource pack response.
        var response = new ResourcePackClientResponsePacket();
        response.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);
        // Send resource pack response.
        this.session.sendPacket(response, true);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackStackPacket packet) {
        // Create resource pack response.
        var response = new ResourcePackClientResponsePacket();
        response.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);
        // Send resource pack response.
        this.session.sendPacket(response, true);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StartGamePacket packet) {
        // Mark the player as initialized.
        this.session.getData().setInitialized(true);
        this.session.getData().setRuntimeId(packet.getRuntimeEntityId());

        // Request the render distance.
        var distancePacket = new RequestChunkRadiusPacket();
        distancePacket.setRadius(64);
        this.session.sendPacket(distancePacket, true);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(DisconnectPacket packet) {
        // Disconnect the player.
        this.session.onDisconnect(packet.isMessageSkipped() ?
                "No reason provided." : packet.getKickMessage());

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RespawnPacket packet) {
        var position = packet.getPosition();
        var state = packet.getState();

        // Log the respawn.
        this.getLogger().info("Respawning at {}. (state: {})",
                position.toString(), state);

        // Check if the server is waiting for the client.
        if (state == RespawnPacket.State.SERVER_READY) {
            this.getPlayer().setPosition(position);
            this.getPlayer().respawn();
        }

        return PacketSignal.HANDLED;
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
                CommandMap.invoke(this.getPlayer(), parsed.replace(
                        this.getPlayer().getUsername(), ""
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
