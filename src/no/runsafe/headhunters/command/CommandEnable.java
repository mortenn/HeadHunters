package no.runsafe.headhunters.command;

import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.text.ChatColour;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

public class CommandEnable extends ExecutableCommand
{
	public CommandEnable(Core core)
	{
		super("enable", "Enables the plugin", "headhunters.admin.enable");
		this.core = core;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters)
	{
		if (this.core.enable())
			return "Headhunters " + ChatColour.GREEN + "enabled";

		core.disable();
		return Constants.ERROR_COLOR + "There are no areas or no waitroom defined!";
	}

	private final Core core;
}
