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
