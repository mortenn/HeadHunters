package nl.naxanria.headhunters.command;

import nl.naxanria.headhunters.Core;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;

public class CommandStop extends PlayerCommand
{
	public CommandStop(Core core)
	{
		super("stop", "Stops the current game", "headhunters.game-control.stop");
		this.core = core;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		this.core.stop(executor);
		return null;
	}

	private final Core core;
}
