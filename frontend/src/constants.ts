import { Attributes, Inventory, Vector3 } from "@backend/Structures.ts";

export const EmptyPosition: Vector3 = {
    x: 6, y: 9, z: 42
};

export const EmptyInventory: Inventory = {
    hotbar: [], items: []
};

export const EmptyAttributes: Attributes = {
    armor: 20, health: 20, hunger: 20, xpLevel: 0, xpProgress: 100
};
