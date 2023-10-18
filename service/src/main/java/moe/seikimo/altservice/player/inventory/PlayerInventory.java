package moe.seikimo.altservice.player.inventory;

import lombok.Data;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.proto.Structures;
import moe.seikimo.altservice.utils.EncodingUtils;
import moe.seikimo.altservice.utils.RandomUtils;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequest;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequestSlotData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DropAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import org.cloudburstmc.protocol.bedrock.packet.InteractPacket;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackRequestPacket;

import java.util.LinkedList;
import java.util.List;

@Data public final class PlayerInventory {
    private final Player player;

    private final List<ItemData> items = new LinkedList<>();
    private final List<ItemData> armor = new LinkedList<>();
    private ItemData offhand = ItemData.AIR;

    private int hotbarSlot = 0;
    private boolean open = false;

    /**
     * Opens the player's inventory on the server.
     */
    public void openInventory() {
        if (this.isOpen()) return;
        this.setOpen(true);

        // Request to open the inventory.
        var invPacket = new InteractPacket();
        invPacket.setAction(InteractPacket.Action.OPEN_INVENTORY);
        invPacket.setRuntimeEntityId(player.getEntityId());

        this.getPlayer().sendPacket(invPacket);
    }

    /**
     * Closes the player's inventory on the server.
     */
    public void closeInventory() {
        if (!this.isOpen()) return;
        this.setOpen(false);

        // Close the inventory.
        var invPacket = new ContainerClosePacket();
        invPacket.setId((byte) ContainerId.INVENTORY);
        invPacket.setServerInitiated(false);

        this.getPlayer().sendPacket(invPacket);
    }

    /**
     * @return The item in the player's main hand.
     */
    public ItemData getItemInMainHand() {
        return this.getItems().get(this.getHotbarSlot());
    }

    /**
     * Gets an item from the inventory.
     *
     * @param slot The slot of the item.
     * @return The item.
     */
    public ItemData getItem(int slot) {
        return this.getItems().get(slot);
    }

    /**
     * Gets an item from the inventory.
     *
     * @param name The identifier of the item.
     * @return The item.
     */
    public ItemData getItem(String name) {
        // Check if the name has a namespace.
        if (!name.startsWith("minecraft:"))
            name = "minecraft:" + name;

        var identifier = name;
        return this.getItems().stream()
                .filter(i -> i.getDefinition().getIdentifier()
                        .equalsIgnoreCase(identifier))
                .findFirst().orElse(null);
    }

    /**
     * Drops an item from the inventory.
     *
     * @param item The item to drop.
     * @param count The amount of items to drop.
     */
    public void drop(ItemData item, int count) {
        // Check if the inventory is open.
        var startedOpen = this.isOpen();
        if (!startedOpen) this.openInventory();

        // Get the item index and container type.
        var itemIndex = this.items.indexOf(item);
        var containerType = itemIndex > 8 ?
                ContainerSlotType.INVENTORY : ContainerSlotType.HOTBAR;

        // Create the request.
        var requestId = RandomUtils.randomInt(0, 1000);
        var request = new ItemStackRequest(requestId, new ItemStackRequestAction[] {
                new DropAction(Math.min(count, item.getCount()),
                        new ItemStackRequestSlotData(
                                containerType, itemIndex, 1
                        ), false
                )
        }, new String[0]);

        // Create the item packet.
        var itemPacket = new ItemStackRequestPacket();
        itemPacket.getRequests().add(request);
        this.getPlayer().sendPacket(itemPacket);

        // Remove the item from the inventory.
        var before = item.getCount();
        var after = before - count;
        this.items.set(itemIndex, ItemData.AIR);

        if (after > 0) {
            this.items.add(itemIndex, item);
        }

        // Close the inventory if it wasn't open before.
        if (!startedOpen) this.closeInventory();
    }

    /**
     * @return The inventory as a protobuf structure.
     */
    public Structures.Inventory toProto() {
        // Check the lists.
        if (this.getArmor().isEmpty()) {
            this.getArmor().addAll(List.of(
                    ItemData.AIR, ItemData.AIR,
                    ItemData.AIR, ItemData.AIR
            ));
        }
        if (this.getItems().size() < 36) {
            // Fill the inventory until it has 36 items.
            var diff = 36 - this.getItems().size();
            for (var i = 0; i < diff; i++) {
                this.getItems().add(ItemData.AIR);
            }
        }

        var armor = this.getArmor().stream()
                .map(EncodingUtils::convert)
                .toList();
        var hotbar = this.getItems().subList(0, 9).stream()
                .map(EncodingUtils::convert)
                .toList();
        var items = this.getItems().subList(9, 36).stream()
                .map(EncodingUtils::convert)
                .toList();

        return Structures.Inventory.newBuilder()
                .setHelmet(armor.get(0))
                .setChestplate(armor.get(1))
                .setLeggings(armor.get(2))
                .setBoots(armor.get(3))
                .addAllHotbar(hotbar)
                .addAllItems(items)
                .build();
    }

    /**
     * @return The armor value of the player.
     */
    public float computeArmor() {
        var armor = 0.0f;
        for (var item : this.getArmor()) {
            if (item == null) continue;

            armor += GameConstants.getArmorPoints(
                    item.getDefinition().getIdentifier());
        }
        return armor;
    }
}
