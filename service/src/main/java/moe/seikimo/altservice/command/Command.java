package moe.seikimo.altservice.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class Command {
    private final String label;

    /**
     * Invoked when the command is executed.
     *
     * @param args The command arguments.
     */
    public void execute(List<String> args) { }

    /**
     * Invoked when the command is executed.
     *
     * @param player The player which should execute the command.
     * @param sender The player which sent the command.
     * @param args   The command arguments.
     */
    public void execute(Player player, ServerPlayer sender, List<String> args) { }

    /**
     * Sends a message to the console.
     *
     * @param message The message to send.
     */
    protected void sendMessage(String message, Object... args) {
        AltBackend.getLogger().info(message, args);
    }
}
