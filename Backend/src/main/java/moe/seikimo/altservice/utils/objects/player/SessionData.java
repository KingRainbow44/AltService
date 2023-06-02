package moe.seikimo.altservice.utils.objects.player;

import lombok.Data;

import java.util.UUID;

@Data
public final class SessionData {
    /* Entity data. */
    private long runtimeId = -1;

    /* Player data. */
    private String displayName = "";
    private String xuid = "";
    private UUID identity = null;

    /* Login flags. */
    private boolean loggedIn = false;
    private boolean initialized = false;
}
