package moe.seikimo.altservice.utils.objects.absolute;

import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.utils.objects.Pair;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleBlockDefinition;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;

import java.io.DataInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;

public interface GameConstants {
    /* An air block definition. */
    BlockDefinition AIR_BLOCK = new SimpleBlockDefinition("minecraft:air", 0, NbtMap.EMPTY);
    /* The block definitions. */
    Map<String, Integer> BLOCK_RUNTIME = new HashMap<>();
    Map<Integer, SimpleBlockDefinition> BLOCK_DEFINITIONS = new HashMap<>();
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
    /* Identifiers of blocks which a player can walk on. */
    Set<String> WALKABLE_BLOCKS = new HashSet<>() {{
        this.add("minecraft:air");
        this.add("minecraft:water");
    }};

    /* Dimension minimum and maximum Y-levels. */
    Map<Integer, Pair<Integer, Integer>> DIMENSIONS = new HashMap<>() {{
        this.put(0, new Pair<>(-64, 320));
        this.put(1, new Pair<>(0, 256));
        this.put(2, new Pair<>(0, 256));
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
                var blockDefinition = new SimpleBlockDefinition(
                        tag.getString("name"), i, tag);

                // Add the definition to the registry.
                registry.add(blockDefinition);
                BLOCK_RUNTIME.put(tag.getString("name"), i);
                BLOCK_DEFINITIONS.put(i, blockDefinition);
            }

            BLOCKS.set(registry.build());
        } catch (Exception ignored) {
            AltBackend.getLogger().warn("Failed to load block palette!");
        }
    }

    /**
     * Gets the armor points of an item.
     *
     * @param itemId The identifier of the item.
     * @return The armor points.
     */
    static float getArmorPoints(String itemId) {
        return switch (itemId) {
            default -> 0.0f;
            case "minecraft:leather_helmet", "minecraft:leather_boots", "minecraft:chainmail_boots", "minecraft:golden_boots" -> 1.0f;
            case "minecraft:leather_chestplate", "minecraft:netherite_boots", "minecraft:netherite_helmet", "minecraft:diamond_boots", "minecraft:diamond_helmet", "minecraft:golden_leggings" -> 3.0f;
            case "minecraft:leather_leggings", "minecraft:iron_boots", "minecraft:iron_helmet", "minecraft:chainmail_helmet", "minecraft:golden_helmet" -> 2.0f;
            case "minecraft:golden_chestplate", "minecraft:iron_leggings", "minecraft:chainmail_chestplate" -> 5.0f;
            case "minecraft:chainmail_leggings" -> 4.0f;
            case "minecraft:iron_chestplate", "minecraft:netherite_leggings", "minecraft:diamond_leggings" -> 6.0f;
            case "minecraft:diamond_chestplate", "minecraft:netherite_chestplate" -> 8.0f;
        };
    }
}
