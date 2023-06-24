package moe.seikimo.altservice.player;

import lombok.Data;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

import java.util.LinkedList;
import java.util.List;

@Data public final class PlayerInventory {
    private final List<ItemData> items = new LinkedList<>();
    private final List<ItemData> armor = new LinkedList<>();
    private ItemData offhand = ItemData.AIR;

    private int hotbarSlot = 0;
}
