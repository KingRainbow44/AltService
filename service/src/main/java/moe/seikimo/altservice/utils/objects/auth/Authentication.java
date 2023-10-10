package moe.seikimo.altservice.utils.objects.auth;

import com.google.gson.JsonObject;
import lombok.Getter;
import moe.seikimo.altservice.utils.EncodingUtils;
import moe.seikimo.altservice.utils.enums.AlgorithmType;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;

import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Handles authentication with a Bedrock server.
 */
public final class Authentication {
    private static final KeyPairGenerator KEY_PAIR_GENERATOR;

    static {
        try {
            // Initialize a new key pair generator.
            KEY_PAIR_GENERATOR = KeyPairGenerator.getInstance("EC");
            KEY_PAIR_GENERATOR.initialize(new ECGenParameterSpec("secp256r1"));
        } catch (Exception exception) {
            throw new AssertionError("Unable to initialize key pair generator", exception);
        }
    }

    @Getter private ECPrivateKey privateKey;
    @Getter private ECPublicKey publicKey;

    @Getter private String xuid;
    @Getter private String displayName;
    @Getter private UUID identity;

    /*
     * Key utility methods.
     */

    /**
     * Returns the Bedrock private key.
     * @return A private key.
     */
    public ECPrivateKey getPreferredPrivateKey() {
        return this.privateKey;
    }

    /**
     * Returns the Bedrock public key.
     * @return A public key.
     */
    public ECPublicKey getPreferredPublicKey() {
        return this.publicKey;
    }

    /**
     * Generates data for offline authentication.
     * Uses a username to create a UUID.
     * @param username The username to use.
     * @return Authentication data. (JSON-encoded)
     */
    public String getOfflineChainData(String username) throws Exception {
        var gson = EncodingUtils.JSON;

        // Generate a UUID & XUID.
        var uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
        var xuid = Long.toString(uuid.getLeastSignificantBits());

        // Generate a key pair for the Bedrock server.
        var ecdsa384KeyPair = EncryptionUtils.createKeyPair();
        this.publicKey = (ECPublicKey) ecdsa384KeyPair.getPublic();
        this.privateKey = (ECPrivateKey) ecdsa384KeyPair.getPrivate();
        // Encode the public key.
        var encodedPublicKey = EncodingUtils.base64Encode(this.publicKey.getEncoded());

        // Create a JWT payload.
        var jwtPayload = new JsonObject();
        jwtPayload.addProperty("exp", Instant.now().getEpochSecond() + TimeUnit.HOURS.toSeconds(6));
        jwtPayload.addProperty("identityPublicKey", encodedPublicKey);
        jwtPayload.addProperty("nbf", Instant.now().getEpochSecond() - TimeUnit.HOURS.toSeconds(6));
        // Create extra data for the JWT payload.
        var extraData = new JsonObject();
        extraData.addProperty("titleId", "896928775"); // Minecraft: Windows 10 Edition title ID.
        extraData.addProperty("identity", uuid.toString());
        extraData.addProperty("displayName", username);
        extraData.addProperty("XUID", xuid);
        jwtPayload.add("extraData", extraData);

        // Create a JWT header.
        var jwtHeader = new JsonObject();
        jwtHeader.addProperty("alg", "ES384");
        jwtHeader.addProperty("x5u", encodedPublicKey);
        // Create a JWT payload.
        var encoder = Base64.getUrlEncoder().withoutPadding();
        var header = encoder.encodeToString(gson.toJson(jwtHeader).getBytes());
        var payload = encoder.encodeToString(gson.toJson(jwtPayload).getBytes());

        // Sign the payload & header.
        var dataToSign = (header + "." + payload).getBytes();
        var signature = this.signBytes(dataToSign);
        var jwt = header + "." + payload + "." + signature;

        // Set the identity & XUID.
        this.xuid = xuid;
        this.identity = uuid;
        this.displayName = username;

        return jwt;
    }

    /*
     * Utility methods.
     */

    /**
     * Signs bytes using the stored private key.
     * @param bytes Bytes to sign.
     * @return Signed bytes.
     */
    public String signBytes(byte[] bytes) throws Exception {
        return this.signBytes(bytes, this.privateKey);
    }

    /**
     * Signs bytes using the specified private key.
     * @param bytes Bytes to sign.
     * @return Signed bytes.
     */
    public String signBytes(byte[] bytes, ECPrivateKey privateKey) throws Exception {
        // Create a signature.
        var signature = Signature.getInstance("SHA384withECDSA");
        signature.initSign(privateKey);
        signature.update(bytes);

        // Sign & encode the data.
        var signatureBytes = EncodingUtils.derToJose(signature.sign(), AlgorithmType.ECDSA384);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }
}
