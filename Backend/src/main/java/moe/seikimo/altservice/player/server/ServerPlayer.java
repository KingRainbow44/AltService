package moe.seikimo.altservice.player.server;

import lombok.*;
import moe.seikimo.altservice.utils.objects.Location;

import java.util.UUID;

/**
 * Represents a player on the server.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public final class ServerPlayer extends ServerEntity {
    private final String username;
    private final UUID uuid;

    public ServerPlayer(long runtimeId, String username, UUID uuid) {
        super(runtimeId, -1);

        this.username = username;
        this.uuid = uuid;
    }
}
