package moe.seikimo.altservice.utils.objects.absolute;

import moe.seikimo.altservice.AltBackend;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleBlockDefinition;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;

import java.io.DataInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;

public interface GameConstants {
    /* An air block definition. */
    BlockDefinition AIR_BLOCK = new SimpleBlockDefinition("minecraft:air", 0, NbtMap.EMPTY);
    /* The block definitions. */
    AtomicReference<SimpleDefinitionRegistry<BlockDefinition>> BLOCKS = new AtomicReference<>();

    /* Identifiers of entities which cannot be attacked. */
    Set<String> INVULNERABLE_ENTITIES = new HashSet<>() {{
        this.add("minecraft:xp_orb");
    }};
    /* Identifiers of blocks which should be ignored. */
    Set<String> IGNORE_BLOCKS = new HashSet<>() {{
        this.add("minecraft:air");
        this.add("minecraft:redstone_wire");
        this.add("minecraft:unpowered_repeater");
        this.add("minecraft:powered_repeater");
    }};

    /* The offset of a Bedrock position to a Java block. */
    float OFFSET = 1.62f;

    /**
     * Initializes the block palette.
     */
    static void initializeBlocks() {
        try (var stream = AltBackend.getResource("block_palette.nbt")) {
            if (stream == null) throw new NullPointerException("No block palette found!");

            // Read the block palette as an NBT stream.
            var nbt = new NBTInputStream(new DataInputStream(
                    new GZIPInputStream(stream)),
                    true, true);
            var palette = (NbtMap) nbt.readTag();
            var blocks = (NbtList<NbtMap>) palette.getList(
                    "blocks", NbtType.COMPOUND);

            // Create a registry.
            var registry = new SimpleDefinitionRegistry.Builder<BlockDefinition>();
            for (var i = 0; i < blocks.size(); i++) {
                // Remove unnecessary data.
                var builder = blocks.get(i).toBuilder();
                builder.remove("name_hash");
                builder.remove("network_id");

                // Create the definition.
                var tag = builder.build();
                registry.add(new SimpleBlockDefinition(
                        tag.getString("name"), i, tag));
            }

            BLOCKS.set(registry.build());
        } catch (Exception ignored) {
            AltBackend.getLogger().warn("Failed to load block palette!");
        }
    }
}
