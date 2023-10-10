package moe.seikimo.altservice.player.inventory;

import lombok.Data;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.utils.RandomUtils;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequest;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequestSlotData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.PlaceAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.TakeAction;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackRequestPacket;

import java.util.LinkedList;
import java.util.List;

@Data public final class Inventory {
    private final Player player;

    private final int id;
    private final ContainerType type;
    private final Vector3i position;

    private final List<ItemData> items
            = new LinkedList<>();

    /**
     * Closes the player's inventory on the server.
     */
    public void closeInventory() {
        // Close the inventory.
        var invPacket = new ContainerClosePacket();
        invPacket.setId((byte) this.getId());
        invPacket.setServerInitiated(false);

        this.getPlayer().sendPacket(invPacket);
    }

    /**
     * Gets the item in the specified slot.
     *
     * @param slot The slot to get the item from.
     * @return The item.
     */
    public ItemData getItem(int slot) {
        return this.getItems().get(slot);
    }

    /**
     * Takes the item from the specified slot.
     * This will add the item to the player's inventory.
     *
     * @param source The source slot in the inventory.
     * @param target The target slot in the player's inventory.
     *               Use -1 to add the item to the first available slot.
     * @param count The amount of items to take.
     */
    public void transfer(int source, int target, int count) {
        var item = this.getItem(source);
        if (item == null) return;

        // Set the count to the item's count if it's -1.
        if (count == -1) count = item.getCount();

        {
            // Create the request.
            var items = Math.min(count, item.getCount());
            var requestId = RandomUtils.randomInt(0, 1000);
            var request = new ItemStackRequest(requestId, new ItemStackRequestAction[] {
                    new TakeAction(items,
                            new ItemStackRequestSlotData(
                                    ContainerSlotType.LEVEL_ENTITY,
                                    source, 1
                            ),
                            new ItemStackRequestSlotData(
                                    ContainerSlotType.CURSOR,
                                    0, 1
                            )
                    ),
                    new PlaceAction(items,
                            new ItemStackRequestSlotData(
                                    ContainerSlotType.CURSOR,
                                    0, 1
                            ),
                            new ItemStackRequestSlotData(
                                    ContainerSlotType.INVENTORY,
                                    target, 1
                            )
                    )
            }, new String[0]);

            // Create the item packet.
            var itemPacket = new ItemStackRequestPacket();
            itemPacket.getRequests().add(request);
            this.getPlayer().sendPacket(itemPacket);
        }

        // Add the item to the player's inventory.
        var inventory = this.getPlayer().getInventory();
        if (target == -1) {
            inventory.getItems().add(item);
        } else {
            inventory.getItems().set(target, item);
        }
    }

    /**
     * Places the item in the specified slot.
     * This will remove the item from the player's inventory.
     *
     * @param source The source slot in the player's inventory.
     * @param target The target slot in the inventory.
     * @param count The amount of items to place.
     */
    public void place(int source, int target, int count) {
        var playerInv = this.getPlayer().getInventory();

        var item = playerInv.getItem(source);
        if (item == null) return;

        // Set the count to the item's count if it's -1.
        if (count == -1) count = item.getCount();
        // Get the index of the item.
        if (target == -1) {
            var items = this.getItems();
            target = items.indexOf(items.stream()
                    .filter(i -> i.getDefinition()
                            .equals(item.getDefinition()))
                    .filter(i -> i.getCount() < 64)
                    .findFirst()
                    .orElse(ItemData.AIR));
        }

        {
            // Create the request.
            var items = Math.min(count, item.getCount());
            var requestId = RandomUtils.randomInt(0, 1000);
            var request = new ItemStackRequest(requestId, new ItemStackRequestAction[] {
                    new TakeAction(items,
                            new ItemStackRequestSlotData(
                                    ContainerSlotType.INVENTORY,
                                    source, 1
                            ),
                            new ItemStackRequestSlotData(
                                    ContainerSlotType.CURSOR,
                                    0, 1
                            )
                    ),
                    new PlaceAction(items,
                            new ItemStackRequestSlotData(
                                    ContainerSlotType.CURSOR,
                                    0, 1
                            ),
                            new ItemStackRequestSlotData(
                                    ContainerSlotType.LEVEL_ENTITY,
                                    target, 1
                            )
                    )
            }, new String[0]);

            // Create the item packet.
            var itemPacket = new ItemStackRequestPacket();
            itemPacket.getRequests().add(request);
            this.getPlayer().sendPacket(itemPacket);
        }

        // Remove the item from the player's inventory.
        playerInv.getItems().remove(item);
    }
}
