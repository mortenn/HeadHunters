package no.runsafe.headhunters.command;

import no.runsafe.framework.command.ExecutableCommand;
import no.runsafe.framework.output.ChatColour;
import no.runsafe.framework.server.ICommandExecutor;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 20-4-13
 * Time: 18:31
 */
public class CommandEnable extends ExecutableCommand {

    Core core;

    public CommandEnable(Core core){
        super("enable", "Enables the plugin", "headhunters.CommandEnable");
        this.core = core;
    }

    @Override
    public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters) {

        if(!this.core.enable())
            return Constants.ERROR_COLOR + "There are no areas defined!";
        else
            return "Headhunters " + ChatColour.GREEN + "enabled";

    }
}
