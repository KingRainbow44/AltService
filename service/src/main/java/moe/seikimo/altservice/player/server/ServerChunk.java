package moe.seikimo.altservice.player.server;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import moe.seikimo.altservice.utils.objects.game.BlockStorage;
import org.cloudburstmc.math.vector.Vector3i;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public final class ServerChunk {
    private final Map<Integer, ServerChunkSection> sections
            = new ConcurrentHashMap<>();

    private final ServerWorld world;
    private final int chunkX;
    private final int chunkZ;

    /**
     * Gets the chunk section at the given index.
     *
     * @param index The chunk section index.
     * @return The chunk section.
     */
    private ServerChunkSection getSectionByIndex(int index) {
        return this.getSections().computeIfAbsent(index,
                k -> new ServerChunkSection(this, k));
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
    public ServerBlock getBlockAt(int x, int y, int z) {
        this.getWorld().getLogger().info(
                "Attempting to read block ({}, {}, {}); reading chunk at ({}, {}); section {}; block ({}, {}, {}).",
                x, y, z, this.getChunkX(), this.getChunkZ(), y >> 4, x % 16, y % 16, z % 16
        );

        return this.getSectionByIndex(y >> 4)
                .getBlockAt(0, x % 16, y % 16, z % 16);
    }

    /**
     * Gets the block at the given coordinates.
     *
     * @param position The block position.
     * @return The block.
     */
    @Nullable
    public ServerBlock getBlockAt(Vector3i position) {
        return this.getBlockAt(
                position.getX(),
                position.getY(),
                position.getZ());
    }

    /**
     * Sets the block at the given coordinates.
     *
     * @param x The block X coordinate.
     * @param y The block Y coordinate.
     * @param z The block Z coordinate.
     * @param runtimeId The block's runtime ID.
     */
    public void setBlockAt(int x, int y, int z, int runtimeId) {
        this.getSectionByIndex(y >> 4)
                .setBlockAt(0, x % 16, y % 16, z % 16, runtimeId);
    }

    /**
     * Decodes chunk data.
     *
     * @param buffer The buffer to decode from.
     * @param subChunks The number of sub-chunks to read.
     */
    public void decodeFrom(ByteBuf buffer, int subChunks) {
        for (var i = 0; i < subChunks; i++) {
            var subChunk = new ServerChunkSection(this, i);

            var version = buffer.readByte();
            switch (version) {
                default -> this.getWorld().getLogger().info("Unknown chunk section version: " + version);
                case 8 -> this.decodeV8(subChunk, buffer);
                case 9 -> this.decodeV9(subChunk, buffer);
            }

            this.getSections().put(i, subChunk);
        }
    }

    /**
     * Decodes version 9 sub-chunk data.
     *
     * @param subChunk The sub-chunk to decode into.
     * @param buffer The buffer to decode from.
     */
    private void decodeV9(ServerChunkSection subChunk, ByteBuf buffer) {
        var layers = buffer.readByte();
        var height = buffer.readByte();
        for (var i = 0; i < layers; i++) {
            subChunk.getLayers().add(BlockStorage.decode(buffer));
        }
    }

    /**
     * Decodes version 8 sub-chunk data.
     *
     * @param subChunk The sub-chunk to decode into.
     * @param buffer The buffer to decode from.
     */
    private void decodeV8(ServerChunkSection subChunk, ByteBuf buffer) {
        var layers = buffer.readByte();
        for (var i = 0; i < layers; i++) {
            subChunk.getLayers().add(BlockStorage.decode(buffer));
        }
    }
}
