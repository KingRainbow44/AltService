import { Component } from "preact";

import MinecraftItem from "@components/MinecraftItem.tsx";

import "@css/widgets/Inventory.scss";

interface SlotProps {
    itemId?: string;
    placeholder?: "helmet" | "chestplate" | "leggings" | "boots" | "shield" | undefined;
}

function InventorySlot(props: SlotProps) {
    return (
        <div class={"Inventory_Slot"}>
            {
                props.itemId ?
                    <MinecraftItem itemId={props.itemId} /> :
                    <img alt={props.placeholder} draggable={false}
                         src={`/resources/item/empty_armor_slot_${props.placeholder}.png`}
                    />
            }
        </div>
    );
}

interface IProps {
    inventory: {
        items: object[];
        hotbar: object[];
        helmet: object;
        chestplate: object;
        leggings: object;
        boots: object;
        shield: object;
    };
}

interface IState {

}

class Inventory extends Component<IProps, IState> {
    constructor(props: IProps) {
        super(props);

        this.state = {};
    }

    render() {
        const inventory = this.props.inventory;

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
                            inventory.items.map((_: any, index: number) =>
                                <InventorySlot itemId={"diamond_sword"} key={index} />
                            )
                        }
                    </div>

                    <div className={"Inventory_Hotbar"}>
                        {
                            inventory.hotbar.map((_: any, index: number) =>
                                <InventorySlot itemId={"diamond_sword"} key={index} />
                            )
                        }
                    </div>
                </div>
            </div>
        );
    }
}

export default Inventory;
