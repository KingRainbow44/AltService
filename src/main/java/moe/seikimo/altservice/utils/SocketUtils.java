package moe.seikimo.altservice.utils;

import org.java_websocket.WebSocket;

public interface SocketUtils {
    /**
     * Gets the address of a socket.
     *
     * @param socket The socket.
     * @return The address of the socket.
     */
    static String getAddress(WebSocket socket) {
        var remoteAddress = socket.getRemoteSocketAddress();
        return remoteAddress.getAddress().getHostAddress() +
                ":" + remoteAddress.getPort();
    }
}
