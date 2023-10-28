package moe.seikimo.altservice.event.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.altservice.event.Event;
import moe.seikimo.altservice.player.Player;

/**
 * An event which happens to clients.
 */
@Getter
@RequiredArgsConstructor
public abstract class ClientEvent extends Event {
    private final Player client;
}
