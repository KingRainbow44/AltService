package moe.seikimo.altservice.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moe.seikimo.altservice.proto.Structures.Player;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public final class Session {
    private final String id;

    @Setter @NotNull
    private Player handle;

    /**
     * Updates the session with the new player data.
     *
     * @param player The new player data.
     */
    public void updateSession(Player player) {
        this.setHandle(player);
    }
}
