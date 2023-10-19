package moe.seikimo.altservice.utils.objects.game;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import moe.seikimo.altservice.utils.ArrayUtils;
import org.cloudburstmc.protocol.common.util.VarInts;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Adapted from JSPrismarine/JSPrismarine. */
@Data
public final class BlockStorage {
    /**
     * Compresses an X, Y, Z coordinate into a single index.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The index.
     */
    public static int getIndex(int x, int y, int z) {
        return ((x << 8) + (z << 4)) | y;
    }

    /**
     * Decodes a block storage from the given buffer.
     *
     * @param buffer The buffer.
     * @return The block storage.
     */
    public static BlockStorage decode(ByteBuf buffer) {
        var bitsPerBlock = buffer.readByte() >> 1;
        var blocksPerWord = (int) Math.floor(32f / bitsPerBlock);
        var wordsPerChunk = (int) Math.ceil(4096f / blocksPerWord);

        var words = new int[wordsPerChunk];
        for (var w = 0; w < wordsPerChunk; w++)
            words[w] = buffer.readIntLE();

        var paletteCount = VarInts.readInt(buffer);
        var palette = new int[paletteCount];
        for (var i = 0; i < paletteCount; i++)
            palette[i] = VarInts.readInt(buffer);

        var position = 0;
        var storage = new BlockStorage(ArrayUtils.cast(palette), null);
        for (var w = 0; w < wordsPerChunk; w++) {
            var word = words[w];
            for (var block = 0; block < blocksPerWord; block++) {
                var state = (word >> ((position % blocksPerWord) * bitsPerBlock)) & ((1 << bitsPerBlock) - 1);

                var x = (position >> 8) & 0xF;
                var y = position & 0xF;
                var z = (position >> 4) & 0xF;

                var translated = palette[state];
                storage.setBlock(x, y, z, translated);
                position++;
            }
        }

        return storage;
    }

    private final List<Integer> blocks = new ArrayList<>();
    private final List<Integer> palette = new ArrayList<>();

    private BlockStorage(
            @Nullable Integer[] palette,
            @Nullable Integer[] blocks
    ) {
        this.palette.addAll(Arrays.asList(palette == null ?
                ArrayUtils.single(0) : palette));
        this.blocks.addAll(Arrays.asList(blocks == null ?
                ArrayUtils.fill(4096, 0) : blocks));
    }

    /**
     * Sets the block inside the storage.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @param runtimeId The runtime ID.
     */
    public void setBlock(int x, int y, int z, int runtimeId) {
        if (!this.getPalette().contains(runtimeId)) {
            this.getPalette().add(runtimeId);
        }

        this.getBlocks().set(
                BlockStorage.getIndex(x, y, z),
                this.getPalette().indexOf(runtimeId));
    }

    /**
     * Gets the block at the given coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The block.
     */
    public int getBlock(int x, int y, int z) {
        var paletteIndex = this.getBlocks().get(
                BlockStorage.getIndex(x, y, z));
        return this.getPalette().get(paletteIndex);
    }
}
