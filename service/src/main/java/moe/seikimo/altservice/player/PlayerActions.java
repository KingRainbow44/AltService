package moe.seikimo.altservice.player;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public final class PlayerActions {
    private boolean follow = false;
    private boolean attack = false;
    private boolean look = false;
    private boolean guard = false;
    private boolean behave = false;

    private boolean guardPlayers = true;
    private boolean guardMobs = true;
    private List<String> behaviors = new ArrayList<>();
}
