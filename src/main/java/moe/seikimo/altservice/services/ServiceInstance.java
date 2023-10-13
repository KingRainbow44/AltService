package moe.seikimo.altservice.services;

import lombok.Data;

@Data
public final class ServiceInstance {
    private final String serverAddress;
    private final short serverPort;

    /**
     * @return The server address and port.
     */
    public String getServer() {
        return this.serverAddress + ":" + this.serverPort;
    }
}
