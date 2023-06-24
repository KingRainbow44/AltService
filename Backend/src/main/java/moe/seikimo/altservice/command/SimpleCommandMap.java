package moe.seikimo.altservice.command;

import moe.seikimo.altservice.command.player.DisconnectCommand;
import moe.seikimo.altservice.command.player.RequestCommand;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SimpleCommandMap implements CommandMap {
    private static final Map<String, Command> commands = new HashMap<>() {{
        // Command defaults should be registered here.
        this.put("request", new RequestCommand());
        this.put("disconnect", new DisconnectCommand());
    }};

    @Override
    public void addCommand(Command command) {
        SimpleCommandMap.commands.put(command.getLabel(), command);
    }

    @Nullable
    public Command getCommand(String label) {
        return SimpleCommandMap.commands.get(label);
    }

    /**
     * Parses and executes a command from the console.
     *
     * @param input The command input.
     */
    public void invoke(String input) {
        var split = input.split(" ");
        if (split.length < 1) return;

        // Parse the command label.
        var label = split[0];
        var arguments = Arrays.asList(split)
                .subList(1, split.length);

        // Get the command.
        var command = this.getCommand(label);
        if (command == null) return;

        // Execute the command.
        command.execute(arguments);
    }
}
