package no.runsafe.headhunters.command;

import no.runsafe.framework.command.ExecutableCommand;
import no.runsafe.framework.output.ChatColour;
import no.runsafe.framework.server.ICommandExecutor;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 20-4-13
 * Time: 18:31
 */
public class Enable extends ExecutableCommand {

    Core core;

    public Enable(Core core){
        super("enable", "Enables the plugin", "headhunters.Enable");
        this.core = core;
    }

    @Override
    public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters) {

        this.core.enable();

        return "Headhunters " + ChatColour.GREEN + "enabled";

    }
}
