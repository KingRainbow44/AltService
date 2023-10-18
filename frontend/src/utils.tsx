import { JSX } from "preact";

/**
 * Fetches the base URL of the backend server.
 */
export function getBaseUrl(): string {
    return import.meta.env.VITE_BACKEND_URL;
}

/**
 * Converts a number of 0-20 to an array of 10 numbers.
 * These numbers are 0, 1, or 2.
 * 0 represents an empty container.
 * 1 represents half a container.
 * 2 represents a full container.
 *
 * @param quantity
 */
export function toArray(quantity: number): number[] {
    if (quantity < 0 || quantity > 20) {
        throw new Error("Quantity must be between 0 and 20");
    }

    const array: number[] = [];
    for (let i = 0; i < 10; i++) {
        if (quantity >= 2) {
            array.push(2);
            quantity -= 2;
        } else if (quantity == 1) {
            array.push(1);
            quantity -= 1;
        } else {
            array.push(0);
        }
    }

    return array;
}

/**
 * Converts text into span elements.
 * Uses Minecraft color codes to determine the color of the span.
 *
 * @param text The Minecraft chat text.
 */
export function textToHtml(text: string) {
    const split = text.split("§");
    if (split.length == 1) return (
        <span>{text}</span>
    );

    const spans: JSX.Element[] = [];

    for (const part of split) {
        const color = part.charAt(0);
        const text = part.substring(1);
        spans.push(<span style={{ color: `#${getColor(color)}` }}>{text}</span>);
    }

    return spans;
}

/**
 * Converts a Minecraft color code to a hex color.
 *
 * @param color The Minecraft color code.
 */
export function getColor(color: string): string {
    switch (color) {
        case "0":
            return "000000";
        case "1":
            return "0000AA";
        case "2":
            return "00AA00";
        case "3":
            return "00AAAA";
        case "4":
            return "AA0000";
        case "5":
            return "AA00AA";
        case "6":
            return "FFAA00";
        case "7":
            return "AAAAAA";
        case "8":
            return "555555";
        case "9":
            return "5555FF";
        case "a":
            return "55FF55";
        case "b":
            return "55FFFF";
        case "c":
            return "FF5555";
        case "d":
            return "FF55FF";
        case "e":
            return "FFFF55";
        case "f":
            return "FFFFFF";
        default:
            return "FFFFFF";
    }
}
