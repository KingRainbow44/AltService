import { getBaseUrl } from "@app/utils.ts";
import { base64decode, base64encode, MessageType } from "@protobuf-ts/runtime";
import { Packet, UnionCmdNotify } from "@backend/Structures.ts";
import {
    ChatMessageNotify,
    FrontendIds,
    UpdateSessionsScNotify
} from "@backend/Frontend.ts";

const messageQueue: string[] = [];
const decoders: { [key: number]: (data: Uint8Array) => any } = {
    [FrontendIds._ChatMessageNotify]: (data: Uint8Array) => ChatMessageNotify.fromBinary(data),
    [FrontendIds._GetAllSessionsScRsp]: (data: Uint8Array) => UpdateSessionsScNotify.fromBinary(data),
    [FrontendIds._UpdateSessionsScNotify]: (data: Uint8Array) => UpdateSessionsScNotify.fromBinary(data)
};

export let socket: WebSocket | null = null;

/**
 * Encodes a packet and sends it to the backend.
 *
 * @param id The packet ID.
 * @param data The packet data.
 */
export function send(
    id: number,
    data: Uint8Array | null
): void {
    // Encode the packet.
    const encoded = Packet.create({ id });
    data && (encoded.data = data);

    // Send the packet.
    const message = Packet.toBinary(encoded);
    const encodedMessage = base64encode(message);
    if (!socket || socket.readyState != WebSocket.OPEN) {
        messageQueue.push(encodedMessage);
    } else {
        socket.send(encodedMessage);
    }
}

/**
 * Forwards a packet to the service instance.
 *
 * @param id The packet ID.
 * @param data The packet data.
 */
export function broadcast(
    id: number,
    data: Uint8Array | null
): void {
    // Encode the packet.
    const encoded = Packet.create({ id });
    data && (encoded.data = data);

    // Wrap the packet in a union packet.
    const union = UnionCmdNotify.create({ packets: [encoded] });
    const message = UnionCmdNotify.toBinary(union);

    // Send the packet.
    send(FrontendIds._FrontendCmdNotify, message);
}

/**
 * Sets up the web socket.
 */
export function setup(): void {
    socket = new WebSocket(`ws://${getBaseUrl()}/socket`);
    socket.onopen = () => {
        console.debug("Connected to backend.");

        if (messageQueue.length > 0) {
            console.debug("Sending queued messages...");
            for (const message of messageQueue) {
                socket!.send(message);
            }
            messageQueue.splice(0, messageQueue.length);
        }
    };
    socket.onclose = () => {
        console.debug("Disconnected from backend.");
        setTimeout(() => {
            console.debug("Reconnecting to backend...");
            socket = new WebSocket(`ws://${getBaseUrl()}/socket`);
        }, 5000);
    };
    socket.onerror = (error) => console.error(error);
    socket.onmessage = (event) => {
        // Parse the message.
        const message = base64decode(event.data);
        const packet = Packet.fromBinary(message);

        // Try to decode the packet.
        const decoder = decoders[packet.id];

        let data = !packet.data ? null :
            decoder ? decoder(packet.data) : null;

        // Emit the packet.
        window.dispatchEvent(new SocketEvent(packet.id, data));
    };
}

/**
 * Handshakes with the backend.
 */
export function handshake(): void {
    send(FrontendIds._FrontendJoinCsReq, null);
}

export default class SocketEvent extends Event {
    constructor(
        public readonly id: number,
        public readonly data: MessageType<object> | any | null
    ) {
        super(`socket:${id}`);
    }
}
