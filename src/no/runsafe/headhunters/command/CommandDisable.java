package no.runsafe.headhunters.command;


import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;

import no.runsafe.framework.text.ChatColour;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 20-4-13
 * Time: 18:31
 */
public class CommandDisable extends ExecutableCommand {

    Core core;

    public CommandDisable(Core core){
        super("disable", "Disables the plugin", "headhunters.admin.disable");
        this.core = core;
    }

    @Override
    public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters) {
        this.core.disable();

        return "Headhunters " + ChatColour.RED + "disabled";
    }
}