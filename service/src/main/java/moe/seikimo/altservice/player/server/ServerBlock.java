package moe.seikimo.altservice.player.server;

import lombok.Data;
import org.cloudburstmc.math.vector.Vector3i;
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

    private final String identifier;
    private final Vector3i position;
}
