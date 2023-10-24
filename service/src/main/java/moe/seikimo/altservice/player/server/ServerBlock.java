package moe.seikimo.altservice.player.server;

import lombok.Data;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleBlockDefinition;

@Data public final class ServerBlock {
    /**
     * Creates a new server block.
     *
     * @param definition The block definition.
     * @return The server block.
     */
    public static ServerBlock from(SimpleBlockDefinition definition) {
        return ServerBlock.from(definition.getIdentifier());
    }

    /**
     * Creates a new server block.
     *
     * @param map The NBT map.
     * @return The server block.
     */
    public static ServerBlock from(NbtMap map) {
        return ServerBlock.from(map.getString("name"));
    }

    /**
     * Creates a new server block.
     *
     * @param identifier The identifier.
     * @return The server block.
     */
    public static ServerBlock from(String identifier) {
        return new ServerBlock(identifier, GameConstants.BLOCK_RUNTIME.get(identifier));
    }

    /**
     * Creates a new server block.
     *
     * @param runtimeId The runtime ID.
     * @return The server block.
     */
    public static ServerBlock from(int runtimeId) {
        var definition = GameConstants.BLOCK_DEFINITIONS.get(runtimeId);
        return new ServerBlock(definition.getIdentifier(), runtimeId);
    }

    private final String identifier;
    private final int runtimeId;

    private ServerWorld world = null;
    private Vector3i location = Vector3i.ZERO;

    /**
     * @return Whether the block is walkable.
     */
    public boolean isWalkable() {
        return GameConstants.WALKABLE_BLOCKS.contains(this.getIdentifier());
    }
}
