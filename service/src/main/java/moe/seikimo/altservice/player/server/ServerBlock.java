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
     * @param block The block identifier.
     * @param definition The block definition.
     * @return The server block.
     */
    public static ServerBlock from(Vector3i block, SimpleBlockDefinition definition) {
        return new ServerBlock(definition.getIdentifier(), block);
    }

    /**
     * Creates a new server block.
     *
     * @param map The NBT map.
     * @return The server block.
     */
    public static ServerBlock from(NbtMap map) {
        return new ServerBlock(map.getString("name"), Vector3i.from(0));
    }

    /**
     * Creates a new server block.
     *
     * @param position The block position.
     * @param runtimeId The runtime ID.
     * @return The server block.
     */
    public static ServerBlock from(Vector3i position, int runtimeId) {
        var definition = GameConstants.BLOCK_DEFINITIONS.get(runtimeId);
        if (definition == null) return null;

        return new ServerBlock(definition.getIdentifier(), position);
    }

    private final String identifier;
    private final Vector3i position;
}
