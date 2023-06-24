package moe.seikimo.altservice.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.script.ScriptLoader;

import java.util.List;

public final class RunScriptCommand extends Command {
    public RunScriptCommand() {
        super("runscript");
    }

    @Override
    public void execute(List<String> args) {
        if (args.size() < 1) {
            this.sendMessage("Please specify a script name.");
            return;
        }

        var script = args.get(0);
        var result = ScriptLoader.invokeScript(script, false);
        if (result == null) {
            this.sendMessage("The script could not be found.");
        } else {
            this.sendMessage("The script was executed successfully.");
        }
    }
}
