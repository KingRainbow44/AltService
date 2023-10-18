import { Component } from "preact";

import MinecraftItem from "@components/MinecraftItem.tsx";

import { Inventory as InventoryType, Item } from "@backend/Structures.ts";

import "@css/widgets/Inventory.scss";

interface SlotProps {
    itemId?: string;
    count?: number;
    damage?: number;

    placeholder?: "helmet" | "chestplate" | "leggings" | "boots" | "shield" | undefined;
}

/**
 * Checks if the item should be shown.
 *
 * @param itemId The item ID.
 */
function shouldShow(itemId: string | undefined): boolean {
    return itemId != undefined && itemId != "" && itemId != "air";
}

function InventorySlot(props: SlotProps) {
    const trimmed = props.itemId?.replace("minecraft:", "");

    return (
        <div class={"Inventory_Slot"}>
            {
                shouldShow(trimmed) ?
                    <MinecraftItem itemId={trimmed ?? ""} /> :
                    props.placeholder ? (
                        <img alt={props.placeholder} draggable={false}
                             src={`/resources/item/empty_armor_slot_${props.placeholder}.png`}
                        />
                    ) : undefined
            }
        </div>
    );
}

interface IProps {
    inventory: InventoryType;
}

interface IState {

}

class Inventory extends Component<IProps, IState> {
    constructor(props: IProps) {
        super(props);

        this.state = {};
    }

    /**
     * Fills the inventory with empty items.
     *
     * @param items The array to fill.
     * @param max The maximum amount of items.
     * @private
     */
    private fill(items: Item[], max: number): void {
        if (items.length >= max) return;

        for (let i = items.length; i < max; i++) {
            items.push({
                itemId: "",
                quantity: 0,
                durability: 0,
            });
        }
    }

    render() {
        const inventory = this.props.inventory;

        const items = inventory.items;
        this.fill(items, 27);
        const hotbar = inventory.hotbar;
        this.fill(hotbar, 9);

        return (
            <div className={"Inventory"}>
                <div class={"Inventory_Equipment"}>
                    <InventorySlot
                        placeholder={"helmet"}
                    />

                    <InventorySlot
                        placeholder={"chestplate"}
                    />

                    <InventorySlot
                        placeholder={"leggings"}
                    />

                    <InventorySlot
                        placeholder={"boots"}
                    />

                    <InventorySlot
                        placeholder={"shield"}
                    />
                </div>

                <div className={"Inventory_General"}>
                    <div className={"Inventory_Items"}>
                        {
                            inventory.items.map((item: Item, index: number) =>
                                <InventorySlot itemId={item.itemId} count={item.quantity}
                                               damage={item.durability} key={index} />
                            )
                        }
                    </div>

                    <div className={"Inventory_Hotbar"}>
                        {
                            inventory.hotbar.map((item: Item, index: number) =>
                                <InventorySlot itemId={item.itemId} count={item.quantity}
                                               damage={item.durability} key={index} />
                            )
                        }
                    </div>
                </div>
            </div>
        );
    }
}

export default Inventory;
