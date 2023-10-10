package moe.seikimo.altservice.utils.objects.network;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public final class HandshakeHeader {
    @SerializedName("x5u")
    private String publicKey; // Base64-encoded public key.

    @SerializedName("alg")
    private String algorithm; // Algorithm type.
}
