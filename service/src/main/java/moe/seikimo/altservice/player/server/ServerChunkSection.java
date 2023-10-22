package moe.seikimo.altservice.player.server;

import lombok.Data;
import moe.seikimo.altservice.utils.objects.game.BlockStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
public final class ServerChunkSection {
    private final List<BlockStorage> layers = new ArrayList<>();

    private final ServerChunk parent;
    private final int chunkY;

    /**
     * Sets the block at the given coordinates.
     *
     * @param layer The layer. 0 = terrain, 1 = liquid.
     * @param x The block X coordinate.
     * @param y The block Y coordinate.
     * @param z The block Z coordinate.
     * @param block The block.
     * @return The block.
     */
    public ServerBlock setBlockAt(int layer, int x, int y, int z, ServerBlock block) {
        return this
                .getLayers().get(layer)
                .setBlockAt(x, y, z, block);
    }

    /**
     * Gets the block at the given coordinates.
     *
     * @param layer The layer. 0 = terrain, 1 = liquid.
     * @param x The block X coordinate.
     * @param y The block Y coordinate.
     * @param z The block Z coordinate.
     * @return The block.
     */
    public ServerBlock getBlockAt(int layer, int x, int y, int z) {
        return this
                .getLayers().get(layer)
                .getBlockAt(x, y, z);
    }

    /**
     * Gets the block at the given coordinates.
     *
     * @param layer The layer.
     * @param x The block X coordinate.
     * @param y The block Y coordinate.
     * @param z The block Z coordinate.
     * @param runtimeId The block's runtime ID.
     */
    public void setBlock(int layer, int x, int y, int z, int runtimeId) {
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
    public int getBlock(int layer, int x, int y, int z) {
        return this.getLayers().get(layer).getBlock(x, y, z);
    }
}
