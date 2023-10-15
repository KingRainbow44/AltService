import { getBaseUrl } from "@app/utils.ts";
import { base64decode } from "@protobuf-ts/runtime";
import { Packet } from "@backend/Structures.ts";
import { ChatMessageNotify, FrontendIds } from "@backend/Frontend.ts";

const decoders: { [key: number]: (data: Uint8Array) => any } = {
    [FrontendIds._ChatMessageNotify]: (data: Uint8Array) => ChatMessageNotify.fromBinary(data)
};

export let socket = new WebSocket(`ws://${getBaseUrl()}/socket`);
socket.onopen = () => console.debug("Connected to backend.");
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

export default class SocketEvent extends Event {
    constructor(
        public readonly id: number,
        public readonly data: any
    ) {
        super(`socket:${id}`);
    }
}
