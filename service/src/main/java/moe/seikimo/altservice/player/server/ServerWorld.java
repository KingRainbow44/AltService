package moe.seikimo.altservice.player.server;

import lombok.Data;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;
import org.cloudburstmc.math.vector.Vector2i;
import org.cloudburstmc.math.vector.Vector3i;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public final class ServerWorld {
    private final Logger logger
            = LoggerFactory.getLogger("Server World");
    private final Map<Vector2i, ServerChunk> chunks
             = new ConcurrentHashMap<>();
    private final Integer dimensionId;

    /**
     * @return The minimum Y coordinate.
     */
    public int getMinimumY() {
        return GameConstants.DIMENSIONS.get(this.getDimensionId()).a();
    }

    /**
     * @return The maximum Y coordinate.
     */
    public int getMaximumY() {
        return GameConstants.DIMENSIONS.get(this.getDimensionId()).b();
    }

    /**
     * Gets the chunk at the given coordinates.
     *
     * @param x The chunk X coordinate.
     * @param z The chunk Z coordinate.
     * @return The chunk.
     */
    public ServerChunk getChunkAt(int x, int z) {
        return this.getChunks().computeIfAbsent(Vector2i.from(x, z),
                k -> new ServerChunk(this, x, z));
    }

    /**
     * Sets the block at the given coordinates.
     *
     * @param layer The layer. 0 = terrain, 1 = liquid.
     * @param x The block X coordinate.
     * @param y The block Y coordinate.
     * @param z The block Z coordinate.
     * @param block The block.
     */
    public ServerBlock setBlockAt(int layer, int x, int y, int z, ServerBlock block) {
        y += 64; // This accounts for the negative sections of the world.

        return this
                .getChunkAt(x >> 4, z >> 4)
                .setBlockAt(layer, y >> 4, x % 16, y % 16, z % 16, block);
    }

    /**
     * Sets the block at the given coordinates.
     *
     * @param layer The layer. 0 = terrain, 1 = liquid.
     * @param position The block position.
     * @param block The block.
     */
    public ServerBlock setBlockAt(int layer, Vector3i position, ServerBlock block) {
        return this.setBlockAt(layer, position.getX(), position.getY(), position.getZ(), block);
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
        y += 64; // This accounts for the negative sections of the world.

        return this
                .getChunkAt(x >> 4, z >> 4)
                .getBlockAt(layer, y >> 4, x % 16, y % 16, z % 16);
    }

    /**
     * Gets the block at the given coordinates.
     *
     * @param layer The layer. 0 = terrain, 1 = liquid.
     * @param position The block position.
     * @return The block.
     */
    public ServerBlock getBlockAt(int layer, Vector3i position) {
        return this.getBlockAt(layer, position.getX(), position.getY(), position.getZ());
    }
}
