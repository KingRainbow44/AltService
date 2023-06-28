package moe.seikimo.altservice.script.event;

import lombok.Builder;
import lombok.Data;
import moe.seikimo.altservice.player.inventory.PlayerInventory;
import moe.seikimo.altservice.utils.objects.Location;

/**
 * Arguments to a script event.
 */
@Data @Builder
public final class ScriptArgs {
    private String player;
    private Location location;
    private PlayerInventory inventory;
}
