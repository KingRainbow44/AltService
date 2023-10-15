import { getBaseUrl } from "@app/utils.ts";

const socket = new WebSocket(`ws://${getBaseUrl()}/socket`);
socket.onopen = () => console.log("Socket opened");
socket.onclose = () => console.log("Socket closed");
socket.onerror = (error) => console.error("Socket error:", error);

export function test() {
    socket.send("test");
}
