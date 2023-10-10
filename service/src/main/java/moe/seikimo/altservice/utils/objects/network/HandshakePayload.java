package moe.seikimo.altservice.utils.objects.network;

import lombok.Getter;

@Getter
public final class HandshakePayload {
    private String salt; // Base64-encoded salt.
}
