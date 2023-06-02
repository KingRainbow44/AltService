package moe.seikimo.altservice.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.seikimo.altservice.AltBackend;

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
    public abstract void execute(List<String> args);

    /**
     * Sends a message to the console.
     *
     * @param message The message to send.
     */
    protected void sendMessage(String message) {
        AltBackend.getLogger().info(message);
    }
}
