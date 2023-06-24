package moe.seikimo.altservice.player;

import lombok.Data;

@Data
public final class PlayerActions {
    private boolean follow = false;
    private boolean attack = false;
    private boolean look = false;
    private boolean none = false;
    private boolean guard = false;

    private boolean guardPlayers = true;
    private boolean guardMobs = true;
}
