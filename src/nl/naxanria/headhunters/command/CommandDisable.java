package nl.naxanria.headhunters.command;

import nl.naxanria.headhunters.Core;
import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.text.ChatColour;

import java.util.HashMap;

public class CommandDisable extends ExecutableCommand
{
	public CommandDisable(Core core)
	{
		super("disable", "Disables the plugin", "headhunters.admin.disable");
		this.core = core;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters)
	{
		this.core.disable();
		return "Headhunters " + ChatColour.RED + "disabled";
	}

	private final Core core;
}