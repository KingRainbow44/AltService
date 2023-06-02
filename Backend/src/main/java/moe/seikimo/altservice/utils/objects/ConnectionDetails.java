package moe.seikimo.altservice.utils.objects;

import java.net.InetSocketAddress;

/**
 * Server connection details.
 * @param address The server address.
 * @param port The server port.
 * @param online Authentication requirement.
 */
public record ConnectionDetails(
        String address,
        int port,
        boolean online
) {

    /**
     * Creates a new ConnectionDetails object from a socket address.
     * @param address The socket address.
     * @return The connection details created.
     */
    public static ConnectionDetails fromSocketAddress(InetSocketAddress address) {
        return fromSocketAddress(address, true);
    }

    /**
     * Creates a new ConnectionDetails object from a socket address.
     * @param address The socket address.
     * @param online Whether the server requires authentication.
     * @return The connection details created.
     */
    public static ConnectionDetails fromSocketAddress(InetSocketAddress address, boolean online) {
        return new ConnectionDetails(address.getHostString(), address.getPort(), online);
    }


    /**
     * Converts the connection details to an InetSocketAddress.
     * @return The InetSocketAddress.
     */
    public InetSocketAddress toSocketAddress() {
        return new InetSocketAddress(this.address, this.port);
    }

    @Override
    public String toString() {
        return this.address + ":" + this.port;
    }
}
