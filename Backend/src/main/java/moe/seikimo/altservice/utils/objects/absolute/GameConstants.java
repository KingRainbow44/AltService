package moe.seikimo.altservice.utils.objects.absolute;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleBlockDefinition;

public interface GameConstants {
    /* An air block definition. */
    BlockDefinition AIR_BLOCK = new SimpleBlockDefinition("minecraft:air", 0, NbtMap.EMPTY);
    /* The offset of a Bedrock position to a Java block. */
    float OFFSET = 1.62f;
}
