package moe.seikimo.altservice.player.server;

import lombok.Data;
import moe.seikimo.altservice.utils.objects.game.BlockStorage;
import org.cloudburstmc.math.vector.Vector3i;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
public final class ServerChunkSection {
    private final List<BlockStorage> layers = new ArrayList<>();

    private final ServerChunk parent;
    private final int chunkY;

    /**
     * Gets the block at the given coordinates.
     *
     * @param layer The layer.
     * @param x The block X coordinate.
     * @param y The block Y coordinate.
     * @param z The block Z coordinate.
     * @param runtimeId The block's runtime ID.
     */
    public void setBlockAt(int layer, int x, int y, int z, int runtimeId) {
        this.getLayers().get(layer).setBlock(x, y, z, runtimeId);
    }

    /**
     * Gets the block at the given coordinates.
     *
     * @param x The block X coordinate.
     * @param y The block Y coordinate.
     * @param z The block Z coordinate.
     * @return The block.
     */
    @Nullable
    public ServerBlock getBlockAt(int layer, int x, int y, int z) {
        var runtimeId = this.getLayers().get(layer).getBlock(x, y, z);
        return ServerBlock.from(Vector3i.from(x, y, z), runtimeId);
    }
}
