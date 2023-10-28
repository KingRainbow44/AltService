package moe.seikimo.altservice.event.client;

import moe.seikimo.altservice.player.Player;

/**
 * Invoked when a client joins the server.
 */
public final class ClientJoinEvent extends ClientEvent {
    public ClientJoinEvent(Player client) {
        super(client);
    }
}
