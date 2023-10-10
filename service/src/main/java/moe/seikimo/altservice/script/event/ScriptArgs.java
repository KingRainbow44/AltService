package moe.seikimo.altservice.script.event;

import lombok.Builder;
import lombok.Data;
import moe.seikimo.altservice.player.inventory.Inventory;
import moe.seikimo.altservice.player.inventory.PlayerInventory;
import moe.seikimo.altservice.player.server.ServerBlock;
import moe.seikimo.altservice.utils.objects.Location;
import org.cloudburstmc.math.vector.Vector3f;

/**
 * Arguments to a script event.
 */
@Data @Builder
public final class ScriptArgs {
    private String player;

    private Vector3f position;
    private Location location;

    private Inventory container;
    private PlayerInventory inventory;

    private ServerBlock block;
    private ServerBlock oldBlock;
}
