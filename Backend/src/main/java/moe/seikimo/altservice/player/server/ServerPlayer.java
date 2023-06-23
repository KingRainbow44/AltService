package moe.seikimo.altservice.player.server;

import lombok.Data;
import moe.seikimo.altservice.utils.objects.Location;

import java.util.UUID;

/**
 * Represents a player on the server.
 */
@Data public final class ServerPlayer {
    private final long runtimeId;
    private final String username;
    private final UUID uuid;

    private Location location = Location.ZERO;
}
