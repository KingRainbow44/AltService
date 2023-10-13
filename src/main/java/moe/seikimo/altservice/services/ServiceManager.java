package moe.seikimo.altservice.services;

import moe.seikimo.altservice.AltService;
import moe.seikimo.altservice.proto.Service;
import moe.seikimo.altservice.proto.Service.ServiceIds;
import moe.seikimo.altservice.proto.Service.ServiceJoinCsReq;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;

public final class ServiceManager {
    private static final Map<String, ServiceInstance> instances = new HashMap<>();

    /**
     * Invoked when a service requests to connect.
     *
     * @param socket The socket.
     * @param request The request.
     */
    public static void onServiceJoin(WebSocket socket, ServiceJoinCsReq request) {
        var address = request.getServerAddress();
        var port = (short) request.getServerPort();
        var combined = address + ":" + port;

        // Check if the service is already registered.
        if (instances.containsKey(combined)) {
            socket.close(); // Close the socket.
            return;
        }

        // Create a new service instance.
        var instance = new ServiceInstance(address, port);
        instances.put(combined, instance);

        // Send back the response packet.
        AltService.send(socket, ServiceIds._ServiceJoinScRsp, null);

        AltService.getLogger().info("Registered service: {}.", instance.getServer());
    }
}
