package moe.seikimo.altservice.network.handler;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.utils.EncodingUtils;
import moe.seikimo.altservice.utils.ThreadUtils;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;
import moe.seikimo.altservice.utils.objects.absolute.NetworkConstants;
import moe.seikimo.altservice.utils.objects.network.HandshakeHeader;
import moe.seikimo.altservice.utils.objects.network.HandshakePayload;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;

public class LoginPacketHandler extends DisconnectablePacketHandler {
    public LoginPacketHandler(PlayerNetworkSession session) {
        super(session);
    }

    @Override
    public PacketSignal handle(NetworkSettingsPacket packet) {
        super.handle(packet);
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
        super.handle(packet);
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
                ThreadUtils.sleep(100L);
            }

            this.session.getClient().setPacketHandler(new InGamePacketHandler(this.session));

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
        super.handle(packet);
        // Create resource pack response.
        var response = new ResourcePackClientResponsePacket();
        response.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);
        // Send resource pack response.
        this.session.sendPacket(response, true);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StartGamePacket packet) {
        // Set the movement type.
        this.session.getData().setServerMovement(
                packet.getAuthoritativeMovementMode() !=
                        AuthoritativeMovementMode.CLIENT);

        // Mark the player as initialized.
        this.session.getData().setInitialized(true);
        this.session.getData().setRuntimeId(packet.getRuntimeEntityId());

        // Request the render distance.
        var distancePacket = new RequestChunkRadiusPacket();
        distancePacket.setRadius(64);
        this.session.sendPacket(distancePacket, true);

        // Perform client setup.
        var codecHelper = this.session.getClient()
                .getPeer().getCodecHelper();

        // Set up the item registry.
        SimpleDefinitionRegistry.Builder<ItemDefinition> itemRegistry =
                SimpleDefinitionRegistry.builder();
        var runtimeIds = new IntOpenHashSet();
        for (var definition : packet.getItemDefinitions()) {
            if (runtimeIds.add(definition.getRuntimeId()))
                itemRegistry.add(definition);
            else
                this.getLogger().warn("Duplicate item entry {}.",
                        definition.getIdentifier());
        }
        codecHelper.setItemDefinitions(itemRegistry.build());
        codecHelper.setBlockDefinitions(GameConstants.BLOCKS.get());

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RespawnPacket packet) {
        super.handle(packet);
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
}
