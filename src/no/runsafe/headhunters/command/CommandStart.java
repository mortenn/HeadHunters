package no.runsafe.headhunters.command;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

public class CommandStart extends PlayerCommand
{
	public CommandStart(Core core)
	{
		super("start", "Forces headhunters match to start", "headhunters.game-control.start", "time");
		this.core = core;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		return core.startInTime(Integer.valueOf(parameters.get("time")));
	}

	private final Core core;
}
