package moe.seikimo.altservice.command;

import org.jetbrains.annotations.Nullable;

public interface CommandMap {

    /**
     * Add a command to the map.
     *
     * @param command The command to add.
     */
    void addCommand(Command command);

    /**
     * Get a command by its label.
     *
     * @param label The command label.
     * @return The command, or null if not found.
     */
    @Nullable
    Command getCommand(String label);

    /**
     * Parses and executes a command from the console.
     *
     * @param input The command input.
     */
    void invoke(String input);
}
